import { z } from 'zod';

export const authorshipSchema = z.array(
    z.object({
      path: z.string(),
      fileType: z.string(),
      lines: z.array(
          z.object({
            lineNumber: z.number(),
            author: z.record(z.string()),
            content: z.string(),
          }),
      ),
      authorContributionMap: z.record(z.number()),
    }),
);

export type AuthorshipSchema = z.infer<typeof authorshipSchema>
