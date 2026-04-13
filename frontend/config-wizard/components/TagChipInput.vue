<template lang="pug">
.tag-chip-input(:class="{ focused: isFocused }", @click="focusInput")
  span.chip(v-for="(tag, i) in modelValue", :key="i")
    | {{ tag }}
    button.chip-remove(type="button", @click.stop="remove(i)") ×
  input.chip-input(
    ref="inputRef",
    v-model="inputValue",
    :placeholder="modelValue.length === 0 ? (placeholder ?? '') : ''",
    @keydown.enter.prevent="add",
    @keydown="handleKey",
    @blur="onBlur",
    @focus="isFocused = true"
  )
</template>

<script setup lang="ts">
import { ref } from 'vue';

const props = defineProps<{
  modelValue: Array<string>;
  placeholder?: string;
}>();

const emit = defineEmits<{
  'update:modelValue': [value: Array<string>];
  'tag-added': [value: string];
  'tag-removed': [value: string];
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
  const removed = props.modelValue[i];
  const next = [...props.modelValue];
  next.splice(i, 1);
  emit('update:modelValue', next);
  emit('tag-removed', removed);
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
  align-items: center;
  background: $color-bg-white;
  border: 1px solid $color-border;
  border-radius: 4px;
  cursor: text;
  display: flex;
  flex-wrap: wrap;
  gap: .3rem;
  min-height: 2.2rem;
  padding: .3rem .5rem;
  transition: border-color .15s;

  &.focused {
    border-color: $color-primary;
    outline: none;
  }
}

.chip {
  align-items: center;
  background: $color-primary-light;
  border: 1px solid mui-color('blue-grey', '200');
  border-radius: 3px;
  color: $color-text-primary;
  display: inline-flex;
  font-size: .8rem;
  gap: .25rem;
  padding: .15rem .4rem;
  white-space: nowrap;
}

.chip-remove {
  background: none;
  border: none;
  color: $color-text-hint;
  cursor: pointer;
  font-size: .9rem;
  line-height: 1;
  padding: 0;

  &:hover {
    color: $color-error;
  }
}

.chip-input {
  background: transparent;
  border: none;
  flex: 1;
  font-family: $font-body;
  font-size: .875rem;
  min-width: 6rem;
  outline: none;
  padding: 0;
}
</style>
