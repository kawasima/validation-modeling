package com.example.enrollment;

import com.example.enrollment.adapter.CourseRepository;
import com.example.enrollment.adapter.StudentRepository;
import com.example.enrollment.domain.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class StudentControllerTest {
    @Test
    void testEnrollStudent() {
        // Given
        Student student = new Student(1, Student.StudentStatus.ACTIVE);
        Course course = new Course(1);

        StudentRepository studentRepository = mock(StudentRepository.class);
        when(studentRepository.findById(anyInt())).thenReturn(student);

        CourseRepository courseRepository = mock(CourseRepository.class);
        when(courseRepository.findById(anyInt())).thenReturn(course);
        when(courseRepository.availableEnrollments(anyInt())).thenReturn(1);

        StudentController studentController = new StudentController(studentRepository, courseRepository);
        // When
        ResponseEntity<ApiResponse<CanEnrollStudent>> response = studentController.enroll(1, 1);

        // Then
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody().getData().student().studentId()).isEqualTo(1);
    }

    @Test
    @DisplayName("学生がアクティブでない場合、エラーを返す")
    void testNonActiveStudentCannotEnroll() {
        // Given
        Student student = new Student(1, Student.StudentStatus.INACTIVE);
        Course course = new Course(1);

        StudentRepository studentRepository = mock(StudentRepository.class);
        when(studentRepository.findById(anyInt())).thenReturn(student);

        CourseRepository courseRepository = mock(CourseRepository.class);
        when(courseRepository.findById(anyInt())).thenReturn(course);
        when(courseRepository.availableEnrollments(anyInt())).thenReturn(1);

        StudentController studentController = new StudentController(studentRepository, courseRepository);
        // When
        ResponseEntity<ApiResponse<CanEnrollStudent>> response = studentController.enroll(1, 1);

        // Then
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    @DisplayName("コースに空きがない場合、エラーを返す")
    void cannotEnrollWhenNoAvailableSeats() {
        // Given
        Student student = new Student(1, Student.StudentStatus.ACTIVE);
        Course course = new Course(1);

        StudentRepository studentRepository = mock(StudentRepository.class);
        when(studentRepository.findById(anyInt())).thenReturn(student);

        CourseRepository courseRepository = mock(CourseRepository.class);
        when(courseRepository.findById(anyInt())).thenReturn(course);
        when(courseRepository.availableEnrollments(anyInt())).thenReturn(0);

        StudentController studentController = new StudentController(studentRepository, courseRepository);
        // When
        ResponseEntity<ApiResponse<CanEnrollStudent>> response = studentController.enroll(1, 1);

        // Then
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

}