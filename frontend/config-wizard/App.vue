<template>
  <div id="wizard-app">
    <header class="wizard-header">
      <div class="header-top">
        <div class="header-brand">
          <img src="/logo.png" alt="RepoSense" class="header-logo" />
          <h1>RepoSense Configuration Wizard</h1>
        </div>
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
          <span v-if="isPreviewLoading" class="preview-loading">updating...</span>
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
const isPreviewLoading = ref(false);
let previewTimer: ReturnType<typeof setTimeout> | null = null;

const refreshPreview = async () => {
  isPreviewLoading.value = true;
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
  } finally {
    isPreviewLoading.value = false;
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
