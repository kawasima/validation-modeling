package com.example.yavi.enrollment.domain;

import am.ik.yavi.core.ConstraintViolation;
import am.ik.yavi.core.Validated;
import com.example.yavi.enrollment.adapter.CourseRepository;

public class EnrollmentRule {
    private final CourseRepository courseRepository;
    public EnrollmentRule(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Validated<CanEnrollStudent> apply(Student student, Course course) {
        if (student.getStatus() != Student.StudentStatus.ACTIVE) {
            return Validated.failureWith(ConstraintViolation.builder()
                    .name("studentStatus")
                    .message("StudentがActiveではありません"));
        }
        if (courseRepository.availableEnrollments(course.getCourseId()) == 0) {
            return Validated.failureWith(ConstraintViolation.builder()
                    .name("availableEnrollments")
                    .message("コースに空きがありません"));
        }
        return Validated.successWith(new CanEnrollStudent(student, course));
    }
}
