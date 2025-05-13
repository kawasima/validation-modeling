package com.example.yavi.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.ArgumentsValidators;
import am.ik.yavi.arguments.LongValidator;
import am.ik.yavi.builder.LongValidatorBuilder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@ToString(callSuper = true)
public class TaskJobOffer extends JobOffer {
    @Getter
    private final long ratePerTaskUnit;

    @Getter
    private final long numberOfTaskUnits;

    @Getter
    private final LimitTaskUnitsPerWorker limitTaskUnitsPerWorker;

    static final LongValidator<Long> ratePerTaskUnitValidator = LongValidatorBuilder.of("ratePerTaskUnit", c -> c.greaterThan(5L).lessThan(1_000_000L))
            .build();

    static final LongValidator<Long> numberOfTaskUnitsValidator = LongValidatorBuilder.of("numberOfTaskUnits", c -> c.greaterThan(1L).lessThan(1_000_000L))
            .build();

    private TaskJobOffer(String title, String description, LocalDate offerExpireDate,
                         long ratePerTaskUnit, long numberOfTaskUnits, LimitTaskUnitsPerWorker limitTaskUnitsPerWorker) {
        super(title, description, offerExpireDate);
        this.ratePerTaskUnit = ratePerTaskUnit;
        this.numberOfTaskUnits = numberOfTaskUnits;
        this.limitTaskUnitsPerWorker = limitTaskUnitsPerWorker;
    }
    static final Arguments1Validator<Map<String, Object>, String> mapTitleValidator = titleValidator.compose(
            m -> (String) m.get("title"));

    static final Arguments1Validator<Map<String, Object>, String> mapDescriptionValidator = descriptionValidator.compose(
            m -> (String) m.get("description"));

    static final Arguments1Validator<Map<String, Object>, LocalDate> mapOfferExpireDateValidator = offerExpireDateValidator.compose(
            m ->  LocalDate.parse(Objects.toString(m.get("offerExpireDate"), "")));

    static final Arguments1Validator<Map<String, Object>, JobOffer> mapValidator = ArgumentsValidators.combine(
            mapTitleValidator,
            mapDescriptionValidator,
            mapOfferExpireDateValidator,
            ratePerTaskUnitValidator.<Map<String, Object>>compose(m -> (long)m.get("ratePerTaskUnit")),
            numberOfTaskUnitsValidator.<Map<String, Object>>compose(m -> (long)m.get("numberOfTaskUnits")),
            LimitTaskUnitsPerWorker.mapValidator.<Map<String, Object>>compose(m -> (Map<String, Object>) m.get("limitTaskUnitsPerWorker"))
    ).apply(TaskJobOffer::new);
}
