package com.example.enrollment;

import am.ik.yavi.arguments.IntegerValidator;
import am.ik.yavi.builder.IntegerValidatorBuilder;

public record Student(
    int studentId,
    StudentStatus status
) {
    enum StudentStatus {
        ACTIVE,
        INACTIVE
    }

    public static final IntegerValidator<Integer> studentIdValidator = IntegerValidatorBuilder.of("studentId",
            c -> c.greaterThanOrEqual(0)
    ).build();
}
