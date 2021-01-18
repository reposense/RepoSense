<template lang="pug">
.segment(v-bind:class="{ untouched: !segment.authored, active: segment.lines.length < 5 }")
  .closer(v-if="!segment.authored && segment.lines.length > 4",
    v-on:click="loadCode()", ref="topButton")
    font-awesome-icon.icon.open(icon="plus-circle", v-bind:title="'Click to open code'")
    font-awesome-icon.icon.hide(icon="chevron-circle-down", v-bind:title="'Click to hide code'")
  div(v-if="loaded", v-hljs="path")
    template(v-for="(line, index) in segment.lines", v-bind:key="index")
      .code
        .line-number {{ segment.lineNumbers[index] + "\n" }}
        .line-content {{ line + "\n" }}
  .closer.bottom(v-if="!segment.authored && segment.lines.length > 4", v-on:click="collapseCode")
    font-awesome-icon.icon.hide(icon="chevron-circle-up", v-bind:title="'Click to hide code'")
</template>

<script>
export default {
  name: 'v-segment',
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
    collapseCode() {
      const segmentTop = this.$refs.topButton.getBoundingClientRect().top;
      if (segmentTop < 0) {
        this.$refs.topButton.scrollIntoView();
      }

      window.toggleNext(this.$el.childNodes[0]);
    },
  },
};
</script>
