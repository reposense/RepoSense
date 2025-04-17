<template lang="pug">
#summary
  c-summary-header(
    v-if="!isWidgetMode",
    v-model:filter-search="filterSearch",
    v-model:filter-group-selection="filterGroupSelection",
    v-model:sort-group-selection="sortGroupSelection",
    v-model:sort-within-group-selection="sortWithinGroupSelection",
    v-model:filter-time-frame="filterTimeFrame",
    v-model:filter-breakdown="filterBreakdown",
    v-model:tmp-filter-since-date="tmpFilterSinceDate",
    v-model:tmp-filter-until-date="tmpFilterUntilDate",
    v-model:view-repo-tags="viewRepoTags",
    v-model:optimise-timeline="optimiseTimeline",
    v-model:all-groups-merged="allGroupsMerged",
    v-model:has-modified-since-date="hasModifiedSinceDate",
    v-model:has-modified-until-date="hasModifiedUntilDate",
    v-model:filtered-file-name="filteredFileName",
    :min-date="minDate",
    :max-date="maxDate",
    :input-date-not-supported="inputDateNotSupported",
    :filter-since-date="filterSinceDate",
    :filter-until-date="filterUntilDate",
    @get-filtered="getFiltered",
    @reset-date-range="resetDateRange",
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
    :filter-group-selection="filterGroupSelection",
    :filter-breakdown="filterBreakdown",
    :filter-time-frame="filterTimeFrame",
    :filter-since-date="filterSinceDate",
    :filter-until-date="filterUntilDate",
    :filter-search="filterSearch",
    :min-date="minDate",
    :max-date="maxDate",
    :sort-group-selection="sortGroupSelection",
    :chart-group-index="chartGroupIndex",
    :chart-index="chartIndex",
    :view-repo-tags="viewRepoTags",
    :optimise-timeline="optimiseTimeline"
  )

  .logo(v-if="isWidgetMode")
    a(:href="getRepoSenseHomeLink()", target="_blank")
      img(:src="getLogoPath()", :width=20, :height=20)
</template>

<script lang='ts'>
import { mapState } from 'vuex';
import { defineComponent } from 'vue';

import cErrorMessageBox from '../components/c-error-message-box.vue';
import cSummaryCharts from '../components/c-summary-charts.vue';
import cFileTypeCheckboxes from '../components/c-file-type-checkboxes.vue';
import cSummaryHeader from '../components/c-summary-header.vue';
import sortFiltered from '../utils/repo-sorter';
import {
  Commit,
  DailyCommit,
  Repo,
  User,
  isCommit,
} from '../types/types';
import {
  AuthorDailyContributions,
  AuthorFileTypeContributions,
} from '../types/zod/commits-type';
import { ZoomInfo } from '../types/vuex.d';
import {
  FilterGroupSelection, FilterTimeFrame, SortGroupSelection, SortWithinGroupSelection,
} from '../types/summary';
import summaryMixin from "../mixin/summaryMixin";

const dateFormatRegex = /^([12]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01]))(T([01]\d|2[0-3]):([0-5]\d)(:([0-5]\d))?)?$/;

