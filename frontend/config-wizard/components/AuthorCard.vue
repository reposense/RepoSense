<template lang="pug">
.author-card
  .author-card-header
    span.nested-card-title {{ author.gitId || `Author #${index + 1}` }}
    button.btn.btn-danger(@click="emit('remove')") Remove
  .author-card-body
    .form-row
      .form-group
        label.form-label
          | Git Host ID
          span.required *
        input.form-input(
          v-model="author.gitId",
          :class="{ 'is-invalid': author.gitId && author.gitId.includes(' ') }",
          placeholder="e.g. johndoe"
        )
        p.field-error(v-if="author.gitId && author.gitId.includes(' ')") Git Host ID cannot contain spaces
      .form-group
        label.form-label Display Name
        input.form-input(v-model="author.displayName", placeholder="e.g. John Doe")
    .form-row
      .form-group
        label.form-label Emails
        tag-chip-input(
          v-model="author.emails",
          placeholder="e.g. john@example.com",
          @tag-added="() => emit('validate-emails', author.emails)",
          @tag-removed="() => emit('validate-emails', author.emails)"
        )
        p.field-error(v-if="emailError") {{ emailError }}
      .form-group
        label.form-label Git Author Names
        tag-chip-input(v-model="author.gitAuthorName", placeholder="e.g. john")
</template>

<script setup lang="ts">
import { type LocalAuthor } from '../types/wizard';
import TagChipInput from './TagChipInput.vue';

const props = defineProps<{
  author: LocalAuthor;
  index: number;
  emailError: string;
}>();

const emit = defineEmits<{
  remove: [];
  'validate-emails': [emails: string[]];
}>();
</script>

<style scoped lang="scss">
@import '../styles/variables';

.author-card {
  border: 1px solid mui-color('blue-grey', '200');
  border-radius: 4px;
  margin-bottom: 0.5rem;
  background: $color-primary-light;
}

.author-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.4rem 0.75rem;
  background: mui-color('blue-grey', '100');
  border-bottom: 1px solid mui-color('blue-grey', '200');
  border-radius: 4px 4px 0 0;
}

.author-card-body {
  padding: 0.75rem;
}
</style>
