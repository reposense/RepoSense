<template lang="pug">
#diffstat
  span(style="display: inline-block")
    span(id="green" :style="{'width': scaledInsertions + 'px'}")
    span(id="red" :style="{'width': scaledDeletions + 'px'}")
    span &nbsp;
</template>

<script>
const MAX_LINES = 600;
const MULTIPLIER = 1.0;

export default {
  name: 'c-diffstat',
  props: {
    insertions: {
      type: Number,
      required: true,
    },
    deletions: {
      type: Number,
      required: true,
    },
  },
  data() {
    return {
      scaledInsertions: this.insertions * MULTIPLIER,
      scaledDeletions: this.deletions * MULTIPLIER,
    };
  },
  beforeMount() {
    this.scaleLinesChanged();
  },
  methods: {
    scaleLinesChanged() {
      const totalChanges = this.insertions + this.deletions;

      if (totalChanges > MAX_LINES) {
        this.scaledInsertions = (this.insertions / totalChanges) * MAX_LINES;
        this.scaledDeletions = (this.deletions / totalChanges) * MAX_LINES;
      }
    },
  },
};
</script>

<style lang="scss" scoped>
#red,
#green {
  display: inline-block;
  min-height: 7px;
}
#red {
  background-color: red;
}
#green {
  background-color: limegreen;
}
</style>
