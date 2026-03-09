<template>
  <div id="wizard-app">
    <header class="wizard-header">
      <div class="header-top">
        <h1>RepoSense Configuration Wizard</h1>
        <button class="quit-btn" @click="quitWizard">Quit</button>
      </div>
      <div class="stepper">
        <span
          v-for="(label, i) in STEPS"
          :key="i"
          class="step-label"
          :class="{ active: store.currentStep === i + 1, done: store.currentStep > i + 1 }"
          @click="store.currentStep > i + 1 ? store.setStep(i + 1) : null"
        >
          <span class="step-number">{{ i + 1 }}</span>
          {{ label }}
        </span>
      </div>
    </header>

    <div
      class="pane-container"
      ref="container"
      @mousemove="onDragMove"
      @mouseup="onDragEnd"
      @mouseleave="onDragEnd"
    >
      <div class="left-pane" :style="{ flex: `0 0 ${leftWidth}%` }">
        <report-step v-if="store.currentStep === 1" />
        <repos-step v-else-if="store.currentStep === 2" />
        <groups-step v-else-if="store.currentStep === 3" />
        <review-step v-else-if="store.currentStep === 4" :yaml-preview="yamlPreview" />
      </div>

      <div
        class="divider"
        :class="{ dragging: isDragging }"
        @mousedown.prevent="onDragStart"
      >
        <div class="divider-handle"></div>
      </div>

      <div class="right-pane">
        <div class="preview-header">
          <span class="preview-filename">report-config.yaml</span>
          <button class="copy-btn" :disabled="!yamlPreview" @click="copyYaml">
            Copy
          </button>
        </div>
        <pre class="yaml-content">{{ yamlPreview || '# Preview will appear here as you fill in the form...' }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { store } from './store';
import ReportStep from './components/ReportStep.vue';
import ReposStep from './components/ReposStep.vue';
import GroupsStep from './components/GroupsStep.vue';
import ReviewStep from './components/ReviewStep.vue';

const STEPS = ['Report Settings', 'Repos & Branches', 'Groups', 'Review & Generate'];

// --- Two-pane resizer ---
const container = ref<HTMLDivElement | null>(null);
const leftWidth = ref(55);
const isDragging = ref(false);

const onDragStart = () => { isDragging.value = true; };
const onDragEnd = () => { isDragging.value = false; };
const onDragMove = (e: MouseEvent) => {
  if (!isDragging.value || !container.value) return;
  const rect = container.value.getBoundingClientRect();
  const pct = ((e.clientX - rect.left) / rect.width) * 100;
  leftWidth.value = Math.min(Math.max(pct, 25), 75);
};

// --- YAML preview ---
const yamlPreview = ref('');
let previewTimer: ReturnType<typeof setTimeout> | null = null;

const refreshPreview = async () => {
  try {
    const resp = await fetch('/api/preview', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(store.config),
    });
    const data = await resp.json();
    if (data.yaml) {
      yamlPreview.value = data.yaml;
    }
  } catch {
    // preview failure is non-critical
  }
};

watch(
  () => store.config,
  () => {
    if (previewTimer) clearTimeout(previewTimer);
    previewTimer = setTimeout(refreshPreview, 300);
  },
  { deep: true },
);

const copyYaml = () => {
  if (yamlPreview.value) {
    navigator.clipboard.writeText(yamlPreview.value);
  }
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

<style>
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #333;
  height: 100vh;
  overflow: hidden;
}

#wizard-app {
  display: flex;
  flex-direction: column;
  height: 100vh;
}

/* Header */
.wizard-header {
  flex-shrink: 0;
  padding: 1rem 2rem;
  border-bottom: 1px solid #e0e0e0;
  background: #fff;
}

.header-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 0.75rem;
}

.wizard-header h1 {
  font-size: 1.25rem;
  font-weight: 600;
  color: #2c3e50;
}

.quit-btn {
  padding: 0.25rem 0.75rem;
  font-size: 0.8rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  background: #fff;
  color: #888;
  cursor: pointer;
}

.quit-btn:hover {
  border-color: #e74c3c;
  color: #e74c3c;
  background: #fff8f8;
}

.stepper {
  display: flex;
  gap: 2rem;
}

.step-label {
  display: flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 0.875rem;
  color: #999;
  white-space: nowrap;
}

.step-label.active {
  color: #42b983;
  font-weight: 600;
}

.step-label.done {
  color: #42b983;
  cursor: pointer;
  text-decoration: underline;
}

