<template lang="pug">
  .app
    img(alt="Vue logo", src="../assets/reposense_logo.svg")
    b-button(variant="success", v-on:click="oAuthAuthenticate()")
      | {{isLoggedIn ? `Logged in as ${loginUser}` : 'login'}}
    h1 Create your first RepoSense report!
    h2 Repositories
    RepoList(
      v-bind:repos="repos",
      v-on:updateRepo="updateRepo($event)",
      v-on:addRepo="repos.push($event)",
      v-on:removeRepo="repos.splice($event, 1)",
    )
    h2 Advanced Options
    AdvancedOptions(
      v-bind:startDate="startDate",
      v-bind:endDate="endDate",
      v-bind:timezone="timezone",
      v-on:updateStartDate="startDate = $event",
      v-on:updateEndDate="endDate = $event",
      v-on:updateTimezone="timezone = $event",
    )
    br
    b-button(variant="success", v-on:click="startGenerateReport()") Generate
    p {{generateReportResult}}
</template>

<script>
import { mapState } from 'vuex';
import RepoList from '../components/RepoList.vue';
import AdvancedOptions from '../components/AdvancedOptions.vue';
import { generateReport } from '../generateReport';

const GITHUB_OAUTH_URL = 'https://github.com/login/oauth/authorize';
const CLIENT_ID = process.env.VUE_APP_CLIENT_ID;

export default {
  name: 'Home',
  components: {
    RepoList,
    AdvancedOptions,
  },
  data() {
    return {
      isLoggedIn: false,
      repos: [{ url: '', branch: '' }],
      startDate: '',
      endDate: '',
      timezone: 8,
      generateReportResult: '',
    };
  },
  computed: {
    ...mapState({
      loginUser: (state) => state.githubStore.loginUser,
    }),
  },
  created() {
    this.initiate();
  },
  methods: {
    updateRepo(e) {
      this.$set(this.repos[e.index], e.field, e.newValue);
    },

    async initiate() {
      await this.$store.dispatch('authenticate');
      this.isLoggedIn = this.$store.getters.isAuthenticated;
    },

    async startGenerateReport() {
      const data = {
        repos: this.repos,
        since: this.startDate,
        until: this.endDate,
        timezone: this.timezone,
      };
      this.generateReportResult = 'Generating Report, please wait...';
      this.generateReportResult = await generateReport(data, this.$store);
    },

    oAuthAuthenticate() {
      const queries = {
        client_id: CLIENT_ID,
        scope: 'public_repo',
      };
      const queryString = new URLSearchParams(queries).toString();
      // This redirects to the github website.
      window.location = `${GITHUB_OAUTH_URL}?${queryString}`;
    },
  },
};
</script>

<style>
body {
  background-color: #E9ECEF;
}

.app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  margin: 15px;
  margin-top: 30px;
}
</style>
