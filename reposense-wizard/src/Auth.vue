<template lang="pug">
  .app
    h1 Authenticating please wait.
</template>

<script>
const GITHUB_OAUTH_URL = 'https://github.com/login/oauth/authorize';
const GATEKEEPER_URL = 'http://localhost:9999/authenticate';
const CLIENT_ID = 'c498493d4c565ced8d0b';

export default {
  name: 'Auth',
  created() {
    this.handleAuthRedirect();
  },
  methods: {
    oAuthAuthenticate() {
      const queries = {
        client_id: CLIENT_ID,
        scope: 'public_repo',
      };
      const queryString = new URLSearchParams(queries).toString();
      window.location = `${GITHUB_OAUTH_URL}?${queryString}`;
    },

    async requestToken(code) {
      const response = await fetch(`${GATEKEEPER_URL}/${code}`);
      const json = await response.json();
      return json.token;
    },

    async handleAuthRedirect() {
      const { code } = this.$route.query;
      if (code == null) {
        this.$router.push({ path: '/', query: {} });
        return;
      }
      try {
        const token = await this.requestToken(code);
        await this.$store.dispatch('authenticate', token);
      } catch {
        this.$router.push({ path: '/', query: {} });
        return;
      }
      this.$router.push({ path: '/', query: {} });
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
