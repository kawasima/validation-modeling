package com.example.raoh.joboffer.data;

/**
 * data 予算 = 範囲予算 OR 上限予算 OR 未定
 */
public sealed interface Budget permits RangeBudget, LimitBudget, UndecidedBudget {}
