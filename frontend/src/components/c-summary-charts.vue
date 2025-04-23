<template lang="pug">
#summary-charts
  .summary-charts(
    v-for="(repo, i) in filteredRepos",
    :ref="'summary-charts-' + i",
    :style="isChartGroupWidgetMode ? {'marginBottom': 0} : {}"
    )
    .summary-charts__title(
      v-if="filterGroupSelection !== 'groupByNone'",
      :ref="'summary-charts-title-' + i",
      :class="{ 'active-background': \
        isSelectedGroup(repo[0].name, repo[0].repoName) && !isChartGroupWidgetMode}"
    )
      .summary-charts__title--index(v-if="!isChartGroupWidgetMode") {{ i+1 }}
      .summary-charts__title--groupname(
        :style="isChartGroupWidgetMode ? {'paddingLeft': 0} : {}"
        )
        template(v-if="filterGroupSelection === 'groupByRepos'") {{ repo[0].repoName }}
        template(
          v-else-if="filterGroupSelection === 'groupByAuthors'",
          :class=" { warn: repo[0].name === '-' }"
        ) {{ getAuthorDisplayName(repo) }} ({{ repo[0].name }})
      .summary-charts__title--contribution
        .tooltip(
          @mouseover="onTooltipHover(`summary-charts-${i}-total-contribution`)",
          @mouseout="resetTooltip(`summary-charts-${i}-total-contribution`)"
        )
          | [{{ getGroupTotalContribution(repo) }} lines]
          span.tooltip-text(
            v-if="filterGroupSelection === 'groupByRepos' && !isChartGroupWidgetMode",
            :ref="`summary-charts-${i}-total-contribution`"
          ) Total contribution of group
          span.tooltip-text(
            v-else-if="filterGroupSelection === 'groupByAuthors' && !isChartGroupWidgetMode",
            :ref="`summary-charts-${i}-total-contribution`"
          ) Total contribution of author
      a(
        v-if="!isGroupMerged(getGroupName(repo)) && !isChartGroupWidgetMode",
        @click="handleMergeGroup(getGroupName(repo))"
      )
        .tooltip(
          @mouseover="onTooltipHover(`summary-charts-${i}-merge-group`)",
          @mouseout="resetTooltip(`summary-charts-${i}-merge-group`)"
        )
          font-awesome-icon.icon-button(:icon="['fas', 'chevron-up']")
          span.tooltip-text(:ref="`summary-charts-${i}-merge-group`") Click to merge group
      a(
        v-if="isGroupMerged(getGroupName(repo)) && !isChartGroupWidgetMode",
        @click="handleExpandGroup(getGroupName(repo))"
      )
        .tooltip(
          @mouseover="onTooltipHover(`summary-charts-${i}-expand-group`)",
          @mouseout="resetTooltip(`summary-charts-${i}-expand-group`)"
        )
          font-awesome-icon.icon-button(:icon="['fas', 'chevron-down']")
          span.tooltip-text(:ref="`summary-charts-${i}-expand-group`") Click to expand group
      a(
        v-if="filterGroupSelection === 'groupByRepos'",
        :class="!isBrokenLink(getRepoLink(repo[0])) ? '' : 'broken-link'",
        :href="getRepoLink(repo[0])", target="_blank"
      )
        .tooltip(
          @mouseover="onTooltipHover(`summary-charts-${i}-repo-link`)",
          @mouseout="resetTooltip(`summary-charts-${i}-repo-link`)"
        )
          font-awesome-icon.icon-button(:icon="getRepoIcon(repo[0])")
          span.tooltip-text(
            v-if="!isChartGroupWidgetMode",
            :ref="`summary-charts-${i}-repo-link`"
          ) {{getGroupRepoLinkMessage(repo[0])}}
      a(
        v-else-if="filterGroupSelection === 'groupByAuthors'",
        :class="!isBrokenLink(getAuthorProfileLink(repo[0], repo[0].name)) ? '' : 'broken-link'",
        :href="getAuthorProfileLink(repo[0], repo[0].name)", target="_blank"
      )
        .tooltip(
          @mouseover="onTooltipHover(`summary-charts-${i}-author-link`)",
          @mouseout="resetTooltip(`summary-charts-${i}-author-link`)"
        )
          font-awesome-icon.icon-button(icon="user")
          span.tooltip-text(
            v-if="!isChartGroupWidgetMode",
            :ref="`summary-charts-${i}-author-link`"
          ) {{getAuthorProfileLinkMessage(repo[0])}}
      template(v-if="isGroupMerged(getGroupName(repo))")
        a(
          v-if="filterGroupSelection !== 'groupByAuthors' && !isChartGroupWidgetMode",
          onclick="deactivateAllOverlays()",
          @click="openTabAuthorship(repo[0], repo, 0, isGroupMerged(getGroupName(repo)))"
        )
          .tooltip(
            @mouseover="onTooltipHover(`summary-charts-${i}-group-code`)",
            @mouseout="resetTooltip(`summary-charts-${i}-group-code`)"
          )
            font-awesome-icon.icon-button(
              icon="code",
              :class="{ 'active-icon': isSelectedTab(repo[0].name, repo[0].repoName, 'authorship', true) }"
            )
            span.tooltip-text(:ref="`summary-charts-${i}-group-code`") Click to view group's code
        a(
          v-if="!isChartGroupWidgetMode",
          onclick="deactivateAllOverlays()",
          @click="openTabZoom(\
            repo[0],\
            getSinceDate(repo[0]),\
            getUntilDate(repo[0]),\
            isGroupMerged(getGroupName(repo))\
          )"
        )
          .tooltip(
            @mouseover="onTooltipHover(`summary-charts-${i}-commit-breakdown`)",
            @mouseout="resetTooltip(`summary-charts-${i}-commit-breakdown`)"
          )
            font-awesome-icon.icon-button(
              icon="list-ul",
              :class="{ 'active-icon': isSelectedTab(repo[0].name, repo[0].repoName, 'zoom', true) }"
            )
            span.tooltip-text(:ref="`summary-charts-${i}-commit-breakdown`") Click to view breakdown of commits
      a(
        v-if="isChartGroupWidgetMode && !isChartWidgetMode",
        :href="getReportLink()", target="_blank"
      )
        .tooltip(
          @mouseover="onTooltipHover(`summary-charts-${i}-commit-breakdown-group-widget`)",
          @mouseout="resetTooltip(`summary-charts-${i}-commit-breakdown-group-widget`)"
        )
          font-awesome-icon.icon-button(
            icon="arrow-up-right-from-square",
          )
          span.tooltip-text(
            v-if="!isChartGroupWidgetMode",
            :ref="`summary-charts-${i}-commit-breakdown-group-widget`"
          ) Click to view breakdown of commits on RepoSense
      a(
        v-if="!isChartGroupWidgetMode",
        @click="getEmbeddedIframe(i)"
      )
        .tooltip(:id="'tooltip-' + i",
          @mouseover="onTooltipHover(`summary-charts-${i}-copy-iframe`)",
          @mouseout="resetTooltip(`summary-charts-${i}-copy-iframe`)"
        )
          font-awesome-icon.icon-button(icon="clipboard")
          span.tooltip-text(:ref="`summary-charts-${i}-copy-iframe`") Click to copy iframe link for group
      .tooltip.summary-chart__title--percentile(
        v-if="sortGroupSelection.includes('totalCommits')"
        ) {{ getPercentile(i) }} %&nbsp
        span.tooltip-text.right-aligned {{ getPercentileExplanation(i) }}
      .summary-charts__title--tags(
        v-if="isViewingTagsByRepo"
      )
        a.tag(
          v-for="tag in getTags(repo)",
          :href="getTagLink(repo[0], tag)",
          target="_blank",
          vbind:key="tag",
          tabindex="-1"
        )
          font-awesome-icon(icon="tags")
          span &nbsp;{{ tag }}

    .blurb-wrapper(
      v-if="filterGroupSelection === 'groupByRepos'",
    )
      c-markdown-chunk.blurb(
        :markdown-text="getRepoBlurb(repo[0])"
      )

    .blurb-wrapper(
      v-if="filterGroupSelection === 'groupByAuthors'")
      c-markdown-chunk.blurb(
        :markdown-text="getAuthorBlurb(repo[0].name)"
      )

    .summary-charts__fileType--breakdown(v-if="filterBreakdown")
      template(v-if="filterGroupSelection !== 'groupByNone'")
        .summary-charts__fileType--breakdown__legend(
          v-for="fileType in getFileTypes(repo)",
          :key="fileType"
        )
          font-awesome-icon(
            icon="circle",
            :style="{ 'color': fileTypeColors[fileType] }"
          )
          span &nbsp; {{ fileType }} &nbsp;

    .summary-chart(
      v-for="(user, j) in getRepo(repo)",
      :style="isChartGroupWidgetMode && j === getRepo(repo).length - 1 ? {'marginBottom': 0} : {}",
      :ref="'summary-chart-' + j",
      :id="user.name === activeUser && user.repoName === activeRepo ? 'selectedChart' : null"
      )
      .summary-chart__title(
        v-if="!isGroupMerged(getGroupName(repo))",
        :class="{ 'active-background': user.name === activeUser && user.repoName === activeRepo \
          && !isChartGroupWidgetMode }"
      )
        .summary-chart__title--index(v-if="!isChartWidgetMode && !isPortfolio") {{ j+1 }}
        .summary-chart__title--repo(v-if="filterGroupSelection === 'groupByNone'") {{ user.repoName }}
        .summary-chart__title--author-repo(v-if="filterGroupSelection === 'groupByAuthors'") {{ user.repoName }}
        .summary-chart__title--name(
          v-if="!isPortfolio && filterGroupSelection !== 'groupByAuthors'",
          :class="{ warn: user.name === '-' }"
        ) {{ user.displayName }} ({{ user.name }})
        .summary-chart__title--contribution.mini-font [{{ user.checkedFileTypeContribution }} lines]
        a(
          v-if="filterGroupSelection !== 'groupByRepos'",
          :class="!isBrokenLink(getRepoLink(user)) ? '' : 'broken-link'",
          :href="getRepoLink(user)", target="_blank"
        )
          .tooltip(
            @mouseover="onTooltipHover(`repo-${i}-author-${j}-repo-link`)",
            @mouseout="resetTooltip(`repo-${i}-author-${j}-repo-link`)"
          )
            font-awesome-icon.icon-button(:icon="getRepoIcon(repo[0])")
            span.tooltip-text(
              v-if="!isChartGroupWidgetMode",
              :ref="`repo-${i}-author-${j}-repo-link`"
            ) {{getRepoLinkMessage(user)}}
        a(
          v-if="filterGroupSelection !== 'groupByAuthors'",
          :class="!isBrokenLink(getAuthorProfileLink(user, user.name)) ? '' : 'broken-link'",
          :href="getAuthorProfileLink(user, user.name)", target="_blank"
        )
          .tooltip(
            @mouseover="onTooltipHover(`repo-${i}-author-${j}-author-link`)",
            @mouseout="resetTooltip(`repo-${i}-author-${j}-author-link`)"
          )
            font-awesome-icon.icon-button(icon="user")
            span.tooltip-text(
              v-if="!isChartGroupWidgetMode",
              :ref="`repo-${i}-author-${j}-author-link`"
            ) {{getAuthorProfileLinkMessage(user)}}
        a(
          v-if="!isChartGroupWidgetMode",
          onclick="deactivateAllOverlays()",
          @click="openTabAuthorship(user, repo, j, isGroupMerged(getGroupName(repo)))"
        )
          .tooltip(
            @mouseover="onTooltipHover(`repo-${i}-author-${j}-author-contribution`)",
            @mouseout="resetTooltip(`repo-${i}-author-${j}-author-contribution`)"
          )
            font-awesome-icon.icon-button(
              icon="code",
              :class="{ 'active-icon': isSelectedTab(user.name, user.repoName, 'authorship', false) }"
            )
            span.tooltip-text(
              :ref="`repo-${i}-author-${j}-author-contribution`"
              ) Click to view author's contribution.
        a(
          v-if="!isChartGroupWidgetMode",
          onclick="deactivateAllOverlays()",
          @click="openTabZoom(\
            user,\
            getSinceDate(user),\
            getUntilDate(user),\
            isGroupMerged(getGroupName(repo))\
          )"
        )
          .tooltip(
            @mouseover="onTooltipHover(`repo-${i}-author-${j}-commit-breakdown`)",
            @mouseout="resetTooltip(`repo-${i}-author-${j}-commit-breakdown`)"
          )
            font-awesome-icon.icon-button(
              icon="list-ul",
              :class="{ 'active-icon': isSelectedTab(user.name, user.repoName, 'zoom', false) }"
            )
            span.tooltip-text(
              :ref="`repo-${i}-author-${j}-commit-breakdown`"
            ) Click to view breakdown of commits
        a(
          v-if="isChartGroupWidgetMode",
          :href="getReportLink()", target="_blank"
        )
          .tooltip(
            @mouseover="onTooltipHover(`repo-${i}-author-${j}-commit-breakdown-group-widget`)",
            @mouseout="resetTooltip(`repo-${i}-author-${j}-commit-breakdown-group-widget`)"
          )
            font-awesome-icon.icon-button(
              icon="arrow-up-right-from-square",
            )
            span.tooltip-text(
              v-if="!isChartGroupWidgetMode",
              :ref="`repo-${i}-author-${j}-commit-breakdown-group-widget`"
            ) Click to view breakdown of commits on RepoSense
        a(
          v-if="!isChartGroupWidgetMode",
          @click="getEmbeddedIframe(i , j)"
        )
          .tooltip(
            :id="'tooltip-' + i + '-' + j",
            @mouseover="onTooltipHover(`repo-${i}-author-${j}-iframe-link`)",
            @mouseout="resetTooltip(`repo-${i}-author-${j}-iframe-link`)"
          )
            font-awesome-icon.icon-button(icon="clipboard")
            span.tooltip-text(:ref="`repo-${i}-author-${j}-iframe-link`") Click to copy iframe link
        .tooltip.summary-chart__title--percentile(
          v-if="filterGroupSelection === 'groupByNone' && sortGroupSelection.includes('totalCommits')"
        ) {{ getPercentile(j) }} %&nbsp
          span.tooltip-text.right-aligned {{ getPercentileExplanation(j) }}
        .summary-chart__title--tags(v-if="isViewingTagsByAuthor")
          a.tag(
            v-for="tag in getTags(repo, user)",
            :href="getTagLink(user, tag)",
            target="_blank",
            vbind:key="tag",
            tabindex="-1"
          )
            font-awesome-icon(icon="tags")
            span &nbsp;{{ tag }}
      .blurb-wrapper(
        v-if="filterGroupSelection === 'groupByRepos'")
        c-markdown-chunk.blurb(
          :markdown-text="getChartBlurb(user.name, repo[0])"
        )
      .blurb-wrapper(
        v-if="filterGroupSelection === 'groupByAuthors'")
        c-markdown-chunk.blurb(
          :markdown-text="getChartBlurb(repo[0].name, user)"
        )
      .summary-chart__ramp(
        @click="openTabZoomSubrange(user, $event, isGroupMerged(getGroupName(repo)))"
      )
        c-ramp(
          :groupby="filterGroupSelection",
          :user="user",
          :tframe="filterTimeFrame",
          :sdate="getSinceDate(user)",
          :udate="getUntilDate(user)",
          :avgsize="avgCommitSize",
          :mergegroup="isGroupMerged(getGroupName(repo))",
          :filtersearch="filterSearch",
          :is-widget-mode="isChartGroupWidgetMode",
          :optimise-timeline="getIsOptimising(user)"
        )
        .overlay

      .summary-chart__contribution
        template(v-if="filterBreakdown")
          .summary-chart__contrib
            c-stacked-bar-chart(
              :bars="getFileTypeContributionBars(user.fileTypeContribution, user.checkedFileTypeContribution)"
            )
        template(v-else)
          .summary-chart__contrib(
            :title="`Total contribution from ${minDate} to ${maxDate}: \
              ${user.checkedFileTypeContribution} lines`"
          )
            c-stacked-bar-chart(
              :bars="getContributionBars(user.checkedFileTypeContribution)"
            )
