<template>
  <wizard-step
    :step-number="1"
    title="Repository Configuration"
    @next="onNext"
  >
    <div class="repo-list">
      <div v-for="(repo, index) in repos" :key="index" class="repo-item card mb-3">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-center mb-3">
            <h5 class="card-title mb-0">Repository #{{ index + 1 }}</h5>
            <button class="btn btn-outline-danger btn-sm" @click="removeRepo(index)" v-if="repos.length > 1">
              Remove
            </button>
          </div>

          <div class="mb-3">
            <label class="form-label">Repository Location (URL or Path) <span class="text-danger">*</span></label>
            <input
              v-model="repo.location"
              type="text"
              class="form-control"
              :class="{ 'is-invalid': repo.error }"
              placeholder="e.g. https://github.com/reposense/RepoSense.git"
              @blur="validateRepo(repo)"
            />
            <div v-if="repo.error" class="invalid-feedback">{{ repo.error }}</div>
            <div v-if="repo.validating" class="form-text">Validating...</div>
          </div>

          <div class="row">
            <div class="col-md-6 mb-3">
              <label class="form-label">Branch</label>
              <input v-model="repo.branch" type="text" class="form-control" placeholder="Default branch if empty" />
            </div>
            <div class="col-md-6 mb-3">
              <label class="form-label">File Formats</label>
              <input v-model="repo.fileFormats" type="text" class="form-control" placeholder="e.g. java;py;js" />
            </div>
          </div>

          <!-- Advanced Toggle -->
          <div class="mb-3">
            <button class="btn btn-link btn-sm p-0" @click="repo.showAdvanced = !repo.showAdvanced">
              {{ repo.showAdvanced ? 'Hide' : 'Show' }} Advanced Options
            </button>
          </div>

          <div v-if="repo.showAdvanced">
            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Ignore Glob List</label>
                <input v-model="repo.ignoreGlobList" type="text" class="form-control" placeholder="e.g. test/**;temp/**" />
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">Ignore Commits List</label>
                <input v-model="repo.ignoreCommitsList" type="text" class="form-control" placeholder="e.g. hash1;hash2" />
              </div>
            </div>

            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Ignore Authors List</label>
                <input v-model="repo.ignoreAuthorsList" type="text" class="form-control" placeholder="e.g. bot1;bot2" />
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">File Size Limit (Bytes)</label>
                <input v-model="repo.fileSizeLimit" type="number" class="form-control" placeholder="Default 500000" />
              </div>
            </div>

            <div class="row">
              <div class="col-md-6 mb-3">
                <label class="form-label">Since Date</label>
                <input v-model="repo.sinceDate" type="date" class="form-control" />
              </div>
              <div class="col-md-6 mb-3">
                <label class="form-label">Until Date</label>
                <input v-model="repo.untilDate" type="date" class="form-control" />
              </div>
            </div>

            <div class="row">
              <div class="col-md-4 mb-3 d-flex align-items-center">
                <div class="form-check">
                  <input v-model="repo.shallowCloning" type="checkbox" class="form-check-input" id="shallowCheck" />
                  <label class="form-check-label" for="shallowCheck">Shallow Cloning</label>
                </div>
              </div>
              <div class="col-md-4 mb-3 d-flex align-items-center">
                <div class="form-check">
                  <input v-model="repo.findPreviousAuthors" type="checkbox" class="form-check-input" id="blameCheck" />
                  <label class="form-check-label" for="blameCheck">Find Previous Authors</label>
                </div>
              </div>
              <div class="col-md-4 mb-3 d-flex align-items-center">
                <div class="form-check">
                  <input v-model="repo.ignoreStandaloneConfig" type="checkbox" class="form-check-input" id="standaloneCheck" />
                  <label class="form-check-label" for="standaloneCheck">Ignore Standalone Config</label>
                </div>
              </div>
            </div>

            <div class="row">
              <div class="col-md-6 mb-3 d-flex align-items-center">
                <div class="form-check">
                  <input v-model="repo.ignoreFileSizeLimit" type="checkbox" class="form-check-input" id="ignoreSizeLimitCheck" />
                  <label class="form-check-label" for="ignoreSizeLimitCheck">Ignore File Size Limit</label>
                </div>
              </div>
              <div class="col-md-6 mb-3 d-flex align-items-center">
                <div class="form-check">
                  <input v-model="repo.skipIgnoredFileAnalysis" type="checkbox" class="form-check-input" id="skipAnalysisCheck" />
                  <label class="form-check-label" for="skipAnalysisCheck">Skip Ignored File Analysis</label>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <button class="btn btn-primary w-100" @click="addRepo">
        Add Another Repository
      </button>
    </div>
  </wizard-step>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { store } from '../store';
import WizardStep from './WizardStep.vue';

const repos = reactive(store.config.repos.length > 0 
  ? store.config.repos.map(r => ({ ...r, validating: false, error: '', showAdvanced: false }))
  : [{ 
      location: '', 
      branch: '', 
      fileFormats: '',
      findPreviousAuthors: false,
      ignoreGlobList: '',
      ignoreStandaloneConfig: false,
      ignoreCommitsList: '',
      ignoreAuthorsList: '',
      shallowCloning: false,
      fileSizeLimit: '',
      ignoreFileSizeLimit: false,
      skipIgnoredFileAnalysis: false,
      sinceDate: '',
      untilDate: '',
      validating: false, 
      error: '',
      showAdvanced: false 
    }]);

const addRepo = () => {
  repos.push({ 
    location: '', 
    branch: '', 
    fileFormats: '',
    findPreviousAuthors: false,
    ignoreGlobList: '',
    ignoreStandaloneConfig: false,
    ignoreCommitsList: '',
    ignoreAuthorsList: '',
    shallowCloning: false,
    fileSizeLimit: '',
    ignoreFileSizeLimit: false,
    skipIgnoredFileAnalysis: false,
    sinceDate: '',
    untilDate: '',
    validating: false, 
    error: '',
    showAdvanced: false 
  });
};

const removeRepo = (index: number) => {
  repos.splice(index, 1);
};

const validateRepo = async (repo: any) => {
  if (!repo.location) return;
  
  repo.validating = true;
  repo.error = '';
  
  try {
    const response = await fetch('/api/validate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ location: repo.location })
    });
    const data = await response.json();
    if (!data.valid) {
      repo.error = data.error || 'Invalid repository location';
    }
  } catch (e) {
    repo.error = 'Connection error during validation';
  } finally {
    repo.validating = false;
  }
};

const onNext = () => {
  const allValid = repos.every(r => r.location && !r.error);
  if (allValid) {
    store.config.repos = repos.map(r => {
      const { validating, error, showAdvanced, ...cleanRepo } = r;
      return cleanRepo;
    });
    store.nextStep();
  } else {
    alert('Please provide valid repository locations.');
  }
};
</script>

<style scoped>
.repo-list {
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
