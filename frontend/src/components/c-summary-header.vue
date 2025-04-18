<template lang="pug">
  form.summary-picker.mui-form--inline(onsubmit="return false;")
    .summary-picker__section
      .tooltip(
        @mouseover="onTooltipHover('filter-files-label')",
        @mouseout="resetTooltip('filter-files-label')"
      )
        .mui-textfield.filter_file(v-if='!isPortfolio')
          label filter files
          input(
            type="text",
            @change="setFilteredFileName",
            v-model="localFilteredFileName"
            )
          button.mui-btn.mui-btn--raised(type="button", @click.prevent="resetFilteredFileName") x
          span.tooltip-text(ref='filter-files-label') Enter a glob to filter the files

      .mui-textfield.search_box(v-if='!isPortfolio')
        input(type="text", v-model="localFilterSearch")
        label search
        button.mui-btn.mui-btn--raised(type="button", @click.prevent="resetFilterSearch") x

      .mui-select.grouping(v-if='!isPortfolio')
        select(v-model="localFilterGroupSelection")
          option(value="groupByNone") None
          option(value="groupByRepos") Repo/Branch
          option(value="groupByAuthors") Author
        label group by

      .mui-select.sort-group(v-if='!isPortfolio')
        select(v-model="localSortGroupSelection", @change="$emit('get-filtered')")
          option(value="groupTitle") &uarr; group title
          option(value="groupTitle dsc") &darr; group title
          option(value="totalCommits") &uarr; contribution
          option(value="totalCommits dsc") &darr; contribution
          option(value="variance") &uarr; variance
          option(value="variance dsc") &darr; variance
          option(value="defaultSortOrder") &uarr; default
          option(value="defaultSortOrder dsc") &darr; default
        label sort groups by

      .mui-select.sort-within-group(v-if='!isPortfolio')
        select(
          v-model="localSortWithinGroupSelection",
          :disabled="localFilterGroupSelection === 'groupByNone' || localAllGroupsMerged",
          @change="$emit('get-filtered')"
        )
          option(value="title") &uarr; title
          option(value="title dsc") &darr; title
          option(value="totalCommits") &uarr; contribution
          option(value="totalCommits dsc") &darr; contribution
          option(value="variance") &uarr; variance
          option(value="variance dsc") &darr; variance
        label sort within groups by

      .mui-select.granularity(v-if='!isPortfolio')
        select(v-model="localFilterTimeFrame", @change="$emit('get-filtered')")
          option(value="commit") Commit
          option(value="day") Day
          option(value="week") Week
        label granularity

      .mui-textfield(v-if='!isPortfolio')
        input(v-if="inputDateNotSupported", type="text", placeholder="yyyy-mm-dd",
          :value="filterSinceDate", @keyup.enter="updateTmpFilterSinceDate",
          onkeydown="formatInputDateOnKeyDown(event)", oninput="appendDashInputDate(event)", maxlength=10)
        input(v-else, type="datetime-local", name="since", step="1", :value="filterSinceDate",
          @input="updateTmpFilterSinceDate", :min="minDate", :max="filterUntilDate")
        label since
      .mui-textfield(v-if='!isPortfolio')
        input(v-if="inputDateNotSupported", type="text", placeholder="yyyy-mm-dd",
          :value="filterUntilDate", @keyup.enter="updateTmpFilterUntilDate",
          onkeydown="formatInputDateOnKeyDown(event)", oninput="appendDashInputDate(event)", maxlength=10)
        input(v-else, type="datetime-local", name="until", step="1", :value="filterUntilDate",
          @input="updateTmpFilterUntilDate", :min="filterSinceDate", :max="maxDate")
        label until
      .mui-textfield(v-if='!isPortfolio')
        a(@click="resetDateRange") Reset date range

      .summary-picker__checkboxes.summary-picker__section
        label.filter-breakdown
          input.mui-checkbox(
            type="checkbox",
            v-model="localFilterBreakdown",
            @change="toggleBreakdown"
          )
          span breakdown by file type

        label.merge-group(
          v-if='!isPortfolio',
          :style="localFilterGroupSelection === 'groupByNone' ? { opacity:0.5 } : { opacity:1.0 }"
        )
          input.mui-checkbox(
            type="checkbox",
            v-model="localAllGroupsMerged",
            :disabled="localFilterGroupSelection === 'groupByNone'"
          )
          span merge all groups

        label.show-tags(v-if='!isPortfolio')
          input.mui-checkbox(
            type="checkbox",
            v-model="localViewRepoTags",
            @change="$emit('get-filtered')"
          )
          span show tags

        label.optimise-timeline
          input.mui-checkbox(
            type="checkbox",
            v-model="localOptimiseTimeline",
            @change="$emit('get-filtered')"
          )
          span trim timeline
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";
import brokenLinkDisabler from '../mixin/brokenLinkMixin';
import tooltipPositioner from '../mixin/dynamicTooltipMixin';
import {
  FilterGroupSelection,
  FilterTimeFrame,
  SortGroupSelection,
  SortWithinGroupSelection,
} from "../types/summary";

