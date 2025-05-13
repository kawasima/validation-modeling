package com.example.yavi.reserve;

import am.ik.yavi.arguments.*;
import am.ik.yavi.builder.IntegerValidatorBuilder;
import am.ik.yavi.builder.StringValidatorBuilder;
import am.ik.yavi.core.Validated;

import java.util.function.Function;

import static java.lang.Integer.parseInt;

public record ReserveTourInput(
        String tourCode,
        int adultCount,
        int childCount,
        String remarks
) {
    static StringValidator<String> tourCodeValidator = StringValidatorBuilder.of("tourCode", c -> c.greaterThanOrEqual(1).lessThanOrEqual(10))
            .build();
    static Function<String, StringValidator<Integer>> integerStringValidatorBuilder = name -> StringValidatorBuilder.of(name, c -> c.notNull().isInteger())
            .build(Integer::parseInt);
    static Function<String, IntegerValidator<Integer>> countValidatorBuilder = name -> IntegerValidatorBuilder.of(name, c -> c.greaterThanOrEqual(0).lessThanOrEqual(5))
            .build();
    static Arguments1Validator<String, Integer> adultCountValidator = (s, locale, context) -> integerStringValidatorBuilder.apply("adultCount")
            .validate(s)
            .flatMap(i -> countValidatorBuilder.apply("adultCount").validate(i));
    static Arguments1Validator<String, Integer> childCountValidator = (s, locale, context) -> integerStringValidatorBuilder.apply("childCount")
            .validate(s)
            .flatMap(i -> countValidatorBuilder.apply("childCount").validate(i));

    static StringValidator<String> remarksValidator = StringValidatorBuilder.of("remarks", c -> c.greaterThanOrEqual(0).lessThanOrEqual(80))
            .build();

    static Arguments4Validator<String, String, String, String, ReserveTourInput> validator = ArgumentsValidators.split(
            tourCodeValidator,
            adultCountValidator,
            childCountValidator,
            remarksValidator
    ).apply(ReserveTourInput::new);

    public ReserveTourInput {
        validator.validated(tourCode, Integer.toString(adultCount), Integer.toString(childCount), remarks);
    }
    public static Validated<ReserveTourInput> parse(String tourCode, String adultCount, String childCount, String remarks) {
        return validator.validate(tourCode, adultCount, childCount, remarks);
    }
}
