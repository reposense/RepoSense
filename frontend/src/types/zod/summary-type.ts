import { z } from 'zod'
import { urlSchema } from './url'

const locationSchema = z.object({
  location: z.string(),
  repoName: z.string(),
  organization: z.string(),
  domainName: z.string(),
})

const repoSchema = z.object({
  location: locationSchema,
  branch: z.string(),
  displayName: z.string(),
  outputFolderName: z.string(),
})

const errorSchema = z.object({
  repoName: z.string(),
  errorMessage: z.string(),
})

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
  supportedDomainUrlMap: z.object({
    NOT_RECOGNIZED: urlSchema,
    github: urlSchema,
  })
})

// Export typescript types
export type Repo = z.infer<typeof repoSchema>;



