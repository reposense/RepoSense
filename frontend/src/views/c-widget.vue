<template lang="pug">
template(v-if="userUpdated")
  #summary-wrapper
    c-summary.widget-padding(
      v-if="!isPortfolio",
      ref="summary",
      :repos="users",
      :error-messages="errorMessages",
      :is-widget-mode="true"
    )

    c-summary-portfolio.widget-padding(
      v-else,
      ref="summary",
      :repos="users",
      :error-messages="errorMessages",
      :is-widget-mode="true"
    )
template(v-else)
  .empty Widget does not support uploading of .zip file generated by RepoSense.
</template>

<script lang='ts'>
import { defineComponent } from 'vue';

import cSummary from './c-summary.vue';
import CSummaryPortfolio from "./c-summary-portfolio.vue";

export default defineComponent({
  name: 'c-widget',
  components: {
    CSummaryPortfolio,
    cSummary,
  },
  props: {
    updateReportZip: {
      type: Function,
      required: true,
    },
    repos: {
      type: Object,
      required: true,
    },
    users: {
      type: Array,
      required: true,
    },
    userUpdated: {
      type: Boolean,
      required: true,
    },
    loadingOverlayOpacity: {
      type: Number,
      required: true,
    },
    tabType: {
      type: String,
      required: true,
    },
    creationDate: {
      type: String,
      required: true,
    },
    reportGenerationTime: {
      type: String,
      required: true,
    },
    errorMessages: {
      type: Object,
      required: true,
    },
  },
  computed: {
    isPortfolio(): boolean {
      return window.isPortfolio;
    }
  },
});
</script>

<style lang="scss">
@import '../styles/_colors.scss';
@import '../styles/summary-chart.scss';
</style>
