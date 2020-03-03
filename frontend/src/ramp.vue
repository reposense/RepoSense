<template lang="pug">
.ramp
  template(v-if="tframe === 'commit'")
    template(v-for="(slice, j) in user.commits")
      a.ramp__slice(
        draggable="false",
        onclick="rampClick(event);",
        v-for="(commit, k) in slice.commitResults",
        v-if="commit.insertions>0",
        v-bind:href="getLink(user, commit)", target="_blank",
        v-bind:title="`[${slice.date}] ${commit.messageTitle}: ${commit.insertions} lines`",
        v-bind:class="'ramp__slice--color' + getSliceColor(slice.date)",
        v-bind:style="{\
          zIndex: user.commits.length - j,\
          borderLeftWidth: getWidth(commit) + 'em',\
          right: ((getSlicePos(slice.date)\
            + (getCommitPos(k, slice.commitResults.length))) * 100) + '%'\
          }"
      )

  template(v-else)
    a.ramp__slice(
      draggable="false",
      onclick="rampClick(event);",
      v-for="(slice, j) in user.commits",
      v-if="slice.insertions > 0",
      v-bind:title="`${tframe === 'day' \
        ? '[' + slice.date + '] Daily' \
        : '[' + slice.date + ' till ' + slice.endDate + '] Weekly'} contribution: ${slice.insertions} lines`",
      v-bind:href="getLink(user, slice)", target="_blank",
      v-bind:class="'ramp__slice--color' + getSliceColor(slice.date)",
      v-bind:style="[\
          {\
            zIndex: user.commits.length - j,\
            borderLeftWidth: getWidth(slice) + 'em',\
            right: (getSlicePos(tframe === 'day' ? slice.date : slice.endDate) * 100) + '%' \
          },\
          /* disallow clickable ramp slices when merging groups that are grouped by authors \
             as unable to form a url that navigates to different repositories of the same author */\
          mergegroup && groupby === 'groupByAuthors' ?\
            { cursor: 'auto', pointerEvents: 'none' }  :\
            { cursor: 'pointer', pointerEvents: 'auto' }\
      ]"
    )
</template>

<script>
window.getBaseLink = function (repoId) {
  return `${window.BASE_URL}/${
    window.REPOS[repoId].location.organization}/${
    window.REPOS[repoId].location.repoName}`;
};

window.rampClick = function rampClick(evt) {
  const isKeyPressed = this.isMacintosh ? evt.metaKey : evt.ctrlKey;
  if (isKeyPressed) {
    evt.preventDefault();
  }
};

export default {
  props: ['groupby', 'user', 'tframe', 'avgsize', 'sdate', 'udate', 'mergegroup'],
  data() {
    return {
      rampSize: 0.01,
    };
  },

  methods: {
    getLink(user, slice) {
      const { REPOS } = window;
      const untilDate = this.tframe === 'week' ? slice.endDate : slice.date;

      if (this.mergegroup) {
        return this.getMergedLink(user, slice, untilDate);
      }

      if (this.tframe === 'commit') {
        return `${getBaseLink(user.repoId)}/commit/${slice.hash}`;
      }

      return `${getBaseLink(user.repoId)}/commits/${REPOS[user.repoId].branch}?`
              + `author=${user.name}&`
              + `since=${slice.date}'T'00:00:00+08:00&`
              + `until=${untilDate}'T'23:59:59+08:00`;
    },
    getMergedLink(user, slice, untilDate) {
      const { REPOS } = window;

      if (this.tframe === 'commit') {
        return `${getBaseLink(slice.repoId)}/commit/${slice.hash}`;
      }

      return `${getBaseLink(user.repoId)}/commits/${REPOS[user.repoId].branch}?`
              + `since=${slice.date}'T'00:00:00+08:00&`
              + `until=${untilDate}'T'23:59:59+08:00`;
    },
    getWidth(slice) {
      if (slice.insertions === 0) {
        return 0;
      }

      const newSize = 100 * (slice.insertions / this.avgsize);
      return Math.max(newSize * this.rampSize, 0.5);
    },

    // position for commit granularity
    getCommitPos(i, total) {
      return (total - i - 1) * window.DAY_IN_MS / total
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
    getSliceColor(date) {
      const timeMs = (new Date(date)).getTime();
      return (timeMs / window.DAY_IN_MS) % 5;
    },
  },
};

</script>

<style lang="scss" scoped>
@import './static/css/colors';

$height: 3rem;

/* Ramp */
.ramp {
  background-color: mui-color('blue', '50');
  font-size: 100%;
  height: $height;
  overflow: hidden;
  position: relative;
}

.ramp__slice {
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
}

</style>
