package com.example.enrollment.adapter;

import com.example.enrollment.domain.Identifier;
import com.example.enrollment.domain.Student;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.jooq.impl.DSL.table;

@Repository
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

    @Override
    public void save(Student student) {
        var record = studentMapper.toRecord(student);
        if (student.getStudentId().isDecided()) {
            dslContext.update(table("students"))
                    .set(record)
                    .where("student_id = ?", student.getStudentId().getValue())
                    .execute();
        } else {
            dslContext.insertInto(table("students"))
                    .set(record)
                    .execute();
        }
    }
}
