import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import eslint from 'vite-plugin-eslint2';
import stylelint from 'vite-plugin-stylelint';
import path from 'path';

// Extract the repository name from the GITHUB_REPOSITORY environment variable
// const repoName = process.env.GITHUB_REPOSITORY
//   ? `/${process.env.GITHUB_REPOSITORY.split('/').pop()}/`
//   : '/'; // Fallback if GITHUB_REPOSITORY is not set

const repoName = '/publish-RepoSense/';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue(), eslint(), stylelint()],
  // This is to disable default fallback behaviour of
  // displaying the head tag of the html in case of
  // missing 'title.md' file
  appType: 'mpa',
  resolve: {
    extensions: ['.ts', '.vue'],
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  base: repoName,
  build: {
    outDir: './build',
    emptyOutDir: true,
  },
  server: {
    port: 9000,
  },
})
