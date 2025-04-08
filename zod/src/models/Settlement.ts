import { z } from 'zod';
import { FixedSettlement } from './FixedSettlement';
import { PerHourSettlement } from './PerHourSettlement';

/**
 * 支払い方式を表す列挙型
 */
export const SettlementType = z.enum(['FIXED', 'PER_HOUR']);

/**
 * 支払い方式の型
 */
export type SettlementType = z.infer<typeof SettlementType>;

/**
 * 支払い方式のスキーマ
 */
export const Settlement = z.discriminatedUnion('type', [
  FixedSettlement,
  PerHourSettlement,
]);

/**
 * 支払い方式の型
 */
export type Settlement = z.infer<typeof Settlement>; 