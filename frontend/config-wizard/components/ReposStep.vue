<template>
  <wizard-step
    :step-number="2"
    title="Repos & Branches"
    @back="store.prevStep()"
    @next="onNext"
  >
    <div v-for="(repo, ri) in repos" :key="ri" class="card">
      <div class="card-header">
        <span class="card-title">Repository #{{ ri + 1 }}</span>
        <button v-if="repos.length > 1" class="btn btn-danger" @click="removeRepo(ri)">
          Remove
        </button>
      </div>
      <div class="card-body">

        <!-- Repo URL -->
        <div class="form-group">
          <label class="form-label">Repository URL <span class="required">*</span></label>
          <input
            v-model="repo.repo"
            class="form-input"
            :class="{ 'is-invalid': repo.error, 'is-valid': repo.valid }"
            placeholder="e.g. https://github.com/reposense/RepoSense.git"
            @blur="validateRepo(repo)"
          />
          <p v-if="repo.error" class="field-error">{{ repo.error }}</p>
          <p v-else-if="repo.validating" class="field-hint">Validating...</p>
          <p v-else-if="repo.valid" class="field-valid">✓ Valid repository location</p>
        </div>

        <!-- Branches -->
        <div class="section-label">Branches</div>
        <div v-for="(branch, bi) in repo.branches" :key="bi" class="nested-card">
          <div class="nested-card-header">
            <span class="nested-card-title">Branch: {{ branch.branch || '(default)' }}</span>
            <button
              v-if="repo.branches.length > 1"
              class="btn btn-danger"
              @click="removeBranch(repo, bi)"
            >
              Remove
            </button>
          </div>
          <div class="nested-card-body">

            <div class="form-row">
              <div class="form-group">
                <label class="form-label">Branch Name</label>
                <input
                  v-model="branch.branch"
                  class="form-input"
                  :class="{ 'is-invalid': branch.branch.includes(' ') }"
                  placeholder="e.g. main (leave empty for default)"
                />
                <p v-if="branch.branch.includes(' ')" class="field-error">
                  Branch name cannot contain spaces
                </p>
              </div>
              <div class="form-group">
                <label class="form-label">File Size Limit (bytes)</label>
                <input
                  v-model="branch.fileSizeLimit"
                  type="number"
                  class="form-input"
                  placeholder="e.g. 500000"
                  min="0"
                />
              </div>
            </div>

            <div class="form-group">
              <label class="form-label">Blurb</label>
              <input
                v-model="branch.blurb"
                class="form-input"
                placeholder="Optional description for this branch"
              />
            </div>

            <div class="form-row">
              <div class="form-group">
                <label class="form-label">Ignore Glob List</label>
                <tag-chip-input
                  v-model="branch.ignoreGlobList"
                  placeholder="e.g. node_modules/**"
                  @tag-added="(tag) => validateGlob(tag, `${ri}-${bi}`)"
                />
                <p v-if="globErrors[`${ri}-${bi}`]" class="field-error">
                  {{ globErrors[`${ri}-${bi}`] }}
                </p>
              </div>
              <div class="form-group">
                <label class="form-label">Ignore Authors List</label>
                <tag-chip-input
                  v-model="branch.ignoreAuthorsList"
                  placeholder="e.g. bot-user"
                />
              </div>
            </div>

            <!-- Authors -->
            <div class="section-label authors-label">
              Authors
              <button class="btn btn-link" @click="addAuthor(branch)">+ Add Author</button>
            </div>

            <div v-if="branch.authors.length === 0" class="empty-hint">
              No authors configured — RepoSense will include all authors.
            </div>

            <div v-for="(author, ai) in branch.authors" :key="ai" class="author-card">
              <div class="author-card-header">
                <span class="nested-card-title">
                  {{ author.gitId || `Author #${ai + 1}` }}
                </span>
                <button class="btn btn-danger" @click="removeAuthor(branch, ai)">Remove</button>
              </div>
              <div class="author-card-body">
                <div class="form-row">
                  <div class="form-group">
                    <label class="form-label">
                      Git Host ID <span class="required">*</span>
                    </label>
                    <input
                      v-model="author.gitId"
                      class="form-input"
                      :class="{ 'is-invalid': author.gitId && author.gitId.includes(' ') }"
                      placeholder="e.g. johndoe"
                    />
                    <p v-if="author.gitId && author.gitId.includes(' ')" class="field-error">
                      Git Host ID cannot contain spaces
                    </p>
                  </div>
                  <div class="form-group">
                    <label class="form-label">Display Name</label>
                    <input
                      v-model="author.displayName"
                      class="form-input"
                      placeholder="e.g. John Doe"
                    />
                  </div>
                </div>
                <div class="form-row">
                  <div class="form-group">
                    <label class="form-label">Emails</label>
                    <tag-chip-input
                      v-model="author.emails"
                      placeholder="e.g. john@example.com"
                      @tag-added="(tag) => validateEmail(tag, `${ri}-${bi}-${ai}`)"
                    />
                    <p v-if="emailErrors[`${ri}-${bi}-${ai}`]" class="field-error">
                      {{ emailErrors[`${ri}-${bi}-${ai}`] }}
                    </p>
                  </div>
                  <div class="form-group">
                    <label class="form-label">Git Author Names</label>
                    <tag-chip-input
                      v-model="author.gitAuthorName"
                      placeholder="e.g. john"
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <button class="btn btn-secondary add-branch-btn" @click="addBranch(repo)">
          + Add Branch
        </button>
      </div>
    </div>

    <button class="btn btn-secondary add-repo-btn" @click="addRepo">
      + Add Repository
    </button>
  </wizard-step>
