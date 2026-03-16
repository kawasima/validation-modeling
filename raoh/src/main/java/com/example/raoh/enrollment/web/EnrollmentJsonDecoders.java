package com.example.raoh.enrollment.web;

import com.example.raoh.enrollment.data.Course;
import com.example.raoh.enrollment.data.Student;
import com.example.raoh.enrollment.gateway.CourseGateway;
import com.example.raoh.enrollment.gateway.StudentGateway;
import net.unit8.raoh.json.JsonDecoder;

import static net.unit8.raoh.json.JsonDecoders.*;

public class EnrollmentJsonDecoders {
    public static JsonDecoder<Student> student(StudentGateway studentGateway) {
        return long_().min(1).flatMap(studentGateway::findById)::decode;
    }

    public static JsonDecoder<Course> course(CourseGateway courseGateway) {
        return long_().min(1).flatMap(courseGateway::findById)::decode;
    }
}
