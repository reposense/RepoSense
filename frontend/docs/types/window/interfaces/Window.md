[**Documentation**](../../../README.md)

***

[Documentation](../../../README.md) / [types/window](../README.md) / Window

# Interface: Window

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16961

A window containing a DOM document; the document property points to the DOM document loaded in that window.

## Extends

- `EventTarget`.`AnimationFrameProvider`.`GlobalEventHandlers`.`WindowEventHandlers`.`WindowLocalStorage`.`WindowOrWorkerGlobalScope`.`WindowSessionStorage`

## Indexable

\[`index`: `number`\]: `Window`

## Properties

### $()

> **$**: (`id`) => `null` \| `HTMLElement`

Defined in: [src/types/window.ts:36](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L36)

#### Parameters

##### id

`string`

#### Returns

`null` \| `HTMLElement`

***

### addHash()

> **addHash**: (`newKey`, `newVal`) => `void`

Defined in: [src/types/window.ts:51](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L51)

#### Parameters

##### newKey

`string`

##### newVal

`string` | `boolean`

#### Returns

`void`

***

### api

> **api**: `Api`

Defined in: [src/types/window.ts:66](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L66)

***

### app

> **app**: `any`

Defined in: [src/types/window.ts:75](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L75)

***

### caches

> `readonly` **caches**: `CacheStorage`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17139

Available only in secure contexts.

#### Inherited from

`WindowOrWorkerGlobalScope.caches`

***

### ~~clientInformation~~

> `readonly` **clientInformation**: `Navigator`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16963

#### Deprecated

This is a legacy alias of `navigator`.

***

### closed

> `readonly` **closed**: `boolean`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16965

Returns true if the window has been closed, false otherwise.

***

### comparator()

> **comparator**: \<`T`\>(`fn`, `sortingOption`?) => `ComparatorFunction`\<`T`\>

Defined in: [src/types/window.ts:55](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L55)

#### Type Parameters

##### T

`T`

#### Parameters

##### fn

`SortingFunction`\<`T`\>

##### sortingOption?

`string`

#### Returns

`ComparatorFunction`\<`T`\>

***

### crossOriginIsolated

> `readonly` **crossOriginIsolated**: `boolean`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17140

#### Inherited from

`WindowOrWorkerGlobalScope.crossOriginIsolated`

***

### crypto

> `readonly` **crypto**: `Crypto`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17141

#### Inherited from

`WindowOrWorkerGlobalScope.crypto`

***

### customElements

> `readonly` **customElements**: `CustomElementRegistry`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16967

Defines a new custom element, mapping the given name to the given constructor as an autonomous custom element.

***

### DAY\_IN\_MS

> **DAY\_IN\_MS**: `number`

Defined in: [src/types/window.ts:41](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L41)

***

### deactivateAllOverlays()

> **deactivateAllOverlays**: () => `void`

Defined in: [src/types/window.ts:47](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L47)

#### Returns

`void`

***

### decodeHash()

> **decodeHash**: () => `void`

Defined in: [src/types/window.ts:54](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L54)

#### Returns

`void`

***

### devicePixelRatio

