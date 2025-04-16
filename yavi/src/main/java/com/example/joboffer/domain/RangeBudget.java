package com.example.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.ArgumentsValidators;
import am.ik.yavi.arguments.LongValidator;
import am.ik.yavi.builder.LongValidatorBuilder;
import lombok.ToString;

import java.util.Map;

@ToString
public final class RangeBudget implements Budget {
    private final Long lowerBound;
    private final Long upperBound;

    static LongValidator<Long> lowerBoundValidator = LongValidatorBuilder.of("lowerBound", c -> c.greaterThan(0L))
            .build();
    static LongValidator<Long> upperBoundValidator = LongValidatorBuilder.of("upperBound", c -> c.greaterThan(0L).lessThan(100_000_000L))
            .build();
    static Arguments1Validator<Map<String, Object>, Long> mapLowerBoundValidator = lowerBoundValidator.compose(m -> ((Number) m.get("lowerBound")).longValue());
    static Arguments1Validator<Map<String, Object>, Long> mapUpperBoundValidator = upperBoundValidator.compose(m -> ((Number) m.get("upperBound")).longValue());

    static Arguments1Validator<Map<String, Object>, RangeBudget> mapValidator = ArgumentsValidators.combine(
                    mapLowerBoundValidator, mapUpperBoundValidator)
            .apply(RangeBudget::new);

    private RangeBudget(Long lowerBound, Long upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
}
