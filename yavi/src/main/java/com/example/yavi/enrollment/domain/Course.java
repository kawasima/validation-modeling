package com.example.yavi.enrollment.domain;

import am.ik.yavi.arguments.*;
import am.ik.yavi.builder.LongValidatorBuilder;
import am.ik.yavi.builder.StringValidatorBuilder;
import am.ik.yavi.core.Validated;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Course {
    private final Identifier courseId;
    private final String name;

    private Course(Identifier courseId, String name) {
        this.courseId = courseId;
        this.name = name;
    }

    public static final LongValidator<Identifier> courseIdValidator = LongValidatorBuilder.of("courseId",
                    c -> c.greaterThanOrEqual(1L)
            ).build()
            .andThen(Identifier::of);
    static final StringValidator<String> nameValidator = StringValidatorBuilder.of("name",
                    c -> c.notBlank().lessThan(100)
            ).build();
    static final Arguments2Validator<Long, String, Course> validator = ArgumentsValidators.split(
                    courseIdValidator,
                    nameValidator
    ).apply(Course::new);



    public static Course of(long courseId, String name) {
        return validator.validated(courseId, name);
    }

    public static Validated<Course> parse(long courseId, String name) {
        return validator.validate(courseId, name);
    }
}
