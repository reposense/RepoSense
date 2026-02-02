<template>
  <wizard-step
    :step-number="3"
    title="Group Configuration"
    @back="store.prevStep()"
    @next="onNext"
  >
    <div class="group-list">
      <div v-for="(group, index) in groups" :key="index" class="group-item card mb-3">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h5 class="card-title mb-0">Group #{{ index + 1 }}</h5>
            <button class="btn btn-outline-danger btn-sm" @click="removeGroup(index)">
              Remove
            </button>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Group Name <span class="text-danger">*</span></label>
              <input v-model="group.name" type="text" class="form-control" placeholder="e.g. code, tests, docs" />
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Globs (Semicolon separated) <span class="text-danger">*</span></label>
              <input v-model="group.globs" type="text" class="form-control" placeholder="e.g. **.java;src/test/**" />
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">Repository (Optional - bound to all if empty)</label>
            <input v-model="group.location" type="text" class="form-control" placeholder="Repo URL or Path" />
          </div>
        </div>
      </div>

      <div class="d-flex gap-2">
        <button class="btn btn-outline-primary flex-grow-1" @click="addGroup">
          Add Another Group
        </button>
        <button class="btn btn-outline-secondary" @click="onNext">
          Skip/Next
        </button>
      </div>
      <p class="form-text mt-2">
        Groups allow you to categorize files in your report. This is optional.
      </p>
    </div>
  </wizard-step>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { store } from '../store';
import WizardStep from './WizardStep.vue';

const groups = reactive(store.config.groups.length > 0 
  ? store.config.groups.map(g => ({ ...g }))
  : []);

const addGroup = () => {
  groups.push({ 
    name: '', 
    globs: '', 
    location: '' 
  });
};

const removeGroup = (index: number) => {
  groups.splice(index, 1);
};

const onNext = () => {
  // Validate that if a group is added, it has a name and globs
  const invalid = groups.some(g => !g.name || !g.globs);
  if (invalid) {
    alert('Every group must have a Name and Globs.');
    return;
  }
  
  store.config.groups = groups.map(g => ({ ...g }));
  store.nextStep();
};
</script>

<style scoped>
.group-list {
  max-height: 60vh;
  overflow-y: auto;
  padding: 0.5rem;
}

.card {
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.form-label {
  font-weight: 500;
}
</style>
