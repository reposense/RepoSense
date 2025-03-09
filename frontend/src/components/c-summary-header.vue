<template lang="pug">
  form.summary-picker.mui-form--inline(onsubmit="return false;")
    .summary-picker__section
      // ! ok
      .mui-textfield.search_box
        input(type="text", @change="updateFilterSearch", v-model="filterSearch")
        label search
        button.mui-btn.mui-btn--raised(type="button", @click.prevent="resetFilterSearch") x
      // watcher in parent
      .mui-select.grouping
        select(v-model="filterGroupSelection")
          option(value="groupByNone") None
          option(value="groupByRepos") Repo/Branch
          option(value="groupByAuthors") Author
        label group by
      //?
      .mui-select.sort-group
        select(v-model="sortGroupSelection", @change="getFiltered")
          option(value="groupTitle") &uarr; group title
          option(value="groupTitle dsc") &darr; group title
          option(value="totalCommits") &uarr; contribution
          option(value="totalCommits dsc") &darr; contribution
          option(value="variance") &uarr; variance
          option(value="variance dsc") &darr; variance
        label sort groups by
      .mui-select.sort-within-group
        select(
          v-model="sortWithinGroupSelection",
          :disabled="filterGroupSelection === 'groupByNone' || allGroupsMerged",
          @change="getFiltered"
        )
          option(value="title") &uarr; title
          option(value="title dsc") &darr; title
          option(value="totalCommits") &uarr; contribution
          option(value="totalCommits dsc") &darr; contribution
          option(value="variance") &uarr; variance
          option(value="variance dsc") &darr; variance
        label sort within groups by
      .mui-select.granularity
        select(v-model="filterTimeFrame", @change="getFiltered")
          option(value="commit") Commit
          option(value="day") Day
          option(value="week") Week
        label granularity
      .mui-textfield
        input(v-if="isSafariBrowser", type="text", placeholder="yyyy-mm-dd",
          :value="filterSinceDate", @keyup.enter="updateTmpFilterSinceDate",
          onkeydown="formatInputDateOnKeyDown(event)", oninput="appendDashInputDate(event)", maxlength=10)
        input(v-else, type="date", name="since", :value="filterSinceDate", @input="updateTmpFilterSinceDate",
          :min="minDate", :max="filterUntilDate")
        label since
      .mui-textfield
        input(v-if="isSafariBrowser", type="text", placeholder="yyyy-mm-dd",
          :value="filterUntilDate", @keyup.enter="updateTmpFilterUntilDate",
          onkeydown="formatInputDateOnKeyDown(event)", oninput="appendDashInputDate(event)", maxlength=10)
        input(v-else, type="date", name="until", :value="filterUntilDate", @input="updateTmpFilterUntilDate",
          :min="filterSinceDate", :max="maxDate")
        label until
      .mui-textfield
        a(@click="resetDateRange") Reset date range
      .summary-picker__checkboxes.summary-picker__section
        label.filter-breakdown
          input.mui-checkbox(
            type="checkbox",
            v-model="filterBreakdown",
            @change="toggleBreakdown"
          )
          span breakdown by file type
        label.merge-group(
          :style="filterGroupSelection === 'groupByNone' ? { opacity:0.5 } : { opacity:1.0 }"
        )
          input.mui-checkbox(
            type="checkbox",
            v-model="allGroupsMerged",
            :disabled="filterGroupSelection === 'groupByNone'"
          )
          span merge all groups
        label.show-tags
          input.mui-checkbox(
            type="checkbox",
            v-model="viewRepoTags",
            @change="getFiltered"
          )
          span show tags
        label.optimise-timeline
          input.mui-checkbox(
            type="checkbox",
            v-model="optimiseTimeline",
            @change="getFiltered"
          )
          span trim timeline
</template>

<script lang="ts">
import {defineComponent} from 'vue';
import {FilterGroupSelection, FilterTimeFrame, SortGroupSelection, SortWithinGroupSelection} from "@/types/summary";
import {mapState} from "vuex";

