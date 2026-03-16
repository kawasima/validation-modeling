package com.example.yavi.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.ArgumentsValidators;

import java.time.LocalDate;
import java.util.Map;

public record CompetitionJobOffer(
        String title,
        String description,
        LocalDate offerExpireDate,
        ContractPrice contractPrice
) implements JobOffer {
    @Override public JobOfferType jobOfferType() { return JobOfferType.COMPETITION; }

    static final Arguments1Validator<Map<String, Object>, JobOffer> mapValidator = ArgumentsValidators.combine(
            mapTitle, mapDescription, mapOfferExpireDate,
            ContractPrice.mapValidator.<Map<String, Object>>compose(m -> (Map<String, Object>) m.get("contractPrice"))
    ).apply(CompetitionJobOffer::new);
}
