import { seal } from 'tweetsodium';
import { Octokit } from '@octokit/core';

const REPO_NAME = 'publish-RepoSense';

export default class GithubApi {
  constructor() {
    this.octokit = null;
    this.publicKey = '';
    this.publicKeyId = '';
    this.loginUser = '';
    this.pat = '';
  }

  setPat(pat) {
    // Personal access token
    this.pat = pat;
  }

  async authenticate(accessToken) {
    accessToken = accessToken || this.pat;
    if (!accessToken) {
      throw Error('Access token not found.');
    }
    this.octokit = new Octokit({ auth: accessToken });

    const userResp = await this.octokit.request('GET /user');
    this.loginUser = userResp.data.login;
    await this.getPublicKey();
  }

  async forkReposense() {
    await this.octokit.request('POST /repos/{owner}/{repo}/forks', {
      owner: 'reposense',
      repo: REPO_NAME,
    });
  }

  async getPublicKey() {
    const response = await this.octokit.request('GET /repos/{owner}/{repo}/actions/secrets/public-key', {
      owner: this.loginUser,
      repo: REPO_NAME,
    });
    this.publicKey = response.data.key;
    this.publicKeyId = response.data.key_id;
  }

  async addSecret(value) {
    value = value || this.pat;
    if (!this.publicKey) {
      await this.getPublicKey();
    }
    // Convert the message and key to Uint8Array's (Buffer implements that interface)
    const messageBytes = Buffer.from(value);
    const keyBytes = Buffer.from(this.publicKey, 'base64');


    // Encrypt using LibSodium.
    const encryptedBytes = seal(messageBytes, keyBytes);


    // Base64 the encrypted secret
    const encrypted = Buffer.from(encryptedBytes).toString('base64');

    await this.octokit.request('PUT /repos/{owner}/{repo}/actions/secrets/{secret_name}', {
      owner: this.loginUser,
      repo: REPO_NAME,
      secret_name: 'ACCESS_TOKEN',
      encrypted_value: encrypted,
      key_id: this.publicKeyId,
    });
  }

  async updateFile(path, strContent) {
    // Get file sha
    const resp = await this.octokit.request('GET /repos/{owner}/{repo}/contents/{path}', {
      owner: this.loginUser,
      repo: REPO_NAME,
      path,
    });
    const { sha } = resp.data;

    // Update the file
    const content = Buffer.from(strContent).toString('base64');
    await this.octokit.request('PUT /repos/{owner}/{repo}/contents/{path}', {
      owner: this.loginUser,
      repo: REPO_NAME,
      path,
      message: `Update ${path} using Reposense Wizard.`,
      content,
      sha,
    });
  }
}
