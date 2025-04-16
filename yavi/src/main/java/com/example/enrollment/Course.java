package com.example.enrollment;

import am.ik.yavi.arguments.IntegerValidator;
import am.ik.yavi.builder.IntegerValidatorBuilder;

public record Course(
        int courseId
) {
    public static final IntegerValidator<Integer> courseIdValidator = IntegerValidatorBuilder.of("courseId",
            c -> c.greaterThanOrEqual(0)
    ).build();
}
