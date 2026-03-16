package com.example.yavi.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.ArgumentsValidators;

import java.time.LocalDate;
import java.util.Map;

public record ProjectJobOffer(
        String title,
        String description,
        LocalDate offerExpireDate,
        Settlement settlement
) implements JobOffer {
    @Override public JobOfferType jobOfferType() { return JobOfferType.PROJECT; }

    static final Arguments1Validator<Map<String, Object>, JobOffer> mapValidator = ArgumentsValidators.combine(
            mapTitle, mapDescription, mapOfferExpireDate,
            Settlement.mapValidator.<Map<String, Object>>compose(m -> (Map<String, Object>) m.get("settlement"))
    ).apply(ProjectJobOffer::new);
}
