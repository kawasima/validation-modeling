package com.example.yavi.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.ArgumentsValidators;
import am.ik.yavi.arguments.LongValidator;
import am.ik.yavi.builder.LongValidatorBuilder;

import java.util.Map;

public record RangeBudget(long lowerBound, long upperBound) implements Budget {
    static final LongValidator<Long> lowerBoundValidator = LongValidatorBuilder.of("lowerBound",
            c -> c.greaterThan(0L)).build();
    static final LongValidator<Long> upperBoundValidator = LongValidatorBuilder.of("upperBound",
            c -> c.greaterThan(0L).lessThan(100_000_000L)).build();

    static final Arguments1Validator<Map<String, Object>, RangeBudget> mapValidator = ArgumentsValidators.combine(
            lowerBoundValidator.<Map<String, Object>>compose(m -> ((Number) m.get("lowerBound")).longValue()),
            upperBoundValidator.<Map<String, Object>>compose(m -> ((Number) m.get("upperBound")).longValue())
    ).apply(RangeBudget::new);
}
