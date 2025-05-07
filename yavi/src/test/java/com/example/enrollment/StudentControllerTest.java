package com.example.enrollment;

import com.example.enrollment.adapter.CourseRepository;
import com.example.enrollment.adapter.StudentRepository;
import com.example.enrollment.domain.*;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jooq.SpringTransactionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {StudentControllerTest.Config.class})
class StudentControllerTest {
    @Configuration
    static class Config {
        @Bean
        public DataSource embeddedDatabase() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .build();
        }
        @Bean
        public DataSourceConnectionProvider connectionProvider(DataSource ds) {
            return new DataSourceConnectionProvider(
                    new TransactionAwareDataSourceProxy(ds));
        }
        @Bean
        public DefaultConfiguration jooqConfiguration(DataSourceConnectionProvider connectionProvider) {
            PlatformTransactionManager transactionManager = new DataSourceTransactionManager(connectionProvider.dataSource());
            DefaultConfiguration config = new DefaultConfiguration();
            config.set(connectionProvider);
            config.set(SQLDialect.H2);
            config.set(new ThreadLocalTransactionProvider(connectionProvider));
            return config;
        }
        @Bean
        public DSLContext dslContext(DefaultConfiguration jooqConfiguration) {
            return DSL.using(jooqConfiguration);
        }
    }

    @Autowired
    private DSLContext dslContext;

    @Test
    void testEnrollStudent() {
        // Given
        Student student = Student.of(1, "kawasima", Student.StudentStatus.ACTIVE);
        Course course = Course.of(1L, "AI");

        StudentRepository studentRepository = mock(StudentRepository.class);
        when(studentRepository.findById(any(Identifier.class))).thenReturn(student);

        CourseRepository courseRepository = mock(CourseRepository.class);
        when(courseRepository.findById(any(Identifier.class))).thenReturn(course);
        when(courseRepository.availableEnrollments(any(Identifier.class))).thenReturn(1);

        StudentController studentController = new StudentController(dslContext, studentRepository, courseRepository);
        // When
        ResponseEntity<ApiResponse<CanEnrollStudent>> response = studentController.enroll(1, 1);

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().getData().student().getStudentId()).isEqualTo(Identifier.of(1L));
    }

    @Test
    @DisplayName("学生がアクティブでない場合、エラーを返す")
    void testNonActiveStudentCannotEnroll() {
        // Given
        Student student = Student.of(1, "kawasima", Student.StudentStatus.INACTIVE);
        Course course = Course.of(1L, "AI");

        StudentRepository studentRepository = mock(StudentRepository.class);
        when(studentRepository.findById(any(Identifier.class))).thenReturn(student);

        CourseRepository courseRepository = mock(CourseRepository.class);
        when(courseRepository.findById(any(Identifier.class))).thenReturn(course);
        when(courseRepository.availableEnrollments(any(Identifier.class))).thenReturn(1);

        StudentController studentController = new StudentController(dslContext, studentRepository, courseRepository);
        // When
        ResponseEntity<ApiResponse<CanEnrollStudent>> response = studentController.enroll(1, 1);

        // Then
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    @DisplayName("コースに空きがない場合、エラーを返す")
    void cannotEnrollWhenNoAvailableSeats() {
        // Given
        Student student = Student.of(1, "kawasima",Student.StudentStatus.ACTIVE);
        Course course = Course.of(1L, "AI");

        StudentRepository studentRepository = mock(StudentRepository.class);
        when(studentRepository.findById(any(Identifier.class))).thenReturn(student);

        CourseRepository courseRepository = mock(CourseRepository.class);
        when(courseRepository.findById(any(Identifier.class))).thenReturn(course);
        when(courseRepository.availableEnrollments(any(Identifier.class))).thenReturn(0);

        StudentController studentController = new StudentController(dslContext, studentRepository, courseRepository);
        // When
        ResponseEntity<ApiResponse<CanEnrollStudent>> response = studentController.enroll(1, 1);

        // Then
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

}