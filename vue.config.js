const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
  outputDir: 'new-frontend/build/',
  pages: {
    index: {
      entry: 'new-frontend/src/main.js',
      template: 'new-frontend/src/public/index.html',
      filename: 'index.html',
    },
  },
  configureWebpack: {
    plugins: [
        new CopyWebpackPlugin({
          patterns: [
              { from: 'new-frontend/public/', to: '.' },
          ],
        }),
    ],
  },
};
