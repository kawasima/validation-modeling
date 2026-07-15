package com.example.raoh.enrollment.web;

import com.example.raoh.enrollment.behavior.EnrollStudent;
import com.example.raoh.enrollment.data.CanEnrollStudent;
import com.example.raoh.enrollment.data.EnrollStudentInput;
import com.example.raoh.enrollment.gateway.CourseGateway;
import com.example.raoh.enrollment.gateway.StudentGateway;
import net.unit8.raoh.Err;
import net.unit8.raoh.Ok;
import net.unit8.raoh.json.JsonDecoder;
import org.jooq.DSLContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

import static com.example.raoh.enrollment.web.EnrollmentEncoders.ENROLLED;
import static com.example.raoh.enrollment.web.EnrollmentJsonDecoders.enrollStudentInput;
import static com.example.raoh.web.ErrorEncoders.ERRORS;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final CourseGateway courseGateway;
    private final DSLContext dslContext;
    private final EnrollStudent enrollStudent;
    private final JsonDecoder<EnrollStudentInput> enrollRequestDecoder;

    public EnrollmentController(StudentGateway studentGateway,
                                CourseGateway courseGateway,
                                DSLContext dslContext) {
        this.courseGateway = courseGateway;
        this.dslContext = dslContext;
        this.enrollStudent = new EnrollStudent(courseGateway);
        this.enrollRequestDecoder = enrollStudentInput(studentGateway, courseGateway);
    }

    /**
     * 受講登録エンドポイント
     */
    @PostMapping
    public ResponseEntity<?> enroll(@RequestBody JsonNode jsonNode) {
        return switch (enrollRequestDecoder.decode(jsonNode)) {
            // behavior 受講登録する = 学生 AND コース -> 受講可能
            case Ok(EnrollStudentInput input) -> switch (enrollStudent.apply(input.student(), input.course())) {
                case Ok(CanEnrollStudent canEnroll) -> {
                    dslContext.transaction(() -> {
                        courseGateway.enrollStudent(canEnroll.course(), canEnroll.student());
                    });
                    yield ResponseEntity.ok(ENROLLED.encode(canEnroll));
                }
                // 業務ルールで受け付けられない。入力そのものは正しい
                case Err(var issues) -> ResponseEntity.unprocessableContent().body(ERRORS.encode(issues));
            };
            // デコード失敗。入力そのものが正しくない
            case Err(var issues) -> ResponseEntity.badRequest().body(ERRORS.encode(issues));
        };
    }
}
