<template lang="pug">
#app
  loading-overlay.overlay-loader(
    :active='loadingOverlayCount > 0',
    :opacity='loadingOverlayOpacity'
  )
    template(#default)
      i.overlay-loading-icon.fa.fa-spinner.fa-spin()
    template(#after)
      h3 {{ loadingOverlayMessage }}

  router-view(
    :update-report-zip="updateReportZip",
    :repos="repos",
    :users="users",
    :user-updated="userUpdated",
    :loading-overlay-opacity="loadingOverlayOpacity",
    :tab-type="tabType",
    :creation-date="creationDate",
    :report-generation-time="reportGenerationTime",
    :error-messages="errorMessages"
    )
</template>

<script lang='ts'>
import { defineComponent } from 'vue';
import JSZip from 'jszip';
import LoadingOverlay from 'vue-loading-overlay';
import { mapState } from 'vuex';
import { Repo } from './types/types';
import { ErrorMessage } from './types/zod/summary-type';
import { ZoomInfo, AuthorshipInfo } from './types/vuex.d';

const loadingResourcesMessage = 'Loading resources...';

const app = defineComponent({
  name: 'app',
  components: {
    LoadingOverlay,
  },
  data(): {
    repos: { [key: string]: Repo },
    users: Array<Repo>,
    userUpdated: boolean,
    loadingOverlayOpacity: number,
    tabType: string,
    creationDate: string,
    reportGenerationTime: string,
    errorMessages: { [key: string]: ErrorMessage }
  } {
    return {
      repos: {} as { [key: string]: Repo },
      users: [] as Array<Repo>,
      userUpdated: false,

      loadingOverlayOpacity: 1,

      tabType: 'empty',
      creationDate: '',
      reportGenerationTime: '',
      errorMessages: {} as { [key: string]: ErrorMessage },
    };
  },
  computed: {
    ...mapState(['loadingOverlayCount', 'loadingOverlayMessage', 'isTabActive']),
  },
  watch: {
    '$store.state.tabZoomInfo': function (): void {
      if (this.$store.state.tabZoomInfo.isRefreshing) {
        return;
      }
      this.activateTab('zoom');
    },
    '$store.state.tabAuthorshipInfo': function (): void {
      this.activateTab('authorship');
    },
  },
  created(): void {
    try {
      window.decodeHash();
    } catch (error) {
      this.userUpdated = false;
    }
    this.updateReportDir();
  },
  methods: {
    // model functions //
    updateReportZip(evt: Event): void {
      this.users = [];
      const target = evt.target as HTMLInputElement;
      if (target.files === null) {
        return;
      }
      JSZip.loadAsync(target.files[0])
        .then((zip) => {
          window.REPORT_ZIP = zip;
        }, () => {
          window.alert('Either the .zip file is corrupted, or you uploaded a .zip file that is not generated '
            + 'by RepoSense.');
        })
        .then(() => this.updateReportView());
    },

    updateReportDir(): void {
      window.REPORT_ZIP = null;

      this.users = [];
      this.updateReportView();
    },

    async updateReportView(): Promise<void> {
      this.$store.commit('updateLoadingOverlayMessage', loadingResourcesMessage);
      this.userUpdated = false;
      await this.$store.dispatch('incrementLoadingOverlayCountForceReload', 1);
      try {
        const summary = await window.api.loadSummary();
        if (summary === null) {
          return;
        }
        const {
          creationDate,
          reportGenerationTime,
          errorMessages,
          names,
          repoBlurbMap,
          authorBlurbMap,
          chartsBlurbMap
        } = summary;
        this.creationDate = creationDate;
        this.reportGenerationTime = reportGenerationTime;
        this.errorMessages = errorMessages;
        this.repos = window.REPOS;
        await Promise.all(names.map((name) => (
          window.api.loadCommits(name)
        )));
        this.loadingOverlayOpacity = 0.5;
        this.getUsers();
        this.renderTabHash();
        this.userUpdated = true;
        this.$store.commit('setBlurbMap', repoBlurbMap);
        this.$store.commit('setAuthorBlurbMap', authorBlurbMap);
        this.$store.commit('setChartsBlurbMap', chartsBlurbMap);
      } catch (error) {
        window.alert(error);
      } finally {
        this.$store.commit('incrementLoadingOverlayCount', -1);
      }
    },
    getUsers(): void {
      const full: Array<Repo> = [];
      Object.keys(this.repos).forEach((repo) => {
        if (this.repos[repo].users) {
          full.push(this.repos[repo]);
        }
      });
      this.users = full;
    },

    // handle opening of sidebar //
    activateTab(tabName: string): void {
      if (this.$refs.tabWrapper) {
        (this.$refs.tabWrapper as HTMLElement).scrollTop = 0;
      }

      this.tabType = tabName;
      this.$store.commit('updateTabState', true);
      window.addHash('tabType', this.tabType);
      window.encodeHash();
    },

    renderAuthorShipTabHash(minDate: string, maxDate: string): void {
      const hash = window.hashParams;
      const info: AuthorshipInfo = {
        author: hash.tabAuthor,
        repo: hash.tabRepo,
        isMergeGroup: hash.authorshipIsMergeGroup === 'true',
        isRefresh: true,
        minDate,
        maxDate,
        location: this.getRepoLink(),
        files: [],
      };
      const tabInfoLength = Object.values(info).filter((x) => x !== null).length;
      if (Object.keys(info).length === tabInfoLength) {
        this.$store.commit('updateTabAuthorshipInfo', info);
      } else if (hash.tabOpen === 'false' || tabInfoLength > 2) {
        this.$store.commit('updateTabState', false);
      }
    },

    renderZoomTabHash(): void {
      const hash = window.hashParams;
      const zoomInfo: ZoomInfo = {
        isRefreshing: true,
        zAuthor: hash.zA,
        zRepo: hash.zR,
        zAvgCommitSize: hash.zACS,
        zSince: hash.zS,
        zUntil: hash.zU,
        zFilterGroup: hash.zFGS,
        zFilterSearch: hash.zFS,
        zTimeFrame: hash.zFTF,
        zIsMerged: hash.zMG === 'true',
        zFromRamp: hash.zFR === 'true',
      };
      const tabInfoLength = Object.values(zoomInfo).filter((x) => x !== null).length;
      if (Object.keys(zoomInfo).length === tabInfoLength) {
        this.$store.commit('updateTabZoomInfo', zoomInfo);
      } else if (hash.tabOpen === 'false' || tabInfoLength > 2) {
        this.$store.commit('updateTabState', false);
      }
    },

    renderTabHash(): void {
      const hash = window.hashParams;
      if (!hash.tabOpen) {
        return;
      }
      this.$store.commit('updateTabState', hash.tabOpen === 'true');

      if (this.isTabActive) {
        if (hash.tabType === 'authorship') {
          let { since, until } = hash;

          // get since and until dates from window if not found in hash
          since = since || window.sinceDate;
          until = until || window.untilDate;
          this.renderAuthorShipTabHash(since, until);
        } else {
          this.renderZoomTabHash();
        }
      }
    },

    getRepoSenseHomeLink(): string {
      const version = window.repoSenseVersion;
      if (!version) {
        return `${window.HOME_PAGE_URL}/RepoSense/`;
      }
      return `${window.HOME_PAGE_URL}`;
    },

    getRepoLink(): string | undefined {
      const { REPOS, hashParams } = window;
      const { location, branch } = REPOS[hashParams.tabRepo];

      if (Object.prototype.hasOwnProperty.call(location, 'organization')) {
        return window.getBranchLink(hashParams.tabRepo, branch);
      }
      return REPOS[hashParams.tabRepo].location.location;
    },
  },
});

export default app;
</script>

<style lang="scss">
@import './styles/style.scss';
@import './styles/panels.scss';
</style>
