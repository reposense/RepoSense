<template lang="pug">
  .app
    img.banner-img(alt="Vue logo", src="../assets/reposense_logo.svg")
    b-button.login-button(
      v-if='isLoggedIn',
      variant="danger",
      v-on:click="logout()"
    ) logout
    b-button.login-button(
      v-else,
      variant="success",
      v-on:click="oAuthAuthenticate()"
    ) login
    br
    p {{isLoggedIn ? `Logged in as: ${loginUser}` : 'Please log in'}}
    br
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
import { mapState, mapGetters } from 'vuex';
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
    ...mapGetters({
      isLoggedIn: 'isAuthenticated',
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

    logout() {
      this.$store.commit('logout');
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

.banner-img {
  width: 50vw;
  max-width: 460px;
}

.login-button {
  position: absolute;
  right: 20px;
  top: 60px;
}
</style>
