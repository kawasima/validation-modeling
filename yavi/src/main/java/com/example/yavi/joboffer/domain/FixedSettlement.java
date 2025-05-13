package com.example.yavi.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.ArgumentsValidators;
import am.ik.yavi.arguments.LongValidator;
import am.ik.yavi.builder.LongValidatorBuilder;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@ToString
public class FixedSettlement extends Settlement {
    static LongValidator<Long> numberOfWorkersValidator = LongValidatorBuilder.of("numberOfWorkers", c -> c.greaterThan(1L).lessThan(1024L))
            .build();

    @Getter
    private final long numberOfWorkers;
    @Getter
    private final Budget budget;

    private FixedSettlement(Long numberOfWorkers, Budget budget) {
        this.numberOfWorkers = numberOfWorkers;
        this.budget = budget;
    }


    static Arguments1Validator<Map<String, Object>, Long> mapNumberOfWorkersValidator = numberOfWorkersValidator.compose(
                m -> (long)m.get("numberOfWorkers"));
    static Arguments1Validator<Map<String, Object>, Budget> mapBudgetValidator = Budget.mapValidator.<Map<String, Object>>compose(m -> (Map<String, Object>) m.get("budget"));

    static Arguments1Validator<Map<String, Object>, FixedSettlement> mapValidator = ArgumentsValidators.combine(
                        mapNumberOfWorkersValidator,
                        mapBudgetValidator
    ).apply(FixedSettlement::new);
}

