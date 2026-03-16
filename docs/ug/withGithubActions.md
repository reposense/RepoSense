{% set title = "Appendix: RepoSense with GitHub Actions" %}
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

<box type="info" seamless>

The instructions below assume you are using GitHub pages to host your report.
</box>

{{ step(1)}} **Fork the _publish-RepoSense_ repository** using this [link](https://github.com/RepoSense/publish-RepoSense/fork). Optionally, you can rename the fork to match your RepoSense report e.g., `project-code-dashboard`.

{{ step(2)}} **Activate GitHub Actions on the forked repository:**

1. Go to the `Actions` page of your fork of the [publish-RepoSense](https://github.com/reposense/publish-RepoSense) repo.
1. Click on the green button that says `I understand my workflows, go ahead and enable them` to enable GitHub Actions on your new repository.

{{ step(3)}} **Update report configuration:**

In your fork, edit `run.sh` (and if applicable, `repo-config.csv`, `author-config.csv`, `group-config.csv`) to customize the command line parameters or repositories to be analyzed.

  {{ embed("Appendix: **`run.sh` format**", 'runSh.md') }}
  {{ embed("Appendix: **Config files format**", 'configFiles.md') }}

{{ step(4)}} **View the generated report:**

Your report will be available at `https://[YOUR_GITHUB_ID].github.io/publish-RepoSense` once you [enable GitHub Pages](https://docs.github.com/en/pages/getting-started-with-github-pages/configuring-a-publishing-source-for-your-github-pages-site#publishing-from-a-branch) with the `gh-pages` branch.

<box type="tip" seamless>

There is also a published GitHub Action ([reposense-action](https://github.com/marketplace/actions/reposense-action)) that can be used to generate and publish RepoSense reports. It is especially convenient for users who wish to skip the forking and setting up of a separate repository. For more information, refer to the [usage description](https://github.com/marketplace/actions/reposense-action#usage).
</box>

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
