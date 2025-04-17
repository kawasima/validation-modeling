package com.example.enrollment;

import am.ik.yavi.arguments.ArgumentsValidators;
import am.ik.yavi.core.ConstraintViolations;
import com.example.enrollment.adapter.CourseRepository;
import com.example.enrollment.adapter.StudentRepository;
import com.example.enrollment.domain.*;
import io.vavr.Tuple2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.example.enrollment.domain.Course.courseIdValidator;
import static com.example.enrollment.domain.Student.studentIdValidator;

@Controller
public class StudentController {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentController(StudentRepository studentRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<CanEnrollStudent>> enroll(@RequestParam("studentId") int studentId,
                                                                @RequestParam("courseId") int courseId) {
        EnrollmentRule enrollmentRule = new EnrollmentRule(courseRepository);

        return ArgumentsValidators.split(studentIdValidator, courseIdValidator)
                .apply(Tuple2::new)
                .validate(studentId, courseId)
                .map(
                        // 業務処理前のI/O
                $ -> new Tuple2<>(
                        studentRepository.findById($._1()),
                        courseRepository.findById($._2())
                ))
                .flatMap($ -> {
                    // 業務処理
                    Student student = $._1();
                    Course course = $._2();
                    return enrollmentRule.apply(student, course);
                }).fold(
                errors -> ResponseEntity.badRequest()
                                .body(ApiResponse.failure(ConstraintViolations.of(errors))),
                enroll -> {
                    // 業務処理後のI/O
                    courseRepository.enrollStudent(enroll.course(), enroll.student());
                    return ResponseEntity.ok(ApiResponse.success(enroll));
                });
    }
}