export default defineComponent({
  name: 'c-summary-header',

  props: {
    initialFilterSearch: {
      type: String,
      default: '',
    },
    initialFilterGroupSelection: {
      type: String as () => FilterGroupSelection,
      default: FilterGroupSelection.GroupByRepos,
    },
    initialSortGroupSelection: {
      type: String as () => SortGroupSelection,
      default: SortGroupSelection.GroupTitleDsc,
    },
    initialSortWithinGroupSelection: {
      type: String as () => SortWithinGroupSelection,
      default: SortWithinGroupSelection.Title,
    },
    initialFilterTimeFrame: {
      type: String as () => FilterTimeFrame,
      default: FilterTimeFrame.Commit,
    },
    initialFilterBreakdown: {
      type: Boolean,
      default: false,
    },
    initialViewRepoTags: {
      type: Boolean,
      default: false,
    },
    initialOptimiseTimeline: {
      type: Boolean,
      default: false,
    },
    minDate: {
      type: String,
      required: true,
    },
    maxDate: {
      type: String,
      required: true,
    },
    initialTmpFilterSinceDate: {
      type: String,
      default: '',
    },
    initialTmpFilterUntilDate: {
      type: String,
      default: '',
    },
    hasModifiedSinceDate: {
      type: Boolean,
      default: false,
    },
    hasModifiedUntilDate: {
      type: Boolean,
      default: false,
    },
  },

  data(): {
    filterSearch: string,
    filterGroupSelection: FilterGroupSelection,
    sortGroupSelection: SortGroupSelection,
    sortWithinGroupSelection: SortWithinGroupSelection,
    filterTimeFrame: FilterTimeFrame,
    filterBreakdown: boolean,
    viewRepoTags: boolean,
    optimiseTimeline: boolean,
    tmpFilterSinceDate: string,
    tmpFilterUntilDate: string,
    isSafariBrowser: boolean,
  } {
    return {
      filterSearch: this.initialFilterSearch,
      filterGroupSelection: this.initialFilterGroupSelection,
      sortGroupSelection: this.initialSortGroupSelection,
      sortWithinGroupSelection: this.initialSortWithinGroupSelection,
      filterTimeFrame: this.initialFilterTimeFrame,
      filterBreakdown: this.initialFilterBreakdown,
      viewRepoTags: this.initialViewRepoTags,
      optimiseTimeline: this.initialOptimiseTimeline,
      tmpFilterSinceDate: this.initialTmpFilterSinceDate,
      tmpFilterUntilDate: this.initialTmpFilterUntilDate,
      isSafariBrowser: /.*Version.*Safari.*/.test(navigator.userAgent),
    };
  },

  computed: {
    allGroupsMerged: {
      get(): boolean {
        if (this.mergedGroups.length === 0) {
          return false;
        }
        return this.$store.getters.isMergedGroupsComplete;
      },
      set(value: boolean): void {
        this.$emit('getFiltered');
      },
    },

    filterSinceDate(): string {
      if (this.tmpFilterSinceDate && this.tmpFilterSinceDate >= this.minDate) {
        return this.tmpFilterSinceDate;
      }
      // If user clears the since date field
      return this.minDate;
    },

    filterUntilDate(): string {
      if (this.tmpFilterUntilDate && this.tmpFilterUntilDate <= this.maxDate) {
        return this.tmpFilterUntilDate;
      }
      return this.maxDate;
    },
    ...mapState(['mergedGroups']),
  },

  methods: {
    resetFilterSearch() {
      this.filterSearch = '';
      this.$emit('update:filterSearch', '');
    },

    updateFilterSearch(evt: Event) {
      const value = (evt.target as HTMLInputElement).value;
      this.filterSearch = value;
      this.$emit('update:filterSearch', value);
    },

    onSortGroupChange() {
      this.$emit('getFiltered');
    },
    onSortWithinGroupChange() {
      this.$emit('getFiltered');
    },
    onTimeFrameChange() {
      this.$emit('getFiltered');
    },
    onViewRepoTagsChange() {
      this.$emit('getFiltered');
    },
    onOptimiseTimelineChange() {
      this.$emit('getFiltered');
    },
    toggleBreakdown() {
      this.$emit('toggleBreakdown');
    },
    resetDateRange() {
      this.$emit('resetDateRange');
    },
    updateTmpFilterSinceDate(event: Event) {
      const since = (event.target as HTMLInputElement).value;

      if (!this.isSafariBrowser) {
        this.tmpFilterSinceDate = since;
        this.$emit('update:tmpFilterSinceDate', since);
        this.$emit('update:hasModifiedSinceDate', true);
        (event.target as HTMLInputElement).value = this.filterSinceDate;
        this.$emit('getFiltered');
      } else if (dateFormatRegex.test(since) && since >= this.minDate) {
        this.tmpFilterSinceDate = since;
        this.$emit('update:tmpFilterSinceDate', since);
        this.$emit('update:hasModifiedSinceDate', true);
        (event.currentTarget as HTMLInputElement).style.removeProperty('border-bottom-color');
        this.$emit('getFiltered');
      } else {
        // invalid since date detected
        (event.currentTarget as HTMLInputElement).style.borderBottomColor = 'red';
      }
    },
    updateTmpFilterUntilDate(event: Event) {
      const until = (event.target as HTMLInputElement).value;

      if (!this.isSafariBrowser) {
        this.tmpFilterUntilDate = until;
        this.$emit('update:tmpFilterUntilDate', until);
        this.$emit('update:hasModifiedUntilDate', true);
        (event.target as HTMLInputElement).value = this.filterUntilDate;
        this.$emit('getFiltered');
      } else if (dateFormatRegex.test(until) && until <= this.maxDate) {
        this.tmpFilterUntilDate = until;
        this.$emit('update:tmpFilterUntilDate', until);
        this.$emit('update:hasModifiedUntilDate', true);
        (event.currentTarget as HTMLInputElement).style.removeProperty('border-bottom-color');
        this.$emit('getFiltered');
      } else {
        // invalid until date detected
        (event.currentTarget as HTMLInputElement).style.borderBottomColor = 'red';
      }
  }
});
</script>

<style lang="scss" scoped>

</style>