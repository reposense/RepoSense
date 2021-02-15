<template lang="pug">
  b-container(fluid)
    b-row
      b-col(cols="9") Repository URL:
      b-col(cols="2") Branch
    template(v-for='(repo, i) in repos')
      b-row
        b-col(cols="9") 
          b-form-input(
            v-model="repo.url",
            placeholder="Repo",
          )
        b-col(cols="2")
          b-form-input(
            v-model="repo.branch",
            placeholder="Branch",
          )
        b-col
          button.plus-button(v-bind:onClick="addRepo") -
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
        button.plus-button(v-bind:onClick="addRepo") +
</template>

<script>
export default {
  name: 'RepoList',
  props: {
    repos: Array
  },
  data() {
    return {
      newUrl: '',
      newBranch: '',
    };
  },
  methods: {
    addRepo() {
      return null;
    },
  }
}
</script>

<style>
.cross-button {
  background-color: #e80e0e;
  border: none;
  color: white;
  height: 15px;
  padding: 0px;
  text-align: center;
  font-size: 9px;
  margin: 5px 5px;
  border-radius: 50%;
  position: absolute;
  right: 0px;
  width: 15px;
}

.plus-button {
  background-color: #4CAF50;
  height: 15px;
  width: 15px;
  border: none;
  color: white;
  padding: 0px;
  text-align: center;
  font-size: 10px;
  margin: 5px 5px;
  border-radius: 50%;
  position: absolute;
  right: 0px;
}
</style>
