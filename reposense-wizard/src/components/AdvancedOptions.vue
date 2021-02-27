<template lang="pug">
  b-container(fluid)
    b-row
      b-col Start Date:
        b-form-input(
          type="date",
          v-bind:value="startDate",
          v-on:change="updateStartDate($event)",
        )
      b-col Until Date:
        b-form-input(
          type="date",
          v-bind:value="endDate",
          v-on:change="updateEndDate($event)",
        )
      b-col Time Zone: UTC{{timezoneStr}}
        b-form-input(
          v-bind:value="timezone",
          v-on:input="updateTimezone($event)",
          type="range",
          min="-12",
          max="14",
          step="1"
        )
</template>

<script>
export default {
  name: 'AdvancedOptions',
  props: {
    startDate: String,
    endDate: String,
    timezone: Number,
  },
  emits: [
      'updateStartDate',
      'updateStartDate',
      'updateTimezone',
  ],
  computed: {
    timezoneStr() {
      if (this.timezone >= 0) {
        return `+${this.timezone}`;
      }
      return this.timezone.toString();
    },
  },
  methods: {
    updateStartDate(startDate) {
      this.$emit('updateStartDate', startDate);
    },
    updateEndDate(endDate) {
      this.$emit('updateStartDate', endDate);
    },
    updateTimezone(tz) {
      this.$emit('updateTimezone', Number(tz));
    },
  },
};
</script>

<style>
</style>
