package com.example.raoh.enrollment.data;

/**
 * data 学生 = 学生ID AND 名前 AND ステータス
 */
public record Student(long studentId, String name, StudentStatus status) {
    public enum StudentStatus { ACTIVE, INACTIVE }
}
