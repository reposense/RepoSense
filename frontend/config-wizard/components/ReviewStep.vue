<template lang="pug">
wizard-step(
  :step-number="4",
  title="Review & Generate",
  :is-last-step="true",
  :next-disabled="validationStatus === 'validating' || validationStatus === 'invalid'",
  @back="store.prevStep()",
  @next="onGenerate"
)
  //- Summary
  .summary-card
    .summary-row
      span.summary-label Repositories
      span.summary-value {{ repoCount }}
    .summary-row
      span.summary-label Branches
      span.summary-value {{ branchCount }}
    .summary-row
      span.summary-label Authors
      span.summary-value {{ authorCount }}
    .summary-row
      span.summary-label Groups
      span.summary-value {{ groupCount }}
  //- Inline preview snippet
  .preview-box
    .preview-box-header
      span report-config.yaml
      span.preview-hint Full preview visible in right pane
    pre.preview-snippet {{ previewSnippet }}
  //- Tier 3 validation status
  .validation-status(v-if="validationStatus !== 'idle'")
    span.status-validating(v-if="validationStatus === 'validating'") ⏳ Validating configuration...
    span.status-valid(v-else-if="validationStatus === 'valid'") ✓ Configuration is valid
    .status-invalid(v-else-if="validationStatus === 'invalid'")
      p ✗ Validation failed: {{ validationError }}
      button.btn.btn-link(@click="validationStatus = 'valid'") Dismiss and generate anyway
  //- Generate result
  .status-box(v-if="status", :class="status.type")
    p {{ status.message }}
    p.status-path(v-if="status.path")
      | Generated at:
      code {{ status.path }}
    .next-steps(v-if="status.type === 'success'")
      p.next-steps-label Next Steps:
      code.run-command java -jar RepoSense.jar --config {{ statusDir(status.path!) }}
      .success-actions
        button.btn.btn-link.copy-cmd-btn(@click="copyCommand(status.path!)") Copy command
        button.btn.btn-danger.close-btn(@click="quitWizard") Close Wizard
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
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

// Tier 3 validation
type ValidationStatus = 'idle' | 'validating' | 'valid' | 'invalid';
const validationStatus = ref<ValidationStatus>('idle');
const validationError = ref('');

const validateConfig = async (): Promise<boolean> => {
  validationStatus.value = 'validating';
  try {
    const resp = await fetch('/api/validate-config', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(store.config),
    });
    const data = await resp.json();
    if (data.valid) {
      validationStatus.value = 'valid';
      return true;
    }
    validationStatus.value = 'invalid';
    validationError.value = data.error || 'Invalid configuration';
    return false;
  } catch {
    validationStatus.value = 'invalid';
    validationError.value = 'Could not reach server for validation';
    return false;
  }
};

interface Status {
  type: 'success' | 'error';
  message: string;
  path?: string;
}
const status = ref<Status | null>(null);

// Run Tier 3 automatically when step is mounted
onMounted(validateConfig);

// --config takes the directory containing report-config.yaml, not the file path itself
const statusDir = (filePath: string) => filePath.substring(0, filePath.lastIndexOf('/') + 1) || './';

const doGenerate = async () => {
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
      status.value = { type: 'error', message: `Error: ${result.error}` };
    }
  } catch {
    status.value = { type: 'error', message: 'Failed to communicate with the server.' };
  }
};

const onGenerate = async () => {
  // If still validating, wait — shouldn't normally happen but guards against fast clicks
  if (validationStatus.value === 'validating') return;
  // If invalid, user must explicitly dismiss before generating
  if (validationStatus.value === 'invalid') return;
  await doGenerate();
};

const copyCommand = (path: string) => {
  navigator.clipboard.writeText(`java -jar RepoSense.jar --config ${statusDir(path)}`);
};

const quitWizard = async () => {
  try {
    await fetch('/api/quit', { method: 'POST' });
  } catch {
    // server is shutting down
  }
  window.close();
};
</script>

<style scoped lang="scss">
@import '../styles/variables';

.summary-card {
  background: $color-bg-light;
  border: 1px solid $color-border;
  border-radius: 6px;
  margin-bottom: 1.25rem;
  padding: 1rem;
}

.summary-row {
  border-bottom: 1px solid $color-bg-medium;
  display: flex;
  font-size: .875rem;
  justify-content: space-between;
  padding: .3rem 0;

  &:last-child { border-bottom: none; }
}

.summary-label { color: $color-text-secondary; }

.summary-value {
  color: $color-text-primary;
  font-weight: 600;
}

.preview-box {
  border: 1px solid $color-border;
  border-radius: 6px;
  margin-bottom: 1.25rem;
  overflow: hidden;
}

.preview-box-header {
  align-items: center;
  background: $color-bg-medium;
  border-bottom: 1px solid $color-border;
  color: $color-text-secondary;
  display: flex;
  font-size: .8rem;
  font-weight: 600;
  justify-content: space-between;
  padding: .4rem .75rem;
}

.preview-hint {
  color: $color-text-hint;
  font-style: italic;
  font-weight: normal;
}

.preview-snippet {
  background: $color-editor-bg;
  color: $color-editor-text;
  font-family: $font-mono;
  font-size: .75rem;
  line-height: 1.5;
  max-height: 12rem;
  overflow: hidden;
  padding: .75rem;
}

.validation-status {
  font-size: .875rem;
  margin-bottom: 1rem;
}

.status-validating { color: $color-text-hint; }

.status-valid {
  color: $color-success;
  font-weight: 500;
}

.status-invalid {
  background: $color-warning-light;
  border: 1px solid $color-warning-border;
  border-radius: 6px;
  color: mui-color('amber', '900');
  padding: .75rem;

  p { margin-bottom: .4rem; }
}

.status-box {
  border-radius: 6px;
  font-size: .875rem;
  padding: 1rem;

  &.success {
    background: $color-success-light;
    border: 1px solid $color-success-border;
    color: $color-success;
  }

  &.error {
    background: $color-error-light;
    border: 1px solid mui-color('red', '200');
    color: mui-color('red', '800');
  }
}

.status-path {
  font-size: .8rem;
  margin-top: .5rem;

  code {
    background: rgba(0, 0, 0, .06);
    border-radius: 3px;
    padding: .1rem .3rem;
  }
}

.next-steps {
  border-top: 1px solid mui-color('green', '300');
  margin-top: .75rem;
  padding-top: .75rem;
}

.next-steps-label {
  font-weight: 600;
  margin-bottom: .4rem;
}

.run-command {
  background: rgba(0, 0, 0, .06);
  border-radius: 4px;
  display: block;
  font-family: $font-mono;
  font-size: .8rem;
  padding: .4rem .6rem;
  word-break: break-all;
}

.success-actions {
  align-items: center;
  display: flex;
  justify-content: space-between;
  margin-top: .4rem;
}

.copy-cmd-btn { font-size: .8rem; }

.close-btn {
  font-size: .8rem;
  padding: .3rem .75rem;
}
</style>
