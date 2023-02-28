import { z } from 'zod';

const lineSchema = z.object({
  lineNumber: z.number(),
  author: z.object({ gitId: z.string() }),
  content: z.string(),
});

const fileResult = z.object({
  path: z.string(),
  fileType: z.string(),
  lines: z.array(lineSchema),
  authorContributionMap: z.record(z.number()),
});

// Contains the zod validation schema for the authorship.json file

export const authorshipSchema = z.array(fileResult);

// Export typescript types
export type AuthorshipSchema = z.infer<typeof authorshipSchema>;
export type FileResult = z.infer<typeof fileResult>;
