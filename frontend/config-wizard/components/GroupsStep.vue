<template lang="pug">
wizard-step(
  :step-number="3",
  title="Groups",
  :can-skip="true",
  @back="store.prevStep()",
  @next="onNext",
  @skip="onSkip"
)
  p.step-description
    | Groups classify files in a repository into named categories (e.g. frontend, backend, tests).
    | This step is optional.
  .empty-hint(v-if="repoGroups.length === 0") No repositories configured. Go back to Step 2 to add repositories.
  .card(v-for="(rg, ri) in repoGroups", :key="ri")
    .card-header
      span.card-title(:title="rg.repoUrl") {{ shortUrl(rg.repoUrl) }}
    .card-body
      .empty-hint(v-if="rg.groups.length === 0") No groups defined for this repository.
      .nested-card(v-for="(group, gi) in rg.groups", :key="gi")
        .nested-card-header
          span.nested-card-title {{ group.groupName || `Group #${gi + 1}` }}
          button.btn.btn-danger(@click="removeGroup(rg, gi)") Remove
        .nested-card-body
          .form-row
            .form-group
              label.form-label
                | Group Name
                span.required *
              input.form-input(v-model="group.groupName", placeholder="e.g. frontend")
            .form-group
              label.form-label
                | Glob Patterns
                span.required *
              tag-chip-input(
                v-model="group.globs",
                placeholder="e.g. src/frontend/**",
                @tag-added="(tag) => validateGlob(tag, `${ri}-${gi}`)"
              )
              p.field-error(v-if="globErrors[`${ri}-${gi}`]") {{ globErrors[`${ri}-${gi}`] }}
      button.btn.btn-secondary.add-group-btn(@click="addGroup(rg)") + Add Group
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
