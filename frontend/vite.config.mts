import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import eslint from 'vite-plugin-eslint2';
import stylelint from 'vite-plugin-stylelint';
import path from 'path';

// VITE_BASE_DIR refers to the root directory of the RepoSense deployment.
// It needs to be configured for GitHub Pages deployment.
const base = process.env.VITE_BASE_DIR
  ? process.env.VITE_BASE_DIR
  : '';

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => {
  let publicDir = 'public';

  if (mode === 'test-default') {
    publicDir = '../build/serveTestReport/default/reposense-report';
  } else if (mode === 'test-portfolio') {
    publicDir = '../build/serveTestReport/portfolio/reposense-report';
  }

  return {
    plugins: [vue(), eslint(), stylelint()],
    // This is to disable default fallback behaviour of
    // displaying the head tag of the html in case of
    // missing 'intro.md' file
    appType: 'mpa',
    resolve: {
      extensions: ['.ts', '.vue'],
      alias: {
        "@": path.resolve(__dirname, "./src"),
      },
    },
    base: base,
    build: {
      outDir: './build',
      emptyOutDir: true,
      chunkSizeWarningLimit: 1000,
      rollupOptions: {
        output: {
          manualChunks: {
            'vue-vendor': ['vue', 'vue-router', 'vuex'],
            'highlight-js': ['highlight.js']
          },
        },
      },
    },
    server: {
      port: 9000,
    },
    publicDir: publicDir,
  };
})
