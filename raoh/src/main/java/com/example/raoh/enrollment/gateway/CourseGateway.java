package com.example.raoh.enrollment.gateway;

import com.example.raoh.enrollment.data.Course;
import com.example.raoh.enrollment.data.Student;
import net.unit8.raoh.Result;

public interface CourseGateway {
    Result<Course> findById(long courseId);
    int availableEnrollments(long courseId);
    void enrollStudent(Course course, Student student);
}
