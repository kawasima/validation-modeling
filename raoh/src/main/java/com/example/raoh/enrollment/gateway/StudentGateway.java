package com.example.raoh.enrollment.gateway;

import com.example.raoh.enrollment.data.Student;
import net.unit8.raoh.Result;

public interface StudentGateway {
    Result<Student> findById(long studentId);
}
