<template lang="pug">
.segment(v-bind:class="{ untouched: !segment.authored, active: segment.lines.length < 5 }")
  .closer(v-if="!segment.authored && segment.lines.length > 4", v-on:click="loadCode()")
    i.fas.fa-plus-circle.icon.open(v-bind:title="'Click to open code'")
    i.fas.fa-chevron-circle-down.icon.hide(v-bind:title="'Click to hide code'")
  div(v-if="loaded", v-hljs="path")
    .code(v-for="(line, index) in segment.lines")
      .line-number {{ segment.lineNumbers[index] + "\n" }}
      .line-content {{ line + "\n" }}
  .closer.bottom(v-if="!segment.authored && segment.lines.length > 4", v-on:click="loadCode()")
    i.fas.fa-chevron-circle-up.icon.hide(v-bind:title="'Click to hide code'")
</template>

<script>
export default {
  props: ['segment', 'path'],
  data() {
    return {
      loaded: this.segment.authored || this.segment.lines.length < 5,
    };
  },
  methods: {
    loadCode() {
      this.loaded = true;
      // Update button and code css only once code has loaded into DOM
      this.$nextTick(() => {
        window.toggleNext(this.$el.childNodes[0]);
      });
    },
  },
};
</script>

<style lang="scss" scoped>
@import '../static/css/colors';

.segment {
  border-left: .25rem solid mui-color('green');

  .code {
    background-color: mui-color('green', '50');
    padding-left: 1rem;
  }

  .line-number {
    color: mui-color('grey');
    float: left;
    // Not allowing user to select text
    -webkit-touch-callout: none; /* iOS Safari */
    -webkit-user-select: none; /* Safari */
    -khtml-user-select: none; /* Konqueror HTML */
    -moz-user-select: none; /* Firefox */
    -ms-user-select: none; /* Internet Explorer/Edge */
    user-select: none; /* Non-prefixed version, currently supported by Chrome and Opera */
    width: 2rem;

    // overwrite all hljs colours
    [class^='hljs'] {
      color: mui-color('grey');
    }
  }

  .line-content {
    padding-left: 2rem;
  }

  &.untouched {
    $grey: mui-color('grey', '400');
    border-left: .25rem solid $grey;
    height: 20px; /* height of a single line of code */
    position: relative;

    &.active {
      height: auto;

      .code {
        background-color: mui-color('white');
        display: inherit;
      }

      .closer {
        .icon {
          &.open {
            display: none;
          }

          &.hide {
            display: inherit;
          }
        }
      }
    }

    .code {
      display: none;
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

        &.open {
          display: inherit;
        }

        &.hide {
          display: none;
        }
      }
    }
  }
}
</style>
