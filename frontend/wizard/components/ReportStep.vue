<template>
  <wizard-step
    :step-number="4"
    title="Report Configuration"
    @back="store.prevStep()"
    @next="onNext"
  >
    <div class="report-form">
      <div class="field">
        <label for="report-title">Report Title:</label>
        <input
          id="report-title"
          v-model="report.title"
          placeholder="e.g. My Project Report"
        />
      </div>
      <div class="field">
        <label for="since-date">Since Date (optional):</label>
        <input
          id="since-date"
          v-model="report.since"
          type="date"
        />
      </div>
      <div class="field">
        <label for="until-date">Until Date (optional):</label>
        <input
          id="until-date"
          v-model="report.until"
          type="date"
        />
      </div>
    </div>
  </wizard-step>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { store } from '../store';
import WizardStep from './WizardStep.vue';

const report = reactive({
  title: store.config.report.title || 'RepoSense Report',
  since: store.config.report.since || '',
  until: store.config.report.until || '',
});

const onNext = () => {
  store.config.report = { ...report };
  store.nextStep();
};
</script>

<style scoped>
.report-form {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

input {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}
</style>
