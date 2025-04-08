package com.example.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.LongValidator;
import am.ik.yavi.builder.LongValidatorBuilder;

import java.util.Map;

public final class LimitBudget implements Budget {
    private final Long limit;

    static LongValidator<Long> limitValidator = LongValidatorBuilder.of("limit", c -> c.greaterThan(0L).lessThan(100_000_000L))
            .build();

    static Arguments1Validator<Map<String, Object>, LimitBudget> mapValidator = limitValidator
            .<Map<String, Object>>compose(m -> ((Number) m.get("limit")).longValue())
            .andThen(LimitBudget::new);

    private LimitBudget(Long limit) {
        this.limit = limit;
    }
}
