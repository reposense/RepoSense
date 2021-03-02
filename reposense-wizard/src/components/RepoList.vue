<template lang="pug">
  b-container.repolist(fluid)
    b-row
      b-col(cols="9") Repository URL:
      b-col(cols="2") Branch:
    template(v-for='(repo, i) in repos')
      b-row.repolist-row-input
        b-col(cols="9")
          b-form-input(
            v-bind:value="repo.url",
            v-on:change="updateRepoUrl(i, $event)",
            placeholder="Please enter the full github url of the repository.",
          )
        b-col(cols="2")
          b-form-input(
            v-bind:value="repo.branch",
            v-on:change="updateRepoBranch(i, $event)",
            placeholder="master",
          )
        b-col
          b-icon.plus-button(
            v-if='i+1 == repos.length',
            icon='plus-circle',
            v-on:click="addRepo()"
          )
          b-icon.cross-button(
            v-else,
            icon='x-circle',
            v-on:click="removeRepo(i)")
    b-row
      b-col
        .repolist-comment Repositories must be in Git and publicly accessible
</template>

<script>
export default {
  name: 'RepoList',
  props: {
    repos: Array,
  },
  emits: [
      'addRepo',
      'removeRepo',
      'updateRepo',
      'updateRepo',
  ],
  methods: {
    addRepo() {
      this.$emit('addRepo', { url: '', branch: '' });
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
  color: #e80e0e;
  margin-top: 10px;
  height: 20px;
  width: 20px;
}

.cross-button:hover {
  color: #8a0808;
}

.plus-button {
  color: #4CAF50;
  margin-top: 10px;
  height: 20px;
  width: 20px;
}

.plus-button:hover {
  color: #275c2a;
}

.repolist {
  padding: 0;
  margin-bottom: 15px;
}

.repolist-comment {
  font-style: italic;
  font-size: 14px;
  font-weight: 400;
  line-height: 1.429;
  color: grey;
}

.repolist-row-input {
  margin-top: 5px;
}
</style>
