<template>
  <wizard-step
    :step-number="5"
    title="Review & Generate"
    :is-last-step="true"
    @back="store.prevStep()"
    @next="onGenerate"
  >
    <div class="review-page">
      <div class="summary">
        <h3>Configuration Summary</h3>
        <p><strong>Repositories:</strong> {{ store.config.repos.length }}</p>
        <p><strong>Authors:</strong> {{ store.config.authors.length }}</p>
        <p><strong>Groups:</strong> {{ store.config.groups.length }}</p>
      </div>

      <div v-if="status" :class="['status-box', status.type]">
        {{ status.message }}
        <p v-if="status.path" class="path-info">Files generated at: <code>{{ status.path }}</code></p>
      </div>
    </div>
  </wizard-step>
</template>

<script setup lang="ts">
import { ref } from 'vue';
import { store } from '../store';
import WizardStep from './WizardStep.vue';

const status = ref<{ type: 'error' | 'success', message: string, path?: string } | null>(null);

const onGenerate = async () => {
  status.value = { type: 'success', message: 'Generating configuration files...' };
  
  try {
    const response = await fetch('/api/generate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(store.config),
    });
    const result = await response.json();

    if (result.success) {
      status.value = { 
        type: 'success', 
        message: 'Successfully generated configuration files!', 
        path: result.path 
      };
    } else {
      status.value = { type: 'error', message: 'Error: ' + result.error };
    }
  } catch (e) {
    status.value = { type: 'error', message: 'Failed to communicate with server.' };
  }
};
</script>

<style scoped>
.review-page {
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.summary {
  background: #f0f0f0;
  padding: 1rem;
  border-radius: 8px;
}

.status-box {
  padding: 1.5rem;
  border-radius: 8px;
  text-align: center;
  font-weight: bold;
}

.status-box.success {
  background-color: #e8f5e9;
  color: #2e7d32;
  border: 1px solid #a5d6a7;
}

.status-box.error {
  background-color: #ffebee;
  color: #c62828;
  border: 1px solid #ef9a9a;
}

.path-info {
  margin-top: 1rem;
  font-size: 0.9rem;
  font-weight: normal;
}

code {
  background: #eee;
  padding: 0.2rem 0.4rem;
  border-radius: 4px;
}
</style>
