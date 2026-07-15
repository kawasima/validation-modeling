package com.example.raoh.enrollment.web;

import com.example.raoh.enrollment.data.CanEnrollStudent;
import net.unit8.raoh.encode.Encoder;
import org.jspecify.annotations.Nullable;

import java.util.Map;

import static net.unit8.raoh.encode.MapEncoders.*;
import static net.unit8.raoh.encode.ObjectEncoders.*;

/**
 * 受講登録レスポンスのエンコーダ。{@link EnrollmentJsonDecoders} の write 側の対。
 */
public final class EnrollmentEncoders {

    private EnrollmentEncoders() {}

    /**
     * 受講可能となった学生とコースを、学生・コースを平らに並べたレスポンスにエンコードする。
     */
    public static final Encoder<CanEnrollStudent, Map<String, @Nullable Object>> ENROLLED = object(
            property("studentId",   (CanEnrollStudent c) -> c.student().studentId(), long_()),
            property("courseId",    (CanEnrollStudent c) -> c.course().courseId(),   long_()),
            property("studentName", (CanEnrollStudent c) -> c.student().name(),      string()),
            property("courseName",  (CanEnrollStudent c) -> c.course().name(),       string()));
}
