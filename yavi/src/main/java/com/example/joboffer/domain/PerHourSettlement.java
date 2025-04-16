package com.example.joboffer.domain;

import am.ik.yavi.arguments.Arguments1Validator;
import am.ik.yavi.arguments.ArgumentsValidators;
import am.ik.yavi.arguments.LongValidator;
import am.ik.yavi.builder.LongValidatorBuilder;
import am.ik.yavi.builder.StringValidatorBuilder;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;

public class PerHourSettlement extends Settlement {
    @Getter
    private final long numberOfWorkers;

    /** 時間単価 */
    @Getter
    private final HourlyRate hourlyRate;
    @Getter
    private final long workHoursPerWeek;

    @Getter
    private final OfferDuration offerDuration;

    static LongValidator<Long> numberOfWorkersValidator = LongValidatorBuilder.of("numberOfWorkers", c -> c.greaterThan(1L).lessThan(1024L))
            .build();
    static LongValidator<Long> workHoursPerWeekValidator = LongValidatorBuilder.of("workHoursPerWeek", c -> c.greaterThan(1L).lessThan(160L))
            .build();

    static Arguments1Validator<String, HourlyRate> hourlyRateValidator = StringValidatorBuilder.of("hourlyRate", c -> c.oneOf(
                    Arrays.stream(HourlyRate.values()).map(HourlyRate::name).toList()
            )).build()
            .andThen(s -> HourlyRate.valueOf(s.toUpperCase()));

    static Arguments1Validator<String, OfferDuration> offerDurationValidator = StringValidatorBuilder.of("offerDuration", c -> c.oneOf(
                    Arrays.stream(OfferDuration.values()).map(OfferDuration::name).toList()
            )).build().andThen(s -> OfferDuration.valueOf(s.toUpperCase()));

    public PerHourSettlement(long numberOfWorkers, HourlyRate hourlyRate, long workHoursPerWeek, OfferDuration offerDuration) {
        this.numberOfWorkers = numberOfWorkers;
        this.hourlyRate = hourlyRate;
        this.workHoursPerWeek = workHoursPerWeek;
        this.offerDuration = offerDuration;
    }

    static Arguments1Validator<Map<String, Object>, PerHourSettlement> mapValidator = ArgumentsValidators.combine(
            numberOfWorkersValidator.<Map<String, Object>>compose(m -> ((Number) m.get("numberOfWorkers")).longValue()),
            hourlyRateValidator.<Map<String, Object>>compose(m -> (String) m.get("hourlyRate")),
            workHoursPerWeekValidator.<Map<String, Object>>compose(m -> ((Number) m.get("workHoursPerWeek")).longValue()),
            offerDurationValidator.<Map<String, Object>>compose(m -> (String) m.get("offerDuration"))
    ).apply(PerHourSettlement::new);

    public enum OfferDuration {
        UNDER_ONE_WEEK,
        ONE_WEEK_TO_ONE_MONTH,
        ONE_MONTH_TO_THREE_MONTHS,
        THREE_MONTHS_TO_SIX_MONTHS,
        OVER_SIX_MONTHS,
    }

    public enum HourlyRate {
        FROM_700_TO_1000,
        FROM_1000_TO_1500,
        FROM_1500_TO_2000,
        FROM_2000_TO_3000,
        FROM_3000_TO_4000,
        FROM_4000_TO_5000,
        OVER_5000,
    }
}
