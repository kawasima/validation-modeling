import { z } from 'zod';

/**
 * 時間単価範囲を表す列挙型
 */
export const HourlyRateRangeType = z.enum([
  '700_TO_1000',
  '1000_TO_1500',
  '1500_TO_2000',
  '2000_TO_3000',
  '3000_TO_4000',
  '4000_TO_5000',
  '5000_OR_MORE',
]);

/**
 * 時間単価範囲の型
 */
export type HourlyRateRangeType = z.infer<typeof HourlyRateRangeType>;

/**
 * 依頼期間を表す列挙型
 */
export const RequestPeriodType = z.enum([
  'WITHIN_1_WEEK',
  '1_WEEK_TO_1_MONTH',
  '1_MONTH_TO_3_MONTHS',
  '3_MONTHS_TO_6_MONTHS',
  '6_MONTHS_OR_MORE',
]);

/**
 * 依頼期間の型
 */
export type RequestPeriodType = z.infer<typeof RequestPeriodType>;

/**
 * 週の稼働時間のスキーマ
 */
export const WeeklyWorkingHours = z.object({
  hours: z.number().int().min(1).max(160),
});

/**
 * 週の稼働時間の型
 */
export type WeeklyWorkingHours = z.infer<typeof WeeklyWorkingHours>;

/**
 * 依頼期間のスキーマ
 */
export const RequestPeriod = z.object({
  period: RequestPeriodType,
});

/**
 * 依頼期間の型
 */
export type RequestPeriod = z.infer<typeof RequestPeriod>;

/**
 * 週の稼働と依頼期間のスキーマ
 */
export const WeeklyWorkingAndRequestPeriod = z.object({
  weeklyWorkingHours: WeeklyWorkingHours,
  requestPeriod: RequestPeriod,
});

/**
 * 週の稼働と依頼期間の型
 */
export type WeeklyWorkingAndRequestPeriod = z.infer<typeof WeeklyWorkingAndRequestPeriod>;

/**
 * 時間単価のスキーマ
 */
export const HourlyRate = z.object({
  rate: HourlyRateRangeType,
});

/**
 * 時間単価の型
 */
export type HourlyRate = z.infer<typeof HourlyRate>;

/**
 * 募集人数のスキーマ
 */
export const NumberOfRecruits = z.object({
  count: z.number().int().min(1).max(1024),
});

/**
 * 募集人数の型
 */
export type NumberOfRecruits = z.infer<typeof NumberOfRecruits>;

/**
 * 時間単価制のスキーマ
 */
export const PerHourSettlement = z.object({
  type: z.literal('PER_HOUR'),
  numberOfRecruits: NumberOfRecruits,
  hourlyRate: HourlyRate,
  weeklyWorkingAndRequestPeriod: WeeklyWorkingAndRequestPeriod,
});

/**
 * 時間単価制の型
 */
export type PerHourSettlement = z.infer<typeof PerHourSettlement>; 