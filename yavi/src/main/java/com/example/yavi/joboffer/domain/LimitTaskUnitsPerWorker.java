package com.example.yavi.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.LongValidator;
import am.ik.yavi.arguments.StringValidator;
import am.ik.yavi.builder.LongValidatorBuilder;
import am.ik.yavi.builder.StringValidatorBuilder;
import am.ik.yavi.core.Validated;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public sealed interface LimitTaskUnitsPerWorker {
    record Unlimited() implements LimitTaskUnitsPerWorker {}
    record Limited(long limit) implements LimitTaskUnitsPerWorker {}

    default Optional<Long> getLimit() {
        return switch (this) {
            case Unlimited _ -> Optional.empty();
            case Limited l -> Optional.of(l.limit());
        };
    }

    StringValidator<String> typeValidator = StringValidatorBuilder.of("type",
            c -> c.oneOf(Set.of("unlimited", "limited"))
    ).build();

    LongValidator<Long> limitValidator = LongValidatorBuilder.of("limit",
            c -> c.greaterThan(1L).lessThan(3000L)).build();

    Arguments1Validator<Map<String, Object>, LimitTaskUnitsPerWorker> mapValidator = (m, locale, context) ->
            typeValidator.validate(Objects.toString(m.get("type")))
                    .flatMap(type -> switch (type) {
                        case "unlimited" -> Validated.successWith((LimitTaskUnitsPerWorker) new Unlimited());
                        case "limited" -> limitValidator.andThen(Limited::new)
                                .validate(((Number) m.get("limit")).longValue())
                                .map(l -> l);
                        default -> throw new IllegalStateException("Unexpected value: " + type);
                    });
}
