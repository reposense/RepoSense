<template>
  <div id="wizard-app">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <header>
      <h1>RepoSense Configuration Wizard</h1>
      <div class="stepper">
        <span :class="{ active: store.currentStep === 1 }">1. Repo</span>
        <span :class="{ active: store.currentStep === 2 }">2. Authors</span>
        <span :class="{ active: store.currentStep === 3 }">3. Groups</span>
        <span :class="{ active: store.currentStep === 4 }">4. Report</span>
        <span :class="{ active: store.currentStep === 5 }">5. Review</span>
      </div>
    </header>

    <main>
      <repo-step v-if="store.currentStep === 1" />
      <author-step v-else-if="store.currentStep === 2" />
      <group-step v-else-if="store.currentStep === 3" />
      <report-step v-else-if="store.currentStep === 4" />
      <review-step v-else-if="store.currentStep === 5" />
    </main>
  </div>
</template>

<script setup lang="ts">
import { store } from './store';
import RepoStep from './components/RepoStep.vue';
import AuthorStep from './components/AuthorStep.vue';
import GroupStep from './components/GroupStep.vue';
import ReportStep from './components/ReportStep.vue';
import ReviewStep from './components/ReviewStep.vue';
</script>

<style>
#wizard-app {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  color: #333;
  max-width: 1000px;
  margin: 0 auto;
  padding: 2rem;
}

header {
  text-align: center;
  margin-bottom: 3rem;
  border-bottom: 1px solid #eee;
  padding-bottom: 1rem;
}

.stepper {
  display: flex;
  justify-content: center;
  gap: 2rem;
  margin-top: 1rem;
}

.stepper span {
  color: #999;
  font-weight: bold;
}

.stepper span.active {
  color: #42b983;
  border-bottom: 2px solid #42b983;
}

main {
  padding: 1rem;
}

.placeholder-step {
  text-align: center;
}

.navigation-buttons {
  margin-top: 2rem;
  display: flex;
  justify-content: center;
  gap: 1rem;
}

button {
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  border: 1px solid #42b983;
  background-color: white;
  color: #42b983;
  cursor: pointer;
  font-weight: bold;
}

button:hover {
  background-color: #42b983;
  color: white;
}
</style>
