import { z } from 'zod';

/**
 * 予算金額範囲を表す列挙型
 */
export const BudgetRangeType = z.enum([
  'UNDER_5K',
  '5K_TO_10K',
  '10K_TO_30K',
  '30K_TO_50K',
  '50K_TO_100K',
  '100K_TO_150K',
]);

/**
 * 予算金額範囲の型
 */
export type BudgetRangeType = z.infer<typeof BudgetRangeType>;

/**
 * 予算タイプを表す列挙型
 */
export const BudgetType = z.enum(['RANGE', 'LIMIT', 'UNDECIDED']);

/**
 * 予算タイプの型
 */
export type BudgetType = z.infer<typeof BudgetType>;

/**
 * 予算金額範囲のスキーマ
 */
export const RangeBudget = z.object({
  type: z.literal('RANGE'),
  range: BudgetRangeType,
});

/**
 * 予算金額範囲の型
 */
export type RangeBudget = z.infer<typeof RangeBudget>;

/**
 * 予算指値のスキーマ
 */
export const LimitBudget = z.object({
  type: z.literal('LIMIT'),
  amount: z.number().int().positive(),
});

/**
 * 予算指値の型
 */
export type LimitBudget = z.infer<typeof LimitBudget>;

/**
 * 予算指定なしのスキーマ
 */
export const UndecidedBudget = z.object({
  type: z.literal('UNDECIDED'),
});

/**
 * 予算指定なしの型
 */
export type UndecidedBudget = z.infer<typeof UndecidedBudget>;

/**
 * 予算のスキーマ
 */
export const Budget = z.discriminatedUnion('type', [
  RangeBudget,
  LimitBudget,
  UndecidedBudget,
]);

/**
 * 予算の型
 */
export type Budget = z.infer<typeof Budget>; 