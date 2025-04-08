package com.example.domain;

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

public class LimitTaskUnitsPerWorker {
    private final long lmit;

    public static final LimitTaskUnitsPerWorker UNLIMITED = new LimitTaskUnitsPerWorker(-1L);

    static StringValidator<String> typeValidator = StringValidatorBuilder.of("type", c -> c.oneOf(
            Set.of("unlimited", "limited")
    )).build();

    static final LongValidator<Long> limitValidator = LongValidatorBuilder.of("limit", c -> c.greaterThan(1L).lessThan(3000L))
            .build();

    public LimitTaskUnitsPerWorker(long limit) {
        this.lmit = limit;
    }

    public Optional<Long> getLimit() {
        if (lmit <= 0) {
            return Optional.empty();
        }
        return Optional.of(lmit);
    }

    static Arguments1Validator<Map<String, Object>, LimitTaskUnitsPerWorker> mapValidator = (m, locale, context) -> typeValidator.validate(Objects.toString(m.get("type")))
            .flatMap(type -> (switch (type) {
                case "unlimited" -> Validated.successWith(UNLIMITED);
                case "limited" -> limitValidator
                        .andThen(LimitTaskUnitsPerWorker::new)
                        .validate(((Number) m.get("limit")).longValue());
                default -> throw new IllegalStateException("Unexpected value: " + type);
            }));
}