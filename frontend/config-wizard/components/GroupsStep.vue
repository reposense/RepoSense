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
                <tag-chip-input
                  v-model="group.globs"
                  placeholder="e.g. src/frontend/**"
                  @tag-added="(tag) => validateGlob(tag, `${ri}-${gi}`)"
                />
                <p v-if="globErrors[`${ri}-${gi}`]" class="field-error">
                  {{ globErrors[`${ri}-${gi}`] }}
                </p>
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
import TagChipInput from './TagChipInput.vue';

interface LocalGroup {
  groupName: string;
  globs: string[];
}

interface LocalRepoGroups {
  repoUrl: string;
  groups: LocalGroup[];
}

const shortUrl = (url: string) => url.replace(/^https?:\/\//, '').replace(/\.git$/, '');

const repoGroups = reactive<LocalRepoGroups[]>(
  store.config.repos.map((r) => ({
    repoUrl: r.repo,
    groups: r.groups.map((g) => ({
      groupName: g['group-name'],
      globs: [...g.globs],
    })),
  })),
);

const globErrors = reactive<Record<string, string>>({});

const addGroup = (rg: LocalRepoGroups) => rg.groups.push({ groupName: '', globs: [] });
const removeGroup = (rg: LocalRepoGroups, i: number) => rg.groups.splice(i, 1);

// Tier 1: glob syntax validation (backend)
const validateGlob = async (pattern: string, key: string) => {
  try {
    const resp = await fetch('/api/validate-glob', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ pattern }),
    });
    const data = await resp.json();
    if (!data.valid) {
      globErrors[key] = `Invalid pattern "${pattern}": ${data.error}`;
    } else {
      delete globErrors[key];
    }
  } catch {
    // non-critical, silently ignore
  }
};

const saveAndAdvance = () => {
  repoGroups.forEach((rg, i) => {
    if (store.config.repos[i]) {
      store.config.repos[i].groups = rg.groups.map((g) => ({
        'group-name': g.groupName,
        globs: [...g.globs],
      }));
    }
  });
  store.nextStep();
};

const onNext = () => {
  if (Object.keys(globErrors).length > 0) {
    alert('Please fix invalid glob patterns before proceeding.');
    return;
  }
  // Tier 1: required fields
  const missingFields = repoGroups.some((rg) =>
    rg.groups.some((g) => !g.groupName.trim() || g.globs.length === 0),
  );
  if (missingFields) {
    alert('Every group must have a name and at least one glob pattern.');
    return;
  }
  // Tier 2: unique group names per repo
  for (const rg of repoGroups) {
    const names = rg.groups.map((g) => g.groupName.trim());
    if (new Set(names).size !== names.length) {
      alert(`Repository "${rg.repoUrl}" has duplicate group names.`);
      return;
    }
  }
  saveAndAdvance();
};

const onSkip = () => {
  store.config.repos.forEach((r) => { r.groups = []; });
  store.nextStep();
};
</script>

<style scoped lang="scss">
@import '../styles/variables';

.step-description {
  font-size: 0.85rem;
  color: $color-text-secondary;
  margin-bottom: 1.25rem;
}

.add-group-btn {
  width: 100%;
  margin-top: 0.25rem;
}
</style>
