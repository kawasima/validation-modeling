package com.example.raoh.enrollment.behavior;

import com.example.raoh.enrollment.data.CanEnrollStudent;
import com.example.raoh.enrollment.data.Course;
import com.example.raoh.enrollment.data.Student;
import com.example.raoh.enrollment.gateway.CourseGateway;
import net.unit8.raoh.Result;

import java.util.function.BiFunction;

/**
 * behavior 受講登録する = 学生 AND コース -> 受講可能
 *
 * 学生がアクティブであること
 * コースに空きがあること
 */
public class EnrollStudent implements BiFunction<Student, Course, Result<CanEnrollStudent>> {
    private final CourseGateway courseGateway;

    public EnrollStudent(CourseGateway courseGateway) {
        this.courseGateway = courseGateway;
    }

    @Override
    public Result<CanEnrollStudent> apply(Student student, Course course) {
        if (student.status() != Student.StudentStatus.ACTIVE) {
            return Result.fail("studentStatus", "StudentがActiveではありません");
        }
        if (courseGateway.availableEnrollments(course.courseId()) == 0) {
            return Result.fail("availableEnrollments", "コースに空きがありません");
        }
        return Result.ok(new CanEnrollStudent(student, course));
    }
}
