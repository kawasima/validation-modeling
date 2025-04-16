package com.example.enrollment.adapter;

import com.example.enrollment.domain.Student;

public interface StudentRepository {
    Student findById(int studentId);
}
