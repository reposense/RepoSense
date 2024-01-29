const StyleLintPlugin = require('stylelint-webpack-plugin');

module.exports = {
  pages: {
    index: {
      entry: 'src/main.ts',
      title: 'RepoSense Report',
    },
  },
  publicPath: './',
  outputDir: 'build/',
  configureWebpack: {
    plugins: [
      new StyleLintPlugin({
        files: ['src/**/*.{vue,scss}'],
      }),
    ],
    module: {
      rules: [
        // Markdown Loader
        {
          test: /\.md$/,
          loader: "raw-loader",
        },
      ],
    }
  },
  chainWebpack: (config) => {
    // Pug Loader
    config.module
      .rule('pug')
      .test(/\.pug$/)
      .use('pug-plain-loader')
      .loader('pug-plain-loader')
      .end();
    config.plugin('copy').tap((options) => {
      options[0].patterns[0].globOptions.ignore.push('*.json');
      return options;
    });
    config.module
      .rule('vue')
      .use('vue-loader')
      .tap((options) => ({
        ...options,
        compilerOptions: {
          whitespace: 'preserve',
        },
      }));
  },
};
