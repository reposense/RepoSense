<template lang="pug">
  #zoom
    .fileTypes
      .checkboxes.mui-form--inline(v-if="fileTypes.length > 0")
        label(style='background-color: #000000; color: #ffffff')
          input.mui-checkbox--fileType(type="checkbox", v-model="isSelectAllChecked", value="all")
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
            v-on:change="updateSelectedFileTypesHash", v-model="selectedFileTypes")
          span {{ fileType }} &nbsp;
  </template>

<script lang="ts">
import { defineComponent, PropType } from 'vue';
import { mapState } from 'vuex';
import { StoreState } from '../types/vuex.d';
export default defineComponent({
  name: 'c-file-type-checkbox',
  props: {
    fileTypes: {
      type: Array as PropType<string[]>,
      required: true,
    },
  },
  data() {
    return {
      selectedFileTypes: [] as string[],
    };
  },
  computed: {
    isSelectAllChecked: {
      get() {
        return this.selectedFileTypes.length === this.fileTypes.length;
      },
      set(value: boolean) {
        if (value) {
          this.selectedFileTypes = this.fileTypes.slice();
        } else {
          this.selectedFileTypes = [];
        }
        this.updateSelectedFileTypesHash();
      },
    },
    ...mapState({
      fileTypeColors: (state: unknown) => (state as StoreState).fileTypeColors,
    }),
  },
  methods: {
    /*
     * Updates the zFT hash key values based on the file types selected.
     * An empty hash indicates that all file types are selected.
     */
    updateSelectedFileTypesHash() {
      const fileTypeHash = this.selectedFileTypes.length > 0
        ? this.selectedFileTypes.reduce((a, b) => `${a}~${b}`)
        : '';
      window.addHash('zFT', fileTypeHash);
      window.encodeHash();
    },
    getSelectedFileTypes() {
      return this.selectedFileTypes;
    },
    getFontColor(color: string) {
      return window.getFontColor(color);
    },
  },
});
</script>