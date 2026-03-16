package com.example.yavi.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.ArgumentsValidators;
import am.ik.yavi.arguments.LongValidator;
import am.ik.yavi.builder.LongValidatorBuilder;

import java.time.LocalDate;
import java.util.Map;

public record TaskJobOffer(
        String title,
        String description,
        LocalDate offerExpireDate,
        long ratePerTaskUnit,
        long numberOfTaskUnits,
        LimitTaskUnitsPerWorker limitTaskUnitsPerWorker
) implements JobOffer {
    @Override public JobOfferType jobOfferType() { return JobOfferType.TASK; }

    static final LongValidator<Long> ratePerTaskUnitValidator = LongValidatorBuilder.of("ratePerTaskUnit",
            c -> c.greaterThan(5L).lessThan(1_000_000L)).build();
    static final LongValidator<Long> numberOfTaskUnitsValidator = LongValidatorBuilder.of("numberOfTaskUnits",
            c -> c.greaterThan(1L).lessThan(1_000_000L)).build();

    static final Arguments1Validator<Map<String, Object>, JobOffer> mapValidator = ArgumentsValidators.combine(
            mapTitle, mapDescription, mapOfferExpireDate,
            ratePerTaskUnitValidator.<Map<String, Object>>compose(m -> (long) m.get("ratePerTaskUnit")),
            numberOfTaskUnitsValidator.<Map<String, Object>>compose(m -> (long) m.get("numberOfTaskUnits")),
            LimitTaskUnitsPerWorker.mapValidator.<Map<String, Object>>compose(m -> (Map<String, Object>) m.get("limitTaskUnitsPerWorker"))
    ).apply(TaskJobOffer::new);
}
