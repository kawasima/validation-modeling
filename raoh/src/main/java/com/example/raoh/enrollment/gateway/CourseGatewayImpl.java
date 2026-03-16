package com.example.raoh.enrollment.gateway;

import com.example.raoh.enrollment.data.Course;
import com.example.raoh.enrollment.data.Student;
import net.unit8.raoh.Result;
import org.jooq.DSLContext;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

@Component
public class CourseGatewayImpl implements CourseGateway {
    private final DSLContext dslContext;

    public CourseGatewayImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Result<Course> findById(long courseId) {
        var record = dslContext.select(
                        field("course_id"),
                        field("name"))
                .from("courses")
                .where(field("course_id").eq(courseId))
                .fetchOne();
        if (record == null) {
            return Result.fail("not_found", "コースが見つかりません");
        }
        return RecordDecoders.course().decode(record);
    }

    @Override
    public int availableEnrollments(long courseId) {
        Integer count = dslContext.selectCount()
                .from("enrollments")
                .where("course_id = ?", courseId)
                .fetchOne(0, int.class);
        return Objects.requireNonNullElse(count, 0);
    }

    @Override
    public void enrollStudent(Course course, Student student) {
        dslContext.insertInto(table("enrollments"))
                .set(field("student_id"), student.studentId())
                .set(field("course_id"), course.courseId())
                .execute();
    }
}
