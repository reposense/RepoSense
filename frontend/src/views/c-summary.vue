<template lang="pug">
#summary
  form.summary-picker.mui-form--inline(v-if="!isWidgetMode", onsubmit="return false;")
    .summary-picker__section
      .mui-textfield.search_box
        input(type="text", v-on:change="updateFilterSearch", v-model="filterSearch")
        label search
        button.mui-btn.mui-btn--raised(type="button", v-on:click.prevent="resetFilterSearch") x
      .mui-select.grouping
        select(v-model="filterGroupSelection")
          option(value="groupByNone") None
          option(value="groupByRepos") Repo/Branch
          option(value="groupByAuthors") Author
        label group by
      .mui-select.sort-group
        select(v-model="sortGroupSelection", v-on:change="getFiltered")
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
          v-bind:disabled="filterGroupSelection === 'groupByNone' || allGroupsMerged",
          v-on:change="getFiltered"
        )
          option(value="title") &uarr; title
          option(value="title dsc") &darr; title
          option(value="totalCommits") &uarr; contribution
          option(value="totalCommits dsc") &darr; contribution
          option(value="variance") &uarr; variance
          option(value="variance dsc") &darr; variance
        label sort within groups by
      .mui-select.granularity
        select(v-model="filterTimeFrame", v-on:change="getFiltered")
          option(value="commit") Commit
          option(value="day") Day
          option(value="week") Week
        label granularity
      .mui-textfield
        input(v-if="isSafariBrowser", type="text", placeholder="yyyy-mm-dd",
          v-bind:value="filterSinceDate", v-on:keyup.enter="updateTmpFilterSinceDate",
          onkeydown="formatInputDateOnKeyDown(event)", oninput="appendDashInputDate(event)", maxlength=10)
        input(v-else, type="date", name="since", v-bind:value="filterSinceDate", v-on:input="updateTmpFilterSinceDate",
          v-bind:min="minDate", v-bind:max="filterUntilDate")
        label since
      .mui-textfield
        input(v-if="isSafariBrowser", type="text", placeholder="yyyy-mm-dd",
          v-bind:value="filterUntilDate", v-on:keyup.enter="updateTmpFilterUntilDate",
          onkeydown="formatInputDateOnKeyDown(event)", oninput="appendDashInputDate(event)", maxlength=10)
        input(v-else, type="date", name="until", v-bind:value="filterUntilDate", v-on:input="updateTmpFilterUntilDate",
          v-bind:min="filterSinceDate", v-bind:max="maxDate")
        label until
      .mui-textfield
        a(v-on:click="resetDateRange") Reset date range
      .summary-picker__checkboxes.summary-picker__section
        label.filter-breakdown
          input.mui-checkbox(
            type="checkbox",
            v-model="filterBreakdown",
            v-on:change="toggleBreakdown"
          )
          span breakdown by file type
        label.merge-group(
          v-bind:style="filterGroupSelection === 'groupByNone' ? { opacity:0.5 } : { opacity:1.0 }"
        )
          input.mui-checkbox(
            type="checkbox",
            v-model="allGroupsMerged",
            v-bind:disabled="filterGroupSelection === 'groupByNone'"
          )
          span merge all groups
        label.show-tags
          input.mui-checkbox(
            type="checkbox",
            v-model="viewRepoTags",
            v-on:change="getFiltered"
          )
          span show tags
  .error-message-box(v-if="Object.entries(errorMessages).length && !isWidgetMode")
    .error-message-box__close-button(v-on:click="dismissTab($event)") &times;
    .error-message-box__message The following issues occurred when analyzing the following repositories:
    .error-message-box__failed-repo(
        v-for="errorBlock in errorIsShowingMore\
          ? errorMessages\
          : Object.values(errorMessages).slice(0, numberOfErrorMessagesToShow)"
      )
      font-awesome-icon(icon="exclamation")
      span.error-message-box__failed-repo--name {{ errorBlock.repoName }}
      .error-message-box__failed-repo--reason(
        v-if="errorBlock.errorMessage.startsWith('Unexpected error stack trace')"
      )
        span Oops, an unexpected error occurred. If this is due to a problem in RepoSense, please report in&nbsp;
        a(
          v-bind:href="getReportIssueGitHubLink(errorBlock.errorMessage)", target="_blank"
        )
          strong our issue tracker&nbsp;
        span or email us at&nbsp;
        a(
          v-bind:href="getReportIssueEmailLink(errorBlock.errorMessage)"
        )
          span {{ getReportIssueEmailAddress() }}
      .error-message-box__failed-repo--reason(v-else) {{ errorBlock.errorMessage }}\
    .error-message-box__show-more-container(v-if="Object.keys(errorMessages).length > numberOfErrorMessagesToShow")
      span(v-if="!errorIsShowingMore") Remaining error messages omitted to save space.&nbsp;
      a(v-if="!errorIsShowingMore", v-on:click="toggleErrorShowMore()") SHOW ALL...
      a(v-else, v-on:click="toggleErrorShowMore()") SHOW LESS...
  .fileTypes(v-if="filterBreakdown && !isWidgetMode")
    .checkboxes.mui-form--inline(v-if="Object.keys(fileTypeColors).length > 0")
      label(style='background-color: #000000; color: #ffffff')
        input.mui-checkbox--fileType#all(type="checkbox", v-model="checkAllFileTypes")
        span All:&nbsp;
      template(v-for="fileType in Object.keys(fileTypeColors)", v-bind:key="fileType")
        label(
          v-bind:style="{ 'background-color': fileTypeColors[fileType], \
            'color': getFontColor(fileTypeColors[fileType])}"
        )
          input.mui-checkbox--fileType(
            type="checkbox",
            v-bind:id="fileType",
            v-bind:value="fileType",
            v-model="checkedFileTypes",
            v-on:change="getFiltered"
          )
          span {{ fileType }}
  c-summary-charts(
    v-bind:filtered="filtered",
    v-bind:checked-file-types="checkedFileTypes",
    v-bind:avg-contribution-size="avgContributionSize",
    v-bind:filter-group-selection="filterGroupSelection",
    v-bind:filter-breakdown="filterBreakdown",
    v-bind:filter-time-frame="filterTimeFrame",
    v-bind:filter-since-date="filterSinceDate",
    v-bind:filter-until-date="filterUntilDate",
    v-bind:filter-search="filterSearch",
    v-bind:min-date="minDate",
    v-bind:max-date="maxDate",
    v-bind:sort-group-selection="sortGroupSelection",
    v-bind:chart-group-index="chartGroupIndex",
    v-bind:chart-index="chartIndex",
    v-bind:view-repo-tags="viewRepoTags"
  )
