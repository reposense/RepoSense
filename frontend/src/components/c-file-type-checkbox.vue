<template lang="pug">
.checkboxes.mui-form--inline(v-if="fileTypes.length > 0")
  label.all-checkbox
    input.mui-checkbox--fileType#all(type="checkbox", v-model="isAllChecked", value="all")
    span(v-html="allCheckboxLabel")
  label(
    v-for="fileType, index in fileTypes",
    v-bind:key="fileType",
    v-bind:style="{\
      'background-color': fileTypeColors[fileType],\
      'color': getFontColor(fileTypeColors[fileType])\
      }"
  )
    input.mui-checkbox--fileType(type="checkbox", v-bind:value="fileType",
      v-model="localSelectedFileTypes", v-bind:id="fileType")
    span(v-html="fileTypeLabels[index]")
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue';

export default defineComponent({
  name: 'c-file-type-checkbox',
  props: {
    fileTypes: {
      type: Array as PropType<Array<string>>,
      required: true,
    },
    fileTypeColors: {
      type: Object as PropType<Record<string, string>>,
      required: true,
    },
    selectedFileTypes: {
      type: Array as PropType<Array<string>>,
      required: true,
    },
    allCheckboxLabel: {
      type: String,
      default: 'All\xA0',
    },
    fileTypeCheckboxLabels: {
      type: Array as PropType<Array<string>>,
      default: undefined,
    },
  },
  emits: ['update-selected-file-types-hash', 'update:selectedFileTypes', 'select-all-checked'],
  computed: {
    fileTypeLabels() {
      return this.fileTypeCheckboxLabels
        ? this.fileTypeCheckboxLabels
        : this.fileTypes.map((str) => `${str}\xA0`);
    },
    isAllChecked: {
      get(): boolean {
        return this.selectedFileTypes.length === this.fileTypes.length;
      },
      set(value: boolean) {
        if (value) {
          this.localSelectedFileTypes = this.fileTypes.slice();
        } else {
          this.localSelectedFileTypes = [];
        }
        this.$emit('select-all-checked', value);
      },
    },
    localSelectedFileTypes: {
      get(): Array<string> {
        return this.selectedFileTypes;
      },
      set(value: Array<string>) {
        this.$emit('update:selectedFileTypes', value);
        this.updateSelectedFileTypesHash();
      },
    },
  },
  methods: {
    getFontColor(color: string) {
      return window.getFontColor(color);
    },
    updateSelectedFileTypesHash() {
      this.$emit('update-selected-file-types-hash');
    },
  },
});
</script>

<style lang="scss" scoped>
.all-checkbox {
  background-color: #000000;
  color: #ffffff;
}

.mui-checkbox--fileType {
  vertical-align: middle;
}
</style>
