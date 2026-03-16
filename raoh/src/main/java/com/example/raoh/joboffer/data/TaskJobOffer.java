package com.example.raoh.joboffer.data;

import java.time.LocalDate;

/**
 * data タスク案件 = タイトル AND 詳細 AND 募集期限 AND 作業単価 AND 作業件数 AND 1人あたり件数制限
 */
public record TaskJobOffer(
        String title,
        String description,
        LocalDate offerExpireDate,
        long ratePerTaskUnit,
        long numberOfTaskUnits,
        LimitTaskUnitsPerWorker limitTaskUnitsPerWorker
) implements JobOffer {}
