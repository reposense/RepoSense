<template lang="pug">
wizard-step(:step-number="1", title="Report Settings", @next="onNext")
  .form-group
    label.form-label(for="report-title") Report Title
    input#report-title.form-input(v-model="title", placeholder="e.g. My Project Report")
    p.field-hint Displayed as the heading of the generated report.
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
