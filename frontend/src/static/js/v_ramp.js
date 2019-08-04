function getBaseLink(repoId) {
  return `http://github.com/${
    window.REPOS[repoId].location.organization}/${
    window.REPOS[repoId].location.repoName}`;
}

window.rampClick = function rampClick(evt) {
  const isKeyPressed = this.isMacintosh ? evt.metaKey : evt.ctrlKey;
  if (isKeyPressed) {
    evt.preventDefault();
  }
};

window.vRamp = {
  props: ['groupby', 'user', 'tframe', 'avgsize', 'sdate', 'udate', 'mergegroup'],
  template: window.$('v_ramp').innerHTML,
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
