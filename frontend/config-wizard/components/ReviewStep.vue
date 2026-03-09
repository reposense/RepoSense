<template>
  <wizard-step
    :step-number="4"
    title="Review & Generate"
    :is-last-step="true"
    @back="store.prevStep()"
    @next="onGenerate"
  >
    <!-- Summary -->
    <div class="summary-card">
      <div class="summary-row">
        <span class="summary-label">Repositories</span>
        <span class="summary-value">{{ repoCount }}</span>
      </div>
      <div class="summary-row">
        <span class="summary-label">Branches</span>
        <span class="summary-value">{{ branchCount }}</span>
      </div>
      <div class="summary-row">
        <span class="summary-label">Authors</span>
        <span class="summary-value">{{ authorCount }}</span>
      </div>
      <div class="summary-row">
        <span class="summary-label">Groups</span>
        <span class="summary-value">{{ groupCount }}</span>
      </div>
    </div>

    <!-- Inline preview (smaller version for the step) -->
    <div class="preview-box">
      <div class="preview-box-header">
        <span>report-config.yaml</span>
        <span class="preview-hint">Full preview visible in right pane</span>
      </div>
      <pre class="preview-snippet">{{ previewSnippet }}</pre>
    </div>

    <!-- Status -->
    <div v-if="status" class="status-box" :class="status.type">
      <p>{{ status.message }}</p>
      <p v-if="status.path" class="status-path">
        Generated at: <code>{{ status.path }}</code>
      </p>
      <div v-if="status.type === 'success'" class="next-steps">
        <p class="next-steps-label">Next Steps:</p>
        <code class="run-command">java -jar RepoSense.jar --repo-config {{ status.path }}</code>
        <div class="success-actions">
          <button class="btn btn-link copy-cmd-btn" @click="copyCommand(status.path!)">
            Copy command
          </button>
          <button class="btn btn-danger close-btn" @click="quitWizard">
            Close Wizard
          </button>
        </div>
      </div>
    </div>
  </wizard-step>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import { store } from '../store';
import WizardStep from './WizardStep.vue';

const props = defineProps<{
  yamlPreview: string;
}>();

const repoCount = computed(() => store.config.repos.length);
const branchCount = computed(() =>
  store.config.repos.reduce((acc, r) => acc + r.branches.length, 0),
);
const authorCount = computed(() =>
  store.config.repos.reduce(
    (acc, r) => acc + r.branches.reduce((bacc, b) => bacc + b.authors.length, 0),
    0,
  ),
);
const groupCount = computed(() =>
  store.config.repos.reduce((acc, r) => acc + r.groups.length, 0),
);

const previewSnippet = computed(() => {
  if (!props.yamlPreview) return '# No preview yet';
  const lines = props.yamlPreview.split('\\n').slice(0, 12);
  if (props.yamlPreview.split('\\n').length > 12) lines.push('...');
  return lines.join('\n');
});

interface Status {
  type: 'success' | 'error';
  message: string;
  path?: string;
}
const status = ref<Status | null>(null);

const onGenerate = async () => {
  status.value = null;
  try {
    const resp = await fetch('/api/generate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(store.config),
    });
    const result = await resp.json();
    if (result.success) {
      status.value = {
        type: 'success',
        message: '✓ report-config.yaml generated successfully!',
        path: result.path,
      };
    } else {
      status.value = { type: 'error', message: 'Error: ' + result.error };
    }
  } catch {
    status.value = { type: 'error', message: 'Failed to communicate with the server.' };
  }
};

const copyCommand = (path: string) => {
  navigator.clipboard.writeText(`java -jar RepoSense.jar --repo-config ${path}`);
};

const quitWizard = async () => {
  try {
    await fetch('/api/quit', { method: 'POST' });
  } catch {
    // server is shutting down, ignore network error
  }
  window.close();
};
</script>

<style scoped>
.summary-card {
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  padding: 1rem;
  margin-bottom: 1.25rem;
  background: #fafafa;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  padding: 0.3rem 0;
  font-size: 0.875rem;
  border-bottom: 1px solid #f0f0f0;
}

.summary-row:last-child {
  border-bottom: none;
}

.summary-label {
  color: #666;
}

.summary-value {
  font-weight: 600;
  color: #2c3e50;
}

.preview-box {
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 1.25rem;
}

.preview-box-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.4rem 0.75rem;
  background: #f5f5f5;
  border-bottom: 1px solid #e0e0e0;
  font-size: 0.8rem;
  font-weight: 600;
  color: #555;
}

.preview-hint {
  font-weight: normal;
  color: #aaa;
  font-style: italic;
}

.preview-snippet {
  padding: 0.75rem;
  font-size: 0.75rem;
  font-family: 'Fira Code', 'Courier New', monospace;
  line-height: 1.5;
  background: #1e1e1e;
  color: #d4d4d4;
  max-height: 12rem;
  overflow: hidden;
}

.status-box {
  border-radius: 6px;
  padding: 1rem;
  font-size: 0.875rem;
}

.status-box.success {
  background: #e8f5e9;
  border: 1px solid #a5d6a7;
  color: #2e7d32;
}

.status-box.error {
  background: #ffebee;
  border: 1px solid #ef9a9a;
  color: #c62828;
}

.status-path {
  margin-top: 0.5rem;
  font-size: 0.8rem;
}

.status-path code {
  background: rgba(0, 0, 0, 0.06);
  padding: 0.1rem 0.3rem;
  border-radius: 3px;
}

.next-steps {
  margin-top: 0.75rem;
  padding-top: 0.75rem;
  border-top: 1px solid #c8e6c9;
}

.next-steps-label {
  font-weight: 600;
  margin-bottom: 0.4rem;
}

.run-command {
  display: block;
  background: rgba(0, 0, 0, 0.06);
  padding: 0.4rem 0.6rem;
  border-radius: 4px;
  font-size: 0.8rem;
  word-break: break-all;
}

.success-actions {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 0.4rem;
}

.copy-cmd-btn {
  font-size: 0.8rem;
}

.close-btn {
  font-size: 0.8rem;
  padding: 0.3rem 0.75rem;
}
</style>
