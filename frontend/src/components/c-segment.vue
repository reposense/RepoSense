<template lang="pug">
.segment(
  v-bind:class="{ untouched: !segment.knownAuthor, active: isOpen, isNotFullCredit: !segment.isFullCredit }",
  v-bind:style="{ 'border-left': `0.25rem solid ${authorColors[segment.knownAuthor]}` }",
  v-bind:title="`${segment.isFullCredit ? 'Author' : 'Co-author'}: ${segment.knownAuthor || \"Unknown\"}`"
)
  .closer(v-if="canOpen",
    v-on:click="toggleCode", ref="topButton")
    font-awesome-icon.icon(
      v-show="!isOpen",
      icon="plus-circle",
      v-bind:title="'Click to open code'"
    )
    font-awesome-icon.icon(
      v-show="isOpen",
      icon="chevron-circle-down",
      v-bind:title="'Click to hide code'"
    )
  div(v-if="isOpen", v-hljs="path")
    //- author color is applied only when the author color exists, else it takes the default mui color value
    .code(
      v-for="(line, index) in segment.lines", v-bind:key="index",
      v-bind:style="{ 'background-color': `${authorColors[segment.knownAuthor]}${transparencyValue}` }"
    )
      .line-number {{ `${segment.lineNumbers[index]}\n` }}
      .line-content {{ `${line}\n` }}
  .closer.bottom(v-if="canOpen", v-on:click="toggleCode")
    font-awesome-icon.icon(
      v-show="isOpen",
      icon="chevron-circle-up",
      v-bind:title="'Click to hide code'"
    )
</template>

<script lang='ts'>
import { defineComponent, PropType } from 'vue';
import { mapState } from 'vuex';
import { StoreState } from '../types/vuex.d';
import { AuthorshipFileSegment } from '../types/types';

export default defineComponent({
  name: 'c-segment',
  props: {
    segment: {
      type: Object as PropType<AuthorshipFileSegment>,
      required: true,
    },
    path: {
      type: String,
      required: true,
    },
  },
  data(): {
    isOpen: boolean,
    canOpen: boolean,
    transparencyValue: string,
    } {
    return {
      isOpen: (this.segment.knownAuthor !== null) || this.segment.lines.length < 5 as boolean,
      canOpen: (this.segment.knownAuthor === null) && this.segment.lines.length > 4 as boolean,
      transparencyValue: (this.segment.isFullCredit ? '50' : '20') as string,
    };
  },
  computed: {
    ...mapState({
      authorColors: (state: unknown) => (state as StoreState).tabAuthorColors,
    }),
  },
  methods: {
    toggleCode(): void {
      this.isOpen = !this.isOpen;
    },
  },
});
</script>

<style lang="scss" scoped>
@import '../styles/hightlight-js-style.css';
@import '../styles/_colors.scss';

.segment {
  border-left: .25rem solid mui-color('green');

  .code {
    background-color: mui-color('github', 'full-authored-code-background');
    padding-left: 1rem;
  }

  .line-number {
    color: mui-color('grey');
    float: left;
    // Not allowing user to select text
    -webkit-touch-callout: none;
    /* iOS Safari */
    -webkit-user-select: none;
    /* Safari */
    -khtml-user-select: none;
    /* Konqueror HTML */
    -moz-user-select: none;
    /* Firefox */
    -ms-user-select: none;
    /* Internet Explorer/Edge */
    user-select: none;
    /* Non-prefixed version, currently supported by Chrome and Opera */
    width: 2rem;

    // overwrite all hljs colors
    [class^='hljs'] {
      color: mui-color('grey');
    }
  }

  .line-content {
    padding-left: 2rem;
    word-break: break-word;
  }

  &.isNotFullCredit {
    .code {
      background-color: mui-color('github', 'partial-authored-code-background');
    }
  }

  &.untouched {
    $grey: mui-color('grey', '400');
    border-left: .25rem solid $grey;
    height: 20px;
    /* height of a single line of code */
    position: relative;

    &.active {
      height: auto;

      .code {
        background-color: mui-color('white');
      }
    }

    .closer {
      cursor: pointer;
      // custom margin for position of toggle icon
      margin: .2rem 0 0 -.45rem;
      position: absolute;

      &.bottom {
        //custom margin for position of toggle icon at the bottom of segment
        margin: -1.05rem 0 0 -.45rem;
      }

      .icon {
        background-color: mui-color('white');
        color: mui-color('grey');
        width: .75em;
      }
    }
  }
}
</style>
