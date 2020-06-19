<variable name="title">RepoSense with Travis</variable>
<frontmatter>
  title: "{{ title | safe }}"
  pageNav: 3
</frontmatter>

{% from 'scripts/macros.njk' import embed, step with context %}

<h1 class="display-4"><md>{{ title }}</md></h1>

<div id="section-setting-up">

<div class="lead">

You can use the CI tool Travis to automate generating and publishing of RepoSense reports.
</div>


<!-- ==================================================================================================== -->

## Setting up

<div id="section-fork-token">

<box type="info" seamless>

The instructions below assume you are using GitHub pages to host your report.
</box>


{{ step(1)}} **Fork the _publish-RepoSense_ repository** using this [link](https://github.com/RepoSense/publish-RepoSense/fork). Optionally, you can rename the fork to match your RepoSense report e.g., `project-code-dashboard`.

{{ step(2)}} **Generate a _personal access token_ or _deploy key_ on GitHub** as explained in the panel below.

  {{ embed("Granting write access on GitHub", "grantingWriteAccess.mbdf") }}
</div>

{{ step(3) }} Sign up and login to [Travis-CI](https://travis-ci.org/).

{{ step(4) }} Go to [your account](https://travis-ci.org/account/repositories), click on `Sync account` to fetch all your repositories into Travis-CI.

{{ step(5) }} Go to your [publish-RepoSense fork in Travis-CI](https://travis-ci.org/search/publish-RepoSense/), under `Current` tab click on `Activate repository`.

{{ step(6) }} In the same page, click on `More options` on the right then access `Settings`:
![Travis-CI Dashboard](../images/publishingguide-travissetting.jpg "Travis-CI Dashboard")

{{ step(7) }} Under `Environment Variables`, name a variable as `GITHUB_TOKEN` or `GITHUB_DEPLOY_KEY` depending on your earlier choice and paste the token/key into its value field; then click `Add`. Ensure that the `Display value in build log` is `switched off` for security reasons:
![Travis-CI Environment Variable](../images/publishingguide-githubtoken.jpg "Travis-CI Environment Variable")

{{ step(8) }}

<span id="section-edit-configs">

In your fork, edit `run.sh` (and if applicable, `repo-config.csv`, `author-config.csv`, `group-config.csv`) to customize the command line parameters or repositories to be analyzed.

  {{ embed("Appendix: **`run.sh` format**", 'runSh.md') }}
  {{ embed("Appendix: **Config files format**", 'configFiles.md') }}
</span>

{{ step(9) }} To access your site, go to the settings of your fork in GitHub, under **GitHub Pages** section, look for `Your site is published at [LINK]`. It should look something like `https://[YOUR_GITHUB_ID].github.io/publish-RepoSense`.
![GitHub Setting](../images/publishingguide-githubsetting.jpg "GitHub Setting")

<box type="info" seamless>

It takes a few minutes for report generation. Meanwhile, you can monitor the progress live at [Travis-CI's Builds](https://travis-ci.org/dashboard/builds).
</box>

</div>

<!-- ==================================================================================================== -->

## Updating the report

**Manual:** Travis UI has a way for you to trigger a build, using which you can cause the report to be updated.

1. Go to [your fork in Travis-CI](https://travis-ci.org/search/publish-RepoSense/), click on `More options` on the right then `Trigger build`.
1. In the pop up, click `Trigger custom build`.

**Automated:** [Travis-CI](https://travis-ci.org/) offers `Cron Jobs` in intervals of daily, weekly or monthly.

1. Login to [Travis-CI](https://travis-ci.org/).
1. Go to [your fork in Travis-CI](https://travis-ci.org/search/publish-RepoSense/), click on `More options` on the right then access `Settings`.
1. Under `Cron Jobs`, choose `master` for `Branch`, `Always run` for `Options` and pick an `Interval` of your choice; then click `Add`.
![Travis-CI Cron](../images/publishingguide-cronsetting.jpg "Travis-CI Cron")