> `readonly` **devicePixelRatio**: `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16968

***

### document

> `readonly` **document**: `Document`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16969

***

### DOMAIN\_URL\_MAP

> **DOMAIN\_URL\_MAP**: `Record`

Defined in: [src/types/window.ts:73](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L73)

***

### encodeHash()

> **encodeHash**: () => `void`

Defined in: [src/types/window.ts:53](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L53)

#### Returns

`void`

***

### enquery()

> **enquery**: (`key`, `val`) => `string`

Defined in: [src/types/window.ts:37](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L37)

#### Parameters

##### key

`string`

##### val

`string`

#### Returns

`string`

***

### ~~event~~

> `readonly` **event**: `undefined` \| `Event`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16971

#### Deprecated

***

### ~~external~~

> `readonly` **external**: `External`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16973

#### Deprecated

***

### filterUnsupported()

> **filterUnsupported**: (`string`) => `undefined` \| `string`

Defined in: [src/types/window.ts:56](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L56)

#### Parameters

##### string

`string`

#### Returns

`undefined` \| `string`

***

### frameElement

> `readonly` **frameElement**: `null` \| `Element`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16974

***

### frames

> `readonly` **frames**: `Window`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16975

***

### getAuthorDisplayName()

> **getAuthorDisplayName**: (`authorRepos`) => `string`

Defined in: [src/types/window.ts:65](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L65)

#### Parameters

##### authorRepos

[`User`](../../types/interfaces/User.md)[]

#### Returns

`string`

***

### getAuthorLink()

> **getAuthorLink**: (`repoId`, `author`) => `undefined` \| `string`

Defined in: [src/types/window.ts:57](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L57)

#### Parameters

##### repoId

`string`

##### author

`string`

#### Returns

`undefined` \| `string`

***

### getBlameLink()

> **getBlameLink**: (`repoId`, `branch`, `filepath`) => `undefined` \| `string`

Defined in: [src/types/window.ts:62](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L62)

#### Parameters

##### repoId

`string`

##### branch

`string`

##### filepath

`string`

#### Returns

`undefined` \| `string`

***

### getBranchLink()

> **getBranchLink**: (`repoId`, `branch`) => `undefined` \| `string`

Defined in: [src/types/window.ts:60](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L60)

#### Parameters

##### repoId

`string`

##### branch

`string`

#### Returns

`undefined` \| `string`

***

### getCommitLink()

> **getCommitLink**: (`repoId`, `commitHash`) => `undefined` \| `string`

Defined in: [src/types/window.ts:61](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L61)

#### Parameters

##### repoId

`string`

##### commitHash

`string`

#### Returns

`undefined` \| `string`

***

### getDateStr()

> **getDateStr**: (`date`) => `string`

Defined in: [src/types/window.ts:48](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L48)

#### Parameters

##### date

`number`

#### Returns

`string`

***

### getFontColor()

> **getFontColor**: (`color`) => `string`

Defined in: [src/types/window.ts:50](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L50)

#### Parameters

##### color

`string`

#### Returns

`string`

***

### getGroupName()

> **getGroupName**: (`group`, `filterGroupSelection`) => `string`

Defined in: [src/types/window.ts:64](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L64)

#### Parameters

##### group

[`User`](../../types/interfaces/User.md)[]

##### filterGroupSelection

`string`

#### Returns

`string`

***

### getHexToRGB()

> **getHexToRGB**: (`color`) => `number`[]

Defined in: [src/types/window.ts:49](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L49)

#### Parameters

##### color

`string`

#### Returns

`number`[]

***

### getHistoryLink()

> **getHistoryLink**: (`repoId`, `branch`, `filepath`) => `undefined` \| `string`

Defined in: [src/types/window.ts:63](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L63)

#### Parameters

##### repoId

`string`

##### branch

`string`

##### filepath

`string`

#### Returns

`undefined` \| `string`

***

### getRepoLink()

> **getRepoLink**: (`repoId`) => `undefined` \| `string`

Defined in: [src/types/window.ts:59](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L59)

#### Parameters

##### repoId

`string`

#### Returns

`undefined` \| `string`

***

### getRepoLinkUnfiltered()

> **getRepoLinkUnfiltered**: (`repoId`) => `string`

Defined in: [src/types/window.ts:58](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L58)

#### Parameters

##### repoId

`string`

#### Returns

`string`

***

### HASH\_DELIMITER

> **HASH\_DELIMITER**: `string`

Defined in: [src/types/window.ts:42](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L42)

***

### hashParams

> **hashParams**: `object`

Defined in: [src/types/window.ts:44](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L44)

#### Index Signature

\[`key`: `string`\]: `string`

***

### history

> `readonly` **history**: `History`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16976

***

### HOME\_PAGE\_URL

> **HOME\_PAGE\_URL**: `string`

Defined in: [src/types/window.ts:39](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L39)

***

### indexedDB

> `readonly` **indexedDB**: `IDBFactory`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17142

#### Inherited from

`WindowOrWorkerGlobalScope.indexedDB`

***

### innerHeight

> `readonly` **innerHeight**: `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16977

***

### innerWidth

> `readonly` **innerWidth**: `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16978

***

### isAuthorshipAnalyzed

> **isAuthorshipAnalyzed**: `boolean`

Defined in: [src/types/window.ts:72](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L72)

***

### isMacintosh

> **isMacintosh**: `boolean`

Defined in: [src/types/window.ts:45](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L45)

***

### isSecureContext

> `readonly` **isSecureContext**: `boolean`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17143

#### Inherited from

`WindowOrWorkerGlobalScope.isSecureContext`

***

### isSinceDateProvided

> **isSinceDateProvided**: `boolean`

Defined in: [src/types/window.ts:70](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L70)

***

### isUntilDateProvided

> **isUntilDateProvided**: `boolean`

Defined in: [src/types/window.ts:71](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L71)

***

### length

> `readonly` **length**: `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16979

***

### localStorage

> `readonly` **localStorage**: `Storage`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17134

#### Inherited from

`WindowLocalStorage.localStorage`

***

### locationbar

> `readonly` **locationbar**: `BarProp`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16983

Returns true if the location bar is visible; otherwise, returns false.

***

### menubar

> `readonly` **menubar**: `BarProp`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16985

Returns true if the menu bar is visible; otherwise, returns false.

***

### name

> **name**: `string`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16986

***

### navigator

> `readonly` **navigator**: `Navigator`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16987

***

### onabort

> **onabort**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5856

Fires when the user aborts the download.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onabort`

***

### onafterprint

> **onafterprint**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17109

#### Inherited from

`WindowEventHandlers.onafterprint`

***

### onanimationcancel

> **onanimationcancel**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5857

#### Inherited from

`GlobalEventHandlers.onanimationcancel`

***

### onanimationend

> **onanimationend**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5858

#### Inherited from

`GlobalEventHandlers.onanimationend`

***

### onanimationiteration

> **onanimationiteration**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5859

#### Inherited from

`GlobalEventHandlers.onanimationiteration`

***

### onanimationstart

> **onanimationstart**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5860

#### Inherited from

`GlobalEventHandlers.onanimationstart`

***

### onauxclick

> **onauxclick**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5861

#### Inherited from

`GlobalEventHandlers.onauxclick`

***

### onbeforeinput

> **onbeforeinput**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5862

#### Inherited from

`GlobalEventHandlers.onbeforeinput`

***

### onbeforeprint

> **onbeforeprint**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17110

#### Inherited from

`WindowEventHandlers.onbeforeprint`

***

### onbeforeunload

> **onbeforeunload**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17111

#### Inherited from

`WindowEventHandlers.onbeforeunload`

***

### onblur

> **onblur**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5867

Fires when the object loses the input focus.

#### Param

The focus event.

#### Inherited from

`GlobalEventHandlers.onblur`

***

### oncancel

> **oncancel**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5868

#### Inherited from

`GlobalEventHandlers.oncancel`

***

### oncanplay

> **oncanplay**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5873

Occurs when playback is possible, but would require further buffering.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.oncanplay`

