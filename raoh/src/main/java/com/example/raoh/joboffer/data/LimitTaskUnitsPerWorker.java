package com.example.raoh.joboffer.data;

/**
 * data 1人あたり件数制限 = 無制限 OR 上限あり(上限値)
 */
public sealed interface LimitTaskUnitsPerWorker {
    record Unlimited() implements LimitTaskUnitsPerWorker {}
    record Limited(long limit) implements LimitTaskUnitsPerWorker {}
}
