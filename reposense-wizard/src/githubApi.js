const { Octokit } = require("@octokit/core");

const REPO_NAME = 'publish-RepoSense';

export const githubApi = {
  debug: true,
  octokit: null,

  authenticate (accessToken) {
    this.octokit = new Octokit({ auth: accessToken });
  },

  async forkReposense () {
    await this.octokit.request('POST /repos/{owner}/{repo}/forks', {
      owner: 'reposense',
      repo: REPO_NAME,
    })
  },

  async addSecret() {

  },
}