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

<style scoped>
.tag-chip-input {
  display: flex;
  flex-wrap: wrap;
  gap: 0.3rem;
  align-items: center;
  min-height: 2.2rem;
  padding: 0.3rem 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  background: #fff;
  cursor: text;
  transition: border-color 0.15s;
}

.tag-chip-input.focused {
  border-color: #42b983;
  outline: none;
}

.chip {
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  padding: 0.15rem 0.4rem;
  background: #e8f5f0;
  border: 1px solid #b2dfcf;
  border-radius: 3px;
  font-size: 0.8rem;
  color: #2c3e50;
  white-space: nowrap;
}

.chip-remove {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 0.9rem;
  line-height: 1;
  color: #888;
  padding: 0;
}

.chip-remove:hover {
  color: #e74c3c;
}

.chip-input {
  flex: 1;
  min-width: 6rem;
  border: none;
  outline: none;
  font-size: 0.875rem;
  background: transparent;
  padding: 0;
}
</style>
