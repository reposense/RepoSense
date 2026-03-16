<template lang="pug">
.nested-card
  .nested-card-header
    span.nested-card-title Branch: {{ branch.branch || '(default)' }}
    button.btn.btn-danger(v-if="canRemove", @click="emit('remove')") Remove
  .nested-card-body
    .form-row
      .form-group
        label.form-label Branch Name
        input.form-input(
          v-model="branch.branch",
          :class="{ 'is-invalid': branch.branch.includes(' ') }",
          placeholder="e.g. main (leave empty for default)"
        )
        p.field-error(v-if="branch.branch.includes(' ')") Branch name cannot contain spaces
      .form-group
        label.form-label File Size Limit (bytes)
        input.form-input(
          v-model="branch.fileSizeLimit",
          type="number",
          placeholder="e.g. 500000",
          min="0"
        )
    .form-group
      label.form-label Blurb
      input.form-input(v-model="branch.blurb", placeholder="Optional description for this branch")
    .form-row
      .form-group
        label.form-label Since Date
        input.form-input(
          type="date",
          v-model="branch.sinceDate",
          :class="{ 'is-invalid': dateRangeError }"
        )
        .time-toggle(v-if="branch.sinceDate")
          button.btn.btn-link(v-if="!branch.showSinceTime", @click="branch.showSinceTime = true") + Add time
          .time-row(v-else)
            input.form-input.form-input--time(type="time", v-model="branch.sinceTime")
            button.btn.btn-link(@click="branch.showSinceTime = false; branch.sinceTime = ''") Remove time
      .form-group
        label.form-label Until Date
        input.form-input(
          type="date",
          v-model="branch.untilDate",
          :class="{ 'is-invalid': dateRangeError }"
        )
        .time-toggle(v-if="branch.untilDate")
          button.btn.btn-link(v-if="!branch.showUntilTime", @click="branch.showUntilTime = true") + Add time
          .time-row(v-else)
            input.form-input.form-input--time(type="time", v-model="branch.untilTime")
            button.btn.btn-link(@click="branch.showUntilTime = false; branch.untilTime = ''") Remove time
    p.field-error(v-if="dateRangeError") {{ dateRangeError }}
    .form-row
      .form-group
        label.form-label Ignore Glob List
        tag-chip-input(
          v-model="branch.ignoreGlobList",
          placeholder="e.g. node_modules/**",
          @tag-added="(tag) => emit('validate-glob', tag)",
          @tag-removed="(tag) => emit('clear-glob-error', tag)"
        )
        p.field-error(v-if="globError") {{ globError }}
      .form-group
        label.form-label Ignore Authors List
        tag-chip-input(v-model="branch.ignoreAuthorsList", placeholder="e.g. bot-user")
    .section-label.authors-label
      | Authors
      button.btn.btn-link(@click="addAuthor") + Add Author
    .empty-hint(v-if="branch.authors.length === 0") No authors configured — RepoSense will include all authors.
    author-card(
      v-for="(author, ai) in branch.authors",
      :key="ai",
      :author="author",
      :index="ai",
      :email-error="emailErrors[String(ai)] || ''",
      @remove="removeAuthor(ai)",
      @validate-emails="(emails) => emit('validate-emails', emails, ai)"
    )
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { type LocalBranch, newAuthor } from '../types/wizard';
import TagChipInput from './TagChipInput.vue';
import AuthorCard from './AuthorCard.vue';

const props = defineProps<{
  branch: LocalBranch;
  canRemove: boolean;
  globError: string;
  emailErrors: Record<string, string>;
}>();

const dateRangeError = computed(() => {
  const { sinceDate, sinceTime, untilDate, untilTime } = props.branch;
  if (!sinceDate || !untilDate) return '';
  if (sinceDate > untilDate) return 'Since date must be on or before until date';
  if (sinceDate === untilDate && sinceTime && untilTime && sinceTime > untilTime) {
    return 'Since time must be on or before until time on the same date';
  }
  return '';
});

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
