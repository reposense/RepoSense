<template lang="pug">
#diffstat
  span(:id="cell0")
  span(:id="cell1")
  span(:id="cell2")
  span(:id="cell3")
  span(:id="cell4")
  span &ensp;
</template>

<script>

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
      cell0: 'grey-cell',
      cell1: 'grey-cell',
      cell2: 'grey-cell',
      cell3: 'grey-cell',
      cell4: 'grey-cell',
    };
  },
  beforeMount() {
    this.updateCells();
  },
  methods: {
    updateCells() {
      const commitDiff = this.insertions - this.deletions;
      const commitSum = this.insertions + this.deletions;
      const threshold = 0.2;
      let cells = [];

      const isDiffBelowThreshold = Math.abs(commitDiff) < threshold * commitSum;
      const isDiffLessThanTen = Math.abs(commitDiff) < 10;

      if (this.insertions === 0 && this.deletions > 0) {
        cells = ['red-cell', 'red-cell', 'red-cell', 'red-cell', 'red-cell'];
      } else if (this.insertions > 0 && this.deletions === 0) {
        cells = ['green-cell', 'green-cell', 'green-cell', 'green-cell', 'green-cell'];
      } else if (this.insertions === 0 && this.deletions === 0) {
        cells = ['grey-cell', 'grey-cell', 'grey-cell', 'grey-cell', 'grey-cell'];
      } else if (this.insertions === 1 && this.deletions === 0) {
        cells = ['green-cell', 'grey-cell', 'grey-cell', 'grey-cell', 'grey-cell'];
      } else if (this.insertions === 0 && this.deletions === 1) {
        cells = ['red-cell', 'grey-cell', 'grey-cell', 'grey-cell', 'grey-cell'];
      } else if (this.insertions === 1 && this.deletions === 1) {
        cells = ['green-cell', 'red-cell', 'grey-cell', 'grey-cell', 'grey-cell'];
      } else if (isDiffBelowThreshold || isDiffLessThanTen) {
        cells = ['green-cell', 'green-cell', 'red-cell', 'red-cell', 'grey-cell'];
      } else if (commitDiff > 0) {
        cells = ['green-cell', 'green-cell', 'green-cell', 'green-cell', 'grey-cell'];
      } else if (commitDiff < 0) {
        cells = ['red-cell', 'red-cell', 'red-cell', 'red-cell', 'grey-cell'];
      }

      if (cells.length > 0) {
        this.cell0 = cells[0];
        this.cell1 = cells[1];
        this.cell2 = cells[2];
        this.cell3 = cells[3];
        this.cell4 = cells[4];
      }
    },
  },
};
</script>

<style lang="scss" scoped>

#green-cell,
#red-cell,
#grey-cell {
  display: inline-block;
  margin-left: 1px;
  margin-right: 1px;
  min-height: 10px;
  min-width: 10px;
}
#green-cell {
  background-color: limegreen;
}
#red-cell {
  background-color: red;
}
#grey-cell {
  background-color: lightgrey;
}
</style>
