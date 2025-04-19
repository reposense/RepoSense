import {PropType, defineComponent} from "vue";

import getNonRepeatingColor from '../utils/random-color-generator';

import {
  Commit,
  DailyCommit,
  Repo,
  User,
  CommitResult,
} from '../types/types';
import {FileTypeAndContribution} from '../types/zod/commits-type';
import {ErrorMessage} from '../types/zod/summary-type';
import {ZoomInfo} from '../types/vuex';

export default defineComponent({
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
    checkedFileTypes: string[],
    fileTypes: string[],
    filtered: User[][],
    filterBreakdown: boolean,
    fileTypeColors: { [key: string]: string },
    filterGroupSelectionWatcherFlag: boolean,
    optimiseTimeline: boolean,
  } {
    return {
      checkedFileTypes: [],
      fileTypes: [],
      filtered: [],
      filterBreakdown: window.isPortfolio, // Auto select filter breakdown if portfolio
      fileTypeColors: {} as { [key: string]: string },
      filterGroupSelectionWatcherFlag: false,
      optimiseTimeline: window.isPortfolio, // Auto select trim timeline if portfolio
    };
  },

  computed: {
    avgContributionSize(): number {
      let totalLines = 0;
      let totalCount = 0;
      this.repos.forEach((repo) => {
        repo.users?.forEach((user: User) => {
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

    isInputDateSupported(): boolean {
      // Safari versions below 14.1 not supported
      const userAgent = navigator.userAgent;
      const safariVersionRegex = /Version\/([\d.]+).*Safari./;
      const versionMatch = userAgent.match(safariVersionRegex);

      if (!versionMatch || !versionMatch[1]) {
        return true; // Not Safari or version parsing failed
      }

      const versionParts = versionMatch[1].split('.').map(Number);
      const major = versionParts[0];
      const minor = versionParts[1] || 0;

      return major > 14 || (major === 14 && minor >= 1);
    },
  },

  methods: {
    // view functions //
    getRepoSenseHomeLink(): string {
      const version = window.repoSenseVersion;
      if (!version) {
        return `${window.HOME_PAGE_URL}/RepoSense/`;
      }
      return `${window.HOME_PAGE_URL}`;
    },

    getLogoPath(): string {
      return window.LOGO_PATH;
    },

    // model functions //
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
        const {date} = commit;
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

    getFilteredFileTypes(commitResult: CommitResult): {
      [key: string]: { insertions: number, deletions: number },
    } {
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

    isAllFileTypesChecked(): boolean {
      return this.checkedFileTypes.length === this.fileTypes.length;
    },
  }
});
