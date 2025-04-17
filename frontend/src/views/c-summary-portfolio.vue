<template lang="pug">
#summary
  c-summary-header(
    v-if="!isWidgetMode",
    v-model:filter-breakdown="filterBreakdown",
    v-model:optimise-timeline="optimiseTimeline",
    :input-date-not-supported="inputDateNotSupported",
    @get-filtered="getFiltered",
    @toggle-breakdown="toggleBreakdown"
  )

  c-error-message-box(
    v-if="!isWidgetMode",
    :error-messages="errorMessages"
  )

  .fileTypes(v-if="filterBreakdown && !isWidgetMode")
    c-file-type-checkboxes(
      :file-types="fileTypes",
      :file-type-colors="fileTypeColors",
      v-model:selected-file-types="checkedFileTypes",
      @update:selected-file-types="getFiltered"
    )

  c-summary-charts(
    :filtered="filtered",
    :checked-file-types="checkedFileTypes",
    :avg-contribution-size="avgContributionSize",
    :filter-breakdown="filterBreakdown",
    :filter-since-date="since",
    :filter-until-date="until",
    :min-date="since",
    :max-date="until",
    :optimise-timeline="optimiseTimeline"
  )

  .logo(v-if="isWidgetMode")
    a(:href="getRepoSenseHomeLink()", target="_blank")
      img(:src="getLogoPath()", :width=20, :height=20)
</template>

<script lang='ts'>
import { defineComponent } from 'vue';

import cErrorMessageBox from '../components/c-error-message-box.vue';
import cSummaryCharts from '../components/c-summary-charts.vue';
import cFileTypeCheckboxes from '../components/c-file-type-checkboxes.vue';
import cSummaryHeader from '../components/c-summary-header.vue';
import sortFiltered from '../utils/repo-sorter';
import {
  Repo,
  User,
} from '../types/types';
import { ZoomInfo } from '../types/vuex.d';
import {
  FilterGroupSelection, SortGroupSelection, SortWithinGroupSelection,
} from '../types/summary';
import summaryMixin from "../mixin/summaryMixin";

export default defineComponent({
  name: 'c-summary-portfolio',

  components: {
    cErrorMessageBox,
    cSummaryCharts,
    cFileTypeCheckboxes,
    cSummaryHeader,
  },

  // Common summary functionality in summaryMixin.ts
  mixins: [summaryMixin],

  data(): {
    since: string,
    until: string,
  } {
    return {
      since: '',
      until: '',
    };
  },

  created(): void {
    this.processFileTypes();
    this.renderFilterHash();
    this.getFiltered();
    if (this.$store.state.tabZoomInfo.isRefreshing) {
      const zoomInfo = Object.assign({}, this.$store.state.tabZoomInfo);
      this.restoreZoomFiltered(zoomInfo);
    }
  },

  methods: {
    // model functions //
    setSummaryHash(): void {
      const { addHash, encodeHash, removeHash } = window;

      addHash('breakdown', this.filterBreakdown);

      if (this.filterBreakdown) {
        const checkedFileTypesHash = this.checkedFileTypes.length > 0
          ? this.checkedFileTypes.join(window.HASH_DELIMITER)
          : '';
        addHash('checkedFileTypes', checkedFileTypesHash);
      } else {
        removeHash('checkedFileTypes');
      }

      if (this.optimiseTimeline) {
        addHash('optimiseTimeline', 'true');
      } else {
        removeHash('optimiseTimeline');
      }

      encodeHash();
    },

    renderFilterHash(): void {
      const convertBool = (txt: string): boolean => (txt === 'true');
      const hash = Object.assign({}, window.hashParams);

      if (hash.breakdown) {
        this.filterBreakdown = convertBool(hash.breakdown);
      }

      if (hash.checkedFileTypes || hash.checkedFileTypes === '') {
        const parsedFileTypes = hash.checkedFileTypes.split(window.HASH_DELIMITER);
        this.checkedFileTypes = parsedFileTypes.filter((type) => this.fileTypes.includes(type));
      }

      if (hash.optimiseTimeline) {
        this.optimiseTimeline = convertBool(hash.optimiseTimeline);
      }
    },

    toggleBreakdown(): void {
      // Reset the file type filter
      if (this.checkedFileTypes.length !== this.fileTypes.length) {
        this.checkedFileTypes = this.fileTypes.slice();
      }
      this.getFiltered();
    },

    async getFiltered(): Promise<void> {
      this.setSummaryHash();
      window.deactivateAllOverlays();

      await this.$store.dispatch('incrementLoadingOverlayCountForceReload', 1);
      this.getFilteredRepos();
      await this.$store.dispatch('incrementLoadingOverlayCountForceReload', -1);
    },

    getFilteredRepos(): void {
      this.filtered = [];

      this.repos.forEach((repo) => {
        repo.users?.forEach((user) => {
          this.getUserCommits(user, '', '');
          this.updateCheckedFileTypeContribution(user);
        });

        if (repo.users) {
          this.filtered.push(repo.users);
        }
      })

      const filterControl = {
        filterGroupSelection: FilterGroupSelection.GroupByRepos,
        sortingOption: SortGroupSelection.GroupTitleDsc.split(' ')[0],
        sortingWithinOption: SortWithinGroupSelection.Title,
        isSortingDsc: false,
        isSortingWithinDsc: false,
      };

      this.filtered = sortFiltered(this.filtered, filterControl);
    },

    restoreZoomFiltered(info: ZoomInfo): void {
      const { zSince, zUntil } = info;
      const filtered: Array<Array<User>> = [];

      const groups: Array<Repo> = JSON.parse(JSON.stringify(this.repos));

      const res: Array<User> = [];
      groups.forEach((repo) => {
        repo.users?.forEach((user) => {
          // only filter users that match with zoom user and previous searched user
          if (this.matchZoomUser(info, user)) {
            this.getUserCommits(user, zSince, zUntil);
            this.updateCheckedFileTypeContribution(user);
            res.push(user);
          }
        });
      });

      if (res.length) {
        filtered.push(res);
      }

      if (filtered.length) [[info.zUser]] = filtered;

      info.zFileTypeColors = this.fileTypeColors;
      info.isRefreshing = false;
      this.$store.commit('updateTabZoomInfo', info);
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss">
@import '../styles/_colors.scss';
@import '../styles/summary-chart.scss';

.error-message-box__show-more-container {
  display: flex;
  justify-content: flex-end;
  margin-top: .3rem;
}

.logo {
  display: flex;
  justify-content: flex-end;
  margin-top: 5px;
}
</style>
