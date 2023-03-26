{% set title = "Appendix: `author-config.csv` advanced syntax" %}
<frontmatter>
  title: "{{ title | safe }}"
</frontmatter>

{% from 'scripts/macros.njk' import embed, step with context %}

<h1 class="display-4"><md>{{ title }}</md></h1>

<div class="lead">

Given below are the advanced syntax available and its use cases for `author-config.csv`. 
</div>

### Multiple `Repository's Location` per author

#### Motivation
Each row in `author-config.csv` is used to provide more details about one particular author, such as the author's Git Host ID, emails, etc.

The column `Repository's Location` in `author-config.csv` usually takes in a **single repository** per row. However, it is likely that the author is working on **multiple repositories** that we would like to analyze using RepoSense.

Below is an example of `author-config.csv`, with less relevant columns hidden:

| Repository's Location                           | Branch  | Author's Git Host ID | ... Hidden columns    |
|-------------------------------------------------|---------|----------------------|-----------------------|
| https://github.com/reposense/RepoSense.git      | master  | sikai00              | --                    |
| https://github.com/reposense/RepoSense.git      | cypress | sikai00              | --                    |
| https://github.com/reposense/RepoSense.git      | release | sikai00              | --                    |
| https://github.com/reposense/testRepo-Alpha.git | master  | sikai00              | --                    |

In order to provide details for the author for all the repositories, we would have to duplicate each row containing the same author details, but for each individual repository!

By specifying multiple repository locations per row, we can prevent duplication of the author's information and keep everything within a single row. 

| Repository's Location                                                                                                | Branch  | Author's Git Host ID | ... Hidden columns    |
|----------------------------------------------------------------------------------------------------------------------|---------|----------------------|-----------------------|
| https://github.com/reposense/RepoSense.git~master\|cypress\|release;https://github.com/reposense/testRepo-Alpha.git  | master  | sikai00              | --                    |

<br>

### Usage

#### Specifying multiple `Repository's Location`

To specify multiple `Repository's Location`, we can use a semicolon `;` as a separator.

Below is an example:

| Repository's Location                                                               | Branch  | Author's Git Host ID | ... Hidden columns    |
|-------------------------------------------------------------------------------------|---------|----------------------|-----------------------|
| https://github.com/reposense/RepoSense.git;https://github.com/MarkBind/markbind.git | master  | sikai00              | --                    |

We have now furnished author details for both repositories in a single row, instead of having two individual rows per Repository's Location.

<br>

#### Specifying multiple `Repository's Location` with a single specific branch

To override the Branch column with a specific branch for a particular `Repository's Location`, we can use a tilde `~` separator to denote our branch.

Below is an example:

| Repository's Location                                                                       | Branch  | Author's Git Host ID | ... Hidden columns    |
|---------------------------------------------------------------------------------------------|---------|----------------------|-----------------------|
| https://github.com/reposense/RepoSense.git~release;https://github.com/MarkBind/markbind.git | master  | sikai00              | --                    |

Instead of using the branch specified in the branch column for `https://github.com/reposense/RepoSense.git`, the "release" branch will be used instead.

`https://github.com/MarkBind/markbind.git` will still use the "master" branch as specified in the branch column.

<br>

#### Specifying multiple `Repository's Location` with multiple specific branches

To list out multiple branches for a particular `Repository's Location`, we can use a pipe `|` separator to demarcate the different branches.

Below is an example:

| Repository's Location                                                | Branch   | Author's Git Host ID | ... Hidden columns    |
|----------------------------------------------------------------------|----------|----------------------|-----------------------|
| https://github.com/reposense/RepoSense.git~master\|release\|cypress; | master   | sikai00              | --                    |

We have now provided author details for three different branches in a single repo, using a single row.
