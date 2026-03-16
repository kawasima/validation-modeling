package com.example.raoh.enrollment.gateway;

import com.example.raoh.enrollment.data.Course;
import com.example.raoh.enrollment.data.Student;
import net.unit8.raoh.jooq.JooqRecordDecoder;

import static net.unit8.raoh.ObjectDecoders.*;
import static net.unit8.raoh.jooq.JooqRecordDecoders.*;

public class RecordDecoders {
    private static final JooqRecordDecoder<Student> STUDENT_DECODER = combine(
            field("student_id", long_()),
            field("name", string()),
            field("status", string().map(Student.StudentStatus::valueOf))
    ).map(Student::new)::decode;

    public static JooqRecordDecoder<Student> student() {
        return STUDENT_DECODER;
    }

    private static final JooqRecordDecoder<Course> COURSE_DECODER = combine(
            field("course_id", long_()),
            field("name", string())
    ).map(Course::new)::decode;

    public static JooqRecordDecoder<Course> course() {
        return COURSE_DECODER;
    }
}
