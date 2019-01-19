## Publishing the report to GitHub Pages

[GitHub Pages](https://pages.github.com/) enables you to publish your report online for free.

1. Fork this repository using this [link](https://github.com/RepoSense/reposense/fork)
1. Follow this [guide](https://help.github.com/articles/creating-a-personal-access-token-for-the-command-line/) to generate a `Personal access token`on GitHub for report publishing. <br/>
*You would **only require** `public_repo` permission*
1. Sign up and login to [Travis-CI](https://travis-ci.org/)
1. Go to [Travis-CI's Dashboard](https://travis-ci.org/dashboard) and the click on the icon on the right to access its setting
1. Under **Environment Variables**, name a variable as `GITHUB_TOKEN` and paste the `Personal access token` to the value field
*Ensure that the `Display value in build log` is* **switched off**
1. Edit [repo-config.csv](config/github-pages/repo-config.csv) and [author-config.csv](config/github-pages/author-config.csv) to include the repository for analysis