***

### oncanplaythrough

> **oncanplaythrough**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5874

#### Inherited from

`GlobalEventHandlers.oncanplaythrough`

***

### onchange

> **onchange**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5879

Fires when the contents of the object or selection have changed.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onchange`

***

### onclick

> **onclick**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5884

Fires when the user clicks the left mouse button on the object

#### Param

The mouse event.

#### Inherited from

`GlobalEventHandlers.onclick`

***

### onclose

> **onclose**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5885

#### Inherited from

`GlobalEventHandlers.onclose`

***

### oncontextmenu

> **oncontextmenu**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5890

Fires when the user clicks the right mouse button in the client area, opening the context menu.

#### Param

The mouse event.

#### Inherited from

`GlobalEventHandlers.oncontextmenu`

***

### oncopy

> **oncopy**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5891

#### Inherited from

`GlobalEventHandlers.oncopy`

***

### oncuechange

> **oncuechange**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5892

#### Inherited from

`GlobalEventHandlers.oncuechange`

***

### oncut

> **oncut**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5893

#### Inherited from

`GlobalEventHandlers.oncut`

***

### ondblclick

> **ondblclick**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5898

Fires when the user double-clicks the object.

#### Param

The mouse event.

#### Inherited from

`GlobalEventHandlers.ondblclick`

***

### ondevicemotion

> **ondevicemotion**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16989

Available only in secure contexts.

***

### ondeviceorientation

> **ondeviceorientation**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16991

Available only in secure contexts.

***

### ondrag

> **ondrag**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5903

Fires on the source object continuously during a drag operation.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.ondrag`

***

### ondragend

> **ondragend**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5908

Fires on the source object when the user releases the mouse at the close of a drag operation.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.ondragend`

***

### ondragenter

> **ondragenter**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5913

Fires on the target element when the user drags the object to a valid drop target.

#### Param

The drag event.

#### Inherited from

`GlobalEventHandlers.ondragenter`

***

### ondragleave

> **ondragleave**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5918

Fires on the target object when the user moves the mouse out of a valid drop target during a drag operation.

#### Param

The drag event.

#### Inherited from

`GlobalEventHandlers.ondragleave`

***

### ondragover

> **ondragover**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5923

Fires on the target element continuously while the user drags the object over a valid drop target.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.ondragover`

***

### ondragstart

> **ondragstart**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5928

Fires on the source object when the user starts to drag a text selection or selected object.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.ondragstart`

***

### ondrop

> **ondrop**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5929

#### Inherited from

`GlobalEventHandlers.ondrop`

***

### ondurationchange

> **ondurationchange**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5934

Occurs when the duration attribute is updated.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.ondurationchange`

***

### onemptied

> **onemptied**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5939

Occurs when the media element is reset to its initial state.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onemptied`

***

### onended

> **onended**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5944

Occurs when the end of playback is reached.

#### Param

The event

#### Inherited from

`GlobalEventHandlers.onended`

***

### onerror

> **onerror**: `OnErrorEventHandler`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5949

Fires when an error occurs during object loading.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onerror`

***

### onfocus

> **onfocus**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5954

Fires when the object receives focus.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onfocus`

***

### onformdata

> **onformdata**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5955

#### Inherited from

`GlobalEventHandlers.onformdata`

***

### ongamepadconnected

> **ongamepadconnected**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17112

#### Inherited from

`WindowEventHandlers.ongamepadconnected`

***

### ongamepaddisconnected

> **ongamepaddisconnected**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17113

#### Inherited from

`WindowEventHandlers.ongamepaddisconnected`

***

### ongotpointercapture

> **ongotpointercapture**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5956

#### Inherited from

`GlobalEventHandlers.ongotpointercapture`

***

### onhashchange

> **onhashchange**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17114

#### Inherited from

`WindowEventHandlers.onhashchange`

***

### oninput

> **oninput**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5957

#### Inherited from

`GlobalEventHandlers.oninput`

***

### oninvalid

> **oninvalid**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5958

#### Inherited from

`GlobalEventHandlers.oninvalid`

***

### onkeydown

> **onkeydown**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5963

Fires when the user presses a key.

#### Param

The keyboard event

#### Inherited from

`GlobalEventHandlers.onkeydown`

***

### ~~onkeypress~~

> **onkeypress**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5969

Fires when the user presses an alphanumeric key.

#### Param

The event.

#### Deprecated

#### Inherited from

`GlobalEventHandlers.onkeypress`

***

### onkeyup

> **onkeyup**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5974

Fires when the user releases a key.

#### Param

The keyboard event

#### Inherited from

`GlobalEventHandlers.onkeyup`

***

### onlanguagechange

> **onlanguagechange**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17115

#### Inherited from

`WindowEventHandlers.onlanguagechange`

***

### onload

> **onload**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5979

Fires immediately after the browser loads the object.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onload`

