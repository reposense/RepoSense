<template lang="pug">
.ramp
  template(v-if="tframe === 'commit'")
    template(v-for="(slice, j) in user.commits")
      template(v-for="(commit, k) in slice.commitResults")
        a.ramp__slice(
          draggable="false",
          v-on:click="rampClick",
          v-bind:href="getLink(commit)", target="_blank",
          v-bind:title="getContributionMessageByCommit(slice, commit)",
          v-bind:class="`ramp__slice--color${getRampColor(commit, slice)}`,\
            !isBrokenLink(getLink(commit)) ? '' : 'broken-link'",
          v-bind:style="{\
            zIndex: user.commits.length - j,\
            borderLeftWidth: `${getWidth(commit)}em`,\
            right: `${((getSlicePos(slice.date)\
              + (getCommitPos(k, slice.commitResults.length))) * 100)}%`\
            }"
        )

  template(v-else)
    a(v-bind:href="getReportLink()", target="_blank")
      .ramp__slice(
        draggable="false",
        v-for="(slice, j) in user.commits",
        v-bind:title="getContributionMessage(slice)",
        v-on:click="openTabZoom(user, slice, $event)",
        v-bind:class="`ramp__slice--color${getSliceColor(slice)}`",
        v-bind:style="{\
          zIndex: user.commits.length - j,\
          borderLeftWidth: `${getWidth(slice)}em`,\
          right: `${(getSlicePos(tframe === 'day' ? slice.date : slice.endDate) * 100)}%` \
          }"
      )
</template>

<script lang='ts'>
import { defineComponent, PropType } from 'vue';
import brokenLinkDisabler from '../mixin/brokenLinkMixin';
import { Commit, CommitResult, User } from '../types/types';

