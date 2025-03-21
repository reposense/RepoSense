{% set title = "Generating a report" %}
<frontmatter>
  title: "{{ title | safe }}"
  pageNav: 3
</frontmatter>

{% from 'scripts/macros.njk' import embed with context %}

<h1 class="display-4"><md>{{ title }}</md></h1>

<div class="lead">

Let's look at different ways to generate RepoSense reports.
</div>


* **If you have Java on your computer**, the straight-forward way to generate a report is to use the RepoSense executable to generate the report locally on your computer, as explained in the [_Generating reports locally_](#generating-reports-locally) section below.

* **If you don't have Java on your computer or do not wish to run the executable on your computer**, some alternatives are provided in the [_Generating reports remotely_](#generating-reports-remotely) section below.

<box type="info" seamless>

RepoSense is built to analyze any type of Git repo, remote or local. It works best when analyzing remote repositories hosted on GitHub, GitLab or BitBucket.
For other types of repositories, external links are disabled.
</box>

<!-- ==================================================================================================== -->

## Generating reports locally

1. **Ensure you have the prerequisites**:
   * **Java 11** or later ([download :fas-download:](https://www.java.com/en/)).
   * **Git `2.23`** or later on the command line. ([download :fas-download:](https://git-scm.com/downloads)).<br> run `git --version` in your OS terminal to confirm the version.

1. **Download the latest JAR file** from our [releases](https://github.com/reposense/RepoSense/releases/latest).

1. **Generate a report**: The simplest use case for RepoSense is to generate a report for the recent history of a repo.<br>
  command: `java -jar RepoSense.jar --repos LIST_OF_REPO_URLS --view`<br>
  Examples:
   * `java -jar RepoSense.jar --repos https://github.com/reposense/RepoSense.git --view` (note the `.git` at the end of the repo URL)
   * `java -jar RepoSense.jar --repos https://github.com/reposense/RepoSense.git c:/myRepose/foo/bar --view` analyzes the two specified repos (one remote, one local).

   The above commands will analyze the given repo(s) for commits done within ==the last month== and open the report in your default Browser.

**To learn how to generate a report using <tooltip content="e.g., generate a report for a different period, for specific file types, for specific authors, etc.">other settings</tooltip>**, head over to the [_**Customizing reports**_](customizingReports.html) section.

<!-- ==================================================================================================== -->

## Generating reports remotely

**You can generate a RepoSense report remotely without installing/running anything on your computer.** This is particularly useful when you are deciding whether to adopt RepoSense.

**The recommended option is to use GitHub Actions.** The instructions are given below.

{{ embed("Appendix: **Using RepoSense with GitHub Actions → Setting up**", "withGithubActions.md#section-setting-up") }}

You may also consider using Netlify to deploy your report. **However, we may not support this method going forward.**
{{ embed("Appendix: **Using RepoSense with Netlify → Setting up**", "withNetlify.md#section-setting-up") }}
