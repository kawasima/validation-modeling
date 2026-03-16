package com.example.raoh.joboffer.data;

/**
 * data 精算方式 = 固定精算 OR 時間精算
 */
public sealed interface Settlement permits FixedSettlement, PerHourSettlement {}
