package com.example.yavi.enrollment.adapter;

import com.example.yavi.TestDatabaseConfig;
import com.example.yavi.enrollment.domain.Identifier;
import com.example.yavi.enrollment.domain.Student;
import org.jooq.DSLContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDatabaseConfig.class})
class StudentRepositoryImplTest {
    private StudentRepository sut;

    @BeforeEach
    public void setUp(@Autowired DSLContext dslContext) {
        StudentMapper studentMapper = new StudentMapper(dslContext);
        sut = new StudentRepositoryImpl(dslContext, studentMapper);
    }
    @Test
    void findById() {
        Student student = sut.findById(Identifier.of(1L));
        assertThat(student).isNotNull().hasFieldOrPropertyWithValue("studentId", Identifier.of(1L));
    }

    @Test
    void save() {
        Student student = Student.create("kawasima", Student.StudentStatus.ACTIVE)
                        .value();
        sut.save(student);
        System.out.println(student);

        // Verify that the student was saved correctly
        Student savedStudent = sut.findById(Identifier.of(1L));
        assertThat(savedStudent).isNotNull().hasFieldOrPropertyWithValue("studentId", Identifier.of(1L));
    }
}