</template>

<script setup lang="ts">
import { reactive } from 'vue';
import { store } from '../store';
import WizardStep from './WizardStep.vue';
import TagChipInput from './TagChipInput.vue';

interface LocalAuthor {
  gitId: string;
  displayName: string;
  emails: string[];
  gitAuthorName: string[];
}

interface LocalBranch {
  branch: string;
  blurb: string;
  ignoreGlobList: string[];
  ignoreAuthorsList: string[];
  fileSizeLimit: string;
  authors: LocalAuthor[];
}

interface LocalRepo {
  repo: string;
  error: string;
  valid: boolean;
  validating: boolean;
  branches: LocalBranch[];
}

const newAuthor = (): LocalAuthor => ({
  gitId: '',
  displayName: '',
  emails: [],
  gitAuthorName: [],
});

const newBranch = (): LocalBranch => ({
  branch: '',
  blurb: '',
  ignoreGlobList: [],
  ignoreAuthorsList: [],
  fileSizeLimit: '',
  authors: [],
});

const newRepo = (): LocalRepo => ({
  repo: '',
  error: '',
  valid: false,
  validating: false,
  branches: [newBranch()],
});

const initRepos = (): LocalRepo[] => {
  if (store.config.repos.length === 0) return [newRepo()];
  return store.config.repos.map((r) => ({
    repo: r.repo,
    error: '',
    valid: true,
    validating: false,
    branches: r.branches.map((b) => ({
      branch: b.branch,
      blurb: b.blurb,
      ignoreGlobList: [...b['ignore-glob-list']],
      ignoreAuthorsList: [...b['ignore-authors-list']],
      fileSizeLimit: b['file-size-limit'] != null ? String(b['file-size-limit']) : '',
      authors: b.authors.map((a) => ({
        gitId: a['author-git-host-id'],
        displayName: a['author-display-name'],
        emails: [...a['author-emails']],
        gitAuthorName: [...a['author-git-author-name']],
      })),
    })),
  }));
};

const repos = reactive<LocalRepo[]>(initRepos());

// Validation error state
// key: `${ri}-${bi}` for branch glob errors
const globErrors = reactive<Record<string, string>>({});
// key: `${ri}-${bi}-${ai}` for author email errors
const emailErrors = reactive<Record<string, string>>({});

