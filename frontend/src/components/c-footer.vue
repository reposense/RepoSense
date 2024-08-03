<template lang="pug">
#footer
  #app-name
    span &nbsp;
    a(
      v-bind:href="getRepoSenseHomeLink()", target="_blank"
    )
      strong RepoSense
    span &nbsp;|&nbsp;
    a(
      v-bind:href="getSpecificCommitLink()", target="_blank"
    )
      strong Version
    span &nbsp;|&nbsp;
    a(
      v-bind:href="getUserGuideLink()", target ="_blank"
    )
      strong User Guide
</template>


<script lang='ts'>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'c-footer',
  methods: {
    getRepoSenseHomeLink(): string {
      const version = window.repoSenseVersion;
      if (!version) {
        return `${window.HOME_PAGE_URL}/RepoSense/`;
      }
      return `${window.HOME_PAGE_URL}`;
    },

    getSpecificCommitLink(): string {
      const version = window.repoSenseVersion;
      if (!version) {
        return `${window.REPOSENSE_REPO_URL}`;
      }
      if (version.startsWith('v')) {
        return `${window.REPOSENSE_REPO_URL}/releases/tag/${version}`;
      }
      return `${window.REPOSENSE_REPO_URL}/commit/${version}`;
    },

    getUserGuideLink(): string {
      const version = window.repoSenseVersion;
      if (!version) {
        return `${window.HOME_PAGE_URL}/RepoSense/ug/index.html`;
      }
      return `${window.HOME_PAGE_URL}/ug/index.html`;
    },
  },
});

</script>