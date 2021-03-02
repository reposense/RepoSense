<template lang="pug">
  .app
    h1 Authenticating please wait.
</template>

<script>
const GATEKEEPER_URL = process.env.VUE_APP_GATEKEEPER_URL;

export default {
  name: 'Auth',
  created() {
    this.handleAuthRedirect();
  },
  methods: {
    async requestToken(code) {
      const response = await fetch(`${GATEKEEPER_URL}/${code}`);
      const json = await response.json();
      return json.token;
    },

    async handleAuthRedirect() {
      const { code } = this.$route.query;
      if (!code) {
        this.$router.push({ path: '/', query: {} });
        return;
      }
      try {
        const token = await this.requestToken(code);
        await this.$store.dispatch('authenticate', token);
        this.$router.push({ path: '/', query: {} });
      } catch {
        this.$router.push({ path: '/', query: {} });
      }
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
