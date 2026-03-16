package com.example.raoh.joboffer.data;

import java.time.LocalDate;

/**
 * data 案件 = タイトル AND 詳細 AND 募集期限 AND 案件詳細
 */
public sealed interface JobOffer permits ProjectJobOffer, TaskJobOffer, CompetitionJobOffer {
    String title();
    String description();
    LocalDate offerExpireDate();
}