const dateFormatRegex = /([12]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01]))$/;

export default defineComponent({
  name: "c-summary-header",
  mixins: [brokenLinkDisabler, tooltipPositioner],
  props: {
    filterSearch: {
      type: String,
      default: "",
    },
    filterGroupSelection: {
      type: String as PropType<FilterGroupSelection>,
      default: FilterGroupSelection.GroupByRepos,
    },
    sortGroupSelection: {
      type: String as PropType<SortGroupSelection>,
      default: SortGroupSelection.GroupTitleDsc,
    },
    sortWithinGroupSelection: {
      type: String as PropType<SortWithinGroupSelection>,
      default: SortWithinGroupSelection.Title,
    },
    filterTimeFrame: {
      type: String as PropType<FilterTimeFrame>,
      default: FilterTimeFrame.Commit,
    },
    filterBreakdown: {
      type: Boolean,
      default: false,
    },
    tmpFilterSinceDate: {
      type: String,
      default: "",
    },
    tmpFilterUntilDate: {
      type: String,
      default: "",
    },
    minDate: {
      type: String,
      default: "",
    },
    maxDate: {
      type: String,
      default: "",
    },
    viewRepoTags: {
      type: Boolean,
      default: false,
    },
    optimiseTimeline: {
      type: Boolean,
      default: false,
    },
    allGroupsMerged: {
      type: Boolean,
      default: false,
    },
    inputDateNotSupported: {
      type: Boolean,
      required: true,
    },
    hasModifiedSinceDate: {
      type: Boolean,
      default: false,
    },
    hasModifiedUntilDate: {
      type: Boolean,
      default: false,
    },
    filterSinceDate: {
      type: String,
      default: "",
    },
    filterUntilDate: {
      type: String,
      default: "",
    },
    filteredFileName: {
      type: String,
      default: "",
    },
  },

  emits: [
    "update:filterSearch",
    "update:filterGroupSelection",
    "update:sortGroupSelection",
    "update:sortWithinGroupSelection",
    "update:filterTimeFrame",
    "update:filterBreakdown",
    "update:tmpFilterSinceDate",
    "update:tmpFilterUntilDate",
    "update:viewRepoTags",
    "update:optimiseTimeline",
    "update:allGroupsMerged",
    "update:hasModifiedSinceDate",
    "update:hasModifiedUntilDate",
    "update:filteredFileName",
    "get-filtered",
    "reset-date-range",
    "toggle-breakdown",
  ],

  data(): { isPortfolio: boolean } {
    return { isPortfolio: window.isPortfolio };
  },

  computed: {
    localFilterSearch: {
      get(): string {
        return this.$props.filterSearch as string;
      },
      set(value: string) {
        this.$emit("update:filterSearch", value);
        this.$emit("get-filtered");
      },
    },

    localFilterGroupSelection: {
      get(): FilterGroupSelection {
        return this.$props.filterGroupSelection as FilterGroupSelection;
      },
      set(value: FilterGroupSelection) {
        this.$emit("update:filterGroupSelection", value);
        this.$emit("get-filtered");
      },
    },

    localFilteredFileName: {
      get() {
        return this.$props.filteredFileName as string;
      },
      set(newValue: string) {
        this.$emit("update:filteredFileName", newValue);
      },
    },

    localSortGroupSelection: {
      get(): SortGroupSelection {
        return this.$props.sortGroupSelection as SortGroupSelection;
      },
      set(value: SortGroupSelection) {
        this.$emit("update:sortGroupSelection", value);
      },
    },

    localSortWithinGroupSelection: {
      get(): SortWithinGroupSelection {
        return this.$props.sortWithinGroupSelection as SortWithinGroupSelection;
      },
      set(value: SortWithinGroupSelection) {
        this.$emit("update:sortWithinGroupSelection", value);
      },
    },

    localFilterTimeFrame: {
      get(): FilterTimeFrame {
        return this.$props.filterTimeFrame as FilterTimeFrame;
      },
      set(value: FilterTimeFrame) {
        this.$emit("update:filterTimeFrame", value);
      },
    },

    localFilterBreakdown: {
      get(): boolean {
        return this.$props.filterBreakdown as boolean;
      },
      set(value: boolean) {
        this.$emit("update:filterBreakdown", value);
      },
    },

    localViewRepoTags: {
      get(): boolean {
        return this.$props.viewRepoTags as boolean;
      },
      set(value: boolean) {
        this.$emit("update:viewRepoTags", value);
      },
    },

    localOptimiseTimeline: {
      get(): boolean {
        return this.$props.optimiseTimeline as boolean;
      },
      set(value: boolean) {
        this.$emit("update:optimiseTimeline", value);
      },
    },

    localAllGroupsMerged: {
      get(): boolean {
        return this.$props.allGroupsMerged as boolean;
      },
      set(value: boolean) {
        this.$emit("update:allGroupsMerged", value);
      },
    },
  },

  methods: {
    resetFilterSearch() {
      this.$emit("update:filterSearch", "");
      this.$emit("get-filtered");
    },

    resetFilteredFileName(): void {
      this.$emit("update:filteredFileName", "");
      window.removeHash("authorshipFilesGlob");
      this.$store.commit("updateAuthorshipRefreshState", false);
      this.$emit("get-filtered");
      window.location.reload();
    },

    setFilteredFileName(evt: Event): void {
      this.$emit("update:filteredFileName", (evt.target as HTMLInputElement).value);
      this.$store.commit("updateAuthorshipRefreshState", true);
      window.addHash("authorshipFilesGlob", this.filteredFileName);
      this.$emit("get-filtered");
      window.location.reload();
    },

    updateTmpFilterSinceDate(event: Event) {
      // Only called from an input onchange event, target guaranteed to be input element
      const since = (event.target as HTMLInputElement).value;
      this.$emit("update:hasModifiedSinceDate", true);

      if (!this.inputDateNotSupported) {
        this.$emit('update:tmpFilterSinceDate', since);
        (event.target as HTMLInputElement).value = this.filterSinceDate;
        this.$emit("get-filtered");
      } else if (dateFormatRegex.test(since) && since >= this.minDate) {
        this.$emit("update:tmpFilterSinceDate", since);
        (event.currentTarget as HTMLInputElement).style.removeProperty(
          "border-bottom-color"
        );
        this.$emit("get-filtered");
      } else {
        // invalid since date detected
        (event.currentTarget as HTMLInputElement).style.borderBottomColor = "red";
      }
    },

    updateTmpFilterUntilDate(event: Event) {
      // Only called from an input onchange event, target guaranteed to be input element
      const until = (event.target as HTMLInputElement).value;
      this.$emit("update:hasModifiedUntilDate", true);

      if (!this.inputDateNotSupported) {
        this.$emit('update:tmpFilterUntilDate', until);
        (event.target as HTMLInputElement).value = this.filterUntilDate;
        this.$emit("get-filtered");
      } else if (dateFormatRegex.test(until) && until <= this.maxDate) {
        this.$emit("update:tmpFilterUntilDate", until);
        (event.currentTarget as HTMLInputElement).style.removeProperty(
          "border-bottom-color"
        );
        this.$emit("get-filtered");
      } else {
        // invalid until date detected
        (event.currentTarget as HTMLInputElement).style.borderBottomColor = "red";
      }
    },

    resetDateRange() {
      this.$emit("reset-date-range");
    },

    toggleBreakdown() {
      this.$emit("toggle-breakdown");
    },
  },
});
</script>
