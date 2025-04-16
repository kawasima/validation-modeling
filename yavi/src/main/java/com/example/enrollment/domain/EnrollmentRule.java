package com.example.enrollment.domain;

import am.ik.yavi.core.ConstraintViolation;
import am.ik.yavi.core.Validated;
import am.ik.yavi.message.SimpleMessageFormatter;
import com.example.enrollment.adapter.CourseRepository;

import java.util.Locale;

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
