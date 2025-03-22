[**Documentation**](../../../README.md)

***

[Documentation](../../../README.md) / [types/types](../README.md) / Repo

# Interface: Repo

Defined in: [src/types/types.ts:56](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/types.ts#L56)

## Extends

- [`RepoRaw`](../../zod/summary-type/type-aliases/RepoRaw.md)

## Properties

### branch

> **branch**: `string`

Defined in: [src/types/zod/summary-type.ts:12](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/zod/summary-type.ts#L12)

#### Inherited from

`RepoRaw.branch`

***

### commits?

> `optional` **commits**: `object`

Defined in: [src/types/types.ts:57](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/types.ts#L57)

#### authorContributionVariance

> **authorContributionVariance**: `Record`\<`string`, `number`\>

#### authorDailyContributionsMap

> **authorDailyContributionsMap**: `Record`\<`string`, `object`[]\>

#### authorDisplayNameMap

> **authorDisplayNameMap**: `Record`\<`string`, `string`\>

#### authorFileTypeContributionMap

> **authorFileTypeContributionMap**: `Record`\<`string`, `Record`\<`string`, `number`\>\>

***

### displayName

> **displayName**: `string`

Defined in: [src/types/zod/summary-type.ts:13](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/zod/summary-type.ts#L13)

#### Inherited from

`RepoRaw.displayName`

***

### files?

> `optional` **files**: `object`[]

Defined in: [src/types/types.ts:58](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/types.ts#L58)

#### authorContributionMap

> **authorContributionMap**: `Record`\<`string`, `number`\>

#### fileType

> **fileType**: `string`

#### isBinary?

> `optional` **isBinary**: `boolean`

#### isIgnored?

> `optional` **isIgnored**: `boolean`

#### lines

> **lines**: `object`[]

#### path

> **path**: `string`

***

### location

> **location**: `object` = `locationSchema`

Defined in: [src/types/zod/summary-type.ts:11](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/zod/summary-type.ts#L11)

#### domainName

> **domainName**: `string`

#### location

> **location**: `string`

#### organization

> **organization**: `string`

#### repoName

> **repoName**: `string`

#### Inherited from

`RepoRaw.location`

***

### outputFolderName

> **outputFolderName**: `string`

Defined in: [src/types/zod/summary-type.ts:14](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/zod/summary-type.ts#L14)

#### Inherited from

`RepoRaw.outputFolderName`

***

### users?

> `optional` **users**: [`User`](User.md)[]

Defined in: [src/types/types.ts:59](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/types.ts#L59)
