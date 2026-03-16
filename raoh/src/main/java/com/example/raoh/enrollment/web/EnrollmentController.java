package com.example.raoh.enrollment.web;

import com.example.raoh.enrollment.behavior.EnrollStudent;
import com.example.raoh.enrollment.data.CanEnrollStudent;
import com.example.raoh.enrollment.data.Course;
import com.example.raoh.enrollment.data.Student;
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

import java.util.Map;

import static com.example.raoh.enrollment.web.EnrollmentJsonDecoders.*;
import static net.unit8.raoh.json.JsonDecoders.*;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {
    private final CourseGateway courseGateway;
    private final DSLContext dslContext;
    private final EnrollStudent enrollStudent;
    private final JsonDecoder<CanEnrollStudent> enrollRequestDecoder;

    public EnrollmentController(StudentGateway studentGateway,
                                CourseGateway courseGateway,
                                DSLContext dslContext) {
        this.courseGateway = courseGateway;
        this.dslContext = dslContext;
        this.enrollStudent = new EnrollStudent(courseGateway);

        enrollRequestDecoder = combine(
                field("studentId", student(studentGateway)),
                field("courseId", course(courseGateway))
        ).map(CanEnrollStudent::new)
                .flatMap(can -> enrollStudent.apply(can.student(), can.course()))::decode;
    }

    /**
     * 受講登録エンドポイント
     */
    @PostMapping
    public ResponseEntity<?> enroll(@RequestBody JsonNode jsonNode) {
        return switch (enrollRequestDecoder.decode(jsonNode)) {
            case Ok(CanEnrollStudent canEnroll) -> {
                dslContext.transaction(() -> {
                    courseGateway.enrollStudent(canEnroll.course(), canEnroll.student());
                });
                yield ResponseEntity.ok(Map.of(
                        "studentId", canEnroll.student().studentId(),
                        "courseId", canEnroll.course().courseId(),
                        "studentName", canEnroll.student().name(),
                        "courseName", canEnroll.course().name()
                ));
            }
            case Err(var issues) -> ResponseEntity.badRequest().body(Map.of(
                    "errors", issues.asList().stream()
                            .map(i -> Map.of("path", i.path().toString(), "message", i.message()))
                            .toList()
            ));
        };
    }
}
