import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import eslint from 'vite-plugin-eslint2';
import stylelint from 'vite-plugin-stylelint';
import path from 'path';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue(), eslint(), stylelint()],
  // This is to disable default fallback behaviour of
  // displaying the head tag of the html in case of 
  // missing 'title.md' file
  appType: 'mpa',
  optimizeDeps: {
    include: ['node_modules/highlight.js'],
  },
  resolve: {
    // We need 'js' for highlight.js
    extensions: ['mjs', 'js', '.ts', '.vue'],
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
  build: {
    outDir: './build',
    emptyOutDir: true,
  },
  server: {
    port: 9000,
  },
})