.step-number {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 1.4rem;
  height: 1.4rem;
  border-radius: 50%;
  font-size: 0.75rem;
  font-weight: 700;
  background: #e0e0e0;
  color: #666;
}

.step-label.active .step-number,
.step-label.done .step-number {
  background: #42b983;
  color: #fff;
}

/* Two-pane layout */
.pane-container {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.left-pane {
  overflow-y: auto;
  padding: 1.5rem;
  min-width: 0;
}

.divider {
  flex-shrink: 0;
  width: 6px;
  background: #e8e8e8;
  cursor: col-resize;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.15s;
}

.divider:hover,
.divider.dragging {
  background: #42b983;
}

.divider-handle {
  width: 2px;
  height: 2rem;
  background: #ccc;
  border-radius: 1px;
}

.divider:hover .divider-handle,
.divider.dragging .divider-handle {
  background: #fff;
}

/* Preview pane */
.right-pane {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: #1e1e1e;
  min-width: 0;
}

.preview-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.5rem 1rem;
  background: #2d2d2d;
  border-bottom: 1px solid #444;
}

.preview-filename {
  font-size: 0.8rem;
  color: #aaa;
  font-family: monospace;
}

.copy-btn {
  padding: 0.2rem 0.6rem;
  font-size: 0.75rem;
  border: 1px solid #555;
  background: #3a3a3a;
  color: #ccc;
  border-radius: 3px;
  cursor: pointer;
}

.copy-btn:hover:not(:disabled) {
  background: #42b983;
  border-color: #42b983;
  color: #fff;
}

.copy-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.yaml-content {
  flex: 1;
  overflow-y: auto;
  padding: 1rem;
  font-family: 'Fira Code', 'Courier New', monospace;
  font-size: 0.8rem;
  line-height: 1.6;
  color: #d4d4d4;
  white-space: pre;
}

/* Shared form styles */
.form-group {
  margin-bottom: 1rem;
}

.form-label {
  display: block;
  font-size: 0.85rem;
  font-weight: 500;
  color: #555;
  margin-bottom: 0.3rem;
}

.required {
  color: #e74c3c;
}

.form-input {
  width: 100%;
  padding: 0.45rem 0.65rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 0.875rem;
  color: #333;
  background: #fff;
  transition: border-color 0.15s;
}

.form-input:focus {
  outline: none;
  border-color: #42b983;
}

.form-input.is-invalid {
  border-color: #e74c3c;
}

.form-input.is-valid {
  border-color: #42b983;
}

.field-hint {
  font-size: 0.75rem;
  color: #888;
  margin-top: 0.25rem;
}

.field-error {
  font-size: 0.75rem;
  color: #e74c3c;
  margin-top: 0.25rem;
}

.field-valid {
  font-size: 0.75rem;
  color: #42b983;
  margin-top: 0.25rem;
}

/* Cards */
.card {
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  margin-bottom: 1rem;
  background: #fff;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.6rem 1rem;
  background: #f8f8f8;
  border-bottom: 1px solid #e0e0e0;
  border-radius: 6px 6px 0 0;
}

.card-title {
  font-size: 0.875rem;
  font-weight: 600;
  color: #2c3e50;
}

.card-body {
  padding: 1rem;
}

/* Buttons */
.btn {
  padding: 0.4rem 0.9rem;
  border-radius: 4px;
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  border: 1px solid transparent;
  transition: background 0.15s, color 0.15s;
}

.btn-primary {
  background: #42b983;
  color: #fff;
  border-color: #42b983;
}

.btn-primary:hover {
  background: #369f6e;
  border-color: #369f6e;
}

.btn-secondary {
  background: #fff;
  color: #42b983;
  border-color: #42b983;
}

.btn-secondary:hover {
  background: #f0faf5;
}

.btn-danger {
  background: #fff;
  color: #e74c3c;
  border-color: #e74c3c;
  font-size: 0.75rem;
  padding: 0.2rem 0.5rem;
}

.btn-danger:hover {
  background: #fdf0ee;
}

.btn-link {
  background: none;
  border: none;
  color: #42b983;
  font-size: 0.8rem;
  cursor: pointer;
  padding: 0.2rem 0;
  text-decoration: underline;
}

.btn-link:hover {
  color: #369f6e;
}

/* Navigation */
.nav-buttons {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid #eee;
}

.nav-right {
  display: flex;
  gap: 0.5rem;
}
</style>
