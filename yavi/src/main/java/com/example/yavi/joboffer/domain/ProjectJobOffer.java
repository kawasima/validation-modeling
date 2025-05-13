package com.example.yavi.joboffer.domain;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.ArgumentsValidators;
import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
public class ProjectJobOffer extends JobOffer {
    @Getter
    private final Settlement settlement;
    private ProjectJobOffer(String title, String description, LocalDate offerExpireDate, Settlement settlement) {
        super(title, description, offerExpireDate);
        this.settlement = settlement;
    }

    static final Arguments1Validator<Map<String, Object>, String> mapTitleValidator = titleValidator.compose(
            m -> (String) m.get("title"));

    static final Arguments1Validator<Map<String, Object>, String> mapDescriptionValidator = descriptionValidator.compose(
                m -> (String) m.get("description"));

    static Arguments1Validator<Map<String, Object>, LocalDate> mapOfferExpireDateValidator = offerExpireDateValidator.compose(
                m ->  LocalDate.parse(Objects.toString(m.get("offerExpireDate"), "")));

    static Arguments1Validator<Map<String, Object>, JobOffer> mapValidator = ArgumentsValidators.combine(
                mapTitleValidator,
                mapDescriptionValidator,
                mapOfferExpireDateValidator,
                Settlement.mapValidator.<Map<String, Object>>compose(m -> (Map<String, Object>) m.get("settlement"))
        ).apply(ProjectJobOffer::new);
}
