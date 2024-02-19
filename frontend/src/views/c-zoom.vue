<template lang="pug">
#zoom
  .panel-title
    span Commits Panel
  .toolbar--multiline(v-if="filteredUser.commits.length && totalCommitMessageBodyCount")
    a(
      v-if="expandedCommitMessagesCount < totalCommitMessageBodyCount",
      v-on:click="toggleAllCommitMessagesBody(true); toggleDiffstatView(true);"
    ) show all commit details
    a(
      v-if="expandedCommitMessagesCount > 0",
      v-on:click="toggleAllCommitMessagesBody(false); toggleDiffstatView(false);"
    ) hide all commit details
  .panel-heading
    .group-name
      span(
        v-if="info.zFilterGroup === 'groupByAuthors'"
      ) {{ filteredUser.displayName }} ({{ filteredUser.name }})
      a(
        v-else,
        v-bind:href="info.zLocation", target="_blank",
        v-bind:title="'Click to open the repo'"
      )
        span {{ filteredUser.repoName }}
    .author(v-if="!info.zIsMerged")
      span &#8627; &nbsp;
      span(v-if="info.zFilterGroup === 'groupByAuthors'") {{ filteredUser.repoName }}
      span(v-else) {{ filteredUser.displayName }} ({{ filteredUser.name }})
    .period
      span &#8627; &nbsp;
      span {{ info.zSince }} to {{ info.zUntil }} &nbsp;
  .zoom__title
    .zoom__title--granularity granularity: one ramp per {{ info.zTimeFrame }}
    .zoom__title--tags
      template(v-for="commit in filteredUser.commits")
        template(v-for="commitResult in commit.commitResults")
          template(v-if="commitResult.tags")
            .tag(
              v-for="tag in commitResult.tags",
              vbind:key="tag",
              v-on:click="scrollToCommit(tag, `tag ${commitResult.hash}`)"
            )
              font-awesome-icon(icon="tags")
              span &nbsp;{{ tag }}

  c-ramp(
    v-bind:groupby="info.zFilterGroup",
    v-bind:user="filteredUser",
    v-bind:tframe="info.zTimeFrame",
    v-bind:sdate="info.zSince",
    v-bind:udate="info.zUntil",
    v-bind:avgsize="info.zAvgCommitSize",
    v-bind:mergegroup="info.zIsMerge",
    v-bind:fromramp="info.zFromRamp",
    v-bind:filtersearch="info.zFilterSearch")

  .sorting.mui-form--inline
    .mui-select.sort-by
      select(v-model="commitsSortType")
        option(value="time") Time
        option(value="linesOfCode") LoC
      label sort by
    .mui-select.sort-order
      select(v-model="toReverseSortedCommits")
        option(v-bind:value='true') Descending
        option(v-bind:value='false') Ascending
      label order

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

  .zoom__day(v-for="day in selectedCommits", v-bind:key="day.date")
    h3(v-if="info.zTimeFrame === 'week'") Week of {{ day.date }}
    h3(v-else) {{ day.date }}
    .c-zoom-commit-message(v-for="slice in day.commitResults",
      v-bind:key="slice.hash", v-bind:day="day", v-bind:slice="slice",
      v-bind:selectedFileTypes="selectedFileTypes", v-bind:fileTypeColors="fileTypeColors",
      v-bind:class="{ 'message-body active': slice.messageBody !== '' }")
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { mapState } from 'vuex';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import brokenLinkDisabler from '../mixin/brokenLinkMixin';
import tooltipPositioner from '../mixin/dynamicTooltipMixin';
import cRamp from '../components/c-ramp.vue';
import cStackedBarChart from '../components/c-stacked-bar-chart.vue';
import cZoomCommitMessage from '../components/c-zoom-commit-message.vue';
import {
  Bar,
  Commit,
  CommitResult,
  DailyCommit,
  WeeklyCommit,
  User,
} from '../types/types';
import CommitsSortType from '../types/zoom';
import { StoreState } from '../types/vuex.d';

function zoomInitialState() {
  return {
    showAllCommitMessageBody: true,
    showDiffstat: true,
    commitsSortType: CommitsSortType.Time,
    toReverseSortedCommits: true,
    isCommitsFinalized: false,
    selectedFileTypes: [] as Array<string>,
    fileTypes: [] as Array<string>,
  };
}

