package com.example.enrollment;

import am.ik.yavi.core.ConstraintViolation;
import am.ik.yavi.core.Validated;
import am.ik.yavi.message.SimpleMessageFormatter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class EnrollmentRule {
    private final CourseRepository courseRepository;
    public EnrollmentRule(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Validated<CanEnrollStudent> apply(Student student, Course course) {
        if (student.status() != Student.StudentStatus.ACTIVE) {
            return Validated.failureWith(new ConstraintViolation(
                    "studentStatus",
                    "student.status",
                    "StudentがActiveではありません",
                    new Object[]{},
                    new SimpleMessageFormatter(),
                    Locale.getDefault()
            ));
        }
        if (courseRepository.availableEnrollments(course.courseId()) == 0) {
            return Validated.failureWith(new ConstraintViolation(
                    "availableEnrollments",
                    "course.enrollment",
                    "コースに空きがありません",
                    new Object[]{},
                    new SimpleMessageFormatter(),
                    Locale.getDefault()
            ));
        }
        return Validated.successWith(new CanEnrollStudent(student, course));
    }
}
