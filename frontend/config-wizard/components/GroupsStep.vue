<template>
  <wizard-step
    :step-number="3"
    title="Groups"
    :can-skip="true"
    @back="store.prevStep()"
    @next="onNext"
    @skip="onSkip"
  >
    <p class="step-description">
      Groups classify files in a repository into named categories (e.g. frontend, backend, tests).
      This step is optional.
    </p>

    <div v-if="repoGroups.length === 0" class="empty-hint">
      No repositories configured. Go back to Step 2 to add repositories.
    </div>

    <div v-for="(rg, ri) in repoGroups" :key="ri" class="card">
      <div class="card-header">
        <span class="card-title" :title="rg.repoUrl">{{ shortUrl(rg.repoUrl) }}</span>
      </div>
      <div class="card-body">
        <div v-if="rg.groups.length === 0" class="empty-hint">
          No groups defined for this repository.
        </div>

        <div v-for="(group, gi) in rg.groups" :key="gi" class="nested-card">
          <div class="nested-card-header">
            <span class="nested-card-title">{{ group.groupName || `Group #${gi + 1}` }}</span>
            <button class="btn btn-danger" @click="removeGroup(rg, gi)">Remove</button>
          </div>
          <div class="nested-card-body">
            <div class="form-row">
              <div class="form-group">
                <label class="form-label">
                  Group Name <span class="required">*</span>
                </label>
                <input
                  v-model="group.groupName"
                  class="form-input"
                  placeholder="e.g. frontend"
                />
              </div>
              <div class="form-group">
                <label class="form-label">
                  Glob Patterns <span class="required">*</span>
                </label>
                <input
                  v-model="group.globs"
                  class="form-input"
                  placeholder="e.g. src/frontend/**;**.vue"
                />
                <p class="field-hint">Semicolon-separated</p>
              </div>
            </div>
          </div>
        </div>

        <button class="btn btn-secondary add-group-btn" @click="addGroup(rg)">
          + Add Group
        </button>
      </div>
    </div>
  </wizard-step>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { store } from '../store';
import WizardStep from './WizardStep.vue';

interface LocalGroup {
  groupName: string;
  globs: string;
}

interface LocalRepoGroups {
  repoUrl: string;
  groups: LocalGroup[];
}

const split = (s: string) => s.split(';').map((t) => t.trim()).filter(Boolean);

const shortUrl = (url: string) => {
  try {
    return url.replace(/^https?:\/\//, '').replace(/\.git$/, '');
  } catch {
    return url;
  }
};

// Initialise from store (preserves groups if navigating back)
const repoGroups = reactive<LocalRepoGroups[]>(
  store.config.repos.map((r) => ({
    repoUrl: r.repo,
    groups: r.groups.map((g) => ({
      groupName: g['group-name'],
      globs: g.globs.join(';'),
    })),
  })),
);

const addGroup = (rg: LocalRepoGroups) => rg.groups.push({ groupName: '', globs: '' });
const removeGroup = (rg: LocalRepoGroups, i: number) => rg.groups.splice(i, 1);

const saveAndAdvance = () => {
  repoGroups.forEach((rg, i) => {
    if (store.config.repos[i]) {
      store.config.repos[i].groups = rg.groups.map((g) => ({
        'group-name': g.groupName,
        globs: split(g.globs),
      }));
    }
  });
  store.nextStep();
};

const onNext = () => {
  const invalid = repoGroups.some((rg) =>
    rg.groups.some((g) => !g.groupName.trim() || !g.globs.trim()),
  );
  if (invalid) {
    alert('Every group must have a name and at least one glob pattern.');
    return;
  }
  saveAndAdvance();
};

const onSkip = () => {
  // Clear all groups before advancing
  store.config.repos.forEach((r) => { r.groups = []; });
  store.nextStep();
};
</script>

<style scoped>
.step-description {
  font-size: 0.85rem;
  color: #666;
  margin-bottom: 1.25rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
}

.nested-card {
  border: 1px solid #e8e8e8;
  border-radius: 4px;
  margin-bottom: 0.75rem;
  background: #fafafa;
}

.nested-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.4rem 0.75rem;
  background: #f0f0f0;
  border-bottom: 1px solid #e8e8e8;
  border-radius: 4px 4px 0 0;
}

.nested-card-title {
  font-size: 0.8rem;
  font-weight: 600;
  color: #555;
}

.nested-card-body {
  padding: 0.75rem;
}

.empty-hint {
  font-size: 0.8rem;
  color: #aaa;
  font-style: italic;
  margin-bottom: 0.75rem;
}

.add-group-btn {
  width: 100%;
  margin-top: 0.25rem;
}
</style>
