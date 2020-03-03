<template lang="pug">
#zoom
  span.large-font Commits Panel
  .toolbar(v-if="filteredUser.commits.length")
    a(v-if="expandedCommitMessagesCount === 0", v-on:click="toggleAllCommitMessagesBody(true)") show all commit messages
    a(v-else, v-on:click="toggleAllCommitMessagesBody(false)") hide all commit messages
  .zoom__title
    .zoom_title--group.large-font
      span(
        v-if="info.filterGroupSelection === 'groupByAuthors'"
      ) {{ filteredUser.displayName }} ({{ filteredUser.name }})
      a(
        v-else,
        v-bind:href="info.location", target="_blank",
        v-bind:title="'Click to open the repo'"
      )
        span {{ filteredUser.repoName }}
    .zoom__title--chart.medium-font(v-if="!info.isMergeGroup")
      span &#8627; &nbsp;
      span(v-if="info.filterGroupSelection === 'groupByAuthors'") {{ filteredUser.repoName }}
      span(v-else) {{ filteredUser.displayName }} ({{ filteredUser.name }})
    .zoom__title--period.medium-font
      span &#8627; &nbsp;
      span {{ info.sinceDate }} to {{ info.untilDate }} &nbsp;
      a(v-on:click="openSummary") [Show ramp chart for this period]
    .zoom__title--granularity.mini-font granularity: one ramp per {{ filterTimeFrame }}
    .zoom__title--tags
      template(v-for="commit in filteredUser.commits")
        template(v-for="commitResult in commit.commitResults")
          template(v-if="commitResult.tags")
            .tag.mini-font(
              v-for="tag in commitResult.tags",
              v-on:click="scrollToCommit(tag, `tag ${commitResult.hash}`)"
            )
              i.fas.fa-tags
              span &nbsp;{{ tag }}

  v_ramp(
    v-bind:groupby="info.filterGroupSelection",
    v-bind:user="filteredUser",
    v-bind:tframe="filterTimeFrame",
    v-bind:sdate="info.sinceDate",
    v-bind:udate="info.untilDate",
    v-bind:avgsize="info.avgCommitSize",
    v-bind:mergegroup="info.isMergeGroup")

  .sorting.mui-form--inline
    .mui-select
      select.medium-font(v-model="commitsSortType")
        option(value="time") Time
        option(value="lineOfCode") LoC
      label.medium-font sort by
    .mui-select
      select.medium-font(v-model="toReverseSortedCommits")
        option(v-bind:value='true') Descending
        option(v-bind:value='false') Ascending
      label.medium-font order

  .zoom__day(v-for="day in filteredUser.commits", v-bind:key="day.date")
    h3(v-if="filterTimeFrame === 'week'") Week of {{ day.date }}
    h3(v-else) {{ day.date }}
    template
      //- use tabindex to enable focus property on div
      .commit-message(
        tabindex="-1",
        v-for="slice in day.commitResults",
        v-bind:key="slice.hash",
        v-bind:class="{ 'message-body active': slice.messageBody !== '' }"
      )
        a(v-bind:href="getSliceLink(slice)", target="_blank") {{ slice.messageTitle }}
        span &nbsp; ({{ slice.insertions }} lines) &nbsp;
        template(v-if="slice.tags", v-for="tag in slice.tags")
          .tag.mini-font(tabindex="-1", v-bind:class="[`${slice.hash}`, tag]")
            i.fas.fa-tags
            span &nbsp;{{ tag }}
        a(
          v-if="slice.messageBody !== ''",
          v-on:click="updateExpandedCommitMessagesCount",
          onclick="toggleNext(this)",
          title="Click to show/hide the commit message body"
        )
          i.commit-message--button.fas.fa-ellipsis-h
        .body(v-if="slice.messageBody !== ''")
          pre {{ slice.messageBody }}
</template>

<script>
import ramp from '../ramp.vue';

