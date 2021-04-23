window.vRamp = {
  props: ['groupby', 'user', 'tframe', 'avgsize', 'sdate', 'udate', 'mergegroup', 'fromramp', 'filtersearch'],
  template: window.$('v_ramp').innerHTML,
  data() {
    return {
      rampSize: 0.01,
    };
  },

  methods: {
    getLink(user, slice) {
      if (this.mergegroup) {
        return `${window.getBaseLink(slice.repoId)}/commit/${slice.hash}`;
      }

      return `${window.getBaseLink(user.repoId)}/commit/${slice.hash}`;
    },
    getWidth(slice) {
      if (slice.insertions === 0) {
        return 0;
      }

      const newSize = 100 * (slice.insertions / this.avgsize);
      return Math.max(newSize * this.rampSize, 0.5);
    },
    getContributionMessage(slice, commit) {
      let title = '';
      if (this.tframe === 'commit') {
        return `[${slice.date}] ${commit.messageTitle}: ${commit.insertions} lines`;
      }

      title = this.tframe === 'day'
            ? `[${slice.date}] Daily `
            : `[${slice.date} till ${slice.endDate}] Weekly `;
      title += `contribution: ${slice.insertions} lines`;
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
        zLocation: window.getBaseLink(user.repoId),
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
      const timeMs = this.fromramp
          ? (new Date(this.sdate)).getTime()
          : (new Date(date)).getTime();

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
