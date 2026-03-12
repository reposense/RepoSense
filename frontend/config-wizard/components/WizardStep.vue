<template>
  <div class="wizard-step">
    <h2 class="step-heading">{{ title }}</h2>
    <div class="step-content">
      <slot></slot>
    </div>
    <div class="nav-buttons">
      <button v-if="stepNumber > 1" class="btn btn-secondary" @click="emit('back')">
        ← Back
      </button>
      <div v-else></div>
      <div class="nav-right">
        <button v-if="canSkip" class="btn btn-secondary" @click="emit('skip')">
          Skip
        </button>
        <button class="btn btn-primary" :disabled="nextDisabled" @click="emit('next')">
          {{ isLastStep ? 'Generate Config' : 'Next →' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  stepNumber: number;
  title: string;
  isLastStep?: boolean;
  canSkip?: boolean;
  nextDisabled?: boolean;
}>();

const emit = defineEmits(['back', 'next', 'skip']);
</script>

<style scoped lang="scss">
@import '../styles/variables';

.wizard-step {
  display: flex;
  flex-direction: column;
  min-height: 100%;
}

.step-heading {
  font-size: 1.1rem;
  font-weight: 600;
  color: $color-text-primary;
  margin-bottom: 1.25rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid $color-primary;
}

.step-content {
  flex: 1;
}
</style>
