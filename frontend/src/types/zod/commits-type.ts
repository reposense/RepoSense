import { z } from 'zod';

const fileTypesAndContributionSchema = z.object({
  insertions: z.number(),
  deletions: z.number(),
});

const commitResult = z.object({
  hash: z.string(),
  messageTitle: z.string(),
  messageBody: z.string(),
  tags: z.array(z.string()).optional(),
  fileTypesAndContributionMap: z.record(fileTypesAndContributionSchema),
});

const authorDailyContributionsSchema = z.object({
  date: z.string(),
  commitResults: z.array(commitResult),
});

const authorFileTypeContributionsSchema = z.record(z.number());

// Contains the zod validation schema for the commits.json file

export const commitsSchema = z.object({
  authorDailyContributionsMap: z.record(z.array(authorDailyContributionsSchema)),
  authorFileTypeContributionMap: z.record(authorFileTypeContributionsSchema),
  authorContributionVariance: z.record(z.number()),
  authorDisplayNameMap: z.record(z.string()),
});

// Export typescript types
export type Commits = z.infer<typeof commitsSchema>;
export type CommitResultRaw = z.infer<typeof commitResult>;
export type AuthorDailyContributions = z.infer<typeof authorDailyContributionsSchema>;
export type AuthorFileTypeContributions = z.infer<typeof authorFileTypeContributionsSchema>;
