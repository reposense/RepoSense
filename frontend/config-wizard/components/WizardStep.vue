<template lang="pug">
.wizard-step
  h2.step-heading {{ title }}
  .step-content
    slot
  .nav-buttons
    button.btn.btn-secondary(v-if="stepNumber > 1", @click="emit('back')") ← Back
    div(v-else)
    .nav-right
      button.btn.btn-secondary(v-if="canSkip", @click="emit('skip')") Skip
      button.btn.btn-primary(:disabled="nextDisabled", @click="emit('next')")
        | {{ isLastStep ? 'Generate Config' : 'Next →' }}
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
