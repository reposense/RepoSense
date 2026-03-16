<template>
  <div class="nested-card">
    <div class="nested-card-header">
      <span class="nested-card-title">Branch: {{ branch.branch || '(default)' }}</span>
      <button v-if="canRemove" class="btn btn-danger" @click="emit('remove')">Remove</button>
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
          <label class="form-label">Since Date</label>
          <input type="date" v-model="branch.sinceDate" class="form-input" />
          <div v-if="branch.sinceDate" class="time-toggle">
            <button
              v-if="!branch.showSinceTime"
              class="btn btn-link"
              @click="branch.showSinceTime = true"
            >+ Add time</button>
            <div v-else class="time-row">
              <input type="time" v-model="branch.sinceTime" class="form-input form-input--time" />
              <button
                class="btn btn-link"
                @click="branch.showSinceTime = false; branch.sinceTime = ''"
              >Remove time</button>
            </div>
          </div>
        </div>
        <div class="form-group">
          <label class="form-label">Until Date</label>
          <input type="date" v-model="branch.untilDate" class="form-input" />
          <div v-if="branch.untilDate" class="time-toggle">
            <button
              v-if="!branch.showUntilTime"
              class="btn btn-link"
              @click="branch.showUntilTime = true"
            >+ Add time</button>
            <div v-else class="time-row">
              <input type="time" v-model="branch.untilTime" class="form-input form-input--time" />
              <button
                class="btn btn-link"
                @click="branch.showUntilTime = false; branch.untilTime = ''"
              >Remove time</button>
            </div>
          </div>
        </div>
      </div>

      <div class="form-row">
        <div class="form-group">
          <label class="form-label">Ignore Glob List</label>
          <tag-chip-input
            v-model="branch.ignoreGlobList"
            placeholder="e.g. node_modules/**"
            @tag-added="(tag) => emit('validate-glob', tag)"
            @tag-removed="(tag) => emit('clear-glob-error', tag)"
          />
          <p v-if="globError" class="field-error">{{ globError }}</p>
        </div>
        <div class="form-group">
          <label class="form-label">Ignore Authors List</label>
          <tag-chip-input
            v-model="branch.ignoreAuthorsList"
            placeholder="e.g. bot-user"
          />
        </div>
      </div>

      <div class="section-label authors-label">
        Authors
        <button class="btn btn-link" @click="addAuthor">+ Add Author</button>
      </div>

      <div v-if="branch.authors.length === 0" class="empty-hint">
        No authors configured — RepoSense will include all authors.
      </div>

      <author-card
        v-for="(author, ai) in branch.authors"
        :key="ai"
        :author="author"
        :index="ai"
        :email-error="emailErrors[String(ai)] || ''"
        @remove="removeAuthor(ai)"
        @validate-emails="(emails) => emit('validate-emails', emails, ai)"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { type LocalBranch, newAuthor } from '../types/wizard';
import TagChipInput from './TagChipInput.vue';
import AuthorCard from './AuthorCard.vue';

const props = defineProps<{
  branch: LocalBranch;
  canRemove: boolean;
  globError: string;
  emailErrors: Record<string, string>;
}>();

const emit = defineEmits<{
  remove: [];
  'validate-glob': [tag: string];
  'clear-glob-error': [tag: string];
  'validate-emails': [emails: string[], authorIndex: number];
  'remove-author': [authorIndex: number];
}>();

const addAuthor = () => props.branch.authors.push(newAuthor());
const removeAuthor = (i: number) => emit('remove-author', i);
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

.authors-label {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.form-input--time {
  width: auto;
}

.time-toggle {
  margin-top: 0.35rem;
}

.time-row {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}
</style>
