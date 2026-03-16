package com.example.raoh.joboffer.data;

/**
 * data 時間精算 = 募集人数 AND 時間単価 AND 週あたり稼働時間 AND 募集期間
 */
public record PerHourSettlement(
        long numberOfWorkers,
        HourlyRate hourlyRate,
        long workHoursPerWeek,
        OfferDuration offerDuration
) implements Settlement {}
