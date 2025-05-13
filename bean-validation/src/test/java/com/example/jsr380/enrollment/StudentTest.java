package com.example.jsr380.enrollment;

import com.example.jsr380.validation.Validated;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StudentTest {
    @Test
    void valid() {
        Validated<Student> student = Student.parse(1L, "John Doe");
        assertThat(student.isValid()).isTrue();
    }

    @Test
    void identifierInvalid() {
        Validated<Student> student = Student.parse(-10L, "John Doe");
        assertThat(student.isValid()).isFalse();
        assertThat(student.errors()).anyMatch(error -> error.getPropertyPath().toString().equals("id.value"));
    }
}