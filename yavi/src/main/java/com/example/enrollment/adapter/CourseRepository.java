package com.example.enrollment.adapter;

import com.example.enrollment.domain.Course;
import com.example.enrollment.domain.Identifier;
import com.example.enrollment.domain.Student;

public interface CourseRepository {
    Course findById(Identifier courseId);

    int availableEnrollments(Identifier courseId);

    // StudentをCourseに登録する
    // Courseの空きチェックはされていることが前提ではあるが、コンカレンシーのために、
    // この段階でもCourseに空きがなくなってしまうことはありうる。
    // その場合は、予期しない例外としてRuntimeErrorが発生する。
    void enrollStudent(Course course, Student student);
}
