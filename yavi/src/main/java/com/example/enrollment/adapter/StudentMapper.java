package com.example.enrollment.adapter;

import com.example.enrollment.domain.Student;
import org.jooq.DSLContext;
import org.jooq.Record;

import static org.jooq.impl.DSL.field;

public class StudentMapper {
    private final DSLContext dslContext;
    public StudentMapper(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public Student toDomain(Record rec) {
        assert rec != null;
        return Student.of(
                rec.get("student_id", Long.class),
                rec.get("name", String.class),
                rec.get("status", Student.StudentStatus.class));
    }

    public Record toRecord(Student student) {
        assert student != null;
        Record rec = dslContext.newRecord();
        if (student.getStudentId().isDecided()) {
            rec.set(field("student_id"), student.getStudentId().getValue());
        }
        rec.set(field("name"), student.getName());
        rec.set(field("status"), student.getStatus());
        return rec;
    }
}
