package com.example.enrollment.adapter;

import com.example.enrollment.domain.Identifier;
import com.example.enrollment.domain.Student;
import org.jooq.DSLContext;

import java.util.Optional;

public class StudentRepositoryImpl implements StudentRepository {
    private final DSLContext dslContext;
    private final StudentMapper studentMapper;

    public StudentRepositoryImpl(DSLContext dslContext, StudentMapper studentMapper) {
        this.dslContext = dslContext;
        this.studentMapper = studentMapper;
    }

    @Override
    public Student findById(Identifier studentId) {
        return Optional.ofNullable(dslContext.select()
                .from("students")
                .where("student_id = ?", studentId.getValue())
                .fetchOne())
                .map(studentMapper::toDomain)
                .orElseThrow(() -> new RecordNotFoundException(studentId));
    }
}