export default defineComponent({
  name: 'c-ramp',
  mixins: [brokenLinkDisabler],
  props: {
    groupby: {
      type: String,
      default: 'groupByRepos',
    },
    user: {
      type: Object as PropType<User>,
      required: true,
    },
    tframe: {
      type: String,
      default: 'commit',
    },
    avgsize: {
      type: [Number, String],
      required: true,
    },
    sdate: {
      type: String,
      required: true,
    },
    udate: {
      type: String,
      required: true,
    },
    mergegroup: {
      type: Boolean,
      default: false,
    },
    fromramp: {
      type: Boolean,
      default: false,
    },
    filtersearch: {
      type: String,
      default: '',
    },
    isWidgetMode: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      rampSize: 0.01 as number,
    };
  },

  computed: {
    mergeCommitRampSize(): number {
      return this.rampSize * 20;
    },
    deletesContributionRampSize(): number {
      return this.rampSize * 20;
    },
  },

  methods: {
    getLink(commit: CommitResult) {
      return window.getCommitLink(commit.repoId, commit.hash);
    },
    getContributions(commit: CommitResult | Commit) {
      return commit.insertions + commit.deletions;
    },
    isDeletesContribution(commit: CommitResult | Commit) {
      return commit.insertions === 0 && commit.deletions > 0;
    },
    getWidth(slice: CommitResult | Commit) {
      // Check if slice contains 'isMergeCommit' attribute
      if ('isMergeCommit' in slice && slice.isMergeCommit) {
        return this.mergeCommitRampSize;
      }
      if (this.getContributions(slice) === 0) {
        return 0;
      }
      if (this.isDeletesContribution(slice)) {
        return this.deletesContributionRampSize;
      }
      // '+' unary operator here attempts to convert this.avgsize to number, if it is not already one
      const newSize = 100 * (slice.insertions / +this.avgsize);
      return Math.max(newSize * this.rampSize, 0.5);
    },
    getContributionMessageByCommit(slice: Commit, commit: CommitResult) {
      return `[${slice.date}] ${commit.messageTitle}: +${commit.insertions} -${commit.deletions} lines `;
    },
    getContributionMessage(slice: Commit) {
      let title = this.tframe === 'day'
          ? `[${slice.date}] Daily `
          : `[${slice.date} till ${slice.endDate}] Weekly `;
      title += `contribution: +${slice.insertions} -${slice.deletions} lines`;
      return title;
    },
    openTabZoom(user: User, slice: Commit, evt: MouseEvent) {
      // prevent opening of zoom tab when cmd/ctrl click
      if (window.isMacintosh ? evt.metaKey : evt.ctrlKey) {
        return;
      }

      const zoomUser = { ...user };
      // Calculate total commit result insertion and deletion for the daily/weekly commit selected
      zoomUser.commits = user.dailyCommits.map(
        (dailyCommit) => ({
          insertions: dailyCommit.commitResults.reduce((acc, currCommitResult) => acc + currCommitResult.insertions, 0),
          deletions: dailyCommit.commitResults.reduce((acc, currCommitResult) => acc + currCommitResult.deletions, 0),
          ...dailyCommit,
          commitResults: dailyCommit.commitResults.map((commitResult) => ({ ...commitResult, isOpen: true })),
        }),
      ) as Commit[];

      const info = {
        zRepo: user.repoName,
        zAuthor: user.name,
        zFilterGroup: this.groupby,
        zTimeFrame: 'commit',
        zAvgCommitSize: slice.insertions,
        zUser: zoomUser,
        zLocation: window.getRepoLink(user.repoId),
        zSince: slice.date,
        zUntil: this.tframe === 'day' ? slice.date : slice.endDate,
        zIsMerged: this.mergegroup,
        zFromRamp: true,
        zFilterSearch: this.filtersearch,
      };
      window.deactivateAllOverlays();

      this.$store.commit('updateTabZoomInfo', info);
    },

    // position for commit granularity
    getCommitPos(i: number, total: number) {
      return (((total - i - 1) * window.DAY_IN_MS) / total)
          / (this.getTotalForPos(this.sdate, this.udate) + window.DAY_IN_MS);
    },
    // position for day granularity
    getSlicePos(date: string) {
      const total = this.getTotalForPos(this.sdate, this.udate);
      return (new Date(this.udate).valueOf() - new Date(date).valueOf()) / (total + window.DAY_IN_MS);
    },

    // get duration in miliseconds between 2 date
    getTotalForPos(sinceDate: string, untilDate: string) {
      return new Date(untilDate).valueOf() - new Date(sinceDate).valueOf();
    },
    getRampColor(commit: CommitResult, slice: Commit) {
      if (this.isDeletesContribution(commit)) {
        return '-deletes';
      }
      return this.getSliceColor(slice);
    },
    getSliceColor(slice: Commit) {
      if (this.isDeletesContribution(slice)) {
        return '-deletes';
      }
      const timeMs = this.fromramp
          ? (new Date(this.sdate)).getTime()
          : (new Date(slice.date)).getTime();

      return (timeMs / window.DAY_IN_MS) % 5;
    },

    // Prevent browser from switching to new tab when clicking ramp
    rampClick(evt: MouseEvent) {
      const isKeyPressed = window.isMacintosh ? evt.metaKey : evt.ctrlKey;
      if (isKeyPressed) {
        evt.preventDefault();
      }
    },
    getReportLink() {
      if (this.isWidgetMode) {
        const url = window.location.href;
        const regexToRemoveWidget = /([?&])((chartIndex|chartGroupIndex)=\d+)/g;
        return url.replace(regexToRemoveWidget, '');
      }
      return undefined;
    },
  },
});
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
@import '../styles/_colors.scss';

/* Ramp */
.ramp {
  $height: 3rem;
  background-color: mui-color('blue', '50');
  font-size: 100%;
  height: $height;
  overflow: hidden;
  position: relative;

  &__slice {
    border-left-color: rgba(0, 0, 0, 0);
    border-left-style: solid;
    display: block;
    height: 0;
    position: absolute;
    width: 0;

    &--color0 {
      border-bottom: $height rgba(mui-color('orange'), .5) solid;
    }

    &--color1 {
      border-bottom: $height rgba(mui-color('light-blue'), .5) solid;
    }

    &--color2 {
      border-bottom: $height rgba(mui-color('green'), .5) solid;
    }

    &--color3 {
      border-bottom: $height rgba(mui-color('indigo'), .5) solid;
    }

    &--color4 {
      border-bottom: $height rgba(mui-color('pink'), .5) solid;
    }

    &--color-deletes {
      border-bottom: $height rgba(mui-color('red'), .7) solid;
    }
  }
}
</style>
