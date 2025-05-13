package com.example.yavi.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.StringValidator;
import am.ik.yavi.builder.StringValidatorBuilder;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public interface Budget {
    StringValidator<BudgetType> typeValidator = StringValidatorBuilder.of("type", c -> c.oneOf(
                    Set.of("range", "limit", "undecided")
            )).build()
            .andThen(s -> BudgetType.valueOf(s.toUpperCase()));

    Arguments1Validator<Map<String, Object>, Budget> mapValidator = (m, locale, context) -> typeValidator.validate(Objects.toString(m.get("type")))
            .flatMap(type -> (switch (type) {
                    case RANGE -> RangeBudget.mapValidator.validate(m, locale, context);
                    case LIMIT -> LimitBudget.mapValidator.validate(m, locale, context);
                    case UNDECIDED -> UndecidedBudget.mapValidator.validate(m, locale, context);
                }).map(Function.identity()
            ));

    enum BudgetType {
        RANGE,
        LIMIT,
        UNDECIDED;
    }
}
