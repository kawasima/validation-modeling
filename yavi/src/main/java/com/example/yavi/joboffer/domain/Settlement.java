package com.example.yavi.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.StringValidator;
import am.ik.yavi.builder.StringValidatorBuilder;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public sealed interface Settlement permits FixedSettlement, PerHourSettlement {
    enum Method { FIXED, PER_HOUR }

    StringValidator<Method> typeValidator = StringValidatorBuilder.of("method",
            c -> c.oneOf(Set.of("fixed", "per_hour"))
    ).build().andThen(s -> Method.valueOf(s.toUpperCase()));

    Arguments1Validator<Map<String, Object>, Settlement> mapValidator = (m, locale, context) ->
            typeValidator.validate(Objects.toString(m.get("type")))
                    .flatMap(method -> (switch (method) {
                        case FIXED -> FixedSettlement.mapValidator.validate(m, locale, context);
                        case PER_HOUR -> PerHourSettlement.mapValidator.validate(m, locale, context);
                    }).map(Function.identity()));
}
