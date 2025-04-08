import { z } from 'zod';

/**
 * 1人あたりの作業件数制限タイプを表す列挙型
 */
export const LimitTaskUnitsPerWorkerType = z.enum(['LIMITED', 'UNLIMITED']);

/**
 * 1人あたりの作業件数制限タイプの型
 */
export type LimitTaskUnitsPerWorkerType = z.infer<typeof LimitTaskUnitsPerWorkerType>;

/**
 * 1人あたりの作業件数上限のスキーマ
 */
export const LimitedTaskUnitsPerWorker = z.object({
  type: z.literal('LIMITED'),
  maxUnits: z.number().int().min(0),
});

/**
 * 1人あたりの作業件数上限の型
 */
export type LimitedTaskUnitsPerWorker = z.infer<typeof LimitedTaskUnitsPerWorker>;

/**
 * 1人あたりの作業件数制限なしのスキーマ
 */
export const UnlimitedTaskUnitsPerWorker = z.object({
  type: z.literal('UNLIMITED'),
});

/**
 * 1人あたりの作業件数制限なしの型
 */
export type UnlimitedTaskUnitsPerWorker = z.infer<typeof UnlimitedTaskUnitsPerWorker>;

/**
 * 1人あたりの作業件数のスキーマ
 */
export const LimitTaskUnitsPerWorker = z.discriminatedUnion('type', [
  LimitedTaskUnitsPerWorker,
  UnlimitedTaskUnitsPerWorker,
]);

/**
 * 1人あたりの作業件数の型
 */
export type LimitTaskUnitsPerWorker = z.infer<typeof LimitTaskUnitsPerWorker>; 