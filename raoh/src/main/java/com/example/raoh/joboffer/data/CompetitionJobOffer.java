package com.example.raoh.joboffer.data;

import java.time.LocalDate;

/**
 * data コンペ案件 = タイトル AND 詳細 AND 募集期限 AND 契約金額
 */
public record CompetitionJobOffer(
        String title,
        String description,
        LocalDate offerExpireDate,
        ContractPrice contractPrice
) implements JobOffer {}
