import { z } from 'zod';

/**
 * 依頼タイプを表す列挙型
 */
export const JobOfferType = z.enum(['PROJECT', 'TASK', 'COMPETITION']);

/**
 * 依頼タイプの型
 */
export type JobOfferType = z.infer<typeof JobOfferType>; 