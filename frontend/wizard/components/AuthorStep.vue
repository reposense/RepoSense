<template>
  <wizard-step
    :step-number="2"
    title="Author Configuration"
    @back="store.prevStep()"
    @next="onNext"
  >
    <div class="author-list">
      <div v-for="(author, index) in authors" :key="index" class="author-item card mb-3">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h5 class="card-title mb-0">Author #{{ index + 1 }}</h5>
            <button class="btn btn-outline-danger btn-sm" @click="removeAuthor(index)">
              Remove
            </button>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Git Host ID <span class="text-danger">*</span></label>
              <input v-model="author.gitHostId" type="text" class="form-control" placeholder="e.g. johndoe" />
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Display Name</label>
              <input v-model="author.displayName" type="text" class="form-control" placeholder="e.g. John Doe" />
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">Associated Emails (Semicolon separated)</label>
            <input v-model="author.emails" type="text" class="form-control" placeholder="e.g. john@example.com;jdoe@gmail.com" />
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Git Author Names (Semicolon separated)</label>
              <input v-model="author.gitAuthorNames" type="text" class="form-control" placeholder="Names in .gitconfig" />
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">Author-specific Ignore Glob List</label>
              <input v-model="author.ignoreGlobList" type="text" class="form-control" placeholder="e.g. vendor/**" />
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">Repository (Optional - bound to all if empty)</label>
            <input v-model="author.location" type="text" class="form-control" placeholder="Repo URL or Path" />
          </div>
        </div>
      </div>

      <div class="d-flex gap-2">
        <button class="btn btn-outline-primary flex-grow-1" @click="addAuthor">
          Add Another Author
        </button>
        <button class="btn btn-outline-secondary" @click="onNext">
          Skip for now
        </button>
      </div>
      <p class="form-text mt-2">
        If no authors are specified, RepoSense will analyze all authors in the repositories.
      </p>
    </div>
  </wizard-step>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { store } from '../store';
import WizardStep from './WizardStep.vue';

const authors = reactive(store.config.authors.length > 0 
  ? store.config.authors.map(a => ({ ...a }))
  : []);

const addAuthor = () => {
  authors.push({ 
    gitHostId: '', 
    emails: '', 
    displayName: '', 
    gitAuthorNames: '', 
    ignoreGlobList: '',
    location: '', 
    branch: '' 
  });
};

const removeAuthor = (index: number) => {
  authors.splice(index, 1);
};

const onNext = () => {
  // Validate that if an author is added, they have a Git Host ID
  const invalid = authors.some(a => !a.gitHostId);
  if (invalid) {
    alert('Every author must have a Git Host ID.');
    return;
  }
  
  store.config.authors = authors.map(a => ({ ...a }));
  store.nextStep();
};
</script>

<style scoped>
.author-list {
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
