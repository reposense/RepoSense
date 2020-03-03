const merge = require('webpack-merge');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

const TerserPlugin = require('terser-webpack-plugin');
const path = require('path');
const config = require('./webpack.common.js');

module.exports = merge(config, {
  mode: 'production',
  output: {
    filename: '[name].min.js',
    path: path.resolve(__dirname, 'dist'),
  },
  plugins: [
      new TerserPlugin(),
      new MiniCssExtractPlugin({
        filename: '[name].css',
      }),
  ],
  module: {
    rules: [
      {
        test: /\.css$/,
        use: [
          MiniCssExtractPlugin.loader,
          'css-loader',
        ],
      },
      {
        test: /\.scss$/,
        use: [
          MiniCssExtractPlugin.loader,
          'css-loader',
          'sass-loader',
        ],
      },
    ],
  }
});
