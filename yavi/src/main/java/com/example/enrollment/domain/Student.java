package com.example.enrollment.domain;

import am.ik.yavi.arguments.*;
import am.ik.yavi.builder.EnumValidatorBuilder;
import am.ik.yavi.builder.IntegerValidatorBuilder;
import am.ik.yavi.builder.LongValidatorBuilder;
import am.ik.yavi.builder.StringValidatorBuilder;
import am.ik.yavi.core.Validated;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Student {
    private final Identifier studentId;
    private final String name;
    private final StudentStatus status;

    public enum StudentStatus {
        ACTIVE,
        INACTIVE
    }

    private Student(Identifier studentId, String name, StudentStatus status) {
        this.studentId = studentId;
        this.name = name;
        this.status = status;
    }

    public static final LongValidator<Identifier> studentIdValidator = LongValidatorBuilder.of("studentId",
                    c -> c.greaterThanOrEqual(1L)
            ).build()
            .andThen(Identifier::of);
    static final StringValidator<String> nameValidator = StringValidatorBuilder.of("name",
            c -> c.notBlank().lessThan(100)
    ).build();
    static final EnumValidator<StudentStatus, StudentStatus> statusValidator = EnumValidatorBuilder.<StudentStatus>of("status",
                    c -> c.notNull()
            ).build();

    static final Arguments3Validator<Long, String, StudentStatus, Student> validator = ArgumentsValidators.split(
                    studentIdValidator,
                    nameValidator,
                    statusValidator
            ).apply(Student::new);

    public static Student of(long studentId, String name, StudentStatus status) {
        return validator.validated(studentId, name, status);
    }

    public static Validated<Student> parse(long studentId, String name, StudentStatus status) {
        return validator.validate(studentId, name, status);
    }

}
