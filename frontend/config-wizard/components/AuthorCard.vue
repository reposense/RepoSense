<template>
  <div class="author-card">
    <div class="author-card-header">
      <span class="nested-card-title">{{ author.gitId || `Author #${index + 1}` }}</span>
      <button class="btn btn-danger" @click="emit('remove')">Remove</button>
    </div>
    <div class="author-card-body">
      <div class="form-row">
        <div class="form-group">
          <label class="form-label">Git Host ID <span class="required">*</span></label>
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
            @tag-added="() => emit('validate-emails', author.emails)"
            @tag-removed="() => emit('validate-emails', author.emails)"
          />
          <p v-if="emailError" class="field-error">{{ emailError }}</p>
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
