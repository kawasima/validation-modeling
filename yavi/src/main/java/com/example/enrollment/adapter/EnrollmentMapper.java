package com.example.enrollment.adapter;

import com.example.enrollment.domain.Course;
import com.example.enrollment.domain.Student;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import static org.jooq.impl.DSL.field;

@Component
public class EnrollmentMapper {
    private final DSLContext dslContext;

    public EnrollmentMapper(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public Record toRecord(Course course, Student student) {
        Record rec = dslContext.newRecord(
                field("student_id"),
                field("course_id"));
        rec.set(field("student_id"), student.getStudentId().getValue());
        rec.set(field("course_id"), course.getCourseId().getValue());
        return rec;
    }
}
