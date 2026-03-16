package com.example.jsr380.joboffer;

import lombok.Data;

import java.time.LocalDate;

@Data
public class JobOfferInput {
    private String title;
    private String description;
    private String jobOfferType;
    private LocalDate offerExpireDate;

    // PROJECT
    private String settlementMethod;
    private Long numberOfWorkers;
    private String budgetType;
    private Long budgetLowerBound;
    private Long budgetUpperBound;
    private Long budgetLimit;
    private String hourlyRate;
    private Long workHoursPerWeek;
    private String offerDuration;

    // TASK
    private Long ratePerTaskUnit;
    private Long numberOfTaskUnits;
    private String limitTaskUnitsType;
    private Long limitTaskUnitsValue;

    // COMPETITION
    private String contractPriceType;
    private Long contractPriceValue;
}
