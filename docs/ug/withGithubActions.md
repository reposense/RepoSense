<frontmatter>
  title: "Using RepoSense with GitHub Actions"
  pageNav: 3
</frontmatter>

{% from 'scripts/macros.njk' import embed, step with context %}

<h1 class="display-4">Appendix: Using RepoSense with GitHub Actions</h1>

<div id="section-setting-up">

<div class="lead">

You can use [_GitHub Actions_](https://github.com/features/actions) (together with other GitHub tools) to automate the generating and publishing of RepoSense reports.
</div>

<!-- ==================================================================================================== -->

## Setting up

<include src="withTravis.md#section-fork-token" />

{{ step(3)}} Go to the [secrets settings](https://github.com/reposense/publish-RepoSense/settings/secrets) of your _publish-RepoSense_ fork, add a new secret as `ACCESS_TOKEN` or `DEPLOY_KEY` depending on your earlier choice and paste the token/key; then click `Add secret`:<br>
![GitHub Actions Secrets](../images/publishingguide-secrets.png "GitHub Actions Secrets")

{{ step(4)}}

<include src="withTravis.md#section-edit-configs" />

{{ step(5)}} To access your site, go to the settings of your fork in GitHub, under **GitHub Pages** section, look for `Your site is published at [LINK]`. It should look something like `https://[YOUR_GITHUB_ID].github.io/publish-RepoSense`.
![GitHub Setting](../images/publishingguide-githubsetting.jpg "GitHub Setting")
</div>

<!-- ==================================================================================================== -->

<div id="section-updating-the-report">

## Updating the report

**Manual:**
* You can trigger GitHub to re-generate and re-deploy the report by pushing an empty commit to your fork.
* Currently, the GitHub Actions UI does not support the manual execution of workflows.

**Automated:** GitHub actions can be set to run periodically.
1. Edit the `.github/workflows/main.yml` and uncomment the `schedule:` section.
1. You may change the expression after `cron:` to a schedule of your choice. Read more about cron syntax [here](https://help.github.com/en/actions/reference/events-that-trigger-workflows#scheduled-events-schedule).
1. Commit your changes.

</div>
