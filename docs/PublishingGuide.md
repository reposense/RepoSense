## Publishing your report to GitHub Pages

[GitHub Pages](https://pages.github.com/) enables you to publish your report online for free.

1. Fork this repository using this [link](https://github.com/RepoSense/reposense/fork)
1. Follow this [guide](https://help.github.com/articles/creating-a-personal-access-token-for-the-command-line/) to generate a `Personal access token` on GitHub for report publishing <br/>
*Remember to **copy it** and you would **only require** `public_repo` permission*
1. Sign up and login to [Travis-CI](https://travis-ci.org/)
1. Go to [Travis-CI's Dashboard](https://travis-ci.org/dashboard), find your fork under **Active repositories** and the click on the icon on the right to access its setting
![Travis Dashboard](images/publishingguide-travissetting.jpg "Travis Dashboard")
1. Under **Environment Variables**, name a variable as `GITHUB_TOKEN` and paste the `Personal access token` to its value field <br/>
*Ensure that the `Display value in build log` is* **switched off** for security reasons
![Travis Environment Variable](images/publishingguide-githubtoken.jpg "Travis Environment Variable")
1. Edit [repo-config.csv](../../../edit/master/config/github-pages/repo-config.csv) and [author-config.csv](../../../edit/master/config/github-pages/author-config.csv) to include repositories you wish to analyse
1. To access your site, go to [the setting of your fork](../../../settings), under **GitHub Pages** look for `Your site is published at [LINK]`

> It takes a few minutes for Travis-CI to generate your report. <br/>
  Meanwhile, you can monitor the progress live at [Travis-CI's Dashboard](https://travis-ci.org/dashboard). <br/>
  Try accessing your site again when a green tick appears beside your fork.  
  
### Keeping your site updated

[Travis-CI](https://travis-ci.org/) offers `Cron Jobs` in intervals of daily, weekly or monthly.

1. Login to [Travis-CI](https://travis-ci.org/)
1. Go to [Travis-CI's Dashboard](https://travis-ci.org/dashboard) and the click on the icon on the right to access its setting
1. Under **Cron Jobs**, choose **master** for `Branch`, **Always run** for `Options` and pick an `Interval` of your choice; then click Add

Alternatively, you can manually trigger an update

1. Go to [Travis-CI's Dashboard](https://travis-ci.org/dashboard), find your fork under **Active repositories** and the click on the icon on the right and click on **Trigger a build**

### Keeping your fork updated with our developments

1. [Use this link](../../../compare/master...reposense:master), which creates a pull request to pull our code into your fork
1. Merge the changes into your fork

### Reducing the time taken by report generation

1. The [travis configuration file](../.travis.yml) in our repository include tests, which is not required in your use case
1. [Use this link](../../../edit/master/.travis.yml) to edit the file
1. You can remove the following line 3 to line 12 in the file
```
3  matrix:
4    include:
5      - os: linux
6        jdk: oraclejdk9
7      - os: osx
8
9  script:
10   - ./config/travis/run-checks.sh
11   - cd ./frontend; npm run lint src/**/*js; cd ..
12   - time travis_retry ./gradlew clean checkstyleMain checkstyleTest test systemTest
```

### Use a standard release instead of rolling release

As RepoSense is being actively developed, its master branch is frequently updated with new features and fixes. <br/>
For stablility or familiarity, you may want to use a standard release. In this case, you can use the `Release` branch in your fork. <br/>

#### Use our latest release

You can find the changelog of latest release [here](https://github.com/reposense/RepoSense/releases/latest)

1. [Use this link](../../../edit/master/.travis.yml) to edit the travis configuration file, under deploy change the branch to **release**
```
deploy:
  ...
  on:
    branch: release <-- change this line
```
2. If you use `Cron Jobs`, edit it to use **release** for `Branch`
3. [Use this link](../../../compare/release...reposense:release) to sync your release branch with ours

#### Use a specific version of release

This section requires you to be familiar with command line git.

> Ensure you have backup your changes in release branch, as it will be **deleted**. <br/>
> Be sure to read [all breaking changes and bugs](https://github.com/reposense/RepoSense/releases) before proceeding.

1. Open terminal and `cd` to reposense project directory
1. Run `git remote add upstream https://github.com/reposense/RepoSense.git`
1. Run `git fetch --all --tags` to get all new tags
1. Run `git tag` to list all the valid tags you can use to checkout
1. Run `git branch -D release` to delete release branch
1. Run `git checkout tags/v1.5.5 -b release`, assuming tag `v1.5.5` was used
1. Run `git push -u origin release -f` to update the release branch on github

> If you intend to use a release `v1.6.1` and earlier, you need to download a updated copy of `.travis.yml` as the deploy feature was added after. <br/>
You can do so by `git checkout upstream/master .travis.yml`
