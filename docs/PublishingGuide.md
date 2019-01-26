## Using Travis to automate publishing of your report to GitHub Pages

[Travis-CI](https://travis-ci.org/) enables you to automate RepoSense report generation and publish the report online to [GitHub Pages](https://pages.github.com/) for free.

1. Fork publish-RepoSense repository using this [link](https://github.com/RepoSense/publish-RepoSense/fork)
1. Follow this [guide](https://help.github.com/articles/creating-a-personal-access-token-for-the-command-line/) to generate a `Personal access token` on GitHub for report publishing <br/>
*Remember to **copy it** and you would **only require** `public_repo` permission*
1. Sign up and login to [Travis-CI](https://travis-ci.org/)
1. Go to [your account](https://travis-ci.org/account/repositories), click on **Sync account** to fetch all your repositories into Travis-CI
1. Go to [your publish-RepoSense fork in Travis-CI](https://travis-ci.org/search/publish-RepoSense/), under **Current** tab click on **Activate repository**
1. In the same page, click on **More options** on the right then access **Settings**
![Travis-CI Dashboard](images/publishingguide-travissetting.jpg "Travis-CI Dashboard")
1. Under **Environment Variables**, name a variable as `GITHUB_TOKEN` and paste the `Personal access token` to its value field; then click **Add** <br/>
*Ensure that the `Display value in build log` is* **switched off** for security reasons
![Travis-CI Environment Variable](images/publishingguide-githubtoken.jpg "Travis-CI Environment Variable")
1. Edit [run.sh](../../../../publish-RepoSense/edit/master/run.sh), [repo-config.csv](../../../../publish-RepoSense/edit/master/configs/repo-config.csv) and [author-config.csv](../../../../publish-RepoSense/edit/master/configs/author-config.csv) to customize the command line parameters or repositories to be analyzed <br/>
*Read our [User Guide](UserGuide.md#customizing-the-analysis) for more information*
1. To access your site, go to the settings of your fork in GitHub, under **GitHub Pages** section, look for `Your site is published at [LINK]` <br/>
*It should look something like `https://[YOUR_GITHUB_ID].github.io/publish-RepoSense`* <br/>

> The changes made to the configuration files should trigger Travis-CI to generate your report. Otherwise, follow the [next section](#keeping-your-site-up-to-date-with-your-code-contribution) to manually trigger a build. <br/><br/>
It takes a few minutes for report generation. Meanwhile, you can monitor the progress live at [Travis-CI's Builds](https://travis-ci.org/dashboard/builds). <br/>
Try accessing your site again when a green tick appears beside your fork.

### Keeping your site up-to-date with your code contribution

[Travis-CI](https://travis-ci.org/) offers `Cron Jobs` in intervals of daily, weekly or monthly.

1. Login to [Travis-CI](https://travis-ci.org/)
1. Go to [your fork in Travis-CI](https://travis-ci.org/search/publish-RepoSense/), click on **More options** on the right then access **Settings**
1. Under **Cron Jobs**, choose **master** for `Branch`, **Always run** for `Options` and pick an `Interval` of your choice; <br/>
then click **Add**
![Travis-CI Cron](images/publishingguide-cronsetting.jpg "Travis-CI Cron")

Alternatively, you can manually trigger an update.

1. Go to [your fork in Travis-CI](https://travis-ci.org/search/publish-RepoSense/), click on **More options** on the right then **Trigger build**
1. In the pop up, click **Trigger custom build**

### Specifying which version of RepoSense to use

As RepoSense is being actively developed, its master branch is frequently updated with new features and fixes. <br/>
For stablility or familiarity, you may want to use the release. <br/>

#### Use our latest release (Stable)

You can find the changelog of latest release [here](https://github.com/reposense/RepoSense/releases/latest).

Edit line 10 of [run.sh](../../../../publish-RepoSense/edit/master/run.sh) to use `--release`. <br />
```
10   ./get-reposense.py --release
```

#### Use our master branch (Beta)

You can find the list of commits to master branch [here](https://github.com/reposense/RepoSense/commits/master) and planned features/fixes [here](https://github.com/reposense/RepoSense/milestones).

Edit line 10 of [run.sh](../../../../publish-RepoSense/edit/master/run.sh) to use `--master`. <br />
```
10   ./get-reposense.py --master
```
#### Use a specific version of release

Be sure to read and understand [all breaking changes and bugs](https://github.com/reposense/RepoSense/releases) before proceeding.

Edit line 10 of [run.sh](../../../../publish-RepoSense/edit/master/run.sh) to use `--tag TAG`, where `TAG` is the [target version of release](https://github.com/reposense/RepoSense/tags). <br />
```
10   ./get-reposense.py --tag v1.6.1
```
