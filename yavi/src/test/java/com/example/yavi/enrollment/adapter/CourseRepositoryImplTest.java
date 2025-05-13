package com.example.yavi.enrollment.adapter;

import com.example.yavi.TestDatabaseConfig;
import com.example.yavi.enrollment.domain.Course;
import com.example.yavi.enrollment.domain.Identifier;
import com.example.yavi.enrollment.domain.Student;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDatabaseConfig.class})
class CourseRepositoryImplTest {
    private CourseRepository sut;

    @BeforeEach
    public void setUp(@Autowired DSLContext dslContext) {
        CourseMapper courseMapper = new CourseMapper(dslContext);
        EnrollmentMapper enrollmentMapper = new EnrollmentMapper(dslContext);
        sut = new CourseRepositoryImpl(dslContext, courseMapper, enrollmentMapper);
    }

    @Test
    void findById() {
        Course course = sut.findById(Identifier.of(1L));
        assertThat(course).isNotNull().hasFieldOrPropertyWithValue("courseId", Identifier.of(1L));
    }

    @Test
    void enrollStudent() {
        Course course = sut.findById(Identifier.of(1L));
        Student student = Student.of(1, "kawasima", Student.StudentStatus.ACTIVE);
        sut.enrollStudent(course, student);
    }
}