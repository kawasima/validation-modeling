package com.example.raoh.joboffer.data;

/**
 * data 契約金額 = エコノミー OR ベーシック OR スタンダード OR プレミアム OR カスタム金額
 */
public sealed interface ContractPrice {
    record Economy() implements ContractPrice {}
    record Basic() implements ContractPrice {}
    record Standard() implements ContractPrice {}
    record Premium() implements ContractPrice {}
    record Custom(long value) implements ContractPrice {}
}
