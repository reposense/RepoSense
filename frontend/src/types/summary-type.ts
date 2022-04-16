import { z } from 'zod';
import { urlShape } from './urlShape-type';

export const summarySchema = z.object({
  repoSenseVersion: z.string(),
  reportGeneratedTime: z.string(),
  reportGenerationTime: z.string(),
  zoneId: z.string(),
  reportTitle: z.string(),
  repos: z.array(
      z.object({
        location: z.object({
          domainName: z.string(),
          location: z.string(),
          repoName: z.string(),
          organization: z.string(),
        }),
        branch: z.string(),
        displayName: z.string(),
        outputFolderName: z.string(),
      }),
  ),
  errorSet: z.array(
      z.object({ repoName: z.string(), errorMessage: z.string() }),
  ),
  sinceDate: z.string(),
  untilDate: z.string(),
  isSinceDateProvided: z.boolean(),
  isUntilDateProvided: z.boolean(),
  supportedDomainUrlMap: z.object({
    NOT_RECOGNIZED: z.object(urlShape.shape),
    github: z.object(urlShape.shape),
  }),
});

export type Summary = z.infer<typeof summarySchema>
