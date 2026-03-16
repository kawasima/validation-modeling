package com.example.yavi.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.LocalDateValidator;
import am.ik.yavi.arguments.StringValidator;
import am.ik.yavi.builder.LocalDateValidatorBuilder;
import am.ik.yavi.builder.StringValidatorBuilder;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public sealed interface JobOffer permits ProjectJobOffer, TaskJobOffer, CompetitionJobOffer {
    String title();
    String description();
    LocalDate offerExpireDate();
    JobOfferType jobOfferType();

    // 共通バリデータ
    StringValidator<String> titleValidator = StringValidatorBuilder.of("title", c -> c.notBlank().lessThan(100)).build();
    StringValidator<String> descriptionValidator = StringValidatorBuilder.of("description", c -> c.notBlank().lessThan(1000)).build();
    LocalDateValidator<LocalDate> offerExpireDateValidator = LocalDateValidatorBuilder.of("offerExpireDate", c -> c.notNull()).build();

    // 共通mapバリデータ（Mapからの抽出）
    Arguments1Validator<Map<String, Object>, String> mapTitle = titleValidator.compose(m -> (String) m.get("title"));
    Arguments1Validator<Map<String, Object>, String> mapDescription = descriptionValidator.compose(m -> (String) m.get("description"));
    Arguments1Validator<Map<String, Object>, LocalDate> mapOfferExpireDate = offerExpireDateValidator.compose(
            m -> LocalDate.parse(Objects.toString(m.get("offerExpireDate"), "")));

    StringValidator<JobOfferType> typeValidator = StringValidatorBuilder.of("type", c -> c.oneOf(
            Set.of("project", "competition", "task")
    )).build().andThen(s -> JobOfferType.valueOf(s.toUpperCase()));

    Arguments1Validator<Map<String, Object>, JobOffer> mapValidator = (m, locale, context) ->
            typeValidator.validate(Objects.toString(m.get("type")))
                    .flatMap(type -> (switch (type) {
                        case PROJECT -> ProjectJobOffer.mapValidator.validate(m, locale, context);
                        case COMPETITION -> CompetitionJobOffer.mapValidator.validate(m, locale, context);
                        case TASK -> TaskJobOffer.mapValidator.validate(m, locale, context);
                    }).map(Function.identity()));
}
