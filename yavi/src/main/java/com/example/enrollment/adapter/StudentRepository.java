package com.example.enrollment.adapter;

import com.example.enrollment.domain.Identifier;
import com.example.enrollment.domain.Student;

public interface StudentRepository {
    Student findById(Identifier studentId);

    void save(Student student);
}
