package com.example.raoh.reserve.data;

/**
 * data ツアー = ツアーID AND ツアーコード AND ツアー名 AND 定員
 */
public record Tour(
        long tourId,
        String tourCode,
        String name,
        int capacity
) {}
