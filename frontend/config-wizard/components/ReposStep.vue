<template lang="pug">
wizard-step(:step-number="2", title="Repos & Branches", @back="store.prevStep()", @next="onNext")
  .card(v-for="(repo, ri) in repos", :key="ri")
    .card-header
      span.card-title Repository \#{{ ri + 1 }}
      button.btn.btn-danger(v-if="repos.length > 1", @click="removeRepo(ri)") Remove
    .card-body
      //- Repo URL
      .form-group
        label.form-label
          | Repository URL
          span.required *
        input.form-input(
          v-model="repo.repo",
          :class="{ 'is-invalid': repo.error, 'is-valid': repo.valid }",
          placeholder="e.g. https://github.com/reposense/RepoSense.git",
          @blur="validateRepo(repo)"
        )
        p.field-error(v-if="repo.error") {{ repo.error }}
        p.field-hint(v-else-if="repo.validating") Validating...
        p.field-valid(v-else-if="repo.valid") ✓ Valid repository location
      //- Branches
      .section-label Branches
      branch-card(
        v-for="(branch, bi) in repo.branches",
        :key="bi",
        :branch="branch",
        :can-remove="repo.branches.length > 1",
        :glob-error="getBranchGlobError(ri, bi)",
        :email-errors="getBranchEmailErrors(ri, bi)",
        @remove="removeBranch(repo, ri, bi)",
        @validate-glob="(tag) => validateGlob(tag, ri, bi)",
        @clear-glob-error="(tag) => clearGlobError(tag, ri, bi)",
        @validate-emails="(emails, ai) => validateAllEmails(emails, ri, bi, ai)",
        @remove-author="(ai) => removeAuthor(ri, bi, ai)"
      )
      button.btn.btn-secondary.add-branch-btn(@click="addBranch(repo)") + Add Branch
  button.btn.btn-secondary.add-repo-btn(@click="addRepo") + Add Repository
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { store } from '../store';
import { type LocalRepo, newBranch, newRepo } from '../types/wizard';
import { parseStoredDate, toStoredDate } from '../utils/dateConversion';
import { useReposValidation } from '../composables/useReposValidation';
import WizardStep from './WizardStep.vue';
import BranchCard from './BranchCard.vue';

const initRepos = (): LocalRepo[] => {
  if (store.config.repos.length === 0) return [newRepo()];
  return store.config.repos.map((r) => ({
    repo: r.repo,
    error: '',
    valid: true,
    validating: false,
    branches: r.branches.map((b) => {
      const since = parseStoredDate(b.since);
      const until = parseStoredDate(b.until);
      return {
        branch: b.branch,
        blurb: b.blurb,
        ignoreGlobList: [...b['ignore-glob-list']],
        ignoreAuthorsList: [...b['ignore-authors-list']],
        fileSizeLimit: b['file-size-limit'] != null ? String(b['file-size-limit']) : '',
        sinceDate: since.date,
        sinceTime: since.time,
        showSinceTime: !!since.time,
        untilDate: until.date,
        untilTime: until.time,
        showUntilTime: !!until.time,
        authors: b.authors.map((a) => ({
          gitId: a['author-git-host-id'],
          displayName: a['author-display-name'],
          emails: [...a['author-emails']],
          gitAuthorName: [...a['author-git-author-name']],
        })),
      };
    }),
  }));
};

const repos = reactive<LocalRepo[]>(initRepos());

const {
  getBranchGlobError,
  getBranchEmailErrors,
  cleanupOnRepoRemove,
  cleanupOnBranchRemove,
  cleanupOnAuthorRemove,
  validateRepo,
  validateGlob,
  clearGlobError,
  validateAllEmails,
  getOnNextError,
} = useReposValidation(repos);

// Mutation helpers — cleanup runs before splice so indices are still valid during key deletion.
const addRepo = () => repos.push(newRepo());
const removeRepo = (i: number) => { cleanupOnRepoRemove(i); repos.splice(i, 1); };
const addBranch = (repo: LocalRepo) => repo.branches.push(newBranch());
const removeBranch = (repo: LocalRepo, ri: number, bi: number) => {
  cleanupOnBranchRemove(ri, bi);
  repo.branches.splice(bi, 1);
};
const removeAuthor = (ri: number, bi: number, ai: number) => {
  cleanupOnAuthorRemove(ri, bi, ai);
  repos[ri].branches[bi].authors.splice(ai, 1);
};

const onNext = () => {
  const error = getOnNextError();
  if (error) {
    alert(error);
    return;
  }

  store.config.repos = repos.map((r) => ({
    repo: r.repo.trim(),
    groups: store.config.repos.find((sr) => sr.repo === r.repo)?.groups ?? [],
    branches: r.branches.map((b) => ({
      // null causes ReportBranchData to default to "HEAD" (see ReportBranchData.DEFAULT_BRANCH)
      branch: b.branch.trim() || null,
      blurb: b.blurb || null,
      'ignore-glob-list': [...b.ignoreGlobList],
      'ignore-authors-list': [...b.ignoreAuthorsList],
      // fileSizeLimit is stored as a string in the form input; converted to number here so
      // Jackson can deserialize it into ReportBranchData's Long field. null causes
      // ReportBranchData to default to DEFAULT_FILE_SIZE_LIMIT (1000000L).
      'file-size-limit': b.fileSizeLimit ? Number(b.fileSizeLimit) : null,
      since: toStoredDate(b.sinceDate, b.sinceTime),
      until: toStoredDate(b.untilDate, b.untilTime),
      authors: b.authors.map((a) => ({
        'author-git-host-id': a.gitId.trim(),
        'author-display-name': a.displayName || null,
        'author-emails': [...a.emails],
        'author-git-author-name': [...a.gitAuthorName],
      })),
    })),
  }));

  store.nextStep();
};
</script>

<style scoped lang="scss">
@import '../styles/variables';

.section-label {
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: $color-text-hint;
  margin: 1rem 0 0.5rem;
}

.add-branch-btn {
  margin-top: 0.75rem;
  width: 100%;
}

.add-repo-btn {
  width: 100%;
  margin-top: 0.5rem;
}
</style>
