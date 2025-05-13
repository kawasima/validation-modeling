package com.example.yavi.enrollment.adapter;

import com.example.yavi.enrollment.domain.Course;
import com.example.yavi.enrollment.domain.Identifier;
import com.example.yavi.enrollment.domain.Student;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Optional;

import static org.jooq.impl.DSL.table;

@Repository
public class CourseRepositoryImpl implements CourseRepository {
    private final DSLContext dslContext;
    private final CourseMapper courseMapper;
    private final EnrollmentMapper enrollmentMapper;

    public CourseRepositoryImpl(DSLContext dslContext, CourseMapper courseMapper, EnrollmentMapper enrollmentMapper) {
        this.dslContext = dslContext;
        this.courseMapper = courseMapper;
        this.enrollmentMapper = enrollmentMapper;
    }

    @Override
    public Course findById(Identifier courseId) {
        return Optional.ofNullable(dslContext.select()
                .from("courses")
                .where("course_id = ?", courseId.getValue())
                .fetchOne())
                .map(courseMapper::toDomain)
                .orElseThrow(() -> new RecordNotFoundException(courseId));
    }

    @Override
    public int availableEnrollments(Identifier courseId) {
        Integer enrollments = dslContext.selectCount()
                .from("enrollments")
                .where("course_id = ?", courseId.getValue())
                .fetchOne(0, int.class);
        return Objects.requireNonNullElse(enrollments, 0);

    }

    @Override
    public void enrollStudent(Course course, Student student) {
        dslContext.insertInto(table("enrollments"))
                .set(enrollmentMapper.toRecord(course, student))
                .execute();
    }
}
