package com.example.yavi.enrollment.adapter;

import com.example.yavi.enrollment.domain.Identifier;
import com.example.yavi.enrollment.domain.Student;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static org.jooq.impl.DSL.field;
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
            Record rec = dslContext.insertInto(table("students"))
                    .set(record)
                    .returning(field("student_id"))
                    .fetchOne();
            Optional.ofNullable(rec)
                    .map(r -> r.get("student_id", Long.class))
                    .ifPresent(id -> student.getStudentId().decide(id));
        }
    }
}
