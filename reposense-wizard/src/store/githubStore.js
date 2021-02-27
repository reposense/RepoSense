// import { seal } from 'tweetsodium';
import { Octokit } from '@octokit/core';

const REPO_NAME = 'publish-RepoSense';
const WAIT_FOR_FORK = 5;

function sleep(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

const githubStore = {
  state: () => ({
    accessToken: localStorage.getItem('accessToken') || null,
    octokit: null,
    publicKey: '',
    publicKeyId: '',
    loginUser: '',
  }),

  mutations: {
    setOctokit(state, accessToken) {
      state.accessToken = accessToken;
      window.localStorage.setItem('accessToken', accessToken);
      state.octokit = new Octokit({ auth: accessToken });
    },

    setLoginUser(state, loginUser) {
      state.loginUser = loginUser;
    },

    setPublicKey(state, { publicKey, publicKeyId }) {
      state.publicKey = publicKey;
      state.publicKeyId = publicKeyId;
    },
  },

  actions: {
    async authenticate({ state, commit, dispatch }, accessToken) {
      accessToken = accessToken || state.accessToken;
      if (!accessToken) {
        return;
      }
      commit('setOctokit', accessToken);

      const userResp = await state.octokit.request('GET /user');
      commit('setLoginUser', userResp.data.login);
      await dispatch('getPublicKey');
    },

    async forkReposense({ state, dispatch }) {
      await state.octokit.request('POST /repos/{owner}/{repo}/forks', {
        owner: 'reposense',
        repo: REPO_NAME,
      });
      let forkSuccess = false;
      let tryCounter = 1;
      while (!forkSuccess && tryCounter < WAIT_FOR_FORK) {
        // eslint-disable-next-line no-await-in-loop
        await sleep(tryCounter * 1000);
        // eslint-disable-next-line no-await-in-loop
        forkSuccess = await dispatch('repoExists');
        tryCounter += 1;
      }
    },

    async repoExists({ state }) {
      const repo = await state.octokit.request('GET /repos/{owner}/{repo}', {
        owner: 'reposense',
        repo: REPO_NAME,
      });
      return repo.status === 200;
    },

    async getPublicKey({ state, commit }) {
      const response = await state.octokit.request('GET /repos/{owner}/{repo}/actions/secrets/public-key', {
        owner: state.loginUser,
        repo: REPO_NAME,
      });
      commit('setPublicKey', { publicKey: response.data.key, publicKeyId: response.data.key_id });
    },

    // async addSecret(value) {
    //   value = value || this.pat;
    //   if (!this.publicKey) {
    //     await this.getPublicKey();
    //   }
    //   // Convert the message and key to Uint8Array's (Buffer implements that interface)
    //   const messageBytes = Buffer.from(value);
    //   const keyBytes = Buffer.from(this.publicKey, 'base64');


    //   // Encrypt using LibSodium.
    //   const encryptedBytes = seal(messageBytes, keyBytes);


    //   // Base64 the encrypted secret
    //   const encrypted = Buffer.from(encryptedBytes).toString('base64');

    //   await this.octokit.request('PUT /repos/{owner}/{repo}/actions/secrets/{secret_name}', {
    //     owner: this.loginUser,
    //     repo: REPO_NAME,
    //     secret_name: 'ACCESS_TOKEN',
    //     encrypted_value: encrypted,
    //     key_id: this.publicKeyId,
    //   });
    // },

    async updateFile({ state }, { path, strContent }) {
      // Get file sha
      const resp = await state.octokit.request('GET /repos/{owner}/{repo}/contents/{path}', {
        owner: state.loginUser,
        repo: REPO_NAME,
        path,
      });
      const { sha } = resp.data;

      // Update the file
      const content = Buffer.from(strContent).toString('base64');
      await state.octokit.request('PUT /repos/{owner}/{repo}/contents/{path}', {
        owner: state.loginUser,
        repo: REPO_NAME,
        path,
        message: `Update ${path} using Reposense Wizard.`,
        content,
        sha,
      });
    },

    // async enableGithubActions() {
    //   await this.octokit.request('PUT /repos/{owner}/{repo}/actions/permissions', {
    //     owner: this.loginUser,
    //     repo: REPO_NAME,
    //     enabled: true,
    //     allowed_actions: 'all',
    //   });
    // },
  },

  getters: {
    isAuthenticated(state) {
      return state.octokit != null;
    },
  },
};

export default githubStore;
