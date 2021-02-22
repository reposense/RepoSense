<template lang="pug">
  b-container(fluid)
    b-row
      b-col(cols="9") Repository URL:
      b-col(cols="2") Branch
    template(v-for='(repo, i) in repos')
      b-row
        b-col(cols="9")
          b-form-input(
            v-bind:value="repo.url",
            v-on:change="updateRepoUrl(i, $event)",
            placeholder="Repo",
          )
        b-col(cols="2")
          b-form-input(
            v-model="repo.branch",
            v-on:change="updateRepoBranch(i, $event)",
            placeholder="Branch",
          )
        b-col
          b-icon.cross-button(icon='x-circle', v-on:click="removeRepo(i)")
    b-row
      b-col(cols="9")
        b-form-input(
          v-model="newUrl",
          placeholder="Repo",
        )
      b-col(cols="2")
        b-form-input(
          v-model="newBranch",
          placeholder="Branch",
        )
      b-col
        b-icon.plus-button(icon='plus-circle', v-on:click="addRepo()")
</template>

<script>
export default {
  name: 'RepoList',
  props: {
    repos: Array,
  },
  data() {
    return {
      newUrl: '',
      newBranch: '',
    };
  },
  methods: {
    addRepo() {
      this.$emit('addRepo', { url: this.newUrl, branch: this.newBranch });
      this.newUrl = '';
      this.newBranch = '';
    },
    removeRepo(index) {
      this.$emit('removeRepo', index);
    },
    updateRepoUrl(index, newUrl) {
      this.$emit('updateRepo', { index, field: 'url', newValue: newUrl });
    },
    updateRepoBranch(index, newBranch) {
      this.$emit('updateRepo', { index, field: 'branch', newValue: newBranch });
    },
  },
};
</script>

<style>
.cross-button {
  background-color: #e80e0e;
  margin: auto;
  height: 50%;
  width: 50%;
}

.cross-button:hover {
  color: #275c2a;
}

.plus-button {
  color: #4CAF50;
  margin: auto;
  height: 50%;
  width: 50%;
}

.plus-button:hover {
  color: #275c2a;
}
</style>
