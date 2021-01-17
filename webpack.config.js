module.rules = {
  test: [
      /\.pug$/,
      /\.scss$/,
  ],
  use: [
      'vue-style-loader',
      'css-loader',
      'sass-loader',
  ],
  loader: 'pug-plain-loader',
};
