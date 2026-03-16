package com.example.raoh.enrollment.data;

/**
 * data 受講可能 = 学生 AND コース
 */
public record CanEnrollStudent(Student student, Course course) {}
