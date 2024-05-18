<template lang="pug">
.checkboxes.mui-form--inline(v-if="fileTypes.length > 0")
  label.all-checkbox
    input.mui-checkbox--fileType#all(type="checkbox", v-model="isAllChecked", value="all")
    span(v-if="allCheckboxLabel", v-bind:title="getTitle(allCheckboxLabel)")
      span {{ getLabel(allCheckboxLabel) }}
    span(v-else) All&nbsp;
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
    span(v-if="fileTypeCheckboxLabels", v-bind:title="getTitle(fileTypeCheckboxLabels[index])")
      span {{ getLabel(fileTypeCheckboxLabels[index]) }}
    span(v-else) {{ this.fileTypes[index] }}&nbsp;
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue';

export default defineComponent({
  name: 'c-file-type-checkboxes',
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
      type: Object as PropType<{
        fileTitle: string,
        fileType: string,
        lineCount: number,
        blankLineCount: number,
      }>,
      default: undefined,
    },
    fileTypeCheckboxLabels: {
      type: Array as PropType<Array<{
        fileTitle: string,
        fileType: string,
        lineCount: number,
        blankLineCount: number,
      }>>,
      default: undefined,
    },
  },
  emits: ['update:selectedFileTypes', 'select-all-checked'],
  computed: {
    isAllChecked: {
      get(): boolean {
        return this.selectedFileTypes.length === this.fileTypes.length;
      },
      set(value: boolean): void {
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
      set(value: Array<string>): void {
        this.$emit('update:selectedFileTypes', value);
      },
    },
  },
  methods: {
    getFontColor(color: string): string {
      return window.getFontColor(color);
    },
    getTitle(label: {
      fileTitle: string,
      fileType: string,
      lineCount: number,
      blankLineCount: number,
    }): string {
      return `${label.fileTitle}: Blank: ${label.blankLineCount}, `
        + `Non-Blank: ${label.lineCount - label.blankLineCount}`;
    },
    getLabel(label: {
      fileTitle: string,
      fileType: string,
      lineCount: number,
      blankLineCount: number,
    }): string {
      return `${label.fileType}\xA0\xA0`
        + `${label.lineCount}\xA0\xA0`
        + `(${label.lineCount - label.blankLineCount})\xA0`;
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
