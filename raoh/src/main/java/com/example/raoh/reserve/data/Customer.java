package com.example.raoh.reserve.data;

/**
 * data 顧客 = 顧客ID AND 名前 AND Eメールアドレス
 */
public record Customer(
        long customerId,
        String name,
        String email
) {}
