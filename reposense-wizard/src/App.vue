<template lang="pug">
  .app
    img(alt="Vue logo", src="./assets/reposense_logo.svg")
    h1 Create your first RepoSense report
    h2 Repositories
    RepoList(v-bind:repos="repos")
    p Repositories must be in Git and publicly accessible
    h2 Advanced Options
</template>

<script>
import RepoList from './components/RepoList.vue'

export default {
  name: 'App',
  components: {
    RepoList
  },
  data() {
    return {
      repos: [{'url': 'aaa.com', 'branch': 'bbb'}],
      startDate: '',
      endDate: '',
      timezone: 8,
    };
  },
}
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
