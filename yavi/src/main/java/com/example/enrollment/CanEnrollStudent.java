package com.example.enrollment;

public record CanEnrollStudent(
        Student student,
        Course course
) { }