***

### onloadeddata

> **onloadeddata**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5984

Occurs when media data is loaded at the current playback position.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onloadeddata`

***

### onloadedmetadata

> **onloadedmetadata**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5989

Occurs when the duration and dimensions of the media have been determined.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onloadedmetadata`

***

### onloadstart

> **onloadstart**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5994

Occurs when Internet Explorer begins looking for media data.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onloadstart`

***

### onlostpointercapture

> **onlostpointercapture**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5995

#### Inherited from

`GlobalEventHandlers.onlostpointercapture`

***

### onmessage

> **onmessage**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17116

#### Inherited from

`WindowEventHandlers.onmessage`

***

### onmessageerror

> **onmessageerror**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17117

#### Inherited from

`WindowEventHandlers.onmessageerror`

***

### onmousedown

> **onmousedown**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6000

Fires when the user clicks the object with either mouse button.

#### Param

The mouse event.

#### Inherited from

`GlobalEventHandlers.onmousedown`

***

### onmouseenter

> **onmouseenter**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6001

#### Inherited from

`GlobalEventHandlers.onmouseenter`

***

### onmouseleave

> **onmouseleave**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6002

#### Inherited from

`GlobalEventHandlers.onmouseleave`

***

### onmousemove

> **onmousemove**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6007

Fires when the user moves the mouse over the object.

#### Param

The mouse event.

#### Inherited from

`GlobalEventHandlers.onmousemove`

***

### onmouseout

> **onmouseout**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6012

Fires when the user moves the mouse pointer outside the boundaries of the object.

#### Param

The mouse event.

#### Inherited from

`GlobalEventHandlers.onmouseout`

***

### onmouseover

> **onmouseover**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6017

Fires when the user moves the mouse pointer into the object.

#### Param

The mouse event.

#### Inherited from

`GlobalEventHandlers.onmouseover`

***

### onmouseup

> **onmouseup**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6022

Fires when the user releases a mouse button while the mouse is over the object.

#### Param

The mouse event.

#### Inherited from

`GlobalEventHandlers.onmouseup`

***

### onoffline

> **onoffline**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17118

#### Inherited from

`WindowEventHandlers.onoffline`

***

### ononline

> **ononline**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17119

#### Inherited from

`WindowEventHandlers.ononline`

***

### ~~onorientationchange~~

> **onorientationchange**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16993

#### Deprecated

***

### onpagehide

> **onpagehide**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17120

#### Inherited from

`WindowEventHandlers.onpagehide`

***

### onpageshow

> **onpageshow**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17121

#### Inherited from

`WindowEventHandlers.onpageshow`

***

### onpaste

> **onpaste**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6023

#### Inherited from

`GlobalEventHandlers.onpaste`

***

### onpause

> **onpause**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6028

Occurs when playback is paused.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onpause`

***

### onplay

> **onplay**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6033

Occurs when the play method is requested.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onplay`

***

### onplaying

> **onplaying**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6038

Occurs when the audio or video has started playing.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onplaying`

***

### onpointercancel

> **onpointercancel**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6039

#### Inherited from

`GlobalEventHandlers.onpointercancel`

***

### onpointerdown

> **onpointerdown**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6040

#### Inherited from

`GlobalEventHandlers.onpointerdown`

***

### onpointerenter

> **onpointerenter**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6041

#### Inherited from

`GlobalEventHandlers.onpointerenter`

***

### onpointerleave

> **onpointerleave**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6042

#### Inherited from

`GlobalEventHandlers.onpointerleave`

***

### onpointermove

> **onpointermove**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6043

#### Inherited from

`GlobalEventHandlers.onpointermove`

***

### onpointerout

> **onpointerout**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6044

#### Inherited from

`GlobalEventHandlers.onpointerout`

***

### onpointerover

> **onpointerover**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6045

#### Inherited from

`GlobalEventHandlers.onpointerover`

***

### onpointerup

> **onpointerup**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6046

#### Inherited from

`GlobalEventHandlers.onpointerup`

***

### onpopstate

> **onpopstate**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17122

#### Inherited from

`WindowEventHandlers.onpopstate`

***

### onprogress

> **onprogress**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6051

Occurs to indicate progress while downloading media data.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onprogress`

***

### onratechange

> **onratechange**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6056

Occurs when the playback rate is increased or decreased.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onratechange`

***

### onrejectionhandled

> **onrejectionhandled**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17123

#### Inherited from

`WindowEventHandlers.onrejectionhandled`

***

### onreset

> **onreset**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6061

Fires when the user resets a form.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onreset`

***

### onresize

> **onresize**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6062

#### Inherited from

`GlobalEventHandlers.onresize`

***

### onscroll

> **onscroll**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6067

Fires when the user repositions the scroll box in the scroll bar on the object.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onscroll`

***

### onsecuritypolicyviolation

> **onsecuritypolicyviolation**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6068

#### Inherited from

`GlobalEventHandlers.onsecuritypolicyviolation`

***

### onseeked

> **onseeked**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6073

Occurs when the seek operation ends.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onseeked`

***

### onseeking

> **onseeking**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6078

Occurs when the current playback position is moved.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onseeking`

***

### onselect

> **onselect**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6083

