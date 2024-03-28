{% set title = "Appendix: `author-config.csv` advanced syntax" %}
<frontmatter>
  title: "{{ title | safe }}"
</frontmatter>

{% from 'scripts/macros.njk' import embed, step with context %}

<h1 class="display-4"><md>{{ title }}</md></h1>

<div class="lead">

Given below are the advanced syntax available for `author-config.csv`.
</div>

## Multiple `Repository's Location` per author

#### Specifying multiple `Repository's Location`

To specify multiple `Repository's Location` in a single row, we can use a semicolon `;` as a separator.

Below is an example:

| Repository's Location                                                                 | Branch  | Author's Git Host ID | ... Hidden columns    |
|---------------------------------------------------------------------------------------|---------|----------------------|-----------------------|
| `https://github.com/reposense/RepoSense.git;https://github.com/MarkBind/markbind.git` | master  | sikai00              | --                    |

We have now furnished author details for both repositories in a single row, instead of having two individual rows per `Repository's Location`.

<br>

#### Specifying `Repository's Location` with one or more specific branches

To list out one or more branches for a particular `Repository's Location`, we can use a pipe `|` separator to demarcate the different branches.

Below is an example:

| Repository's Location                                                  | Branch   | Author's Git Host ID | ... Hidden columns    |
|------------------------------------------------------------------------|----------|----------------------|-----------------------|
| `https://github.com/reposense/RepoSense.git~master\|release\|cypress`  | master   | sikai00              | --                    |

We have now provided author details for three different branches in a single repo, using a single row.

<box type="info" seamless>

The branch specified through the delimiter syntax will take precedence over the `Branch` column.
</box>

<!-- ==================================================================================================== -->

## Using GitHub branch URL as `Repository's Location`

We can also use GitHub branch URL as copied from the address bar directly, to be able to quickly use the URL to specify the `Repository's Location` and a specific `Branch` at the same time.

Below is an example:

| Repository's Location                                 | Branch   | Author's Git Host ID | ... Hidden columns    |
|-------------------------------------------------------|----------|----------------------|-----------------------|
| `https://github.com/reposense/RepoSense/tree/release` |          | sikai00              | --                    |

There is no need to specify the `Branch` column now, as we have specified it through the GitHub branch URL.

<box type="warning" seamless>

GitLab and BitBucket branch URL are not supported at the moment.
</box>

<!-- ==================================================================================================== -->

## Combining the advanced syntax

It is possible to combine the above-mentioned advanced syntax. By doing so, we can go from:

| Repository's Location                                      | Branch   | Author's Git Host ID | ... Hidden columns    |
|------------------------------------------------------------|----------|----------------------|-----------------------|
| `https://github.com/MarkBind/markbind/tree/vue3-migration` |          | sikai00              | --                    |
| `https://github.com/reposense/RepoSense.git`               | master   | sikai00              | --                    |
| `https://github.com/reposense/RepoSense.git`               | cypress  | sikai00              | --                    |

to:

| Repository's Location                                                                                                 | Branch   | Author's Git Host ID | ... Hidden columns   |
|-----------------------------------------------------------------------------------------------------------------------|----------|----------------------|----------------------|
| `https://github.com/MarkBind/markbind/tree/vue3-migration;https://github.com/reposense/RepoSense.git~master\|cypress` |          | sikai00              | --                   |

<br>
