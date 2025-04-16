package com.example.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.StringValidator;
import am.ik.yavi.builder.StringValidatorBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class Settlement {
    static StringValidator<SettlementMethod> typeValidator = StringValidatorBuilder.of("method", c -> c.oneOf(
            List.of("fixed")
    )).build()
            .andThen(s -> SettlementMethod.valueOf(s.toUpperCase()));

    static Arguments1Validator<Map<String, Object>, Settlement> mapValidator = (m, locale,context) -> typeValidator.validate(Objects.toString(m.get("type")))
            .flatMap(method -> (switch (method) {
                case FIXED -> FixedSettlement.mapValidator.validate(m, locale, context);
                case PER_HOUR -> PerHourSettlement.mapValidator.validate(m, locale, context);
            }).map(Function.identity()));
    enum SettlementMethod { FIXED, PER_HOUR }
}
