package com.example.enrollment.adapter;

import com.example.TestDatabaseConfig;
import com.example.enrollment.domain.Identifier;
import com.example.enrollment.domain.Student;
import org.jooq.DSLContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDatabaseConfig.class})
class StudentRepositoryImplTest {
    @Test
    void findById(@Autowired DSLContext dslContext) {
        StudentMapper studentMapper = new StudentMapper(dslContext);
        StudentRepository studentRepository = new StudentRepositoryImpl(dslContext, studentMapper);
        Student student = studentRepository.findById(Identifier.of(1L));
        assertThat(student).isNotNull().hasFieldOrPropertyWithValue("studentId", Identifier.of(1L));
    }

    @Test
    void save(@Autowired DSLContext dslContext) {
        StudentMapper studentMapper = new StudentMapper(dslContext);
        StudentRepository studentRepository = new StudentRepositoryImpl(dslContext, studentMapper);
        Student student = Student.of(1, "kawasima", Student.StudentStatus.ACTIVE);
        studentRepository.save(student);

        // Verify that the student was saved correctly
        Student savedStudent = studentRepository.findById(Identifier.of(1L));
        assertThat(savedStudent).isNotNull().hasFieldOrPropertyWithValue("studentId", Identifier.of(1L));
    }
}