</template>

<script lang="ts">
import { defineComponent } from 'vue';
import { mapState } from 'vuex';

import brokenLinkDisabler from '../mixin/brokenLinkMixin';
import tooltipPositioner from '../mixin/dynamicTooltipMixin';
import cRamp from './c-ramp.vue';
import cStackedBarChart from './c-stacked-bar-chart.vue';
import cMarkdownChunk from './c-markdown-chunk.vue';
import { Bar, User } from '../types/types';
import { FilterGroupSelection, FilterTimeFrame, SortGroupSelection } from '../types/summary';
import { StoreState, ZoomInfo } from '../types/vuex.d';
import { AuthorFileTypeContributions } from '../types/zod/commits-type';

export default defineComponent({
  name: 'c-summary-charts',
  components: {
    cRamp,
    cStackedBarChart,
    cMarkdownChunk,
  },
  mixins: [brokenLinkDisabler, tooltipPositioner],
  props: {
    checkedFileTypes: {
      type: Array<string>,
      required: true,
    },
    filtered: {
      type: Array<Array<User>>,
      required: true,
    },
    avgContributionSize: {
      type: Number,
      required: true,
    },
    filterBreakdown: {
      type: Boolean,
      default: false,
    },
    filterGroupSelection: {
      type: String,
      default: FilterGroupSelection.GroupByRepos,
    },
    filterTimeFrame: {
      type: String,
      default: FilterTimeFrame.Commit,
    },
    filterSinceDate: {
      type: String,
      required: true,
    },
    filterUntilDate: {
      type: String,
      required: true,
    },
    isMergeGroup: {
      type: Boolean,
      default: false,
    },
    minDate: {
      type: String,
      required: true,
    },
    maxDate: {
      type: String,
      required: true,
    },
    filterSearch: {
      type: String,
      default: '',
    },
    sortGroupSelection: {
      type: String,
      default: SortGroupSelection.GroupTitle,
    },
    chartGroupIndex: {
      type: Number,
      default: undefined,
    },
    chartIndex: {
      type: Number,
      default: undefined,
    },
    viewRepoTags: {
      type: Boolean,
      default: false,
    },
    optimiseTimeline: {
      type: Boolean,
      default: false,
    },
  },
  data(): {
    drags: Array<number>,
    activeRepo: string | null,
    activeUser: string | null,
    activeTabType: string | null,
    isTabOnMergedGroup: boolean,
    isPortfolio: boolean,
  } {
    return {
      drags: [] as Array<number>,
      activeRepo: null as string | null,
      activeUser: null as string | null,
      activeTabType: null as string | null,
      isTabOnMergedGroup: false,
      isPortfolio: window.isPortfolio,
    };
  },

  computed: {
    avgCommitSize(): number {
      let totalCommits = 0;
      let totalCount = 0;
      this.filteredRepos.forEach((repo) => {
        repo.forEach((user: User) => {
          user.commits?.forEach((slice) => {
            if (slice.insertions > 0) {
              totalCount += 1;
              totalCommits += slice.insertions;
            }
          });
        });
      });
      return totalCommits / totalCount;
    },
    filteredRepos(): Array<Array<User>> {
      const repos = this.filtered.filter((repo) => repo.length > 0);
      if (this.isChartGroupWidgetMode && this.chartGroupIndex! < repos.length) {
        return [repos[this.chartGroupIndex!]];
      }
      return repos;
    },
    isChartGroupWidgetMode(): boolean {
      return this.chartGroupIndex !== undefined && this.chartGroupIndex >= 0;
    },
    isChartWidgetMode(): boolean {
      return this.chartIndex !== undefined && this.chartIndex >= 0 && this.isChartGroupWidgetMode;
    },
    isViewingTagsByRepo() {
      return this.filterGroupSelection === FilterGroupSelection.GroupByRepos && this.viewRepoTags;
    },
    isViewingTagsByAuthor() {
      return (this.filterGroupSelection === FilterGroupSelection.GroupByAuthors
        || this.filterGroupSelection === FilterGroupSelection.GroupByNone)
      && this.viewRepoTags;
    },
    ...mapState({
      mergedGroups: (state: unknown) => (state as StoreState).mergedGroups,
      fileTypeColors: (state: unknown) => (state as StoreState).fileTypeColors,
    }),
  },
  watch: {
    '$store.state.isTabActive': function () {
      if (!this.$store.state.isTabActive) {
        this.removeSelectedTab();
      }
    },

    // watching so highlighted only when summary charts are rendered
    filteredRepos() {
      this.$nextTick(() => {
        if (this.activeRepo !== null && this.activeUser !== null) {
          this.scrollToActiveRepo();
        }
      });
    },
  },
  created(): void {
    this.retrieveSelectedTabHash();
  },
  methods: {
    getFileTypeContributionBars(
      fileTypeContribution: AuthorFileTypeContributions,
      checkedFileTypeContribution: number | undefined,
    ): Array<Bar> {
      let currentBarWidth = 0;
      const fullBarWidth = 100;
      const contributionPerFullBar = (this.avgContributionSize * 2);

      const allFileTypesContributionBars: Array<Bar> = [];
      if (contributionPerFullBar === 0) {
        return allFileTypesContributionBars;
      }

      Object.keys(fileTypeContribution)
        .filter((fileType) => this.checkedFileTypes.includes(fileType))
        .forEach((fileType) => {
          const contribution = fileTypeContribution[fileType];
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
            allFileTypesContributionBars.push({
              width,
              color: this.fileTypeColors[fileType],
              tooltipText: `${fileType}: ${fileTypeContribution[fileType]} lines, \
                total: ${checkedFileTypeContribution} lines (contribution from ${this.minDate} to
                  ${this.maxDate})`,
            });
          });
        });

      return allFileTypesContributionBars;
    },

    getFileTypes(repo: Array<User>): Array<string> {
      const fileTypes: Array<string> = [];
      repo.forEach((user) => {
        Object.keys(user.fileTypeContribution).forEach((fileType) => {
          if (this.checkedFileTypes.includes(fileType) && !fileTypes.includes(fileType)) {
            fileTypes.push(fileType);
          }
        });
      });
      return fileTypes;
    },

    getGroupTotalContribution(group: Array<User>): number {
      return group.reduce((acc, ele) => acc + (ele.checkedFileTypeContribution ?? 0), 0);
    },

    getContributionBars(totalContribution: number): Array<Bar> {
      const res: Array<Bar> = [];
      const contributionLimit = (this.avgContributionSize * 2);
      if (contributionLimit === 0) {
        return res;
      }
      const cnt = Math.floor(totalContribution / contributionLimit);
      for (let cntId = 0; cntId < cnt; cntId += 1) {
        res.push({ width: 100 });
      }

      const last = (totalContribution % contributionLimit) / contributionLimit;
      if (last !== 0) {
        res.push({ width: last * 100 });
      }

      return res;
    },

    getAuthorProfileLink(repo: User, userName: string): string | undefined {
      return window.getAuthorLink(repo.repoId, userName);
    },

    getGroupRepoLinkMessage(repo: User): string {
      return this.getLinkMessage(this.getRepoLink(repo), 'Click to view group\'s repo');
    },

    getAuthorProfileLinkMessage(repo: User): string {
      return this.getLinkMessage(this.getAuthorProfileLink(repo, repo.name), 'Click to view author\'s profile');
    },

    getRepoLinkMessage(repo: User): string {
      return this.getLinkMessage(this.getRepoLink(repo), 'Click to view repo');
    },

    getRepoLink(repo: User): string | undefined {
      const { REPOS } = window;
      const { location, branch } = REPOS[repo.repoId];

      if (Object.prototype.hasOwnProperty.call(location, 'organization')) {
        return window.getBranchLink(repo.repoId, branch);
      }
      this.removeSelectedTab();
      return repo.location;
    },

    getRepoIcon(repo: User): Array<string> {
      const domainName = window.REPOS[repo.repoId].location.domainName;

      switch (domainName) {
      case 'github':
        return ['fab', 'github'];
      case 'gitlab':
        return ['fab', 'gitlab'];
      case 'bitbucket':
        return ['fab', 'bitbucket'];
      default:
        return ['fas', 'database'];
      }
    },

    getSinceDate(user: User): string {
      if (this.getIsOptimising(user)) {
        // Get the earliest commit date
        return user.commits.reduce((prev, curr) => (new Date(prev.date) < new Date(curr.date) ? prev : curr)).date;
      }

      if (this.isPortfolio) {
        // Portfolio mode disregards filter date ranges
        return user.sinceDate;
      }

      return this.filterSinceDate;
    },

    getUntilDate(user: User): string {
      if (this.getIsOptimising(user)) {
        // Get the latest commit date
        return user.commits.reduce((prev, curr) => (new Date(prev.date) > new Date(curr.date) ? prev : curr)).date;
      }

      if (this.isPortfolio) {
        // Portfolio mode disregards filter date ranges
        return user.untilDate;
      }

      return this.filterUntilDate;
    },

    getIsOptimising(user: User): boolean {
      return user.commits.length !== 0 && this.optimiseTimeline;
    },

    getTagLink(repo: User, tag: string): string | undefined {
      return window.filterUnsupported(`${window.getRepoLinkUnfiltered(repo.repoId)}releases/tag/${tag}`);
    },

    // triggering opening of tabs //
    openTabAuthorship(user: User, repo: Array<User>, index: number, isMerged: boolean): void {
      const {
        minDate, maxDate, checkedFileTypes,
      } = this;

      const info = {
        minDate,
        maxDate,
        checkedFileTypes,
        author: user.name,
        repo: user.repoName,
        name: user.displayName,
        isMergeGroup: isMerged,
        location: this.getRepoLink(repo[index]),
        files: [],
      };
      this.addSelectedTab(user.name, user.repoName, 'authorship', isMerged);
      this.$store.commit('updateTabAuthorshipInfo', info);
    },

    openTabZoomSubrange(user: User, evt: MouseEvent, isMerged: boolean): void {
      const isKeyPressed = window.isMacintosh ? evt.metaKey : evt.ctrlKey;

      if (isKeyPressed) {
        if (this.drags.length === 0) {
          this.dragViewDown(evt);
        } else {
          this.dragViewUp(evt);
        }
      }

      // skip if accidentally clicked on ramp chart
      if (this.drags.length === 2 && this.drags[1] - this.drags[0]) {
        const fromDate = (new Date(this.getSinceDate(user))).valueOf();
        const toDate = (new Date(this.getUntilDate(user))).valueOf();

        const tdiff = toDate - fromDate + window.DAY_IN_MS;
        const idxs = this.drags.map((x) => (x * tdiff) / 100);
        const tsince = window.getDateStr(fromDate + idxs[0]);
        const tuntil = window.getDateStr(fromDate + idxs[1]);
        this.drags = [];
        this.openTabZoom(user, tsince, tuntil, isMerged);
      }
    },

    openTabZoom(user: User, since: string, until: string, isMerged: boolean): void {
      const {
        avgCommitSize, filterGroupSelection, filterTimeFrame, filterSearch,
      } = this;
      // Deep copy to ensure changes in zoom (e.g. toggle state) won't affect summary, and vice versa
      const clonedUser = JSON.parse(JSON.stringify(user));
      const info: ZoomInfo = {
        zRepo: user.repoName,
        zAuthor: user.name,
        zFilterGroup: filterGroupSelection,
        zTimeFrame: filterTimeFrame,
        zAvgCommitSize: avgCommitSize,
        zUser: clonedUser,
        zLocation: this.getRepoLink(user),
        zSince: since || user.sinceDate,
        zUntil: until || user.untilDate,
        zIsMerged: isMerged,
        zFileTypeColors: this.fileTypeColors,
        zFromRamp: false,
        zFilterSearch: filterSearch,
        zAvgContributionSize: this.getAvgContributionSize(),
      };
      this.addSelectedTab(user.name, user.repoName, 'zoom', isMerged);
      this.$store.commit('updateTabZoomInfo', info);
    },

    async getEmbeddedIframe(chartGroupIndex: number, chartIndex: number = -1): Promise<void> {
      const isChartIndexProvided = chartIndex !== -1;
      // Set height of iframe according to number of charts to avoid scrolling
      let totalChartHeight = 0;
      if (!isChartIndexProvided) {
        totalChartHeight += (this.$refs[`summary-charts-${chartGroupIndex}`] as Array<HTMLElement>)[0].clientHeight;
      } else {
        totalChartHeight += (this.$refs[`summary-chart-${chartIndex}`] as Array<HTMLElement>)[0].clientHeight;
        totalChartHeight += this.filterGroupSelection === 'groupByNone'
          ? 0
          : (this.$refs[`summary-charts-title-${chartGroupIndex}`] as Array<HTMLElement>)[0].clientHeight;
      }

      const margins = 30;
      const iframeStart = '<iframe src="';
      const iframeEnd = `" frameBorder="0" width="800px" height="${totalChartHeight + margins}px"></iframe>`;
      const [baseUrl, ...params] = window.location.href.split('?');
      const groupIndexParam = isChartIndexProvided ? `&chartIndex=${chartIndex}` : '';
      const url = `${baseUrl}#/widget/?${params.join('?')}&chartGroupIndex=${chartGroupIndex}${groupIndexParam}`;
      const iframe = iframeStart + url + iframeEnd;
      if (navigator.clipboard) {
        navigator.clipboard.writeText(iframe);
      } else {
        // Clipboard API is not supported (non-secure origin of neither HTTPS nor localhost)
        const textarea = document.createElement('textarea');
        textarea.value = iframe;
        textarea.setAttribute('readonly', '');
        textarea.style.position = 'absolute';
        textarea.style.left = '-9999px';
        document.body.appendChild(textarea);
        textarea.select();
        document.execCommand('copy');
        document.body.removeChild(textarea);
      }
      const tooltipId = `tooltip-${chartGroupIndex}${isChartIndexProvided ? `-${chartIndex}` : ''}`;
      this.updateCopyTooltip(tooltipId, 'Copied iframe');
    },
    updateCopyTooltip(tooltipId: string, text: string): void {
      const tooltipElement = document.getElementById(tooltipId);
      if (tooltipElement && tooltipElement.querySelector('.tooltip-text')) {
        const tooltipTextElement = tooltipElement.querySelector('.tooltip-text');
        const originalText = tooltipTextElement!.textContent;
        tooltipElement.querySelector('.tooltip-text')!.textContent = text;
        setTimeout(() => {
          tooltipTextElement!.textContent = originalText;
        }, 2000);
      }
    },
    getReportLink(): string {
      const url = window.location.href;
      const regexToRemoveWidget = /([?&])((chartIndex|chartGroupIndex)=\d+)/g;
      return url.replace(regexToRemoveWidget, '');
    },
    getRepo(repo: Array<User>): Array<User> {
      if (this.isChartGroupWidgetMode && this.isChartWidgetMode) {
        return [repo[this.chartIndex!]];
      }
      return repo;
    },

    getBaseTarget(target: HTMLElement | null): HTMLElement | null {
      if (!target) {
        // Should never reach here - function assumes that target is a child of the div with class 'summary-chart__ramp'
        // eslint-disable-next-line no-console
        console.error('Error: The getBaseTarget function in c-summary-charts.vue has been called on an element that is '
        + 'not a child of the div with class summary-chart__ramp. This might affect the drag view functionality.');
        return null;
      }
      return target.className === 'summary-chart__ramp'
          ? target
          : this.getBaseTarget(target.parentElement);
    },

    dragViewDown(evt: MouseEvent): void {
      window.deactivateAllOverlays();

      const pos = evt.clientX;
      const ramp = this.getBaseTarget(evt.target as HTMLElement);
      if (!ramp || !ramp.parentElement) {
        return;
      }
      this.drags = [pos];

      const base = ramp.offsetWidth;
      const offset = ramp.parentElement.offsetLeft;

      const overlay = ramp.getElementsByClassName('overlay')[0] as HTMLElement;
      overlay.style.marginLeft = '0';
      overlay.style.width = `${((pos - offset) * 100) / base}%`;
      overlay.className += ' edge';
    },

    dragViewUp(evt: MouseEvent): void {
      window.deactivateAllOverlays();
      const ramp = this.getBaseTarget(evt.target as HTMLElement);
      if (!ramp || !ramp.parentElement) {
        return;
      }

      const base = ramp.offsetWidth;
      this.drags.push(evt.clientX);
      this.drags.sort((a, b) => a - b);

      const offset = ramp.parentElement.offsetLeft;
      this.drags = this.drags.map((x) => ((x - offset) * 100) / base);

      const overlay = ramp.getElementsByClassName('overlay')[0] as HTMLElement;
      overlay.style.marginLeft = `${this.drags[0]}%`;
      overlay.style.width = `${this.drags[1] - this.drags[0]}%`;
      overlay.className += ' show';
    },

    getPercentile(index: number): string {
      if (this.filterGroupSelection === FilterGroupSelection.GroupByNone) {
        return (Math.round(((index + 1) * 1000) / this.filtered[0].length) / 10).toFixed(1);
      }
      return (Math.round(((index + 1) * 1000) / this.filtered.length) / 10).toFixed(1);
    },

    getGroupName(group: Array<User>): string {
      return window.getGroupName(group, this.filterGroupSelection);
    },

    isGroupMerged(groupName: string): boolean {
      return this.mergedGroups.includes(groupName);
    },

    handleMergeGroup(groupName: string): void {
      const info = this.mergedGroups;
      info.push(groupName);
      this.$store.commit('updateMergedGroup', info);
    },

    handleExpandGroup(groupName: string): void {
      const info = this.mergedGroups.filter((x) => x !== groupName);
      this.$store.commit('updateMergedGroup', info);
    },

    getAuthorDisplayName(repo: Array<User>): string {
      return window.getAuthorDisplayName(repo);
    },

    retrieveSelectedTabHash(): void {
      const hash = window.hashParams;

      if (hash.tabAuthor) {
        this.activeUser = hash.tabAuthor;
      } else if (hash.zA) {
        this.activeUser = hash.zA;
      }

      if (hash.tabRepo) {
        this.activeRepo = hash.tabRepo;
      } else if (hash.zR) {
        this.activeRepo = hash.zR;
      }

      if (hash.isTabOnMergedGroup) {
        if (this.filterGroupSelection === FilterGroupSelection.GroupByAuthors) {
          this.activeRepo = null;
        } else if (this.filterGroupSelection === FilterGroupSelection.GroupByRepos) {
          this.activeUser = null;
        }
        this.isTabOnMergedGroup = true;
      }

      if (hash.tabType) {
        this.activeTabType = hash.tabType;
      }
    },

    addSelectedTab(userName: string, repo: string, tabType: string, isMerged: boolean): void {
      if (!isMerged || this.filterGroupSelection === FilterGroupSelection.GroupByAuthors) {
        this.activeUser = userName;
      } else {
        this.activeUser = null;
      }

      if (isMerged && this.filterGroupSelection === FilterGroupSelection.GroupByAuthors) {
        this.activeRepo = null;
      } else {
        this.activeRepo = repo;
      }

      if (isMerged) {
        window.addHash('isTabOnMergedGroup', 'true');
        this.isTabOnMergedGroup = true;
      } else {
        window.removeHash('isTabOnMergedGroup');
        this.isTabOnMergedGroup = false;
      }

      this.activeTabType = tabType;
      window.encodeHash();

      this.$nextTick(() => this.scrollToActiveRepo());
    },

    removeSelectedTab(): void {
      this.activeUser = null;
      this.activeRepo = null;
      this.activeTabType = null;

      window.removeHash('isTabOnMergedGroup');
      window.encodeHash();
    },

    isSelectedTab(userName: string, repo: string, tabType: string, isMerged: boolean): boolean {
      if (!isMerged) {
        return this.activeUser === userName && this.activeRepo === repo
            && this.activeTabType === tabType;
      }

      if (this.filterGroupSelection === FilterGroupSelection.GroupByAuthors) {
        return this.isTabOnMergedGroup && this.activeUser === userName
            && this.activeTabType === tabType;
      }

      return this.isTabOnMergedGroup && this.activeRepo === repo
          && this.activeTabType === tabType;
    },

    isSelectedGroup(userName: string, repo: string): boolean {
      return this.isTabOnMergedGroup
          && ((this.filterGroupSelection === FilterGroupSelection.GroupByRepos && this.activeRepo === repo)
          || (this.filterGroupSelection === FilterGroupSelection.GroupByAuthors && this.activeUser === userName));
    },

    getPercentileExplanation(j: number): string {
      const explanation = `Based on the current sorting order, this item is in the top ${this.getPercentile(j)}%`;
      return explanation;
    },

    getAvgContributionSize(): number {
      let totalContribution = 0;
      let totalCommits = 0;

      this.filteredRepos.forEach((repo) => {
        repo.forEach((user) => {
          user.commits?.forEach((commit) => {
            totalCommits += 1;
            totalContribution += (commit.insertions + commit.deletions);
          });
        });
      });

      return totalContribution / totalCommits;
    },

    scrollToActiveRepo(): void {
      const chart = document.getElementById('selectedChart');
      if (chart) {
        chart.scrollIntoView({ block: 'nearest' });
      }
    },

    getTags(repo: Array<User>, user?: User): Array<string> {
      if (user) repo = repo.filter((r) => r.name === user.name && r.repoId === user.repoId);
      return [...new Set(repo.flatMap((r) => r.commits).flatMap((c) => c.commitResults).flatMap((r) => r.tags))]
        .filter(Boolean) as Array<string>;
    },

    getRepoBlurb(repo: User): string {
      const link = this.getRepoLink(repo);
      if (!link) {
        return '';
      }
      const blurb: string | undefined = this.$store.state.repoBlurbMap[link];
      if (!blurb) {
        return '';
      }
      return blurb;
    },

    getChartBlurb(userName: string, repo: User) : string {
      const link = this.getRepoLink(repo);
      const blurb: string | undefined = this.$store.state.chartsBlurbMap[`${link}|${userName}`]
      if (!blurb) {
        return '';
      }
      return blurb;
    },

    getAuthorBlurb(userName: string): string {
      const blurb: string | undefined = this.$store.state.authorBlurbMap[userName]
      if (!blurb) {
        return '';
      }
      return blurb;
    }
  },
});
</script>

<style lang="scss" scoped>
@import '../styles/tags.scss';
@import '../styles/_colors.scss';

.blurb-wrapper {
  padding-bottom: 5px;

  .blurb {
    background-color: #F6F8FA;
    border-color: #E9EBEF;
    border-radius: 4px;
    border-style: solid;
    border-width: 1px;
    overflow-y: hidden;
    // This is needed because the inline style of normalize.css adds bottom margins to all p tags, including the
    // ones in the blurb.
    padding-top: 10px;
    // This is needed because the parent summary-wrapper center aligns everything
    text-align: initial;
  }
}
</style>
