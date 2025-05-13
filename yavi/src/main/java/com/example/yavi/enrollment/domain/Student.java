package com.example.yavi.enrollment.domain;

import am.ik.yavi.arguments.*;
import am.ik.yavi.builder.EnumValidatorBuilder;
import am.ik.yavi.builder.LongValidatorBuilder;
import am.ik.yavi.builder.StringValidatorBuilder;
import am.ik.yavi.core.Validated;
import am.ik.yavi.core.ViolationMessage;
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
                    c -> c.predicate(id -> id > 0L || id == -1L, ViolationMessage.of("identifier", "must be greater than 0 or -1"))
            ).build()
            .andThen(id -> id < 0L ? Identifier.undecided() : Identifier.of(id));
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
    public static Validated<Student> create(String name, StudentStatus status) {
        return validator.validate(-1L, name, status);
    }

    public static Validated<Student> parse(long studentId, String name, StudentStatus status) {
        return validator.validate(studentId, name, status);
    }
}
