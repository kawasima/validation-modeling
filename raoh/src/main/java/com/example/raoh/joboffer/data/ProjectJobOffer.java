package com.example.raoh.joboffer.data;

import java.time.LocalDate;

/**
 * data プロジェクト案件 = タイトル AND 詳細 AND 募集期限 AND 精算方式
 */
public record ProjectJobOffer(
        String title,
        String description,
        LocalDate offerExpireDate,
        Settlement settlement
) implements JobOffer {}
