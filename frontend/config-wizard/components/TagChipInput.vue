<template>
  <div class="tag-chip-input" :class="{ focused: isFocused }" @click="focusInput">
    <span v-for="(tag, i) in modelValue" :key="i" class="chip">
      {{ tag }}
      <button type="button" class="chip-remove" @click.stop="remove(i)">×</button>
    </span>
    <input
      ref="inputRef"
      v-model="inputValue"
      class="chip-input"
      :placeholder="modelValue.length === 0 ? (placeholder ?? '') : ''"
      @keydown.enter.prevent="add"
      @keydown="handleKey"
      @blur="onBlur"
      @focus="isFocused = true"
    />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const props = defineProps<{
  modelValue: string[];
  placeholder?: string;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: string[]];
  'tag-added': [value: string];
}>();

const inputValue = ref('');
const isFocused = ref(false);
const inputRef = ref<HTMLInputElement | null>(null);

const focusInput = () => inputRef.value?.focus();

const add = () => {
  const val = inputValue.value.trim();
  if (!val || props.modelValue.includes(val)) return;
  emit('update:modelValue', [...props.modelValue, val]);
  emit('tag-added', val);
  inputValue.value = '';
};

const remove = (i: number) => {
  const next = [...props.modelValue];
  next.splice(i, 1);
  emit('update:modelValue', next);
};

const handleKey = (e: KeyboardEvent) => {
  if (e.key === ',' || e.key === ';') {
    e.preventDefault();
    add();
  }
};

const onBlur = () => {
  isFocused.value = false;
  if (inputValue.value.trim()) add();
};
</script>

<style scoped lang="scss">
@import '../styles/variables';

.tag-chip-input {
  display: flex;
  flex-wrap: wrap;
  gap: 0.3rem;
  align-items: center;
  min-height: 2.2rem;
  padding: 0.3rem 0.5rem;
  border: 1px solid $color-border;
  border-radius: 4px;
  background: $color-bg-white;
  cursor: text;
  transition: border-color 0.15s;

  &.focused {
    border-color: $color-primary;
    outline: none;
  }
}

.chip {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.15rem 0.4rem;
  background: $color-primary-light;
  border: 1px solid mui-color('blue-grey', '200');
  border-radius: 3px;
  font-size: 0.8rem;
  color: $color-text-primary;
  white-space: nowrap;
}

.chip-remove {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 0.9rem;
  line-height: 1;
  color: $color-text-hint;
  padding: 0;

  &:hover {
    color: $color-error;
  }
}

.chip-input {
  flex: 1;
  min-width: 6rem;
  border: none;
  outline: none;
  font-size: 0.875rem;
  font-family: $font-body;
  background: transparent;
  padding: 0;
}
</style>
