<frontmatter>
  title: "Project Management"
  pageNav: 3
</frontmatter>

<include src="versionWarning.mbdf" />

<h1 class="display-4">Project Maintenance</h1>

<div class="lead">

This page contains information about project management tasks. The target audience is senior developers (and above).
</div>

<!-- ==================================================================================================== -->


## Merging PRs

* **Use the 'squash and merge' option** unless the situation warrants a different option.
* **For the merge commit**, follow conventions at [_GitHub conventions_ @SE-EDU](https://se-education.org/guides/conventions/github.html).

<!-- ==================================================================================================== -->


## Deploying the production website

We have two versions of the website:

1. **Production website** at https://reposense.org
   * matches the latest released version
   * deployed manually after each new release
1. **Dev website** at https://reposense.org/RepoSense
   * matches the latest `master` branch
   * deployed automatically by Travis whenever the `master` branch is updated

The production website differs from the dev website in some ways e.g.,

* It has a `CNAME` file (to indicate that it is the target destination for the `reposense.org` domain name)
* Its DG pages show a warning that it is not the latest version of the DG.

These variations are mainly managed by MarkBind via the `site.config` file. That is why the `site.config` file in the `release` branch is slightly different from the one in the `master` branch.

After each release, do the following steps to deploy the production website:
1. Switch to the `release` branch
1. `cd docs`
1. `markbind build`
1. `markbind deploy`
1. After a few minutes, check https://markbind.org to ensure it has been updated as intended.
