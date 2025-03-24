<template lang="pug">
#summary
  c-summary-header(
    v-if="!isWidgetMode",
    v-model:filter-breakdown="filterBreakdown",
    v-model:optimise-timeline="optimiseTimeline",
    :is-safari-browser="isSafariBrowser",
    @get-filtered="getFiltered",
    @toggle-breakdown="toggleBreakdown"
  )

  .error-message-box(v-if="Object.entries(errorMessages).length && !isWidgetMode")
    .error-message-box__close-button(@click="dismissTab($event)") &times;
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
          :href="getReportIssueGitHubLink(errorBlock.errorMessage)", target="_blank"
        )
          strong our issue tracker&nbsp;
        span or email us at&nbsp;
        a(
          :href="getReportIssueEmailLink(errorBlock.errorMessage)"
        )
          span {{ getReportIssueEmailAddress() }}
      .error-message-box__failed-repo--reason(v-else) {{ errorBlock.errorMessage }}\
    .error-message-box__show-more-container(v-if="Object.keys(errorMessages).length > numberOfErrorMessagesToShow")
      span(v-if="!errorIsShowingMore") Remaining error messages omitted to save space.&nbsp;
      a(v-if="!errorIsShowingMore", @click="toggleErrorShowMore()") SHOW ALL...
      a(v-else, @click="toggleErrorShowMore()") SHOW LESS...
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
</template>

<script lang='ts'>
import { PropType, defineComponent } from 'vue';

import cSummaryCharts from '../components/c-summary-charts.vue';
import cFileTypeCheckboxes from '../components/c-file-type-checkboxes.vue';
import cSummaryHeader from '../components/c-summary-header.vue';
import getNonRepeatingColor from '../utils/random-color-generator';
import sortFiltered from '../utils/repo-sorter';
import {
  Commit,
  DailyCommit,
  Repo,
  User,
  CommitResult,
} from '../types/types';
import { ErrorMessage } from '../types/zod/summary-type';
import {
  FileTypeAndContribution,
} from '../types/zod/commits-type';
import { ZoomInfo } from '../types/vuex.d';
import {
  FilterGroupSelection, SortGroupSelection, SortWithinGroupSelection,
} from '../types/summary';

export default defineComponent({
  name: 'c-summary-portfolio',
  components: {
    cSummaryCharts,
    cFileTypeCheckboxes,
    cSummaryHeader,
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
  data(): {
    checkedFileTypes: Array<string>,
    fileTypes: Array<string>,
    filtered: Array<Array<User>>,
    filterBreakdown: boolean,
    fileTypeColors: { [key: string]: string },
    isSafariBrowser: boolean,
    filterGroupSelectionWatcherFlag: boolean,
    errorIsShowingMore: boolean,
    numberOfErrorMessagesToShow: number,
    optimiseTimeline: boolean,
    since: string,
    until: string,
  } {
    return {
      checkedFileTypes: [] as Array<string>,
      fileTypes: [] as Array<string>,
      filtered: [] as Array<Array<User>>,
      filterBreakdown: window.isPortfolio, // Auto select filter breakdown if portfolio
      fileTypeColors: {} as { [key: string]: string },
      isSafariBrowser: /.*Version.*Safari.*/.test(navigator.userAgent),
      filterGroupSelectionWatcherFlag: false,
      errorIsShowingMore: false,
      numberOfErrorMessagesToShow: 4,
      optimiseTimeline: window.isPortfolio, // Auto select trim timeline if portfolio
      since: '',
      until: '',
    };
  },
  computed: {
    avgContributionSize(): number {
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
  mounted(): void {
    // Delay execution of filterGroupSelection watcher
    // to prevent clearing of merged groups
    setTimeout(() => {
      this.filterGroupSelectionWatcherFlag = true;
    }, 0);
  },
  methods: {
    dismissTab(event: Event): void {
      if (event.target instanceof Element && event.target.parentNode instanceof HTMLElement) {
        event.target.parentNode.style.display = 'none';
      }
    },

    // view functions //
    getReportIssueGitHubLink(stackTrace: string): string {
      return `${window.REPOSENSE_REPO_URL}/issues/new?title=${this.getReportIssueTitle()
      }&body=${this.getReportIssueMessage(stackTrace)}`;
    },

    getReportIssueEmailAddress(): string {
      return 'seer@comp.nus.edu.sg';
    },

    getReportIssueEmailLink(stackTrace: string): string {
      return `mailto:${this.getReportIssueEmailAddress()}?subject=${this.getReportIssueTitle()
      }&body=${this.getReportIssueMessage(stackTrace)}`;
    },

    getReportIssueTitle(): string {
      return `${encodeURI('Unexpected error with RepoSense version ')}${window.repoSenseVersion}`;
    },

    getReportIssueMessage(message: string): string {
      return encodeURI(message);
    },

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
        isSortingDsc: '',
        isSortingWithinDsc: '',
      };

      this.filtered = sortFiltered(this.filtered, filterControl);
    },

    processFileTypes(): void {
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

    getUserCommits(user: User, sinceDate: string, untilDate: string): null {
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

    filterCommitByCheckedFileTypes(commit: DailyCommit): void {
      let commitResults = commit.commitResults.map((result) => {
        const filteredFileTypes = this.getFilteredFileTypes(result);
        this.updateCommitResultWithFileTypes(result, filteredFileTypes);
        return result;
      });

      if (!this.isAllFileTypesChecked()) {
        commitResults = commitResults.filter(
          (result) => Object.values(result.fileTypesAndContributionMap).length > 0,
        );
      }

      // Typecast from DailyCommit to Commit as we add insertions and deletions fields
      (commit as Commit).insertions = commitResults.reduce((acc, result) => acc + result.insertions, 0);
      (commit as Commit).deletions = commitResults.reduce((acc, result) => acc + result.deletions, 0);
      commit.commitResults = commitResults;
    },

    getFilteredFileTypes(commitResult: CommitResult): { [key: string]: { insertions: number; deletions: number; }; } {
      return Object.keys(commitResult.fileTypesAndContributionMap)
        .filter(this.isFileTypeChecked)
        .reduce((obj: { [key: string]: FileTypeAndContribution }, fileType) => {
          obj[fileType] = commitResult.fileTypesAndContributionMap[fileType];
          return obj;
        }, {});
    },

    isFileTypeChecked(fileType: string): boolean {
      if (this.filterBreakdown) {
        return this.checkedFileTypes.includes(fileType);
      }
      return true;
    },

    updateCommitResultWithFileTypes(
      commitResult: CommitResult,
      filteredFileTypes: { [key: string]: FileTypeAndContribution },
    ): void {
      commitResult.insertions = Object.values(filteredFileTypes)
        .reduce((acc, fileType) => acc + fileType.insertions, 0);
      commitResult.deletions = Object.values(filteredFileTypes)
        .reduce((acc, fileType) => acc + fileType.deletions, 0);
      commitResult.fileTypesAndContributionMap = filteredFileTypes;
    },

    // updating filters programmatically //
    updateCheckedFileTypeContribution(ele: User): void {
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

    matchZoomUser(info: ZoomInfo, user: User): boolean {
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

    toggleErrorShowMore(): void {
      this.errorIsShowingMore = !this.errorIsShowingMore;
    },

    isAllFileTypesChecked(): boolean {
      return this.checkedFileTypes.length === this.fileTypes.length;
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
