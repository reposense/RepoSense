const path = require('path');

const { CleanWebpackPlugin } = require('clean-webpack-plugin');
const VueLoaderPlugin = require('vue-loader/lib/plugin');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
  entry: {
    main: path.resolve(__dirname, 'src/index.js'),
  },
  resolve: {
    modules: [path.resolve(__dirname), 'node_modules'],
  },
  module: {
    rules: [
        {
          test: /\.vue$/,
          use: 'vue-loader',
        },
        {
          test: /\.pug$/,
          oneOf: [
            {
              resourceQuery: /^\?vue/,
              use: ['pug-plain-loader'],
            },
            // this applies to index.pug
            {
              use: ['html-loader', 'pug-plain-loader'],
            },
          ],
        },
    ],
  },
  plugins: [
      new CleanWebpackPlugin(),
      new VueLoaderPlugin(),
      new HtmlWebpackPlugin({
        template: './frontend/src/index.pug',
        filename: 'index.html',
        minify: false,
      }),
  ],
};