export default defineComponent({
  name: 'c-zoom',
  components: {
    FontAwesomeIcon,
    cRamp,
    cStackedBarChart,
    cZoomCommitMessage,
  },
  mixins: [brokenLinkDisabler, tooltipPositioner],
  data() {
    return {
      ...zoomInitialState(),
    };
  },

  computed: {
    sortingFunction() {
      const commitSortFunction = this.commitsSortType === CommitsSortType.Time
        ? (commit: Commit) => commit.date
        : (commit: Commit) => commit.insertions;

      return (a: Commit, b: Commit) => (this.toReverseSortedCommits ? -1 : 1)
        * window.comparator(commitSortFunction)(a, b);
    },
    filteredUser(): User {
      const {
        zUser, zSince, zUntil, zTimeFrame,
      } = this.info;

      if (!zUser) {
        throw new Error('zUser is not defined');
      }

      const filteredUser: User = Object.assign({}, zUser);

      if (zTimeFrame === 'week') {
        filteredUser.commits = zUser.commits.filter(
          (commit: WeeklyCommit) => commit.endDate >= zSince && commit.endDate <= zUntil,
        ).sort(this.sortingFunction);
      } else {
        filteredUser.commits = zUser.commits.filter(
          (commit: DailyCommit) => commit.date >= zSince && commit.date <= zUntil,
        ).sort(this.sortingFunction);
      }

      const tempUser: User = { ...filteredUser };
      tempUser.commits = [];
      filteredUser.commits.forEach((commit) => {
        const newCommit = { ...commit };
        newCommit.commitResults = [];

        if (this.commitsSortType === CommitsSortType.Time) {
          newCommit.commitResults = this.toReverseSortedCommits
            ? commit.commitResults.slice().reverse()
            : commit.commitResults.slice();
        } else {
          const cResultsSortingFunction = (a: CommitResult, b: CommitResult) => (this.toReverseSortedCommits ? -1 : 1)
            * window.comparator((cResult: CommitResult) => cResult.insertions)(a, b);
          newCommit.commitResults = commit.commitResults.slice().sort(cResultsSortingFunction);
        }
        tempUser.commits.push(newCommit);
      });
      return tempUser;
    },

    selectedCommits(): Array<Commit> {
      if (this.isSelectAllChecked) {
        return this.filteredUser.commits;
      }

      const commits = [] as Array<Commit>;
      this.filteredUser.commits.forEach((commit) => {
        const filteredCommit = { ...commit };
        filteredCommit.commitResults = [];
        commit.commitResults.forEach((slice) => {
          if (Object.keys(slice.fileTypesAndContributionMap).some(
            (fileType) => this.selectedFileTypes.indexOf(fileType) !== -1,
          )) {
            filteredCommit.commitResults.push(slice);
          }
        });
        if (filteredCommit.commitResults.length > 0) {
          commits.push(filteredCommit);
        }
      });
      return commits;
    },
    totalCommitMessageBodyCount(): number {
      let nonEmptyCommitMessageCount = 0;
      this.selectedCommits.forEach((commit) => {
        commit.commitResults.forEach((commitResult) => {
          if (commitResult.messageBody !== '') {
            nonEmptyCommitMessageCount += 1;
          }
        });
      });

      return nonEmptyCommitMessageCount;
    },
    expandedCommitMessagesCount(): number {
      return this.selectedCommits.reduce((prev, commit) => (
        prev + commit.commitResults.filter((slice) => slice.isOpen).length
      ), 0);
    },
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
      info: (state: unknown) => (state as StoreState).tabZoomInfo,
    }),
  },

  watch: {
    info() {
      const newData = {
        ...zoomInitialState(),
      };
      Object.assign(this.$data, newData);
      this.initiate();
      this.setInfoHash();
    },
    commitsSortType() {
      window.addHash('zCST', this.commitsSortType);
      window.encodeHash();
    },
    toReverseSortedCommits() {
      window.addHash('zRSC', this.toReverseSortedCommits.toString());
      window.encodeHash();
    },
  },
  created() {
    this.initiate();
    this.retrieveHashes();
    this.setInfoHash();
  },
  beforeUnmount() {
    this.removeZoomHashes();
  },

  methods: {
    initiate() {
      // This code crashes if info.zUser is not defined
      this.updateFileTypes();
      this.selectedFileTypes = this.fileTypes.slice();
    },

    getContributionBars(slice: CommitResult): Array<Bar> {
      let currentBarWidth = 0;
      const fullBarWidth = 100;

      let avgContributionSize = this.info.zAvgContributionSize;
      if (avgContributionSize === undefined || avgContributionSize > 1000) {
        avgContributionSize = 1000;
      }

      const contributionPerFullBar = avgContributionSize;

      const diffstatMappings: { [key: string]: number } = { limegreen: slice.insertions, red: slice.deletions };
      const allContributionBars: Array<Bar> = [];

      if (contributionPerFullBar === 0) {
        return allContributionBars;
      }

      Object.keys(diffstatMappings)
        .forEach((color) => {
          const contribution = diffstatMappings[color];
          let barWidth = (contribution / contributionPerFullBar) * fullBarWidth;
          const contributionBars = [];

          // if contribution bar for file type is able to fit on the current line
          if (currentBarWidth + barWidth < fullBarWidth) {
            contributionBars.push(barWidth);
            currentBarWidth += barWidth;
          } else {
            // take up all the space left on the current line
            contributionBars.push(fullBarWidth - currentBarWidth);
            barWidth -= fullBarWidth - currentBarWidth;
            // additional bar width will start on a new line
            const numOfFullBars = Math.floor(barWidth / fullBarWidth);
            for (let i = 0; i < numOfFullBars; i += 1) {
              contributionBars.push(fullBarWidth);
            }
            const remainingBarWidth = barWidth % fullBarWidth;
            if (remainingBarWidth > 0) {
              contributionBars.push(remainingBarWidth);
            }
            currentBarWidth = remainingBarWidth;
          }

          contributionBars.forEach((width) => {
            const bar = {
              color,
              width,
            };
            allContributionBars.push(bar);
          });
        });

      return allContributionBars;
    },

    getSliceLink(slice: CommitResult): string | undefined {
      if (this.info.zIsMerged) {
        return window.getCommitLink(slice.repoId, slice.hash);
      }
      return window.getCommitLink(this.info.zUser!.repoId, slice.hash);
    },

    scrollToCommit(tag: string, commit: string) {
      const el = this.$el.getElementsByClassName(`${commit} ${tag}`)[0];
      if (el) {
        el.focus();
      }
    },

    updateFileTypes() {
      const commitsFileTypes = new Set<string>();
      this.filteredUser.commits.forEach((commit) => {
        commit.commitResults.forEach((slice) => {
          Object.keys(slice.fileTypesAndContributionMap).forEach((fileType) => {
            commitsFileTypes.add(fileType);
          });
        });
      });
      this.fileTypes = Object.keys(this.filteredUser.fileTypeContribution).filter(
        (fileType) => commitsFileTypes.has(fileType),
      );
    },

    retrieveHashes() {
      this.retrieveSortHash();
      this.retrieveSelectedFileTypesHash();
    },

    retrieveSortHash() {
      const hash = window.hashParams;
      if (hash.zCST && Object.values(CommitsSortType).includes(hash.zCST as CommitsSortType)) {
        this.commitsSortType = hash.zCST as CommitsSortType;
      }
      if (hash.zRSC) {
        this.toReverseSortedCommits = (hash.zRSC === 'true');
      }
    },

    retrieveSelectedFileTypesHash() {
      const hash = window.hashParams;

      if (hash.zFT || hash.zFT === '') {
        this.selectedFileTypes = hash.zFT
          .split(window.HASH_DELIMITER)
          .filter((fileType) => this.fileTypes.includes(fileType));
      }
    },

    updateSelectedFileTypesHash() {
      const fileTypeHash = this.selectedFileTypes.length > 0
        ? this.selectedFileTypes.reduce((a, b) => `${a}~${b}`)
        : '';

      window.addHash('zFT', fileTypeHash);
      window.encodeHash();
    },

    setInfoHash() {
      const { addHash, encodeHash } = window;
      const {
        zAvgCommitSize, zSince, zUntil, zFilterGroup,
        zTimeFrame, zIsMerged, zAuthor, zRepo, zFromRamp, zFilterSearch,
      } = this.info;
      addHash('zA', zAuthor);
      addHash('zR', zRepo);
      addHash('zACS', zAvgCommitSize.toString());
      addHash('zS', zSince);
      addHash('zFS', zFilterSearch);
      addHash('zU', zUntil);
      addHash('zMG', zIsMerged.toString());
      addHash('zFTF', zTimeFrame);
      addHash('zFGS', zFilterGroup);
      addHash('zFR', zFromRamp.toString());
      encodeHash();
    },

    toggleSelectedCommitMessageBody(slice: CommitResult) {
      this.$store.commit('toggleZoomCommitMessageBody', slice);
    },

    toggleAllCommitMessagesBody(isOpen: boolean) {
      this.showAllCommitMessageBody = isOpen;
      this.$store.commit('setAllZoomCommitMessageBody', {
        isOpen,
        commits: this.selectedCommits,
      });
    },

    toggleDiffstatView(isVisible: boolean) {
      this.showDiffstat = isVisible;
    },

    removeZoomHashes() {
      window.removeHash('zA');
      window.removeHash('zR');
      window.removeHash('zFS');
      window.removeHash('zACS');
      window.removeHash('zS');
      window.removeHash('zU');
      window.removeHash('zFGS');
      window.removeHash('zFTF');
      window.removeHash('zMG');
      window.removeHash('zFT');
      window.removeHash('zCST');
      window.removeHash('zRSC');
      window.encodeHash();
    },

    containsAtLeastOneSelected(fileTypes: Array<string>): boolean {
      for (let i = 0; i < fileTypes.length; i += 1) {
        if (this.selectedFileTypes.includes(fileTypes[i])) {
          return true;
        }
      }
      return false;
    },

    getFontColor(color: string) {
      return window.getFontColor(color);
    },
  },
});

</script>

<style lang="scss" scoped>
@import '../styles/_colors.scss';

#tab-zoom {
  .zoom {
    &__title {
      &--granularity {
        @include mini-font;
        margin-top: .5rem;
      }

      &--tags {
        margin: .25rem 0 .25rem 0;

        .tag {
          cursor: pointer;
        }
      }
    }

    &__toggle-commit-message-body {
      padding-top: 10px;
    }

    &__day,
    &__title {
      @include small-font;

      h3 {
        @include large-font;
      }

      /* Tags in commits */
      .tag {
        @include mini-font;
        background: mui-color('grey', '600');
        border-radius: 5px;
        color: mui-color('white');
        display: inline-block;
        margin: .2rem .2rem .2rem 0;
        padding: 0 3px 0 3px;

        .fa-tags {
          width: .65rem;
        }
      }
    }
  }
}
</style>
