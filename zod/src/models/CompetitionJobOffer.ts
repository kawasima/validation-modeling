import { z } from 'zod';
import { JobOfferBase } from './JobOfferBase';
import { ContractPrice } from './ContractPrice';

/**
 * コンペ依頼のスキーマ
 */
export const CompetitionJobOffer = z.object({
  type: z.literal('COMPETITION'),
  base: JobOfferBase,
  contractPrice: ContractPrice,
});

/**
 * コンペ依頼の型
 */
export type CompetitionJobOffer = z.infer<typeof CompetitionJobOffer>; 