<template lang="pug">
.ramp
  template(v-if="tframe === 'commit'")
    template(v-for="(slice, j) in user.commits")
      template(v-for="(commit, k) in slice.commitResults.filter(commitResult => getContributions(commitResult) > 0)")
        a.ramp__slice(
          draggable="false",
          v-on:click="rampClick",
          v-bind:href="getLink(commit)", target="_blank",
          v-bind:title="getContributionMessage(slice, commit)",
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
    a.ramp__slice(
      draggable="false",
      v-for="(slice, j) in user.commits.filter(commit => getContributions(commit) > 0)",
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

<script>
import brokenLinkDisabler from '../mixin/brokenLinkMixin';
import User from '../utils/user';

export default {
  name: 'c-ramp',
  mixins: [brokenLinkDisabler],
  props: {
    groupby: {
      type: String,
      default: 'groupByRepos',
    },
    user: {
      type: User,
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
  },
  data() {
    return {
      rampSize: 0.01,
      deletesContributionRampSize: this.rampSize * 20,
    };
  },

  methods: {
    getLink(commit) {
      return window.getCommitLink(commit.repoId, commit.hash);
    },
    getContributions(commit) {
      return commit.insertions + commit.deletions;
    },
    isDeletesContribution(commit) {
      return commit.insertions === 0 && commit.deletions > 0;
    },
    getWidth(slice) {
      if (this.getContributions(slice) === 0) {
        return 0;
      }
      if (this.isDeletesContribution(slice)) {
        return this.deletesContributionRampSize;
      }

      const newSize = 100 * (slice.insertions / this.avgsize);
      return Math.max(newSize * this.rampSize, 0.5);
    },
    getContributionMessage(slice, commit) {
      let title = '';
      if (this.tframe === 'commit') {
        return `[${slice.date}] ${commit.messageTitle}: +${commit.insertions} -${commit.deletions} lines `;
      }

      title = this.tframe === 'day'
            ? `[${slice.date}] Daily `
            : `[${slice.date} till ${slice.endDate}] Weekly `;
      title += `contribution: +${slice.insertions} -${slice.deletions} lines`;
      return title;
    },
    openTabZoom(user, slice, evt) {
      // prevent opening of zoom tab when cmd/ctrl click
      if (window.isMacintosh ? evt.metaKey : evt.ctrlKey) {
        return;
      }

      const zoomUser = { ...user };
      zoomUser.commits = user.dailyCommits;

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
    getCommitPos(i, total) {
      return (((total - i - 1) * window.DAY_IN_MS) / total)
          / (this.getTotalForPos(this.sdate, this.udate) + window.DAY_IN_MS);
    },
    // position for day granularity
    getSlicePos(date) {
      const total = this.getTotalForPos(this.sdate, this.udate);
      return (new Date(this.udate) - new Date(date)) / (total + window.DAY_IN_MS);
    },

    // get duration in miliseconds between 2 date
    getTotalForPos(sinceDate, untilDate) {
      return new Date(untilDate) - new Date(sinceDate);
    },
    getRampColor(commit, slice) {
      if (this.isDeletesContribution(commit)) {
        return '-deletes';
      }
      return this.getSliceColor(slice);
    },
    getSliceColor(slice) {
      if (this.isDeletesContribution(slice)) {
        return '-deletes';
      }
      const timeMs = this.fromramp
          ? (new Date(this.sdate)).getTime()
          : (new Date(slice.date)).getTime();

      return (timeMs / window.DAY_IN_MS) % 5;
    },

    // Prevent browser from switching to new tab when clicking ramp
    rampClick(evt) {
      const isKeyPressed = window.isMacintosh ? evt.metaKey : evt.ctrlKey;
      if (isKeyPressed) {
        evt.preventDefault();
      }
    },
  },
};
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
