<template>
  <div class="wizard-step">
    <h2>Step {{ stepNumber }}: {{ title }}</h2>
    <div class="step-content">
      <slot></slot>
    </div>
    <div class="navigation-buttons">
      <button @click="onBack" :disabled="stepNumber === 1">Back</button>
      <button @click="onNext">{{ isLastStep ? 'Generate' : 'Next' }}</button>
    </div>
  </div>
</template>

<script setup lang="ts">
const props = defineProps<{
  stepNumber: number;
  title: string;
  isLastStep?: boolean;
}>();

const emit = defineEmits(['back', 'next']);

const onBack = () => {
  emit('back');
};

const onNext = () => {
  emit('next');
};
</script>

<style scoped>
.wizard-step {
  max-width: 600px;
  margin: 0 auto;
  text-align: left;
}

.step-content {
  margin-bottom: 2rem;
}

.navigation-buttons {
  display: flex;
  justify-content: space-between;
}

button {
  padding: 0.5rem 1rem;
  cursor: pointer;
}

button:disabled {
  cursor: not-allowed;
  opacity: 0.5;
}
</style>
