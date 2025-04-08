import { z } from 'zod';
import { JobOfferType } from './JobOfferType';
import { ProjectJobOffer } from './ProjectJobOffer';
import { TaskJobOffer } from './TaskJobOffer';
import { CompetitionJobOffer } from './CompetitionJobOffer';

/**
 * 依頼のスキーマ
 */
export const JobOffer = z.discriminatedUnion('type', [
  ProjectJobOffer,
  TaskJobOffer,
  CompetitionJobOffer,
]);

/**
 * 依頼の型
 */
export type JobOffer = z.infer<typeof JobOffer>; 