Fires when the current selection changes.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onselect`

***

### onselectionchange

> **onselectionchange**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6084

#### Inherited from

`GlobalEventHandlers.onselectionchange`

***

### onselectstart

> **onselectstart**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6085

#### Inherited from

`GlobalEventHandlers.onselectstart`

***

### onslotchange

> **onslotchange**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6086

#### Inherited from

`GlobalEventHandlers.onslotchange`

***

### onstalled

> **onstalled**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6091

Occurs when the download has stopped.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onstalled`

***

### onstorage

> **onstorage**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17124

#### Inherited from

`WindowEventHandlers.onstorage`

***

### onsubmit

> **onsubmit**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6092

#### Inherited from

`GlobalEventHandlers.onsubmit`

***

### onsuspend

> **onsuspend**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6097

Occurs if the load operation has been intentionally halted.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onsuspend`

***

### ontimeupdate

> **ontimeupdate**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6102

Occurs to indicate the current playback position.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.ontimeupdate`

***

### ontoggle

> **ontoggle**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6103

#### Inherited from

`GlobalEventHandlers.ontoggle`

***

### ontouchcancel?

> `optional` **ontouchcancel**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6104

#### Inherited from

`GlobalEventHandlers.ontouchcancel`

***

### ontouchend?

> `optional` **ontouchend**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6105

#### Inherited from

`GlobalEventHandlers.ontouchend`

***

### ontouchmove?

> `optional` **ontouchmove**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6106

#### Inherited from

`GlobalEventHandlers.ontouchmove`

***

### ontouchstart?

> `optional` **ontouchstart**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6107

#### Inherited from

`GlobalEventHandlers.ontouchstart`

***

### ontransitioncancel

> **ontransitioncancel**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6108

#### Inherited from

`GlobalEventHandlers.ontransitioncancel`

***

### ontransitionend

> **ontransitionend**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6109

#### Inherited from

`GlobalEventHandlers.ontransitionend`

***

### ontransitionrun

> **ontransitionrun**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6110

#### Inherited from

`GlobalEventHandlers.ontransitionrun`

***

### ontransitionstart

> **ontransitionstart**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6111

#### Inherited from

`GlobalEventHandlers.ontransitionstart`

***

### onunhandledrejection

> **onunhandledrejection**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17125

#### Inherited from

`WindowEventHandlers.onunhandledrejection`

***

### onunload

> **onunload**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17126

#### Inherited from

`WindowEventHandlers.onunload`

***

### onvolumechange

> **onvolumechange**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6116

Occurs when the volume is changed, or playback is muted or unmuted.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onvolumechange`

***

### onwaiting

> **onwaiting**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6121

Occurs when playback stops because the next frame of a video resource is not available.

#### Param

The event.

#### Inherited from

`GlobalEventHandlers.onwaiting`

***

### ~~onwebkitanimationend~~

> **onwebkitanimationend**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6123

#### Deprecated

This is a legacy alias of `onanimationend`.

#### Inherited from

`GlobalEventHandlers.onwebkitanimationend`

***

### ~~onwebkitanimationiteration~~

> **onwebkitanimationiteration**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6125

#### Deprecated

This is a legacy alias of `onanimationiteration`.

#### Inherited from

`GlobalEventHandlers.onwebkitanimationiteration`

***

### ~~onwebkitanimationstart~~

> **onwebkitanimationstart**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6127

#### Deprecated

This is a legacy alias of `onanimationstart`.

#### Inherited from

`GlobalEventHandlers.onwebkitanimationstart`

***

### ~~onwebkittransitionend~~

> **onwebkittransitionend**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6129

#### Deprecated

This is a legacy alias of `ontransitionend`.

#### Inherited from

`GlobalEventHandlers.onwebkittransitionend`

***

### onwheel

> **onwheel**: `null` \| (`this`, `ev`) => `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:6130

#### Inherited from

`GlobalEventHandlers.onwheel`

***

### opener

> **opener**: `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16994

***

### ~~orientation~~

> `readonly` **orientation**: `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16996

#### Deprecated

***

### origin

> `readonly` **origin**: `string`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17144

#### Inherited from

`WindowOrWorkerGlobalScope.origin`

***

### outerHeight

> `readonly` **outerHeight**: `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16997

***

### outerWidth

> `readonly` **outerWidth**: `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16998

***

### ~~pageXOffset~~

> `readonly` **pageXOffset**: `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17000

#### Deprecated

This is a legacy alias of `scrollX`.

***

### ~~pageYOffset~~

> `readonly` **pageYOffset**: `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17002

#### Deprecated

This is a legacy alias of `scrollY`.

***

### parent

> `readonly` **parent**: `Window`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17008

Refers to either the parent WindowProxy, or itself.

It can rarely be null e.g. for contentWindow of an iframe that is already removed from the parent.

***

### performance

> `readonly` **performance**: `Performance`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17145

#### Inherited from

`WindowOrWorkerGlobalScope.performance`

***

### personalbar

> `readonly` **personalbar**: `BarProp`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17010

Returns true if the personal bar is visible; otherwise, returns false.

***

### removeHash()

> **removeHash**: (`key`) => `void`

Defined in: [src/types/window.ts:52](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L52)

#### Parameters

##### key

`string`

#### Returns

`void`

***

### REPORT\_ZIP

> **REPORT\_ZIP**: `null` \| `JSZip`