// Mutation helpers
const addRepo = () => repos.push(newRepo());
const removeRepo = (i: number) => repos.splice(i, 1);
const addBranch = (repo: LocalRepo) => repo.branches.push(newBranch());
const removeBranch = (repo: LocalRepo, i: number) => repo.branches.splice(i, 1);
const addAuthor = (branch: LocalBranch) => branch.authors.push(newAuthor());
const removeAuthor = (branch: LocalBranch, i: number) => branch.authors.splice(i, 1);

// Tier 1: URL validation (backend)
const validateRepo = async (repo: LocalRepo) => {
  if (!repo.repo) return;
  repo.validating = true;
  repo.error = '';
  repo.valid = false;
  try {
    const resp = await fetch('/api/validate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ location: repo.repo }),
    });
    const data = await resp.json();
    if (data.valid) {
      repo.valid = true;
    } else {
      repo.error = data.error || 'Invalid repository location';
    }
  } catch {
    repo.error = 'Could not validate — server unreachable';
  } finally {
    repo.validating = false;
  }
};

// Tier 1: Glob syntax validation (backend) — called when a tag is added
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

// Tier 1: Email format validation (frontend regex) — called when a tag is added
const EMAIL_RE = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const validateEmail = (email: string, key: string) => {
  if (!EMAIL_RE.test(email)) {
    emailErrors[key] = `"${email}" is not a valid email address`;
  } else {
    delete emailErrors[key];
  }
};

const onNext = () => {
  // Tier 1: required fields
  if (repos.some((r) => !r.repo.trim())) {
    alert('Every repository must have a URL.');
    return;
  }
  if (repos.some((r) => r.branches.some((b) => b.branch.includes(' ')))) {
    alert('Branch names cannot contain spaces.');
    return;
  }
  if (repos.some((r) => r.branches.some((b) =>
    b.authors.some((a) => !a.gitId.trim() || a.gitId.includes(' ')),
  ))) {
    alert('Every author must have a valid Git Host ID (no spaces).');
    return;
  }
  if (Object.keys(globErrors).length > 0) {
    alert('Please fix invalid glob patterns before proceeding.');
    return;
  }
  if (Object.keys(emailErrors).length > 0) {
    alert('Please fix invalid email addresses before proceeding.');
    return;
  }

  // Tier 2: duplicate checks
  const urls = repos.map((r) => r.repo.trim());
  if (new Set(urls).size !== urls.length) {
    alert('Duplicate repository URLs are not allowed.');
    return;
  }
  for (const repo of repos) {
    const branchNames = repo.branches.map((b) => b.branch.trim());
    if (new Set(branchNames).size !== branchNames.length) {
      alert(`Repository "${repo.repo}" has duplicate branch names.`);
      return;
    }
    for (const branch of repo.branches) {
      const authorIds = branch.authors.map((a) => a.gitId.trim());
      if (new Set(authorIds).size !== authorIds.length) {
        alert(`Branch "${branch.branch || 'default'}" in "${repo.repo}" has duplicate author IDs.`);
        return;
      }
    }
  }

  store.config.repos = repos.map((r) => ({
    repo: r.repo.trim(),
    groups: store.config.repos.find((sr) => sr.repo === r.repo)?.groups ?? [],
    branches: r.branches.map((b) => ({
      branch: b.branch.trim() || null,
      blurb: b.blurb || null,
      'ignore-glob-list': [...b.ignoreGlobList],
      'ignore-authors-list': [...b.ignoreAuthorsList],
      'file-size-limit': b.fileSizeLimit ? Number(b.fileSizeLimit) : null,
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

<style scoped>
.section-label {
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: #888;
  margin: 1rem 0 0.5rem;
}

.authors-label {
  display: flex;
  align-items: center;
  justify-content: space-between;
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

.author-card {
  border: 1px solid #ddeedd;
  border-radius: 4px;
  margin-bottom: 0.5rem;
  background: #f0faf5;
}

.author-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.4rem 0.75rem;
  background: #e0f5ec;
  border-bottom: 1px solid #ddeedd;
  border-radius: 4px 4px 0 0;
}

.author-card-body {
  padding: 0.75rem;
}

.empty-hint {
  font-size: 0.8rem;
  color: #aaa;
  font-style: italic;
  margin-bottom: 0.5rem;
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
