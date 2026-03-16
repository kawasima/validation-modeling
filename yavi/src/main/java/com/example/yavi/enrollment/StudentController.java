package com.example.yavi.enrollment;

import am.ik.yavi.arguments.ArgumentsValidators;
import am.ik.yavi.core.ConstraintViolations;
import com.example.yavi.ApiResponse;
import com.example.yavi.enrollment.adapter.CourseRepository;
import com.example.yavi.enrollment.adapter.StudentRepository;
import com.example.yavi.enrollment.domain.*;
import org.jooq.DSLContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.example.yavi.enrollment.domain.Course.courseIdValidator;
import static com.example.yavi.enrollment.domain.Student.studentIdValidator;

@Controller
public class StudentController {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final DSLContext dslContext;

    record Pair<A, B>(A first, B second) {}

    public StudentController(DSLContext dslContext, StudentRepository studentRepository, CourseRepository courseRepository) {
        this.dslContext = dslContext;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<CanEnrollStudent>> enroll(@RequestParam("studentId") long studentId,
                                                                @RequestParam("courseId") long courseId) {
        EnrollmentRule enrollmentRule = new EnrollmentRule(courseRepository);

        return ArgumentsValidators.split(studentIdValidator, courseIdValidator)
                .apply(Pair::new)
                .validate(studentId, courseId)
                .map(
                        // 業務処理前のI/O
                $ -> new Pair<>(
                        studentRepository.findById($.first()),
                        courseRepository.findById($.second())
                ))
                .flatMap($ -> {
                    // 業務処理
                    Student student1 = $.first();
                    Course course1 = $.second();
                    return enrollmentRule.apply(student1, course1);
                }).fold(
                errors -> ResponseEntity.badRequest()
                                .body(ApiResponse.failure(ConstraintViolations.of(errors))),
                enroll -> {
                    // 業務処理後のI/O
                    return dslContext.transactionResult(() -> {
                        courseRepository.enrollStudent(enroll.course(), enroll.student());
                        return ResponseEntity.ok(ApiResponse.success(enroll));
                    });
                });
    }
}