export default defineComponent({
  name: 'c-summary',

  components: {
    cErrorMessageBox,
    cSummaryCharts,
    cFileTypeCheckboxes,
    cSummaryHeader,
  },

  mixins: [summaryMixin],

  data(): {
    filterSearch: string,
    filterGroupSelection: FilterGroupSelection,
    sortGroupSelection: SortGroupSelection,
    sortWithinGroupSelection: SortWithinGroupSelection,
    sortingOption: string,
    isSortingDsc: boolean,
    sortingWithinOption: string,
    isSortingWithinDsc: boolean,
    filterTimeFrame: FilterTimeFrame,
    tmpFilterSinceDate: string,
    tmpFilterUntilDate: string,
    hasModifiedSinceDate: boolean,
    hasModifiedUntilDate: boolean,
    filterHash: string,
    minDate: string,
    maxDate: string,
    chartGroupIndex: number | undefined,
    chartIndex: number | undefined,
    viewRepoTags: boolean,
    filteredFileName: string,
  } {
    return {
      filterSearch: '',
      filterGroupSelection: FilterGroupSelection.GroupByRepos,
      sortGroupSelection: SortGroupSelection.GroupTitleDsc, // UI for sorting groups
      sortWithinGroupSelection: SortWithinGroupSelection.Title, // UI for sorting within groups
      sortingOption: '',
      isSortingDsc: false,
      sortingWithinOption: '',
      isSortingWithinDsc: false,
      filterTimeFrame: FilterTimeFrame.Commit,
      tmpFilterSinceDate: '',
      tmpFilterUntilDate: '',
      hasModifiedSinceDate: window.isSinceDateProvided,
      hasModifiedUntilDate: window.isUntilDateProvided,
      filterHash: '',
      minDate: window.sinceDate,
      maxDate: window.untilDate,
      chartGroupIndex: undefined as number | undefined,
      chartIndex: undefined as number | undefined,
      viewRepoTags: false,
      filteredFileName: ''
    };
  },

  computed: {
    allGroupsMerged: {
      get(): boolean {
        if (this.mergedGroups.length === 0) {
          return false;
        }
        return this.mergedGroups.length === this.filtered.length;
      },
      set(value: boolean): void {
        if (value) {
          const mergedGroups: Array<string> = [];
          this.filtered.forEach((group) => {
            mergedGroups.push(this.getGroupName(group));
          });
          this.filtered = [];
          this.$store.commit('updateMergedGroup', mergedGroups);
        } else {
          this.$store.commit('updateMergedGroup', []);
        }
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

  watch: {
    filterGroupSelection(): void {
      // Deactivates watcher
      if (!this.filterGroupSelectionWatcherFlag) {
        return;
      }
      const { allGroupsMerged } = this;

      this.$store.dispatch('incrementLoadingOverlayCountForceReload', 1).then(() => {
        this.getFilteredRepos();
        this.updateMergedGroup(allGroupsMerged);
      }).then(async () => {
        await this.$store.dispatch('incrementLoadingOverlayCountForceReload', -1);
      });
    },

    '$store.state.summaryDates': function (): void {
      this.hasModifiedSinceDate = true;
      this.hasModifiedUntilDate = true;
      this.tmpFilterSinceDate = this.$store.state.summaryDates.since;
      this.tmpFilterUntilDate = this.$store.state.summaryDates.until;
      window.deactivateAllOverlays();
      this.getFiltered();
    },

    mergedGroups: {
      deep: true,
      handler(): void {
        this.getFiltered();
      },
    },
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

      addHash('search', this.filterSearch);
      addHash('sort', this.sortGroupSelection);
      addHash('sortWithin', this.sortWithinGroupSelection);

      if (this.hasModifiedSinceDate) {
        addHash('since', this.filterSinceDate);
      }

      if (this.hasModifiedUntilDate) {
        addHash('until', this.filterUntilDate);
      }

      addHash('timeframe', this.filterTimeFrame);

      let mergedGroupsHash = this.mergedGroups.join(window.HASH_DELIMITER);
      if (mergedGroupsHash.length === 0) {
        mergedGroupsHash = '';
      }
      addHash('mergegroup', mergedGroupsHash);

      addHash('groupSelect', this.filterGroupSelection);
      addHash('breakdown', this.filterBreakdown);

      if (this.filterBreakdown) {
        const checkedFileTypesHash = this.checkedFileTypes.length > 0
          ? this.checkedFileTypes.join(window.HASH_DELIMITER)
          : '';
        addHash('checkedFileTypes', checkedFileTypesHash);
      } else {
        removeHash('checkedFileTypes');
      }

      if (this.viewRepoTags) {
        addHash('viewRepoTags', 'true');
      } else {
        removeHash('viewRepoTags');
      }
      if (this.optimiseTimeline) {
        addHash('optimiseTimeline', 'true');
      } else {
        removeHash('optimiseTimeline');
      }

      window.addHash('filteredFileName', this.filteredFileName);
      encodeHash();
    },

    renderFilterHash(): void {
      const convertBool = (txt: string): boolean => (txt === 'true');
      const hash = Object.assign({}, window.hashParams);

      if (hash.search) { this.filterSearch = hash.search; }
      if (hash.sort && Object.values(SortGroupSelection).includes(hash.sort as SortGroupSelection)) {
        this.sortGroupSelection = hash.sort as SortGroupSelection;
      }
      if (hash.sortWithin
        && Object.values(SortWithinGroupSelection).includes(hash.sortWithin as SortWithinGroupSelection)) {
        this.sortWithinGroupSelection = hash.sortWithin as SortWithinGroupSelection;
      }

      if (hash.timeframe && Object.values(FilterTimeFrame).includes(hash.timeframe as FilterTimeFrame)) {
        this.filterTimeFrame = hash.timeframe as FilterTimeFrame;
      }
      if (hash.mergegroup) {
        this.$store.commit(
          'updateMergedGroup',
          hash.mergegroup.split(window.HASH_DELIMITER),
        );
      }
      if (hash.since && dateFormatRegex.test(hash.since)) {
        this.tmpFilterSinceDate = hash.since;
      }
      if (hash.until && dateFormatRegex.test(hash.until)) {
        this.tmpFilterUntilDate = hash.until;
      }

      if (hash.groupSelect && Object.values(FilterGroupSelection).includes(hash.groupSelect as FilterGroupSelection)) {
        this.filterGroupSelection = hash.groupSelect as FilterGroupSelection;
      }
      if (hash.breakdown) {
        this.filterBreakdown = convertBool(hash.breakdown);
      }
      if (hash.checkedFileTypes || hash.checkedFileTypes === '') {
        const parsedFileTypes = hash.checkedFileTypes.split(window.HASH_DELIMITER);
        this.checkedFileTypes = parsedFileTypes.filter((type) => this.fileTypes.includes(type));
      }
      if (hash.chartGroupIndex) {
        this.chartGroupIndex = parseInt(hash.chartGroupIndex, 10);
      }
      if (hash.chartIndex) {
        this.chartIndex = parseInt(hash.chartIndex, 10);
      }
      if (hash.viewRepoTags) {
        this.viewRepoTags = convertBool(hash.viewRepoTags);
      }
      if (hash.optimiseTimeline) {
        this.optimiseTimeline = convertBool(hash.optimiseTimeline);
      }
      if (hash.filteredFileName) {
        this.filteredFileName = hash.filteredFileName;
      }
    },

    getGroupName(group: Array<User>): string {
      return window.getGroupName(group, this.filterGroupSelection);
    },

    isMatchSearchedUser(filterSearch: string, user: User): boolean {
      return !filterSearch || filterSearch.toLowerCase()
        .split(' ')
        .filter(Boolean)
        .some((param) => user.searchPath.includes(param));
    },

    isMatchSearchedTag(filterSearch: string, tag: string) {
      return !filterSearch || filterSearch.toLowerCase()
        .split(' ')
        .filter(Boolean)
        .some((param) => tag.includes(param));
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
      this.getMergedRepos();
      await this.$store.dispatch('incrementLoadingOverlayCountForceReload', -1);
    },

    getFilteredRepos(): void {
      // array of array, sorted by repo
      const full: Array<Array<User>> = [];
      const tagSearchPrefix = 'tag:';

      // create deep clone of this.repos to not modify the original content of this.repos
      // when merging groups
      const groups = this.hasMergedGroups() ? JSON.parse(JSON.stringify(this.repos)) as Array<Repo> : this.repos;

      if (this.filterSearch.startsWith(tagSearchPrefix)) {
        const searchedTags = this.filterSearch.split(tagSearchPrefix)[1];
        groups.forEach((repo) => {
          const commits = repo.commits;
          if (!commits) return;

          const res: Array<User> = [];
          Object.entries(commits.authorDailyContributionsMap).forEach(([author, contributions]) => {
            contributions = contributions as Array<AuthorDailyContributions>;
            const tags = contributions.flatMap((c) => c.commitResults).flatMap((r) => r.tags);

            if (tags.some((tag) => tag && this.isMatchSearchedTag(searchedTags, tag))) {
              const user = repo.users?.find((u) => u.name === author);
              if (user) {
                this.updateCheckedFileTypeContribution(user);
                res.push(user);
              }
            }
          });

          if (res.length) {
            full.push(res);
          }
        });
      } else {
        groups.forEach((repo) => {
          const res: Array<User> = [];

          // filtering
          repo.users?.forEach((user) => {
            if (this.isMatchSearchedUser(this.filterSearch, user)) {
              this.getUserCommits(
                user,
                new Date(this.filterSinceDate) > new Date(user.sinceDate) ? this.filterSinceDate : user.sinceDate,
                new Date(this.filterUntilDate) < new Date(user.untilDate) ? this.filterUntilDate : user.untilDate,
              );
              if (this.filterTimeFrame === 'week') {
                this.splitCommitsWeek(user, this.filterSinceDate, this.filterUntilDate);
              }
              this.updateCheckedFileTypeContribution(user);
              res.push(user);
            }
          });

          if (res.length) {
            full.push(res);
          }
        });
      }

      this.filtered = full;
      this.getOptionWithOrder();

      const filterControl = {
        filterGroupSelection: this.filterGroupSelection,
        sortingOption: this.sortingOption,
        sortingWithinOption: this.sortingWithinOption,
        isSortingDsc: this.isSortingDsc,
        isSortingWithinDsc: this.isSortingWithinDsc,
      };
      this.getOptionWithOrder();
      this.filtered = sortFiltered(this.filtered, filterControl);
    },

    updateMergedGroup(allGroupsMerged: boolean): void {
      // merge group is not allowed when group by none
      // also reset merged groups
      if (this.filterGroupSelection === 'groupByNone' || !allGroupsMerged) {
        this.$store.commit('updateMergedGroup', []);
      } else {
        const mergedGroups: Array<string> = [];
        this.filtered.forEach((group) => {
          mergedGroups.push(this.getGroupName(group));
        });
        this.$store.commit('updateMergedGroup', mergedGroups);
      }
    },

    getMergedRepos(): void {
      this.filtered.forEach((group, groupIndex) => {
        if (this.mergedGroups.includes(this.getGroupName(group))) {
          this.mergeGroupByIndex(this.filtered, groupIndex);
        }
      });
    },

    mergeGroupByIndex(filtered: Array<Array<User>>, groupIndex: number): void {
      const dateToIndexMap: { [key: string]: number } = {};
      const dailyIndexMap: { [key: string]: number } = {};
      const mergedCommits: Array<Commit> = [];
      const mergedDailyCommits: Array<DailyCommit> = [];
      const mergedFileTypeContribution: AuthorFileTypeContributions = {};
      let mergedVariance = 0;
      let totalMergedCheckedFileTypeCommits = 0;
      filtered[groupIndex].forEach((user) => {
        user.commits?.forEach((commit) => {
          this.mergeCommits(commit, user, dateToIndexMap, mergedCommits);
        });
        user.dailyCommits.forEach((commit) => {
          this.mergeCommits(commit, user, dailyIndexMap, mergedDailyCommits);
        });

        this.mergeFileTypeContribution(user, mergedFileTypeContribution);

        totalMergedCheckedFileTypeCommits += user.checkedFileTypeContribution || 0;
        mergedVariance += user.variance;
      });
      mergedCommits.sort(window.comparator((ele) => ele.date));
      filtered[groupIndex][0].commits = mergedCommits;
      filtered[groupIndex][0].dailyCommits = mergedDailyCommits;
      filtered[groupIndex][0].fileTypeContribution = mergedFileTypeContribution;
      filtered[groupIndex][0].variance = mergedVariance;
      filtered[groupIndex][0].checkedFileTypeContribution = totalMergedCheckedFileTypeCommits;

      // only take the merged group
      filtered[groupIndex] = filtered[groupIndex].slice(0, 1);
    },

    hasMergedGroups(): boolean {
      return this.mergedGroups.length > 0;
    },

    mergeCommits(
      commit: Commit | DailyCommit,
      user: User,
      dateToIndexMap: { [key: string]: number },
      merged: Array<Commit> | Array<DailyCommit>,
    ): void {
      const {
        commitResults, date,
      } = commit;

      // bind repoId to each commit
      commitResults.forEach((commitResult) => {
        commitResult.repoId = user.repoId;
      });

      if (Object.prototype.hasOwnProperty.call(dateToIndexMap, date)) {
        const commitWithSameDate = merged[dateToIndexMap[date]];

        commitResults.forEach((commitResult) => {
          commitWithSameDate.commitResults.push(commitResult);
        });
        if (isCommit(commit) && isCommit(commitWithSameDate)) {
          const { insertions, deletions } = commit;
          commitWithSameDate.insertions += insertions;
          commitWithSameDate.deletions += deletions;
        }
      } else {
        dateToIndexMap[date] = Object.keys(dateToIndexMap).length;
        merged.push(JSON.parse(JSON.stringify(commit)));
      }
    },

    mergeFileTypeContribution(user: User, merged: AuthorFileTypeContributions): void {
      Object.entries(user.fileTypeContribution).forEach((fileType) => {
        const key = fileType[0];
        const value = fileType[1];

        if (!Object.prototype.hasOwnProperty.call(merged, key)) {
          merged[key] = 0;
        }
        merged[key] += value;
      });
    },

    splitCommitsWeek(user: User, sinceDate: string, untilDate: string): void {
      const { commits } = user;
      if (commits === undefined) {
        return;
      }

      const res: Array<Commit> = [];

      const nextMondayDate = this.dateRounding(sinceDate, 0); // round up for the next monday

      const nextMondayMs = (new Date(nextMondayDate)).getTime();
      const sinceMs = new Date(sinceDate).getTime();
      const untilMs = (new Date(untilDate)).getTime();

      if (nextMondayDate <= untilDate) {
        this.pushCommitsWeek(sinceMs, nextMondayMs - 1, res, commits);
        this.pushCommitsWeek(nextMondayMs, untilMs, res, commits);
      } else {
        this.pushCommitsWeek(sinceMs, untilMs, res, commits);
      }
      user.commits = res;
    },

    pushCommitsWeek(sinceMs: number, untilMs: number, res: Array<Commit>, commits: Array<Commit>): void {
      const diff = Math.round(Math.abs((untilMs - sinceMs) / window.DAY_IN_MS));
      const weekInMS = window.DAY_IN_MS * 7;

      for (let weekId = 0; weekId < diff / 7; weekId += 1) {
        const startOfWeekMs = sinceMs + (weekId * weekInMS);
        const endOfWeekMs = startOfWeekMs + weekInMS - window.DAY_IN_MS;
        const endOfWeekMsWithinUntilMs = endOfWeekMs <= untilMs ? endOfWeekMs : untilMs;

        const week: Commit = {
          insertions: 0,
          deletions: 0,
          date: window.getDateStr(startOfWeekMs),
          endDate: window.getDateStr(endOfWeekMsWithinUntilMs),
          commitResults: [],
        };

        this.addLineContributionWeek(endOfWeekMsWithinUntilMs, week, commits);
        if (week.commitResults.length > 0) {
          res.push(week);
        }
      }
    },

    addLineContributionWeek(endOfWeekMs: number, week: Commit, commits: Array<Commit>): void {
      // commits are not contiguous, meaning there are gaps of days without
      // commits, so we are going to check each commit's date and make sure
      // it is within the duration of a week
      while (commits.length > 0
          && (new Date(commits[0].date)).getTime() <= endOfWeekMs) {
        const commit = commits.shift();
        // shift() never returns undefined here because we check for commits.length > 0,
        // but TypeScript is unable to infer this
        if (commit === undefined) {
          break;
        }
        week.insertions += commit.insertions;
        week.deletions += commit.deletions;
        commit.commitResults.forEach((commitResult) => week.commitResults.push(commitResult));
      }
    },
    getOptionWithOrder(): void {
      const [sortingOption, isSortingDsc] = this.sortGroupSelection.split(' ');
      this.sortingOption = sortingOption;
      this.isSortingDsc = isSortingDsc === 'dsc';

      const [sortingWithinOption, isSortingWithinDsc] = this.sortWithinGroupSelection.split(' ');
      this.sortingWithinOption = sortingWithinOption;
      this.isSortingWithinDsc = isSortingWithinDsc === 'dsc';
    },

    // updating filters programmatically //
    resetDateRange(): void {
      this.hasModifiedSinceDate = false;
      this.hasModifiedUntilDate = false;
      this.tmpFilterSinceDate = '';
      this.tmpFilterUntilDate = '';
      window.removeHash('since');
      window.removeHash('until');
      this.getFiltered();
    },

    restoreZoomFiltered(info: ZoomInfo): void {
      const {
        zSince, zUntil, zTimeFrame, zIsMerged, zFilterSearch,
      } = info;
      const filtered: Array<Array<User>> = [];

      const groups: Array<Repo> = JSON.parse(JSON.stringify(this.repos));

      const res: Array<User> = [];
      groups.forEach((repo) => {
        repo.users?.forEach((user) => {
          // only filter users that match with zoom user and previous searched user
          if (this.matchZoomUser(info, user) && this.isMatchSearchedUser(zFilterSearch, user)) {
            this.getUserCommits(user, zSince, zUntil);
            if (zTimeFrame === 'week') {
              this.splitCommitsWeek(user, zSince, zUntil);
            }
            this.updateCheckedFileTypeContribution(user);
            res.push(user);
          }
        });
      });

      if (res.length) {
        filtered.push(res);
      }

      if (zIsMerged) {
        this.mergeGroupByIndex(filtered, 0);
      }

      if (filtered.length) [[info.zUser]] = filtered;

      info.zFileTypeColors = this.fileTypeColors;
      info.isRefreshing = false;
      this.$store.commit('updateTabZoomInfo', info);
    },

    dateRounding(datestr: string, roundDown: number): string {
      // rounding up to nearest monday
      const date = new Date(datestr);
      const day = date.getUTCDay();
      let datems = date.getTime();
      if (roundDown) {
        datems -= ((day + 6) % 7) * window.DAY_IN_MS;
      } else {
        datems += ((8 - day) % 7) * window.DAY_IN_MS;
      }

      return window.getDateStr(datems);
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
