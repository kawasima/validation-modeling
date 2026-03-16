package com.example.yavi.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.LongValidator;
import am.ik.yavi.builder.LongValidatorBuilder;

import java.util.Map;

public record LimitBudget(long limit) implements Budget {
    static final LongValidator<Long> limitValidator = LongValidatorBuilder.of("limit",
            c -> c.greaterThan(0L).lessThan(100_000_000L)).build();

    static final Arguments1Validator<Map<String, Object>, LimitBudget> mapValidator = limitValidator
            .<Map<String, Object>>compose(m -> ((Number) m.get("limit")).longValue())
            .andThen(LimitBudget::new);
}
