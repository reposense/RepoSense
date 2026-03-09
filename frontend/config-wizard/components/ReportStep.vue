<template>
  <wizard-step
    :step-number="1"
    title="Report Settings"
    @next="onNext"
  >
    <div class="form-group">
      <label class="form-label" for="report-title">Report Title</label>
      <input
        id="report-title"
        v-model="title"
        class="form-input"
        placeholder="e.g. My Project Report"
      />
      <p class="field-hint">Displayed as the heading of the generated report.</p>
    </div>
  </wizard-step>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { store } from '../store';
import WizardStep from './WizardStep.vue';

const title = ref(store.config.title || 'RepoSense Report');

const onNext = () => {
  store.config.title = title.value || 'RepoSense Report';
  store.nextStep();
};
</script>
