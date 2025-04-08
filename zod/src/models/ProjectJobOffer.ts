import { z } from 'zod';
import { JobOfferBase } from './JobOfferBase';
import { Settlement } from './Settlement';

/**
 * プロジェクト依頼のスキーマ
 */
export const ProjectJobOffer = z.object({
  type: z.literal('PROJECT'),
  base: JobOfferBase,
  settlement: Settlement,
  deliveryDate: z.date(),
});

/**
 * プロジェクト依頼の型
 */
export type ProjectJobOffer = z.infer<typeof ProjectJobOffer>; 