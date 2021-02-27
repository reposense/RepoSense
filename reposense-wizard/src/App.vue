<template lang="pug">
  .app
    img(alt="Vue logo", src="./assets/reposense_logo.svg")
    b-button(variant="success", v-on:click="oAuthAuthenticate()")
      | {{isLoggedIn ? 'already in' : 'login'}}
    h1 Create your first RepoSense report with ReposenseWizard!
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
    b-form-input(
      v-model="pat",
      placeholder="Personal Access Token",
    )
    br
    b-button(variant="success", v-on:click="startGenerateReport()") Generate
    p {{generateReportResult}}
</template>

<script>
import RepoList from './components/RepoList.vue';
import AdvancedOptions from './components/AdvancedOptions.vue';
import GithubApi from './utils/githubApi';
import generateReport from './utils/generateReport';
import { oAuthAuthenticate, handleAuthRedirect } from './utils/auth';

export default {
  name: 'App',
  components: {
    RepoList,
    AdvancedOptions,
  },
  data() {
    return {
      githubApi: new GithubApi(),
      isLoggedIn: false,
      repos: [],
      startDate: '',
      endDate: '',
      timezone: 8,
      generateReportResult: '',
    };
  },
  created() {
    this.initiate();
  },
  methods: {
    updateRepo(e) {
      this.$set(this.repos[e.index], e.field, e.newValue);
    },

    async initiate() {
      this.isLoggedIn = await handleAuthRedirect(this.githubApi);
    },

    async startGenerateReport() {
      const data = {
        repos: this.repos,
      };
      this.generateReportResult = 'Generating Report, please wait...';
      this.generateReportResult = await generateReport(data, this.githubApi);
    },

    oAuthAuthenticate() {
      oAuthAuthenticate();
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
