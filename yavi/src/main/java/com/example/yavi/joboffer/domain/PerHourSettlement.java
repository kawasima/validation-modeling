package com.example.yavi.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.ArgumentsValidators;
import am.ik.yavi.arguments.LongValidator;
import am.ik.yavi.builder.LongValidatorBuilder;
import am.ik.yavi.builder.StringValidatorBuilder;

import java.util.Arrays;
import java.util.Map;

public record PerHourSettlement(
        long numberOfWorkers,
        HourlyRate hourlyRate,
        long workHoursPerWeek,
        OfferDuration offerDuration
) implements Settlement {
    static final LongValidator<Long> numberOfWorkersValidator = LongValidatorBuilder.of("numberOfWorkers",
            c -> c.greaterThan(1L).lessThan(1024L)).build();
    static final LongValidator<Long> workHoursPerWeekValidator = LongValidatorBuilder.of("workHoursPerWeek",
            c -> c.greaterThan(1L).lessThan(160L)).build();
    static final Arguments1Validator<String, HourlyRate> hourlyRateValidator = StringValidatorBuilder.of("hourlyRate",
            c -> c.oneOf(Arrays.stream(HourlyRate.values()).map(HourlyRate::name).toList())
    ).build().andThen(s -> HourlyRate.valueOf(s.toUpperCase()));
    static final Arguments1Validator<String, OfferDuration> offerDurationValidator = StringValidatorBuilder.of("offerDuration",
            c -> c.oneOf(Arrays.stream(OfferDuration.values()).map(OfferDuration::name).toList())
    ).build().andThen(s -> OfferDuration.valueOf(s.toUpperCase()));

    static final Arguments1Validator<Map<String, Object>, PerHourSettlement> mapValidator = ArgumentsValidators.combine(
            numberOfWorkersValidator.<Map<String, Object>>compose(m -> ((Number) m.get("numberOfWorkers")).longValue()),
            hourlyRateValidator.<Map<String, Object>>compose(m -> (String) m.get("hourlyRate")),
            workHoursPerWeekValidator.<Map<String, Object>>compose(m -> ((Number) m.get("workHoursPerWeek")).longValue()),
            offerDurationValidator.<Map<String, Object>>compose(m -> (String) m.get("offerDuration"))
    ).apply(PerHourSettlement::new);
}
