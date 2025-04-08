import { z } from 'zod';

/**
 * 依頼ベースのスキーマ
 */
export const JobOfferBase = z.object({
  title: z.string().min(1),
  description: z.string().min(1),
  requiredSkills: z.array(z.string()).optional(),
  attachments: z.array(z.string()).optional(),
  specialNotes: z.array(z.string()).default([]),
  applicationDeadline: z.date(),
});

/**
 * 依頼ベースの型
 */
export type JobOfferBase = z.infer<typeof JobOfferBase>; 