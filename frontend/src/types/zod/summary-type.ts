import { z } from 'zod';

const locationSchema = z.object({
  location: z.string(),
  repoName: z.string(),
  organization: z.string(),
  domainName: z.string(),
});

const repoSchema = z.object({
  location: locationSchema,
  branch: z.string(),
  displayName: z.string(),
  outputFolderName: z.string(),
});

const errorSchema = z.object({
  repoName: z.string(),
  errorMessage: z.string(),
});

const urlSchema = z.object({
  BASE_URL: z.string(),
  BLAME_PATH: z.string(),
  BRANCH: z.string(),
  COMMIT_PATH: z.string(),
  HISTORY_PATH: z.string(),
  REPO_URL: z.string(),
});

const supportedDomainUrlMapSchema = z.record(urlSchema);

// Contains the zod validation schema for the summary.json file

export const summarySchema = z.object({
  repoSenseVersion: z.string(),
  reportGeneratedTime: z.string(),
  reportGenerationTime: z.string(),
  zoneId: z.string(),
  reportTitle: z.string(),
  repos: z.array(repoSchema),
  errorSet: z.array(errorSchema),
  sinceDate: z.string(),
  untilDate: z.string(),
  isSinceDateProvided: z.boolean(),
  isUntilDateProvided: z.boolean(),
  supportedDomainUrlMap: supportedDomainUrlMapSchema,
});

// Export typescript types
export type DomainUrlMap = z.infer<typeof supportedDomainUrlMapSchema>;
export type RepoRaw = z.infer<typeof repoSchema>;
export type ErrorMessage = z.infer<typeof errorSchema>;
