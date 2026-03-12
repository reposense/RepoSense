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
          <label class="form-label">
            Repository URL <span class="required">*</span>
          </label>
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
            <span class="nested-card-title">
              Branch: {{ branch.branch || '(default)' }}
            </span>
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
                  placeholder="e.g. main (leave empty for default)"
                />
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
                <input
                  v-model="branch.ignoreGlobList"
                  class="form-input"
                  placeholder="e.g. node_modules/**;build/** (semicolon-separated)"
                />
              </div>
              <div class="form-group">
                <label class="form-label">Ignore Authors List</label>
                <input
                  v-model="branch.ignoreAuthorsList"
                  class="form-input"
                  placeholder="e.g. bot-user;ci-bot (semicolon-separated)"
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
                <button class="btn btn-danger" @click="removeAuthor(branch, ai)">
                  Remove
                </button>
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
                      placeholder="e.g. johndoe"
                    />
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
                    <input
                      v-model="author.emails"
                      class="form-input"
                      placeholder="e.g. john@example.com;jdoe@work.com"
                    />
                    <p class="field-hint">Semicolon-separated</p>
                  </div>
                  <div class="form-group">
                    <label class="form-label">Git Author Names</label>
                    <input
                      v-model="author.gitAuthorName"
                      class="form-input"
                      placeholder="e.g. john;John Doe"
                    />
                    <p class="field-hint">Semicolon-separated</p>
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

// Local form types (multi-value fields stored as semicolon-separated strings)
interface LocalAuthor {
  gitId: string;
  displayName: string;
  emails: string;
  gitAuthorName: string;
}

interface LocalBranch {
  branch: string;
  blurb: string;
  ignoreGlobList: string;
  ignoreAuthorsList: string;
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

const split = (s: string) => s.split(';').map((t) => t.trim()).filter(Boolean);

const newAuthor = (): LocalAuthor => ({
  gitId: '',
  displayName: '',
  emails: '',
  gitAuthorName: '',
});

const newBranch = (): LocalBranch => ({
  branch: '',
  blurb: '',
  ignoreGlobList: '',
  ignoreAuthorsList: '',
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

// Initialise from store if navigating back
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
      ignoreGlobList: b['ignore-glob-list'].join(';'),
      ignoreAuthorsList: b['ignore-authors-list'].join(';'),
      fileSizeLimit: b['file-size-limit'] != null ? String(b['file-size-limit']) : '',
      authors: b.authors.map((a) => ({
        gitId: a['author-git-host-id'],
        displayName: a['author-display-name'],
        emails: a['author-emails'].join(';'),
        gitAuthorName: a['author-git-author-name'].join(';'),
      })),
    })),
  }));
};

const repos = reactive<LocalRepo[]>(initRepos());

// Mutation helpers
const addRepo = () => repos.push(newRepo());
const removeRepo = (i: number) => repos.splice(i, 1);
const addBranch = (repo: LocalRepo) => repo.branches.push(newBranch());
const removeBranch = (repo: LocalRepo, i: number) => repo.branches.splice(i, 1);
const addAuthor = (branch: LocalBranch) => branch.authors.push(newAuthor());
const removeAuthor = (branch: LocalBranch, i: number) => branch.authors.splice(i, 1);

// URL validation
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

// Convert local form state → store structure and advance
const onNext = () => {
  const anyEmpty = repos.some((r) => !r.repo.trim());
  if (anyEmpty) {
    alert('Every repository must have a URL.');
    return;
  }
  const anyInvalidAuthor = repos.some((r) =>
    r.branches.some((b) => b.authors.some((a) => !a.gitId.trim())),
  );
  if (anyInvalidAuthor) {
    alert('Every author must have a Git Host ID.');
    return;
  }

  store.config.repos = repos.map((r) => ({
    repo: r.repo.trim(),
    // Preserve groups from previous step visits
    groups: store.config.repos.find((sr) => sr.repo === r.repo)?.groups ?? [],
    branches: r.branches.map((b) => ({
      branch: b.branch,
      blurb: b.blurb,
      'ignore-glob-list': split(b.ignoreGlobList),
      'ignore-authors-list': split(b.ignoreAuthorsList),
      'file-size-limit': b.fileSizeLimit ? Number(b.fileSizeLimit) : null,
      authors: b.authors.map((a) => ({
        'author-git-host-id': a.gitId.trim(),
        'author-display-name': a.displayName,
        'author-emails': split(a.emails),
        'author-git-author-name': split(a.gitAuthorName),
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
