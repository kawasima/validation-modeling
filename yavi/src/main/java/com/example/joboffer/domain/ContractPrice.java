package com.example.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.LongValidator;
import am.ik.yavi.arguments.StringValidator;
import am.ik.yavi.builder.LongValidatorBuilder;
import am.ik.yavi.builder.StringValidatorBuilder;
import am.ik.yavi.core.Validated;
import lombok.Getter;

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public sealed interface ContractPrice permits ContractPrice.Economy, ContractPrice.Basic, ContractPrice.Standard, ContractPrice.Premium, ContractPrice.Custom {
    StringValidator<String> typeValidator = StringValidatorBuilder.of("type", c -> c.oneOf(
            Set.of(
                "economy",
                "basic",
                "standard",
                "premium",
                "custom"
            ))).build();

    Arguments1Validator<Map<String, Object>, ContractPrice> mapValidator = (m, locale, context) -> typeValidator.validate(Objects.toString(m.get("type")))
            .flatMap(type -> (switch (type) {
                case "economy" -> Validated.successWith(Economy.INSTANCE);
                case "basic" -> Validated.successWith(Basic.INSTANCE);
                case "standard" -> Validated.successWith(Standard.INSTANCE);
                case "premium" -> Validated.successWith(Premium.INSTANCE);
                case "custom" -> Custom.mapValidator.validate(m, locale, context);
                default -> throw new IllegalStateException("Unexpected value: " + type);
            }).map(Function.identity()));

    final class Economy implements ContractPrice {
        public static final Economy INSTANCE = new Economy();
    }

    final class Basic implements ContractPrice {
        public static final Basic INSTANCE = new Basic();
    }

    final class Standard implements ContractPrice {
        public static final Standard INSTANCE = new Standard();
    }

    final class Premium implements ContractPrice {
        public static final Premium INSTANCE = new Premium();
    }

    final class Custom implements ContractPrice {
        static final LongValidator<Long> valueValidator = LongValidatorBuilder.of("value", c -> c.notNull().greaterThan(0L))
                .build();
        @Getter
        private final long value;

        public Custom(long value) {
            this.value = value;
        }

        static final Arguments1Validator<Map<String, Object>, Custom> mapValidator = valueValidator.<Map<String, Object>>compose(m -> ((Number) m.get("value")).longValue())
                .andThen(Custom::new);
    }

}
