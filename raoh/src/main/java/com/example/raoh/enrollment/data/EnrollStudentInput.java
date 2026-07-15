package com.example.raoh.enrollment.data;

/**
 * data 受講登録入力 = 学生 AND コース
 */
public record EnrollStudentInput(Student student, Course course) {}
