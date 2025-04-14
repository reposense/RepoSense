{% set title = "Customizing reports" %}
<frontmatter>
  title: "{{ title | safe }}"
  pageNav: 3
</frontmatter>

{% from 'scripts/macros.njk' import embed, step with context %}

<h1 class="display-4"><md>{{ title }}</md></h1>

<div class="lead">

The report can be customized using several ways, as explained below.
</div>

<!-- ------------------------------------------------------------------------------------------------------ -->

### Customize using CLI flags

**The simplest approach is to provide additional flags when running RepoSense.** The various flags are given in the panel below.

  {{ embed("Appendix: **CLI syntax reference**", "cli.md") }}

<!-- ------------------------------------------------------------------------------------------------------ -->

### Customize using CSV config files

**Another, more powerful, way to customize the report is by using dedicated config files.** In this case you need to use the `--config` flag instead of the `--repo` flag when running RepoSense, as follows:

  {{ embed("Appendix: **CLI syntax reference → `config` flag**", "cli.md#section-config") }}

<box type="tip" seamless>

**Managing config files collaboratively**: If you use RepoSense to monitor a large number of programmers, it may be more practical to get the programmers to submit PRs to update the config files as necessary (<tooltip content="a coder realizes some of her code is missing from the report because she used multiple Git usernames, and wants to add the additional usernames to the config file">example use case</tooltip>).

To ensure that their PRs are correct, you can use [Netlify _deploy previews_](https://www.netlify.com/blog/2016/07/20/introducing-deploy-previews-in-netlify/) to preview how the report would look like after the PR has been merged. More details are in the panels below.

  {{ embed("Appendix: **Using RepoSense with Netlify → Setting up**", "withNetlify.md#section-setting-up") }}

  {{ embed("Appendix: **Using RepoSense with Netlify → PR previews**", "withNetlify.md#section-pr-previews") }}

</box>

<!-- ------------------------------------------------------------------------------------------------------ -->

### Get target repos to provide more info
**If feasible, you can also customize the target repos to play well with RepoSense** in the following two ways:

1\. Add a _standalone config file_ to the repo to provide more config details to RepoSense. The format of the file is given below.
  {{ embed("Appendix: **Standalone config file format**", "configFiles.md#section-standalone") }}

2\. To have more precise control over which code segment is attributed to which author, authors can annotate their code using `@@author` tags, as explained below.
  {{ embed("Appendix: **Using `@@author` tags**", "usingAuthorTags.md") }}

<box type="info" seamless>

In both instances, it is **necessary to commit any changes** for them to be detected by RepoSense.

</box>

3\. Add a Git `.mailmap` file at the top-level of the repository, specifying mapped authors/commiters and/or e-mail addresses as per [gitmailmap documentation](https://git-scm.com/docs/gitmailmap). Any mappings specified here will be applied by Git before all other RepoSense configurations. Configuration via `.mailmap` is particularly useful if you want the mapping to apply for all Git commands as well instead of just for RepoSense.

<!-- ------------------------------------------------------------------------------------------------------ -->

### Personalizing Reports

#### Add favicon.ico
Ensure you have a valid `favicon.ico` file. This icon will appear in the browser tab when your report is viewed. Copy or move your `favicon.ico file` into the `assets` folder of the config directory 

Specifying the config directory can be done as follows:
{{ embed("Appendix: **CLI syntax reference → `config` flag**", "cli.md#section-config") }}

#### Add a title
A title component can be added by creating a file titled `title.md` in the config directory.

Specifying the config directory can be done as follows:
{{ embed("Appendix: **CLI syntax reference → `config` flag**", "cli.md#section-config") }}

The title can render a combination of Markdown/HTML and plaintext ([example](https://github.com/reposense/RepoSense/blob/master/docs/ug/title.md)), and will appear on the top of the left panel as shown below:
![Title Component Example](../images/title-example.png)

Do note that the width of the title is bound by the width of the left panel.

For more information on how to use Markdown, see the [Markdown Guide](https://www.markdownguide.org/).

#### Add blurbs for branches or authors
A blurb can be added for a repository or author by creating the respective blurb markdown files in the config directory.
- For a repository:
  - Create a file titled `repo-blurbs.md` in the config directory. The format of the file is given below:
    {{ embed("Appendix: **Config files format**", "configFiles.md#section-repo-blurbs") }}
  - The blurbs will be visible when grouping by `Repo/Branch`.
- For an author:
  - Create a file titled `author-blurbs.md` in the config directory. The format of the file is given below:
    {{ embed("Appendix: **Config files format**", "configFiles.md#section-author-blurbs") }}
  - The blurbs will be visible when grouping by `Author`.

Specifying the config directory can be done as follows:
{{ embed("Appendix: **CLI syntax reference → `config` flag**", "cli.md#section-config") }}
