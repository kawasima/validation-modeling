package com.example.raoh.joboffer.data;

/**
 * data 固定精算 = 募集人数 AND 予算
 */
public record FixedSettlement(
        long numberOfWorkers,
        Budget budget
) implements Settlement {}
