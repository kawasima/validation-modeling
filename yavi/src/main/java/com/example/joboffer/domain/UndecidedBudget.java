package com.example.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.builder.ObjectValidatorBuilder;

import java.util.Map;

public final class UndecidedBudget implements Budget {
    static Arguments1Validator<Map<String, Object>, UndecidedBudget> mapValidator = ObjectValidatorBuilder.<UndecidedBudget>of("undecidedBudget", c -> c.notNull())
            .build()
            .<Map<String, Object>>compose(m -> new UndecidedBudget());
}
