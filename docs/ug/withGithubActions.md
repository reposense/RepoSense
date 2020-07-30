<variable name="title">Appendix: RepoSense with GitHub Actions</variable>
<frontmatter>
  title: "{{ title | safe }}"
  pageNav: 3
</frontmatter>

{% from 'scripts/macros.njk' import embed, step with context %}

<h1 class="display-4"><md>{{ title }}</md></h1>

<div id="section-setting-up">

<div class="lead">

You can use [_GitHub Actions_](https://github.com/features/actions) (together with other GitHub tools) to automate the generating and publishing of RepoSense reports.
</div>

<!-- ==================================================================================================== -->

## Setting up

<include src="withTravis.md#section-fork-token" />

{{ step(3)}} **Add the token/key as a secret:**

1. Go to the `Settings` page of your fork of the [publish-RepoSense](https://github.com/reposense/publish-RepoSense) repo.
1. Click on the `Secrets` menu item on the left of that page.
1. Click on `Add secret`.
1. Add a new secret with the name `ACCESS_TOKEN` or `DEPLOY_KEY` (depending on your earlier choice) and the value of the token/key you copied earlier.<br>
![GitHub Actions Secrets](../images/publishingguide-secrets.png "GitHub Actions Secrets")

{{ step(4)}} **Update report configuration:**

<include src="withTravis.md#section-edit-configs" />

{{ step(5)}} **View the generated report:**

To access your regenerated RepoSense report, go to the settings of your fork in GitHub, under **GitHub Pages** section, look for `Your site is published at [LINK]`. It should look something like `https://[YOUR_GITHUB_ID].github.io/publish-RepoSense`.
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
