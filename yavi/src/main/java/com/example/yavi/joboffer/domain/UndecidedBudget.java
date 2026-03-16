package com.example.yavi.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.core.Validated;

import java.util.Map;

public record UndecidedBudget() implements Budget {
    static final Arguments1Validator<Map<String, Object>, UndecidedBudget> mapValidator =
            (m, locale, context) -> Validated.successWith(new UndecidedBudget());
}
