package com.example.yavi.enrollment.adapter;

import com.example.yavi.enrollment.domain.Identifier;
import com.example.yavi.enrollment.domain.Student;

public interface StudentRepository {
    Student findById(Identifier studentId);

    void save(Student student);
}
