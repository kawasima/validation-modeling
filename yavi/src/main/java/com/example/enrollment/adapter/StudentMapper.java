package com.example.enrollment.adapter;

import com.example.enrollment.domain.Student;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

import static org.jooq.impl.DSL.field;

@Component
public class StudentMapper {
    private static final List<Field<?>> FIELDS = List.of(
            field("name"),
            field("status")
    );

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
        Record rec;
        if (student.getStudentId().isDecided()) {
            rec = dslContext.newRecord(Stream.concat(
                    Stream.of(field("student_id")), FIELDS.stream()
            ).toList());
            rec.set(field("student_id"), student.getStudentId().getValue());
        } else {
            rec = dslContext.newRecord(FIELDS);
        }
        rec.set(field("name"), student.getName());
        rec.set(field("status"), student.getStatus().name());
        return rec;
    }
}
