package com.example.yavi.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.LongValidator;
import am.ik.yavi.arguments.StringValidator;
import am.ik.yavi.builder.LongValidatorBuilder;
import am.ik.yavi.builder.StringValidatorBuilder;
import am.ik.yavi.core.Validated;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public sealed interface ContractPrice {
    record Economy() implements ContractPrice {}
    record Basic() implements ContractPrice {}
    record Standard() implements ContractPrice {}
    record Premium() implements ContractPrice {}
    record Custom(long value) implements ContractPrice {
        static final LongValidator<Long> valueValidator = LongValidatorBuilder.of("value",
                c -> c.notNull().greaterThan(0L)).build();
        static final Arguments1Validator<Map<String, Object>, Custom> mapValidator = valueValidator
                .<Map<String, Object>>compose(m -> ((Number) m.get("value")).longValue())
                .andThen(Custom::new);
    }

    StringValidator<String> typeValidator = StringValidatorBuilder.of("type",
            c -> c.oneOf(Set.of("economy", "basic", "standard", "premium", "custom"))
    ).build();

    Arguments1Validator<Map<String, Object>, ContractPrice> mapValidator = (m, locale, context) ->
            typeValidator.validate(Objects.toString(m.get("type")))
                    .flatMap(type -> (switch (type) {
                        case "economy" -> Validated.successWith((ContractPrice) new Economy());
                        case "basic" -> Validated.successWith((ContractPrice) new Basic());
                        case "standard" -> Validated.successWith((ContractPrice) new Standard());
                        case "premium" -> Validated.successWith((ContractPrice) new Premium());
                        case "custom" -> Custom.mapValidator.validate(m, locale, context).map(Function.identity());
                        default -> throw new IllegalStateException("Unexpected value: " + type);
                    }));
}