Defined in: [src/types/window.ts:46](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L46)

***

### REPOS

> **REPOS**: `object`

Defined in: [src/types/window.ts:43](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L43)

#### Index Signature

\[`key`: `string`\]: [`Repo`](../../types/interfaces/Repo.md)

***

### REPOSENSE\_REPO\_URL

> **REPOSENSE\_REPO\_URL**: `string`

Defined in: [src/types/window.ts:38](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L38)

***

### repoSenseVersion

> **repoSenseVersion**: `string`

Defined in: [src/types/window.ts:69](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L69)

***

### screen

> `readonly` **screen**: `Screen`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17011

***

### screenLeft

> `readonly` **screenLeft**: `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17012

***

### screenTop

> `readonly` **screenTop**: `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17013

***

### screenX

> `readonly` **screenX**: `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17014

***

### screenY

> `readonly` **screenY**: `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17015

***

### scrollbars

> `readonly` **scrollbars**: `BarProp`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17019

Returns true if the scrollbars are visible; otherwise, returns false.

***

### scrollX

> `readonly` **scrollX**: `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17016

***

### scrollY

> `readonly` **scrollY**: `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17017

***

### self

> `readonly` **self**: `Window` & *typeof* `globalThis`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17020

***

### sessionStorage

> `readonly` **sessionStorage**: `Storage`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17161

#### Inherited from

`WindowSessionStorage.sessionStorage`

***

### sinceDate

> **sinceDate**: `string`

Defined in: [src/types/window.ts:67](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L67)

***

### speechSynthesis

> `readonly` **speechSynthesis**: `SpeechSynthesis`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17021

***

### ~~status~~

> **status**: `string`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17023

#### Deprecated

***

### statusbar

> `readonly` **statusbar**: `BarProp`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17025

Returns true if the status bar is visible; otherwise, returns false.

***

### toolbar

> `readonly` **toolbar**: `BarProp`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17027

Returns true if the toolbar is visible; otherwise, returns false.

***

### top

> `readonly` **top**: `null` \| `Window`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17028

***

### UNSUPPORTED\_INDICATOR

> **UNSUPPORTED\_INDICATOR**: `string`

Defined in: [src/types/window.ts:40](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L40)

***

### untilDate

> **untilDate**: `string`

