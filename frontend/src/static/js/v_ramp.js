function addDays(dateStr, numDays) {
  const date = new Date(dateStr);
  return getDateStr(date.getTime() + numDays * DAY_IN_MS);
}

window.vRamp = {
  props: ['user', 'tframe', 'avgsize'],
  template: window.$('v_ramp').innerHTML,
  data() {return {
    rampSize: 0.01,
  };},

  methods: {
    getPos(i, total) {
      return (total - i - 1) / total;
    },
    getLink(user, slice) {
      const { REPOS } = window;
      const untilDate = this.tframe === 'week' ? addDays(slice.date, 6) : slice.date;

      return `http://github.com/${
        REPOS[user.repoId].location.organization}/${
        REPOS[user.repoId].location.repoName}/commits/${
        REPOS[user.repoId].branch}?`
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

  create(){
    console.log(this.user, this.tframe);
  }
};
