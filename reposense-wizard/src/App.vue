<template lang="pug">
  .app
    img(alt="Vue logo", src="./assets/reposense_logo.svg")
    h1 Create your first RepoSense report
    h2 Repositories
    RepoList(
      v-bind:repos="repos",
      v-on:updateRepo="updateRepo($event)",
      v-on:addRepo="repos.push($event)",
      v-on:removeRepo="repos.splice($event, 1)",
    )
    p Repositories must be in Git and publicly accessible
    h2 Advanced Options
    AdvancedOptions
    b-button(variant="success") Generate
</template>

<script>
import RepoList from './components/RepoList.vue';
import AdvancedOptions from './components/AdvancedOptions.vue';

export default {
  name: 'App',
  components: {
    RepoList,
    AdvancedOptions,
  },
  data() {
    return {
      repos: [{ url: 'aaa.com', branch: 'bbb' }],
      startDate: '',
      endDate: '',
      timezone: 8,
    };
  },
  methods: {
    updateRepo(e) {
      this.$set(this.repos[e.index], e.field, e.newValue);
    },
  },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #E9ECEF;
  margin-top: 60px;
}
</style>
