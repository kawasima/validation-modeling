import { z } from 'zod';
import { Budget } from './Budget';
import { NumberOfRecruits } from './PerHourSettlement';

/**
 * 固定報酬制のスキーマ
 */
export const FixedSettlement = z.object({
  type: z.literal('FIXED'),
  numberOfRecruits: NumberOfRecruits,
  budget: Budget,
});

/**
 * 固定報酬制の型
 */
export type FixedSettlement = z.infer<typeof FixedSettlement>; 