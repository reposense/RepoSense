const { Octokit } = require("@octokit/core");
const sodium = require('tweetsodium');

const REPO_NAME = 'publish-RepoSense';

export const githubApi = {
  debug: true,
  octokit: null,
  publicKey: '',
  loginUser: '',

  authenticate (accessToken) {
    this.octokit = new Octokit({ auth: accessToken });
  },

  async forkReposense () {
    await this.octokit.request('POST /repos/{owner}/{repo}/forks', {
      owner: 'reposense',
      repo: REPO_NAME,
    })
  },

  async getPublicKey() {
    this.publicKey = await this.octokit.request('GET /repos/{owner}/{repo}/actions/secrets/public-key', {
      owner: this.loginUser,
      repo: REPO_NAME
    })
  },

  async addSecret(value) {
    // Convert the message and key to Uint8Array's (Buffer implements that interface)
    const messageBytes = Buffer.from(value);
    const keyBytes = Buffer.from(this.publicKey, 'base64');


    // Encrypt using LibSodium.
    const encryptedBytes = sodium.seal(messageBytes, keyBytes);


    // Base64 the encrypted secret
    const encrypted = Buffer.from(encryptedBytes).toString('base64');

    await this.octokit.request('PUT /repos/{owner}/{repo}/actions/secrets/{secret_name}', {
      owner: this.loginUser,
      repo: REPO_NAME,
      secret_name: 'ACCESS_TOKEN',
      encrypted_value: encrypted,
    });
  },
}