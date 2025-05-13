package com.example.yavi.enrollment;

import com.example.yavi.ApiResponse;
import com.example.yavi.TestDatabaseConfig;
import com.example.yavi.enrollment.adapter.CourseRepository;
import com.example.yavi.enrollment.adapter.StudentRepository;
import com.example.yavi.enrollment.domain.*;
import com.example.yavi.joboffer.domain.StudentController;
import org.jooq.DSLContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestDatabaseConfig.class})
class StudentControllerTest {
    @Test
    void testEnrollStudent(@Autowired DSLContext dslContext) {
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
    void testNonActiveStudentCannotEnroll(@Autowired DSLContext dslContext) {
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
    void cannotEnrollWhenNoAvailableSeats(@Autowired DSLContext dslContext) {
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