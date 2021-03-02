<template lang="pug">
  b-container.advanced-options(fluid)
    b-row
      b-col Start Date:&nbsp;
        b-form-input(
          type="date",
          v-bind:value="startDate",
          v-on:change="updateStartDate($event)",
        )
        i {{ startDate ? '' : "Default first commit date used"}}
      b-col Until Date:&nbsp;
        b-form-input(
          type="date",
          v-bind:value="endDate",
          v-on:change="updateEndDate($event)",
        )
        i {{ endDate ? '' : "Default today's date used"}}
      b-col Time Zone:&nbsp;
        i UTC{{timezoneStr}}
        b-form-input(
          v-bind:value="timezone",
          v-on:input="updateTimezone($event)",
          type="range",
          min="-12",
          max="14",
          step="0.5"
        )
</template>

<script>
import { timezoneToStr } from '../generateReport';

export default {
  name: 'AdvancedOptions',
  props: {
    startDate: String,
    endDate: String,
    timezone: Number,
  },
  emits: [
      'updateStartDate',
      'updateEndDate',
      'updateTimezone',
  ],
  computed: {
    timezoneStr() {
      return timezoneToStr(this.timezone, true);
    },
  },
  methods: {
    updateStartDate(startDate) {
      this.$emit('updateStartDate', startDate);
    },
    updateEndDate(endDate) {
      this.$emit('updateEndDate', endDate);
    },
    updateTimezone(tz) {
      this.$emit('updateTimezone', Number(tz));
    },
  },
};
</script>

<style>
.advanced-options {
  padding: 0;
}

.advanced-options-col {
  margin-bottom: 15px;
}

</style>
