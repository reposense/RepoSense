<template lang="pug">
.fileTypes(v-if="fileTypes.length > 0")
  .checkboxes.mui-form--inline
    label.all-checkbox
      input.mui-checkbox--fileType(type="checkbox", v-model="isAllChecked",
      value="all")
      span All&nbsp;
    label(
      v-for="fileType in fileTypes",
      v-bind:key="fileType",
      v-bind:style="{\
        'background-color': fileTypeColors[fileType],\
        'color': getFontColor(fileTypeColors[fileType])\
        }"
    )
      input.mui-checkbox--fileType(type="checkbox", v-bind:value="fileType",
        v-model="selectedFileTypes")
      span {{ fileType }} &nbsp;
</template>

<script lang="ts">
import { defineComponent, PropType } from 'vue';

export default defineComponent({
  name: 'c-file-type-checkbox',
  props: {
    fileTypes: {
      type: Array as PropType<Array<String>>,
      required: true,
    },
    fileTypeColors: {
      type: Object as PropType<Record<string, string>>,
      required: true,
    },
    modelValue: {
      type: Array as PropType<Array<String>>,
      required: true,
    },
  },
  emits: ['update-selected-file-types-hash', 'update:modelValue'],
  computed: {
    isAllChecked: {
      get(): boolean {
        return this.selectedFileTypes.length === this.fileTypes.length;
      },
      set(value: boolean) {
        if (value) {
          this.selectedFileTypes = this.fileTypes.slice();
        } else {
          this.selectedFileTypes = [];
        }
      },
    },
    selectedFileTypes: {
      get(): Array<String> {
        return this.modelValue;
      },
      set(value: Array<String>) {
        this.$emit('update:modelValue', value);
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
</style>
