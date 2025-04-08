import { z } from 'zod';

/**
 * 契約金額タイプを表す列挙型
 */
export const ContractPriceType = z.enum([
  'ECONOMY',
  'BASIC',
  'STANDARD',
  'PREMIUM',
  'CUSTOM',
]);

/**
 * 契約金額タイプの型
 */
export type ContractPriceType = z.infer<typeof ContractPriceType>;

/**
 * エコノミープランのスキーマ
 */
export const EconomyContractPrice = z.object({
  type: z.literal('ECONOMY'),
});

/**
 * エコノミープランの型
 */
export type EconomyContractPrice = z.infer<typeof EconomyContractPrice>;

/**
 * ベーシックプランのスキーマ
 */
export const BasicContractPrice = z.object({
  type: z.literal('BASIC'),
});

/**
 * ベーシックプランの型
 */
export type BasicContractPrice = z.infer<typeof BasicContractPrice>;

/**
 * スタンダードプランのスキーマ
 */
export const StandardContractPrice = z.object({
  type: z.literal('STANDARD'),
});

/**
 * スタンダードプランの型
 */
export type StandardContractPrice = z.infer<typeof StandardContractPrice>;

/**
 * プレミアムプランのスキーマ
 */
export const PremiumContractPrice = z.object({
  type: z.literal('PREMIUM'),
});

/**
 * プレミアムプランの型
 */
export type PremiumContractPrice = z.infer<typeof PremiumContractPrice>;

/**
 * カスタム契約金額のスキーマ
 */
export const CustomContractPrice = z.object({
  type: z.literal('CUSTOM'),
  amount: z.number().int().positive(),
});

/**
 * カスタム契約金額の型
 */
export type CustomContractPrice = z.infer<typeof CustomContractPrice>;

/**
 * 契約金額のスキーマ
 */
export const ContractPrice = z.discriminatedUnion('type', [
  EconomyContractPrice,
  BasicContractPrice,
  StandardContractPrice,
  PremiumContractPrice,
  CustomContractPrice,
]);

/**
 * 契約金額の型
 */
export type ContractPrice = z.infer<typeof ContractPrice>; 