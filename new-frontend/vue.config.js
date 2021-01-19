const StyleLintPlugin = require('stylelint-webpack-plugin');

module.exports = {
  publicPath: './',
  outputDir: 'build/',
  configureWebpack: {
    plugins: [
        new StyleLintPlugin({
          files: ['src/**/*.{vue,scss}'],
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
    config.optimization.minimize(false);
  },
};