Defined in: [src/types/window.ts:68](https://github.com/joeng03/RepoSense/blob/3f722058ea4a4c6de9dfb6b764fc6baf0e159e62/frontend/src/types/window.ts#L68)

***

### visualViewport

> `readonly` **visualViewport**: `null` \| `VisualViewport`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17029

***

### window

> `readonly` **window**: `Window` & *typeof* `globalThis`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17030

## Accessors

### location

#### Get Signature

> **get** **location**(): `Location`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16980

##### Returns

`Location`

#### Set Signature

> **set** **location**(`href`): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:16981

##### Parameters

###### href

`string` | `Location`

##### Returns

`void`

## Methods

### addEventListener()

#### Call Signature

> **addEventListener**\<`K`\>(`type`, `listener`, `options`?): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17075

Appends an event listener for events whose type attribute value is type. The callback argument sets the callback that will be invoked when the event is dispatched.

The options argument sets listener-specific options. For compatibility this can be a boolean, in which case the method behaves exactly as if the value was specified as options's capture.

When set to true, options's capture prevents callback from being invoked when the event's eventPhase attribute value is BUBBLING_PHASE. When false (or not present), callback will not be invoked when event's eventPhase attribute value is CAPTURING_PHASE. Either way, callback will be invoked if event's eventPhase attribute value is AT_TARGET.

When set to true, options's passive indicates that the callback will not cancel the event by invoking preventDefault(). This is used to enable performance optimizations described in § 2.8 Observing event listeners.

When set to true, options's once indicates that the callback will only be invoked once after which the event listener will be removed.

If an AbortSignal is passed for options's signal, then the event listener will be removed when signal is aborted.

The event listener is appended to target's event listener list and is not appended if it has the same type, callback, and capture.

##### Type Parameters

###### K

`K` *extends* keyof `WindowEventMap`

##### Parameters

###### type

`K`

###### listener

(`this`, `ev`) => `any`

###### options?

`boolean` | `AddEventListenerOptions`

##### Returns

`void`

##### Overrides

`EventTarget.addEventListener`

#### Call Signature

> **addEventListener**(`type`, `listener`, `options`?): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17076

Appends an event listener for events whose type attribute value is type. The callback argument sets the callback that will be invoked when the event is dispatched.

The options argument sets listener-specific options. For compatibility this can be a boolean, in which case the method behaves exactly as if the value was specified as options's capture.

When set to true, options's capture prevents callback from being invoked when the event's eventPhase attribute value is BUBBLING_PHASE. When false (or not present), callback will not be invoked when event's eventPhase attribute value is CAPTURING_PHASE. Either way, callback will be invoked if event's eventPhase attribute value is AT_TARGET.

When set to true, options's passive indicates that the callback will not cancel the event by invoking preventDefault(). This is used to enable performance optimizations described in § 2.8 Observing event listeners.

When set to true, options's once indicates that the callback will only be invoked once after which the event listener will be removed.

If an AbortSignal is passed for options's signal, then the event listener will be removed when signal is aborted.

The event listener is appended to target's event listener list and is not appended if it has the same type, callback, and capture.

##### Parameters

###### type

`string`

###### listener

`EventListenerOrEventListenerObject`

###### options?

`boolean` | `AddEventListenerOptions`

##### Returns

`void`

##### Overrides

`EventTarget.addEventListener`

***

### alert()

> **alert**(`message`?): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17031

#### Parameters

##### message?

`any`

#### Returns

`void`

***

### atob()

> **atob**(`data`): `string`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17146

#### Parameters

##### data

`string`

#### Returns

`string`

#### Inherited from

`WindowOrWorkerGlobalScope.atob`

***

### blur()

> **blur**(): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17032

#### Returns

`void`

***

### btoa()

> **btoa**(`data`): `string`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17147

#### Parameters

##### data

`string`

#### Returns

`string`

#### Inherited from

`WindowOrWorkerGlobalScope.btoa`

***

### cancelAnimationFrame()

> **cancelAnimationFrame**(`handle`): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:2214

#### Parameters

##### handle

`number`

#### Returns

`void`

#### Inherited from

`AnimationFrameProvider.cancelAnimationFrame`

***

### cancelIdleCallback()

> **cancelIdleCallback**(`handle`): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17033

#### Parameters

##### handle

`number`

#### Returns

`void`

***

### ~~captureEvents()~~

> **captureEvents**(): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17035

#### Returns

`void`

#### Deprecated

***

### clearInterval()

> **clearInterval**(`id`): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17148

#### Parameters

##### id

`undefined` | `number`

#### Returns

`void`

#### Inherited from

`WindowOrWorkerGlobalScope.clearInterval`

***

### clearTimeout()

> **clearTimeout**(`id`): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17149

#### Parameters

##### id

`undefined` | `number`

#### Returns

`void`

#### Inherited from

`WindowOrWorkerGlobalScope.clearTimeout`

***

### close()

> **close**(): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17037

Closes the window.

#### Returns

`void`

***

### confirm()

> **confirm**(`message`?): `boolean`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17038

#### Parameters

##### message?

`string`

#### Returns

`boolean`

***

### createImageBitmap()

#### Call Signature

> **createImageBitmap**(`image`, `options`?): `Promise`\<`ImageBitmap`\>

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17150

##### Parameters

###### image

`ImageBitmapSource`

###### options?

`ImageBitmapOptions`

##### Returns

`Promise`\<`ImageBitmap`\>

##### Inherited from

`WindowOrWorkerGlobalScope.createImageBitmap`

#### Call Signature

> **createImageBitmap**(`image`, `sx`, `sy`, `sw`, `sh`, `options`?): `Promise`\<`ImageBitmap`\>

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17151

##### Parameters

###### image

`ImageBitmapSource`

###### sx

`number`

###### sy

`number`

###### sw

`number`

###### sh

`number`

###### options?

`ImageBitmapOptions`

##### Returns

`Promise`\<`ImageBitmap`\>

##### Inherited from

`WindowOrWorkerGlobalScope.createImageBitmap`

***

### dispatchEvent()

> **dispatchEvent**(`event`): `boolean`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:5344

Dispatches a synthetic event event to target and returns true if either event's cancelable attribute value is false or its preventDefault() method was not invoked, and false otherwise.

#### Parameters

##### event

`Event`

#### Returns

`boolean`

#### Inherited from

`EventTarget.dispatchEvent`

***

### fetch()

> **fetch**(`input`, `init`?): `Promise`\<`Response`\>

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17152

#### Parameters

##### input

`URL` | `RequestInfo`

##### init?

`RequestInit`

#### Returns

`Promise`\<`Response`\>

#### Inherited from

`WindowOrWorkerGlobalScope.fetch`

***

### focus()

> **focus**(): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17040

Moves the focus to the window's browsing context, if any.

#### Returns

`void`

***

### getComputedStyle()

> **getComputedStyle**(`elt`, `pseudoElt`?): `CSSStyleDeclaration`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17041

#### Parameters

##### elt

`Element`

##### pseudoElt?

`null` | `string`

#### Returns

`CSSStyleDeclaration`

***

### getSelection()

> **getSelection**(): `null` \| `Selection`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17042

#### Returns

`null` \| `Selection`

***

### matchMedia()

> **matchMedia**(`query`): `MediaQueryList`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17043

#### Parameters

##### query

`string`

#### Returns

`MediaQueryList`

***

### moveBy()

> **moveBy**(`x`, `y`): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17044

#### Parameters

##### x

`number`

##### y

`number`

#### Returns

`void`

***

### moveTo()

> **moveTo**(`x`, `y`): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17045

#### Parameters

##### x

`number`

##### y

`number`

#### Returns

`void`

***

### open()

> **open**(`url`?, `target`?, `features`?): `null` \| `Window`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17046

#### Parameters

##### url?

`string` | `URL`

##### target?

`string`

##### features?

`string`

#### Returns

`null` \| `Window`

***

### postMessage()

#### Call Signature

> **postMessage**(`message`, `targetOrigin`, `transfer`?): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17058

Posts a message to the given window. Messages can be structured objects, e.g. nested objects and arrays, can contain JavaScript values (strings, numbers, Date objects, etc), and can contain certain data objects such as File Blob, FileList, and ArrayBuffer objects.

Objects listed in the transfer member of options are transferred, not just cloned, meaning that they are no longer usable on the sending side.

A target origin can be specified using the targetOrigin member of options. If not provided, it defaults to "/". This default restricts the message to same-origin targets only.

If the origin of the target window doesn't match the given target origin, the message is discarded, to avoid information leakage. To send the message to the target regardless of origin, set the target origin to "*".

Throws a "DataCloneError" DOMException if transfer array contains duplicate objects or if message could not be cloned.

##### Parameters

###### message

`any`

###### targetOrigin

`string`

###### transfer?

`Transferable`[]

##### Returns

`void`

#### Call Signature

> **postMessage**(`message`, `options`?): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17059

##### Parameters

###### message

`any`

###### options?

`WindowPostMessageOptions`

##### Returns

`void`

***

### print()

> **print**(): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17060

#### Returns

`void`

***

### prompt()

> **prompt**(`message`?, `_default`?): `null` \| `string`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17061

#### Parameters

##### message?

`string`

##### \_default?

`string`

#### Returns

`null` \| `string`

***

### queueMicrotask()

> **queueMicrotask**(`callback`): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17153

#### Parameters

##### callback

`VoidFunction`

#### Returns

`void`

#### Inherited from

`WindowOrWorkerGlobalScope.queueMicrotask`

***

### ~~releaseEvents()~~

> **releaseEvents**(): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17063

#### Returns

`void`

#### Deprecated

***

### removeEventListener()

#### Call Signature

> **removeEventListener**\<`K`\>(`type`, `listener`, `options`?): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17077

Removes the event listener in target's event listener list with the same type, callback, and options.

##### Type Parameters

###### K

`K` *extends* keyof `WindowEventMap`

##### Parameters

###### type

`K`

###### listener

(`this`, `ev`) => `any`

###### options?

`boolean` | `EventListenerOptions`

##### Returns

`void`

##### Overrides

`EventTarget.removeEventListener`

#### Call Signature

> **removeEventListener**(`type`, `listener`, `options`?): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17078

Removes the event listener in target's event listener list with the same type, callback, and options.

##### Parameters

###### type

`string`

###### listener

`EventListenerOrEventListenerObject`

###### options?

`boolean` | `EventListenerOptions`

##### Returns

`void`

##### Overrides

`EventTarget.removeEventListener`

***

### reportError()

> **reportError**(`e`): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17154

#### Parameters

##### e

`any`

#### Returns

`void`

#### Inherited from

`WindowOrWorkerGlobalScope.reportError`

***

### requestAnimationFrame()

> **requestAnimationFrame**(`callback`): `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:2215

#### Parameters

##### callback

`FrameRequestCallback`

#### Returns

`number`

#### Inherited from

`AnimationFrameProvider.requestAnimationFrame`

***

### requestIdleCallback()

> **requestIdleCallback**(`callback`, `options`?): `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17064

#### Parameters

##### callback

`IdleRequestCallback`

##### options?

`IdleRequestOptions`

#### Returns

`number`

***

### resizeBy()

> **resizeBy**(`x`, `y`): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17065

#### Parameters

##### x

`number`

##### y

`number`

#### Returns

`void`

***

### resizeTo()

> **resizeTo**(`width`, `height`): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17066

#### Parameters

##### width

`number`

##### height

`number`

#### Returns

`void`

***

### scroll()

#### Call Signature

> **scroll**(`options`?): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17067

##### Parameters

###### options?

`ScrollToOptions`

##### Returns

`void`

#### Call Signature

> **scroll**(`x`, `y`): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17068

##### Parameters

###### x

`number`

###### y

`number`

##### Returns

`void`

***

### scrollBy()

#### Call Signature

> **scrollBy**(`options`?): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17069

##### Parameters

###### options?

`ScrollToOptions`

##### Returns

`void`

#### Call Signature

> **scrollBy**(`x`, `y`): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17070

##### Parameters

###### x

`number`

###### y

`number`

##### Returns

`void`

***

### scrollTo()

#### Call Signature

> **scrollTo**(`options`?): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17071

##### Parameters

###### options?

`ScrollToOptions`

##### Returns

`void`

#### Call Signature

> **scrollTo**(`x`, `y`): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17072

##### Parameters

###### x

`number`

###### y

`number`

##### Returns

`void`

***

### setInterval()

> **setInterval**(`handler`, `timeout`?, ...`arguments`?): `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17155

#### Parameters

##### handler

`TimerHandler`

##### timeout?

`number`

##### arguments?

...`any`[]

#### Returns

`number`

#### Inherited from

`WindowOrWorkerGlobalScope.setInterval`

***

### setTimeout()

> **setTimeout**(`handler`, `timeout`?, ...`arguments`?): `number`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17156

#### Parameters

##### handler

`TimerHandler`

##### timeout?

`number`

##### arguments?

...`any`[]

#### Returns

`number`

#### Inherited from

`WindowOrWorkerGlobalScope.setTimeout`

***

### stop()

> **stop**(): `void`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17074

Cancels the document load.

#### Returns

`void`

***

### structuredClone()

> **structuredClone**(`value`, `options`?): `any`

Defined in: node\_modules/typescript/lib/lib.dom.d.ts:17157

#### Parameters

##### value

`any`

##### options?

`StructuredSerializeOptions`

#### Returns

`any`

#### Inherited from

`WindowOrWorkerGlobalScope.structuredClone`
