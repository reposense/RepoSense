const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
  publicPath: process.env.NODE_ENV === 'production'
    ? '/tp-dashboard/'
    : '/',
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
  chainWebpack: (config) => {
    // Pug Loader
    config.module
        .rule('pug')
        .test(/\.pug$/)
        .use('pug-plain-loader')
        .loader('pug-plain-loader')
        .end();
  },
};
