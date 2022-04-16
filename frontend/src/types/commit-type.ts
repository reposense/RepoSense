import { z } from 'zod';

export const commitsSchema = z.object({
  authorFileTypeContributionMap: z.record(z.record(z.number())),
  authorDailyContributionsMap: z.record(z.array(
      z.object({
        date: z.string(),
        commitResults: z.array(
            z.object({
              deletions: z.number().optional(),
              fileTypesAndContributionMap: z.record(z.object({
                deletions: z.number(), insertions: z.number(),
              })),
              hash: z.string(),
              insertions: z.number().optional(),
              messageBody: z.string(),
              messageTitle: z.string(),
              repoId: z.string().optional(),
            }),
        ),
      }),
  )),
  authorContributionVariance: z.record(z.number()),
  authorDisplayNameMap: z.record(z.string()),
});

export type CommitsSchema = z.infer<typeof commitsSchema>
