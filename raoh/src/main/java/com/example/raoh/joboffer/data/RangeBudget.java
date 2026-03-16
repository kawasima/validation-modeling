package com.example.raoh.joboffer.data;

/**
 * data 範囲予算 = 下限 AND 上限
 */
public record RangeBudget(long lowerBound, long upperBound) implements Budget {}