const commitSortDict = {
  lineOfCode: (commit) => commit.insertions,
  time: (commit) => commit.date,
};

export default {
  props: ['info'],
  data() {
    return {
      filterTimeFrame: window.hashParams.timeframe,
      showAllCommitMessageBody: true,
      expandedCommitMessagesCount: this.totalCommitMessageBodyCount,
      commitsSortType: 'time',
      toReverseSortedCommits: true,
    };
  },

  computed: {
    sortingFunction() {
      return (a, b) => (this.toReverseSortedCommits ? -1 : 1)
              * window.comparator(commitSortDict[this.commitsSortType])(a, b);
    },
    filteredUser() {
      const { user } = this.info;
      const filteredUser = Object.assign({}, user);

      const date = this.filterTimeFrame === 'week' ? 'endDate' : 'date';
      filteredUser.commits = user.commits.filter(
              (commit) => commit[date] >= this.info.sinceDate && commit[date] <= this.info.untilDate,
      ).sort(this.sortingFunction);

      return filteredUser;
    },
    totalCommitMessageBodyCount() {
      let nonEmptyCommitMessageCount = 0;
      this.filteredUser.commits.forEach((commit) => {
        commit.commitResults.forEach((commitResult) => {
          if (commitResult.messageBody !== '') {
            nonEmptyCommitMessageCount += 1;
          }
        });
      });

      return nonEmptyCommitMessageCount;
    },
  },
  methods: {
    openSummary() {
      this.$emit('view-summary', this.info.sinceDate, this.info.untilDate);
    },

    getSliceLink(slice) {
      if (this.info.isMergeGroup) {
        return `${window.getBaseLink(slice.repoId)}/commit/${slice.hash}`;
      }
      return `${window.getBaseLink(this.info.user.repoId)}/commit/${slice.hash}`;
    },

    scrollToCommit(tag, commit) {
      const el = this.$el.getElementsByClassName(`${commit} ${tag}`)[0];
      if (el) {
        el.focus();
      }
    },

    toggleAllCommitMessagesBody(isActive) {
      this.showAllCommitMessageBody = isActive;

      const toRename = this.showAllCommitMessageBody ? 'commit-message message-body active' : 'commit-message message-body';

      const commitMessageClasses = document.getElementsByClassName('commit-message message-body');
      Array.from(commitMessageClasses).forEach((commitMessageClass) => {
        commitMessageClass.className = toRename;
      });

      this.expandedCommitMessagesCount = isActive ? this.totalCommitMessageBodyCount : 0;
    },

    updateExpandedCommitMessagesCount() {
      this.expandedCommitMessagesCount = document.getElementsByClassName('commit-message message-body active')
              .length;
    },
  },
  mounted() {
    this.updateExpandedCommitMessagesCount();
  },
  components: {
    v_ramp: ramp,
  },
};

</script>

<style lang="scss" scoped>
@import '../static/css/colors';

.zoom {
  &__title {
    &--chart {
      margin-left: .3rem;
    }

    &--period {
      margin-left: 2rem;
    }

    &--granularity {
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
    /* Tags in commits */
    .tag {
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

/* Commit Message Body in Zoom Tab */
.commit-message {
  padding: 5px;

  &:focus,
  &:focus-within {
    border: 1px solid mui-color('blue', '500');
  }

  &.active {
    .body {
      background-color: mui-color('white');
      border: 1px solid mui-color('grey', '700');
      display: grid;
      margin: .25rem 0 .25rem 0;
      overflow-x: auto;
      padding: .4rem;
      resize: none;
    }
  }

  .body {
    display: none;
  }

  .tag {
    cursor: pointer;

    &:focus {
      border: 1px solid mui-color('blue', '500');
      outline: none;
    }
  }

  &--button {
    color: mui-color('grey', '700');
    padding-left: .5rem;

    &:hover {
      cursor: pointer;
    }
  }

  pre {
    margin: 0;
  }
}

</style>
