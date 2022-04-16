import { z } from 'zod';

export const urlShape = z.object({
  BASE_URL: z.string(),
  BLAME_PATH: z.string(),
  BRANCH: z.string(),
  COMMIT_PATH: z.string(),
  HISTORY_PATH: z.string(),
  REPO_URL: z.string(),
});

export type UrlShape = z.infer<typeof urlShape>