</template>

<script lang='ts'>
import { mapState } from 'vuex';
import { PropType, defineComponent } from 'vue';

import cSummaryCharts from '../components/c-summary-charts.vue';
import getNonRepeatingColor from '../utils/random-color-generator';
import sortFiltered from '../utils/repo-sorter';
import {
  Commit,
  DailyCommit,
  Repo,
  User,
  isCommit,
  CommitResult,
} from '../types/types';
import { ErrorMessage } from '../types/zod/summary-type';
import {
  AuthorDailyContributions,
  AuthorFileTypeContributions,
  FileTypeAndContribution,
} from '../types/zod/commits-type';
import { ZoomInfo } from '../types/vuex.d';
import {
  FilterGroupSelection, FilterTimeFrame, SortGroupSelection, SortWithinGroupSelection,
} from '../types/summary';

const dateFormatRegex = /([12]\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01]))$/;

export default defineComponent({
  name: 'c-summary',
  components: {
    cSummaryCharts,
  },
  props: {
    repos: {
      type: Array<Repo>,
      required: true,
    },
    errorMessages: {
      type: Object as PropType<ErrorMessage>,
      default() {
        return {};
      },
    },
    isWidgetMode: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      checkedFileTypes: [] as Array<string>,
      fileTypes: [] as Array<string>,
      filtered: [] as Array<Array<User>>,
      filterSearch: '',
      filterGroupSelection: FilterGroupSelection.GroupByRepos,
      sortGroupSelection: SortGroupSelection.GroupTitleDsc, // UI for sorting groups
      sortWithinGroupSelection: SortWithinGroupSelection.Title, // UI for sorting within groups
      sortingOption: '',
      isSortingDsc: '',
      sortingWithinOption: '',
      isSortingWithinDsc: '',
      filterTimeFrame: FilterTimeFrame.Commit,
      filterBreakdown: false,
      tmpFilterSinceDate: '',
      tmpFilterUntilDate: '',
      hasModifiedSinceDate: window.isSinceDateProvided,
      hasModifiedUntilDate: window.isUntilDateProvided,
      filterHash: '',
      minDate: window.sinceDate,
      maxDate: window.untilDate,
      fileTypeColors: {} as { [key: string]: string },
      isSafariBrowser: /.*Version.*Safari.*/.test(navigator.userAgent),
      filterGroupSelectionWatcherFlag: false,
      chartGroupIndex: undefined as number | undefined,
      chartIndex: undefined as number | undefined,
      errorIsShowingMore: false,
      numberOfErrorMessagesToShow: 4,
      viewRepoTags: false,
    };
  },
  computed: {
    checkAllFileTypes: {
      get() {
        return this.checkedFileTypes.length === this.fileTypes.length;
      },
      set(value: boolean) {
        if (value) {
          this.checkedFileTypes = this.fileTypes.slice();
        } else {
          this.checkedFileTypes = [];
        }
        this.getFiltered();
      },
    },

    avgContributionSize() {
      let totalLines = 0;
      let totalCount = 0;
      this.repos.forEach((repo) => {
        repo.users?.forEach((user) => {
          if (user.checkedFileTypeContribution === undefined || user.checkedFileTypeContribution === 0) {
            this.updateCheckedFileTypeContribution(user);
          }

          if (user.checkedFileTypeContribution && user.checkedFileTypeContribution > 0) {
            totalCount += 1;
            totalLines += user.checkedFileTypeContribution;
          }
        });
      });
      if (totalCount === 0) {
        return 0;
      }
      return totalLines / totalCount;
    },

    allGroupsMerged: {
      get() {
        if (this.mergedGroups.length === 0) {
          return false;
        }
        return this.mergedGroups.length === this.filtered.length;
      },
      set(value: boolean) {
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

    filterSinceDate() {
      if (this.tmpFilterSinceDate && this.tmpFilterSinceDate >= this.minDate) {
        return this.tmpFilterSinceDate;
      }
      // If user clears the since date field
      return this.minDate;
    },

    filterUntilDate() {
      if (this.tmpFilterUntilDate && this.tmpFilterUntilDate <= this.maxDate) {
        return this.tmpFilterUntilDate;
      }
      return this.maxDate;
    },

    ...mapState(['mergedGroups']),
  },
  watch: {

    filterGroupSelection() {
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

    '$store.state.summaryDates': function () {
      this.hasModifiedSinceDate = true;
      this.hasModifiedUntilDate = true;
      this.tmpFilterSinceDate = this.$store.state.summaryDates.since;
      this.tmpFilterUntilDate = this.$store.state.summaryDates.until;
      window.deactivateAllOverlays();
      this.getFiltered();
    },

    mergedGroups: {
      deep: true,
      handler() {
        this.getFiltered();
      },
    },
  },
  created() {
    this.processFileTypes();
    this.renderFilterHash();
    this.getFiltered();
    if (this.$store.state.tabZoomInfo.isRefreshing) {
      const zoomInfo = Object.assign({}, this.$store.state.tabZoomInfo);
      this.restoreZoomFiltered(zoomInfo);
    }
  },
  mounted() {
    // Delay execution of filterGroupSelection watcher
    // to prevent clearing of merged groups
    setTimeout(() => {
      this.filterGroupSelectionWatcherFlag = true;
    }, 0);
  },
  methods: {
    dismissTab(event: Event) {
      if (event.target instanceof Element && event.target.parentNode instanceof HTMLElement) {
        event.target.parentNode.style.display = 'none';
      }
    },

    // view functions //
    getReportIssueGitHubLink(stackTrace: string) {
      return `${window.REPOSENSE_REPO_URL}/issues/new?title=${this.getReportIssueTitle()
      }&body=${this.getReportIssueMessage(stackTrace)}`;
    },

    getReportIssueEmailAddress() {
      return 'seer@comp.nus.edu.sg';
    },

    getReportIssueEmailLink(stackTrace: string) {
      return `mailto:${this.getReportIssueEmailAddress()}?subject=${this.getReportIssueTitle()
      }&body=${this.getReportIssueMessage(stackTrace)}`;
    },

    getReportIssueTitle() {
      return `${encodeURI('Unexpected error with RepoSense version ')}${window.repoSenseVersion}`;
    },

    getReportIssueMessage(message: string) {
      return encodeURI(message);
    },

    // model functions //
    resetFilterSearch() {
      this.filterSearch = '';
      this.getFiltered();
    },
    updateFilterSearch(evt: Event) {
      // Only called from an input onchange event, target guaranteed to be input element
      this.filterSearch = (evt.target as HTMLInputElement).value;
      this.getFiltered();
    },

    setSummaryHash() {
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

      encodeHash();
    },

    renderFilterHash() {
      const convertBool = (txt: string) => (txt === 'true');
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
    },

    getGroupName(group: Array<User>) {
      return window.getGroupName(group, this.filterGroupSelection);
    },

    isMatchSearchedUser(filterSearch: string, user: User) {
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

    toggleBreakdown() {
      // Reset the file type filter
      if (this.checkedFileTypes.length !== this.fileTypes.length) {
        this.checkedFileTypes = this.fileTypes.slice();
      }
      this.getFiltered();
    },

    async getFiltered() {
      this.setSummaryHash();
      window.deactivateAllOverlays();

      await this.$store.dispatch('incrementLoadingOverlayCountForceReload', 1);
      this.getFilteredRepos();
      this.getMergedRepos();
      await this.$store.dispatch('incrementLoadingOverlayCountForceReload', -1);
    },

    getFilteredRepos() {
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
              this.getUserCommits(user, this.filterSinceDate, this.filterUntilDate);
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

    updateMergedGroup(allGroupsMerged: boolean) {
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

    getMergedRepos() {
      this.filtered.forEach((group, groupIndex) => {
        if (this.mergedGroups.includes(this.getGroupName(group))) {
          this.mergeGroupByIndex(this.filtered, groupIndex);
        }
      });
    },

    mergeGroupByIndex(filtered: Array<Array<User>>, groupIndex: number) {
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

    hasMergedGroups() {
      return this.mergedGroups.length > 0;
    },

    mergeCommits(
      commit: Commit | DailyCommit,
      user: User,
      dateToIndexMap: { [key: string]: number },
      merged: Array<Commit> | Array<DailyCommit>,
    ) {
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

    mergeFileTypeContribution(user: User, merged: AuthorFileTypeContributions) {
      Object.entries(user.fileTypeContribution).forEach((fileType) => {
        const key = fileType[0];
        const value = fileType[1];

        if (!Object.prototype.hasOwnProperty.call(merged, key)) {
          merged[key] = 0;
        }
        merged[key] += value;
      });
    },

    processFileTypes() {
      const selectedColors = ['#ffe119', '#4363d8', '#3cb44b', '#f58231', '#911eb4', '#46f0f0', '#f032e6',
        '#bcf60c', '#fabebe', '#008080', '#e6beff', '#9a6324', '#fffac8', '#800000', '#aaffc3', '#808000', '#ffd8b1',
        '#000075', '#808080'];
      const fileTypeColors = {} as { [key: string]: string };
      let i = 0;

      this.repos.forEach((repo) => {
        repo.users?.forEach((user) => {
          Object.keys(user.fileTypeContribution).forEach((fileType) => {
            if (!Object.prototype.hasOwnProperty.call(fileTypeColors, fileType)) {
              if (i < selectedColors.length) {
                fileTypeColors[fileType] = selectedColors[i];
                i += 1;
              } else {
                fileTypeColors[fileType] = getNonRepeatingColor(Object.values(fileTypeColors));
              }
            }
            if (!this.fileTypes.includes(fileType)) {
              this.fileTypes.push(fileType);
            }
          });
        });
        this.fileTypeColors = fileTypeColors;
      });

      this.checkedFileTypes = this.fileTypes.slice();
      this.$store.commit('updateFileTypeColors', this.fileTypeColors);
    },

    splitCommitsWeek(user: User, sinceDate: string, untilDate: string) {
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

    pushCommitsWeek(sinceMs: number, untilMs: number, res: Array<Commit>, commits: Array<Commit>) {
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

    addLineContributionWeek(endOfWeekMs: number, week: Commit, commits: Array<Commit>) {
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

    getUserCommits(user: User, sinceDate: string, untilDate: string) {
      user.commits = [];
      const userFirst = user.dailyCommits[0];
      const userLast = user.dailyCommits[user.dailyCommits.length - 1];

      if (!userFirst) {
        return null;
      }

      if (!sinceDate || sinceDate === 'undefined') {
        sinceDate = userFirst.date;
      }

      if (!untilDate) {
        untilDate = userLast.date;
      }

      user.dailyCommits.forEach((commit) => {
        const { date } = commit;
        if (date >= sinceDate && date <= untilDate) {
          const filteredCommit: DailyCommit = JSON.parse(JSON.stringify(commit));
          this.filterCommitByCheckedFileTypes(filteredCommit);

          if (filteredCommit.commitResults.length > 0) {
            filteredCommit.commitResults.forEach((commitResult) => {
              if (commitResult.messageBody !== '') {
                commitResult.isOpen = true;
              }
            });
            // The typecast is safe here as we add the insertions and deletions fields
            // in the filterCommitByCheckedFileTypes method above
            user.commits?.push(filteredCommit as Commit);
          }
        }
      });

      return null;
    },

    filterCommitByCheckedFileTypes(commit: DailyCommit) {
      let commitResults = commit.commitResults.map((result) => {
        const filteredFileTypes = this.getFilteredFileTypes(result);
        this.updateCommitResultWithFileTypes(result, filteredFileTypes);
        return result;
      });

      if (!this.checkAllFileTypes) {
        commitResults = commitResults.filter(
          (result) => Object.values(result.fileTypesAndContributionMap).length > 0,
        );
      }

      // Typecast from DailyCommit to Commit as we add insertions and deletions fields
      (commit as Commit).insertions = commitResults.reduce((acc, result) => acc + result.insertions, 0);
      (commit as Commit).deletions = commitResults.reduce((acc, result) => acc + result.deletions, 0);
      commit.commitResults = commitResults;
    },

    getFilteredFileTypes(commitResult: CommitResult) {
      return Object.keys(commitResult.fileTypesAndContributionMap)
        .filter(this.isFileTypeChecked)
        .reduce((obj: { [key: string]: FileTypeAndContribution }, fileType) => {
          obj[fileType] = commitResult.fileTypesAndContributionMap[fileType];
          return obj;
        }, {});
    },

    isFileTypeChecked(fileType: string) {
      if (this.filterBreakdown) {
        return this.checkedFileTypes.includes(fileType);
      }
      return true;
    },

    updateCommitResultWithFileTypes(
      commitResult: CommitResult,
      filteredFileTypes: { [key: string]: FileTypeAndContribution },
    ) {
      commitResult.insertions = Object.values(filteredFileTypes)
        .reduce((acc, fileType) => acc + fileType.insertions, 0);
      commitResult.deletions = Object.values(filteredFileTypes)
        .reduce((acc, fileType) => acc + fileType.deletions, 0);
      commitResult.fileTypesAndContributionMap = filteredFileTypes;
    },

    getOptionWithOrder() {
      [this.sortingOption, this.isSortingDsc] = this.sortGroupSelection.split(' ');
      [this.sortingWithinOption, this.isSortingWithinDsc] = this.sortWithinGroupSelection.split(' ');
    },

    // updating filters programically //
    resetDateRange() {
      this.hasModifiedSinceDate = false;
      this.hasModifiedUntilDate = false;
      this.tmpFilterSinceDate = '';
      this.tmpFilterUntilDate = '';
      window.removeHash('since');
      window.removeHash('until');
      this.getFiltered();
    },

    updateTmpFilterSinceDate(event: Event) {
      // Only called from an input onchange event, target guaranteed to be input element
      const since = (event.target as HTMLInputElement).value;
      this.hasModifiedSinceDate = true;

      if (!this.isSafariBrowser) {
        this.tmpFilterSinceDate = since;
        (event.target as HTMLInputElement).value = this.filterSinceDate;
        this.getFiltered();
      } else if (dateFormatRegex.test(since) && since >= this.minDate) {
        this.tmpFilterSinceDate = since;
        (event.currentTarget as HTMLInputElement).style.removeProperty('border-bottom-color');
        this.getFiltered();
      } else {
        // invalid since date detected
        (event.currentTarget as HTMLInputElement).style.borderBottomColor = 'red';
      }
    },

    updateTmpFilterUntilDate(event: Event) {
      // Only called from an input onchange event, target guaranteed to be input element
      const until = (event.target as HTMLInputElement).value;
      this.hasModifiedUntilDate = true;

      if (!this.isSafariBrowser) {
        this.tmpFilterUntilDate = until;
        (event.target as HTMLInputElement).value = this.filterUntilDate;
        this.getFiltered();
      } else if (dateFormatRegex.test(until) && until <= this.maxDate) {
        this.tmpFilterUntilDate = until;
        (event.currentTarget as HTMLInputElement).style.removeProperty('border-bottom-color');
        this.getFiltered();
      } else {
        // invalid until date detected
        (event.currentTarget as HTMLInputElement).style.borderBottomColor = 'red';
      }
    },

    updateCheckedFileTypeContribution(ele: User) {
      let validCommits = 0;
      Object.keys(ele.fileTypeContribution).forEach((fileType) => {
        if (!this.filterBreakdown) {
          validCommits += ele.fileTypeContribution[fileType];
        } else if (this.checkedFileTypes.includes(fileType)) {
          validCommits += ele.fileTypeContribution[fileType];
        }
      });
      ele.checkedFileTypeContribution = validCommits;
    },

    restoreZoomFiltered(info: ZoomInfo) {
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
    matchZoomUser(info: ZoomInfo, user: User) {
      const {
        zIsMerged, zFilterGroup, zRepo, zAuthor,
      } = info;
      if (zIsMerged) {
        return zFilterGroup === 'groupByRepos'
          ? user.repoName === zRepo
          : user.name === zAuthor;
      }
      return user.repoName === zRepo && user.name === zAuthor;
    },

    dateRounding(datestr: string, roundDown: number) {
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

    getFontColor(color: string) {
      return window.getFontColor(color);
    },

    toggleErrorShowMore() {
      this.errorIsShowingMore = !this.errorIsShowingMore;
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
</style>
