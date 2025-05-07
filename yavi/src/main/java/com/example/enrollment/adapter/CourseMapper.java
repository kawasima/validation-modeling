package com.example.enrollment.adapter;

import com.example.enrollment.domain.Course;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Component;

@Component
public class CourseMapper {
    private final DSLContext dslContext;

    public CourseMapper(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    public Course toDomain(Record rec) {
        assert rec != null;
        return Course.of(
                rec.get("course_id", Long.class),
                rec.get("name", String.class)
        );
    }

    public Record toRecord(Course course) {
        assert course != null;
        Record rec = dslContext.newRecord();
        if (course.getCourseId().isDecided()) {
            rec.set(DSL.field("course_id"), course.getCourseId().getValue());
        }
        rec.set(DSL.field("name"), course.getName());
        return rec;
    }
}
