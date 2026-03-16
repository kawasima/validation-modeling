package com.example.yavi.reserve.domain;

import am.ik.yavi.arguments.Arguments3Validator;
import am.ik.yavi.arguments.ArgumentsValidators;
import am.ik.yavi.arguments.LongValidator;
import am.ik.yavi.arguments.StringValidator;
import am.ik.yavi.builder.LongValidatorBuilder;
import am.ik.yavi.builder.StringValidatorBuilder;
import am.ik.yavi.core.Validated;
import am.ik.yavi.core.ViolationMessage;
import com.example.yavi.enrollment.domain.Identifier;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Customer {
    private final Identifier customerId;
    private final String name;
    private final String email;

    private Customer(Identifier customerId, String name, String email) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
    }

    public static final LongValidator<Identifier> customerIdValidator = LongValidatorBuilder.of("customerId",
                    c -> c.predicate(id -> id > 0L || id == -1L, ViolationMessage.of("identifier", "must be greater than 0 or -1"))
            ).build()
            .andThen(id -> id < 0L ? Identifier.undecided() : Identifier.of(id));

    static final StringValidator<String> nameValidator = StringValidatorBuilder.of("name",
            c -> c.notBlank().lessThan(100)
    ).build();

    static final StringValidator<String> emailValidator = StringValidatorBuilder.of("email",
            c -> c.notBlank().lessThan(255).email()
    ).build();

    static final Arguments3Validator<Long, String, String, Customer> validator = ArgumentsValidators.split(
            customerIdValidator,
            nameValidator,
            emailValidator
    ).apply(Customer::new);

    public static Customer of(long customerId, String name, String email) {
        return validator.validated(customerId, name, email);
    }

    public static Validated<Customer> create(String name, String email) {
        return validator.validate(-1L, name, email);
    }

    public static Validated<Customer> parse(long customerId, String name, String email) {
        return validator.validate(customerId, name, email);
    }
}
