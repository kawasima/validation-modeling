package com.example.raoh.reserve.data;

/**
 * data 予約入力 = ツアー AND 顧客 AND 大人人数 AND 子供人数 AND 備考
 */
public record ReserveTourInput(
        Tour tour,
        Customer customer,
        int adultCount,
        int childCount,
        String remarks
) {}
