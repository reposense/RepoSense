function addDays(dateStr, numDays) {
  const date = new Date(dateStr);
  return window.getDateStr(date.getTime() + numDays * window.DAY_IN_MS);
}

function getBaseLink(repoId) {
  return `http://github.com/${
    window.REPOS[repoId].location.organization}/${
    window.REPOS[repoId].location.repoName}`;
}
window.getBaseLink = getBaseLink;

window.vRamp = {
  props: ['user', 'tframe', 'avgsize'],
  template: window.$('v_ramp').innerHTML,
  data() {
    return {
      rampSize: 0.01,
    };
  },

  methods: {
    getPos(i, total) {
      return (total - i - 1) / total;
    },
    getLink(user, slice) {
      const { REPOS } = window;
      const untilDate = this.tframe === 'week' ? addDays(slice.date, 6) : slice.date;

      if (this.tframe === 'commit') {
        return `${getBaseLink(user.repoId)}/commit/${slice.hash}`;
      }
      return `${getBaseLink(user.repoId)}/commits/${REPOS[user.repoId].branch}?`
                  + `author=${user.name}&`
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
  },
};
