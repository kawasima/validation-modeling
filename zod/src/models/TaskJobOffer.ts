import { z } from 'zod';
import { JobOfferBase } from './JobOfferBase';
import { LimitTaskUnitsPerWorker } from './LimitTaskUnitsPerWorker';

/**
 * 作業単価・件数のスキーマ
 */
export const TaskUnitsAndPrice = z.object({
  unitPrice: z.number().int().min(5),
  numberOfUnits: z.number().int().min(1),
});

/**
 * 作業単価・件数の型
 */
export type TaskUnitsAndPrice = z.infer<typeof TaskUnitsAndPrice>;

/**
 * タスク依頼のスキーマ
 */
export const TaskJobOffer = z.object({
  type: z.literal('TASK'),
  base: JobOfferBase,
  taskUnitsAndPrice: TaskUnitsAndPrice,
  limitTaskUnitsPerWorker: LimitTaskUnitsPerWorker,
});

/**
 * タスク依頼の型
 */
export type TaskJobOffer = z.infer<typeof TaskJobOffer>; 