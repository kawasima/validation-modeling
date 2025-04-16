package com.example.enrollment.domain;

public class CanEnrollStudent {
    private final Student student;
    private final Course course;

    CanEnrollStudent(Student student, Course course) {
        this.student = student;
        this.course = course;
    }

    public Student student() {
        return student;
    }

    public Course course() {
        return course;
    }
}