[**Documentation**](../../../../README.md)

***

[Documentation](../../../../README.md) / [types/zod/authorship-type](../README.md) / authorshipSchema

# Variable: authorshipSchema

> `const` **authorshipSchema**: `ZodArray`\<`ZodObject`\<\{ `authorContributionMap`: `ZodRecord`\<`ZodString`, `ZodNumber`\>; `fileType`: `ZodString`; `isBinary`: `ZodOptional`\<`ZodBoolean`\>; `isIgnored`: `ZodOptional`\<`ZodBoolean`\>; `lines`: `ZodArray`\<`ZodObject`\<\{ `author`: `ZodObject`\<\{ `gitId`: `ZodString`; \}, `"strip"`, `ZodTypeAny`, \{ `gitId`: `string`; \}, \{ `gitId`: `string`; \}\>; `content`: `ZodString`; `isFullCredit`: `ZodDefault`\<`ZodBoolean`\>; `lineNumber`: `ZodNumber`; \}, `"strip"`, `ZodTypeAny`, \{ `author`: \{ `gitId`: `string`; \}; `content`: `string`; `isFullCredit`: `boolean`; `lineNumber`: `number`; \}, \{ `author`: \{ `gitId`: `string`; \}; `content`: `string`; `isFullCredit`: `boolean`; `lineNumber`: `number`; \}\>, `"many"`\>; `path`: `ZodString`; \}, `"strip"`, `ZodTypeAny`, \{ `authorContributionMap`: `Record`\<`string`, `number`\>; `fileType`: `string`; `isBinary`: `boolean`; `isIgnored`: `boolean`; `lines`: `object`[]; `path`: `string`; \}, \{ `authorContributionMap`: `Record`\<`string`, `number`\>; `fileType`: `string`; `isBinary`: `boolean`; `isIgnored`: `boolean`; `lines`: `object`[]; `path`: `string`; \}\>, `"many"`\>

Defined in: [src/types/zod/authorship-type.ts:21](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/zod/authorship-type.ts#L21)
