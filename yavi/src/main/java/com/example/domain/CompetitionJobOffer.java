package com.example.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.ArgumentsValidators;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@ToString(callSuper = true)
public class CompetitionJobOffer extends JobOffer {
    @Getter
    private final ContractPrice contractPrice;

    private CompetitionJobOffer(String title, String description, LocalDate offerExpireDate, ContractPrice contractPrice) {
        super(title, description, offerExpireDate);
        this.contractPrice = contractPrice;
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
                ContractPrice.mapValidator.<Map<String, Object>>compose(m -> (Map<String, Object>) m.get("contractPrice"))
        ).apply(CompetitionJobOffer::new);
}
