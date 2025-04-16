package com.example.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.LocalDateValidator;
import am.ik.yavi.arguments.StringValidator;
import am.ik.yavi.builder.LocalDateValidatorBuilder;
import am.ik.yavi.builder.StringValidatorBuilder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

@ToString
public abstract class JobOffer {
    static final StringValidator<String> titleValidator = StringValidatorBuilder.of("title", c -> c.notBlank().lessThan(100))
            .build();
    static final StringValidator<String> descriptionValidator = StringValidatorBuilder.of("description", c -> c.notBlank().lessThan(1000))
            .build();
    static final LocalDateValidator<LocalDate> offerExpireDateValidator = LocalDateValidatorBuilder.of("offerExpireDate", c -> c.notNull())
            .build();

    static StringValidator<JobOfferType> typeValidator = StringValidatorBuilder.of("type", c -> c.oneOf(
                    Set.of("project", "competition", "task")
            )).build()
            .andThen(s -> JobOfferType.valueOf(s.toUpperCase()));

    public static Arguments1Validator<Map<String, Object>, JobOffer> mapValidator = (m, locale, context) -> typeValidator.validate(Objects.toString(m.get("type")))
            .flatMap(type -> (switch (type) {
                case PROJECT -> ProjectJobOffer.mapValidator.validate(m, locale, context);
                case COMPETITION -> CompetitionJobOffer.mapValidator.validate(m, locale, context);
                case TASK -> TaskJobOffer.mapValidator.validate(m, locale, context);
            }).map(Function.identity()
            ));

    @Getter
    private final String title;
    @Getter
    private final String description;
    @Getter
    private final LocalDate offerExpireDate;

    protected JobOffer(String title, String description, LocalDate offerExpireDate) {
        this.title = title;
        this.description = description;
        this.offerExpireDate = offerExpireDate;
    }
}
