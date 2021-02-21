<template lang="pug">
  b-container(fluid)
    b-row
      b-col Start Date:
      b-col Until Date:
      b-col Time Zone:
</template>

<script>
export default {
  name: 'AdvancedOptions',
  props: {
    startDate: String,
    endDate: String,
    timezone: Number,
  },
  methods: {
    updateStartDate(startDate) {
      this.$emit('updateStartDate', startDate);
    },
    updateEndDate(endDate) {
      this.$emit('updateStartDate', endDate);
    },
  },
}
</script>

<style>
</style>
