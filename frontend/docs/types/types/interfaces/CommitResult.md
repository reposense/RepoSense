[**Documentation**](../../../README.md)

***

[Documentation](../../../README.md) / [types/types](../README.md) / CommitResult

# Interface: CommitResult

Defined in: [src/types/types.ts:10](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/types.ts#L10)

## Extends

- [`CommitResultRaw`](../../zod/commits-type/type-aliases/CommitResultRaw.md)

## Properties

### deletions

> **deletions**: `number`

Defined in: [src/types/types.ts:13](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/types.ts#L13)

***

### fileTypesAndContributionMap

> **fileTypesAndContributionMap**: `Record`\<`string`, \{ `deletions`: `number`; `insertions`: `number`; \}\>

Defined in: [src/types/zod/commits-type.ts:14](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/zod/commits-type.ts#L14)

#### Inherited from

`CommitResultRaw.fileTypesAndContributionMap`

***

### hash

> **hash**: `string`

Defined in: [src/types/zod/commits-type.ts:9](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/zod/commits-type.ts#L9)

#### Inherited from

`CommitResultRaw.hash`

***

### insertions

> **insertions**: `number`

Defined in: [src/types/types.ts:12](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/types.ts#L12)

***

### isMergeCommit

> **isMergeCommit**: `boolean`

Defined in: [src/types/zod/commits-type.ts:12](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/zod/commits-type.ts#L12)

#### Inherited from

`CommitResultRaw.isMergeCommit`

***

### isOpen?

> `optional` **isOpen**: `boolean`

Defined in: [src/types/types.ts:14](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/types.ts#L14)

***

### messageBody

> **messageBody**: `string`

Defined in: [src/types/zod/commits-type.ts:11](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/zod/commits-type.ts#L11)

#### Inherited from

`CommitResultRaw.messageBody`

***

### messageTitle

> **messageTitle**: `string`

Defined in: [src/types/zod/commits-type.ts:10](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/zod/commits-type.ts#L10)

#### Inherited from

`CommitResultRaw.messageTitle`

***

### repoId

> **repoId**: `string`

Defined in: [src/types/types.ts:11](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/types.ts#L11)

***

### tags?

> `optional` **tags**: `string`[]

Defined in: [src/types/zod/commits-type.ts:13](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/zod/commits-type.ts#L13)

#### Inherited from

`CommitResultRaw.tags`
