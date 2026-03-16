package com.example.yavi.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.ArgumentsValidators;
import am.ik.yavi.arguments.LongValidator;
import am.ik.yavi.builder.LongValidatorBuilder;

import java.util.Map;

public record FixedSettlement(long numberOfWorkers, Budget budget) implements Settlement {
    static final LongValidator<Long> numberOfWorkersValidator = LongValidatorBuilder.of("numberOfWorkers",
            c -> c.greaterThan(1L).lessThan(1024L)).build();

    static final Arguments1Validator<Map<String, Object>, FixedSettlement> mapValidator = ArgumentsValidators.combine(
            numberOfWorkersValidator.<Map<String, Object>>compose(m -> (long) m.get("numberOfWorkers")),
            Budget.mapValidator.<Map<String, Object>>compose(m -> (Map<String, Object>) m.get("budget"))
    ).apply(FixedSettlement::new);
}
