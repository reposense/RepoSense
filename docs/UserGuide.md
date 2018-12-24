# RepoSense - User Guide

* [Getting Started](#getting-started)
* [Interpreting the Report](#interpreting-the-report)
  * [Chart Panel](#chart-panel)
  * [Code Panel](#code-panel)
  * [Tool Bar](#tool-bar)
* [Configuring a Repo to Provide Additional Data to RepoSense](#configuring-a-repo-to-provide-additional-data-to-reposense)
  * [Provide Data Using a json Config File](#provide-data-using-a-json-config-file)
  * [Provide Data Using `@@author` Tags](#provide-data-using-author-tags)
* [Customizing the Analysis](#customizing-the-analysis)
  * [Customize Using Command Line Parameters](#customize-using-command-line-parameters)
  * [Customize Using csv Config Files](#customize-using-csv-config-files)
    * [`repo-config.csv`](#repo-configcsv)
    * [`author-config.csv`](#author-configcsv)
* [Analyzing Multiple Repos](#analyzing-multiple-repos)


## Getting Started

First, ensure that you have the necessary prerequisites:
* **JDK `1.8.0_60`** or later
* **git `2.14`** or later on the command line (run `git --version` in your OS terminal to confirm)

Next, download the latest executable Jar from our [releases](https://github.com/reposense/RepoSense/releases/latest).

The simplest use case for RepoSense is to generate a report for the entire history of a repo. Here are the steps:
1. Generate the report for the repo by executing the following command in a terminal:<br/>
   Format : `java -jar RepoSense.jar -repo FULL_REPO_URL` (note the `.git` at the end)<br>
   Example: `java -jar RepoSense.jar -repo https://github.com/reposense/RepoSense.git`
1. The previous step analyzes the default branch of the repo and creates the report in a directory named `reposense-report`. Run the following command to view the report (it will open up in your default Browser):<br/>
   `java -jar RepoSense.jar -view reposense-report`

<hr>

## Interpreting the Report


As the report consist of static pages, it can be viewed using a Web Browser, and can be deployed on most Web hosting platforms (such as [GitHub Pages](https://pages.github.com/)). Assuming the report has been generated already, here are the two options to load the report onto a Browser:
* Run RepoSense with the `-view` option:<br>
  Format: `java -jar RepoSense.jar -view REPORT_FOLDER`<br>
  Example: `java -jar RepoSense.jar -view ./myReport/reposense-report`
* Open the `index.html` (in the report directory) using a Browser and if the report was not loaded automatically, upload the `archive.zip` (in the same directory) manually.


Here is an example of how the report looks like:

![report](images/report.png)

It consists of three main parts: the [_Chart Panel_](#chart-panel), the [_Code Panel_](#code-panel), and the [_Tool Bar_](#tool-bar),  each of which is explained in the sections below.


### Chart Panel

![Ramp Charts](images/rampchart.png)<br>

The `Chart Panel` (an example is shown above) contains _Ramp Charts_ and _Contribution Bars_.

**Ramp Chart**: This is a visualization of frequency and quantity of contributions of an author for a specific repository.

* **Rows**: Each _row_ (i.e., light blue rectangle) represents the contribution timeline of an author for a specific repository.
* **Ramp**: Each row contains **ramps** -- the pointy saw-tooth shapes you see in the screenshot above. A ramp represents the contributions of an author possibly aggregated over a period (e.g., a day or a week).
  * The area of the ramp is proportional to the amount of contribution the author did at that time period.
  * The position of the right edge of the ramp (perpendicular to the blue bar) represents the period (the day or the week) in which the contribution was made.
  * Hover the pointer over a ramp to see the total number of lines represented by that ramp.
  * Click on the ramp to see on GitHub the list of commits represented by that ramp.
* To make comparison between two authors easier, the color of the ramps that represent different authors' contributions at the same time period are the same.
* Ramps representing big contributions can overlap with earlier time periods. This represents the possibility that if the work committed during a specific period is big, it could have started in an earlier time period.

**Contribution Bar**: The total amount of code contributed by an author during the _total analysis period_ is represented by the length of the **red bars** (called _contribution bars_) that appear at the bottom of the row.
* Hover over a contribution bar to see the exact amount of the contribution.
* If an author contributed significantly higher than other authors, the contribution bar can span multiple lines (see the 4th author in the screenshot for an example).


### Code Panel

The `Code Panel` allows users to see the code attributed to a specific author. Click on the name of the author in the `Chart Panel` to display the `Code Panel` on the right.
* The Code Panel shows the files that contain author's contributions, sorted by the number of lines written.
* Clicking the file title toggles the file content.
* Code attributed to the author is highlighted in green.
* Non-trivial code segments that are not written by the selected author are hidden by default, but you can toggle them by clicking on the `...` icon.


### Tool Bar
The `Tool Bar` at the top provides a set of configuration options that control the Chart Panel.
* `Search` : filters the author and repository by keywords.
  * Multiple keywords/terms can be used, separated by spaces.
  * Entries that contain _any_ (not necessarily _all_) of the search terms will be displayed.
  * The keywords used to filter author and repository are case-insensitive.
* `Sort by` : sorting criteria for the Chart Panel
  * `Total Contribution` : the amount of lines, written by the author, in the repository.
  * `Variance` : the [variance](https://en.wikipedia.org/wiki/Variance) of the number of lines that the author has contributed to the repository daily, sorted from low to high.
  * `Author Name` : the author's display name.
  * `Repo/Branch Name` : see note [1] below.
* `Granularity` : the period of time for which commits are aggregated in the Ramp Chart.
    * `Day`: commits within a day (commits made within 00:00 to 23:59 in **GMT+8**) are shown as one ramp
    * `Week`:  commits within a week are shown as one ramp
* `Since`, `Until` : the date range for the Ramp Chart (not applied to the Contribution Bars).
* `Reverse` : if checked, the sorting will be done in the reverse order of the default sorting order
* `Group` : if checked, authors from the same repo/branch<sup>[1]</sup> will be grouped together. This has precedence over the `sort by` feature; the `sort by` feature will only change display order of the authors under the same repo/branch<sup>[1]</sup>.

Notes:<br>
[1] **`Repo/Branch`**: the repo/branch name is constructed as `ORGANIZATION_REPOSITORY_BRANCH` e.g., `resposense_reposense_master`

**Bookmarking a specific toolbar setting**: The URL changes according to the toolbar configuration. You can save a specific configuration of the report by bookmarking the url (using browser functionality).

<hr>

## Configuring a Repo to Provide Additional Data to RepoSense

When a repo is being analyzed by RepoSense, there are **two ways repo owners can provide additional details to RepoSense**: using a config file, or annotating code using `@@author` tags. The two approaches are explained in the sections below.

### Provide Data Using a json Config File

Repo owners can provide the following additional information to RepoSense using a config file that we call the **_standalone config file_**:
* which files/authors/commits to analyze/omit
* which git and github usernames belong to which authors
* the display of an author

To use this feature, add a `_reposense/config.json`  to the root of your repo using the format in the example below ([another example](../_reposense/config.json)) and **commit it** (reason: RepoSense can see committed code only):
```json
{
  "ignoreGlobList": ["about-us/**", "**index.html"],
  "formats": ["html", "css"],
  "ignoreCommitList": ["90018e49f129ce7e0abdc8b18e91c9813588c601", "67890def"],
  "authors":
  [
    {
      "githubId": "alice",
      "displayName": "Alice T.",
      "authorNames": ["AT", "A"],
      "ignoreGlobList": ["**.css"]
    },
    {
      "githubId": "bob"
    }
  ]
}
```
Note: all fields are optional unless specified otherwise.

**Fields to provide _repository-level_ info**:

* `ignoreGlobList`: Folders/files to ignore, specified using the [_glob format_](https://docs.oracle.com/javase/tutorial/essential/io/fileOps.html#glob).
* `formats`: File formats to analyze. Default: `adoc cs css fxml gradle html java js json jsp md py tag xml`
* `ignoreCommitList`: The list of commits to ignore during analysis. For accurate results, the commits should be provided with their full hash.

**Fields to provide _author-level_ info**:<br>
Note: `authors` field should contain _all_ authors that should be captured in the analysis.
* `githubId`: Github username of the author. :exclamation: Mandatory field.
* `displayName`: Name to display on the report for this author.
* `authorNames`: Git Author Name(s) used in the author's commits. By default RepoSense assumes an author would use her GitHub username as the Git username too. The meaning of _Git Author Name_ is explained in [_A Note About Git Author Name_](#a-note-about-git-author-name).
* `ignoreGlobList`: _Additional_ (i.e. on top of the repo-level `ignoreGlobList`) folders/files to ignore for a specific author . In the example above, the actual `ignoreGlobList` for `alice` would be `["about-us/**", "**index.html", "**.css"]`

To verify your standalone configuration is as intended, add the `_reposense/config.json` to your local copy of repo and run RepoSense against it as follows:<br>
* Format : `java -jar RepoSense.jar -repo LOCAL_REPO_LOCATION` <br>
* Example: `java -jar RepoSense.jar -repo c:/myRepose/foo/bar`<br>
After that, view the report to see if the configuration you specified in the config file is being reflected correctly in the report.

##### **A Note About Git Author Name**

`Git Author Name` refers to the customizable author's display name set in the local `.gitconfig` file. For example, in the Git Log's display:
```
...
commit cd7f610e0becbdf331d5231887d8010a689f87c7
Author: ConfiguredAuthorName <author@example.com>
Date:   Fri Feb 9 19:14:41 2018 +0800

    Make some changes to show my new author's name

commit e3f699fd4ef128eebce98d5b4e5b3bb06a512f49
Author: ActualGitHubId <author@example.com>
Date:   Fri Feb 9 19:13:13 2018 +0800

    Initial commit
 ...
```
`ActualGitHubId` and `ConfiguredAuthorName` are both `Git Author Name` of the same author. To set the author name to the value you want (e.g., to set it to your GitHub username), you can use the following command ([more info](https://www.git-tower.com/learn/git/faq/change-author-name-email)):
```
git config --global user.name "YOUR_AUTHOR_NAME”
```
RepoSense expects the Git Author Name to be the same as author's GitHub username. If an author's `Git Author Name` is different from her `GitHub ID`, the `Git Author Name` needs to be specified in the standalone config file. If the author has more than one `Git Author Name`, multiple values can be entered too.

> Note: Symbols such as `"`, `!`, `/` etc. in your author name will be omitted, which may reduce the accuracy of the analysis if 2 names in the repository are approximately similar.

### Provide Data Using `@@author` Tags

If you want to override the code authorships deduced by RepoSense (which is based on Git blame/log data), you can use `@@author` tags to specify certain code segments should be credited to a certain author irrespective of git history. An example scenario where this is useful is when a method was originally written by one author but a second author did some minor refactoring to it; in this case RepoSense might attribute the code to the second author while you may want to attribute the code to the first author.

There are 2 types of `@@author` tags:
- Start Tags (format: `@@author AUTHOR_GITHUB_ID`): A start tag indicates the start of a code segment written by the author identified by the `AUTHOR_GITHUB_ID`.
- End Tags (format: `@@author`): Optional. If not provided, the code till the next start tag (or the end of the file) will be attributed to the author specified in the start tag above. Use only when necessary to minimize polluting your code with these extra tags.

The `@@author` tags should be enclosed within a comment, using the comment syntax of the file in concern. Below are some examples:

![author tags](images/add-author-tags.png)

Special thanks to [Collate](https://github.com/se-edu/collate) for providing the inspiration for this functionality.

Note: Remember to **commit** the files after the changes. (reason: RepoSense can see committed code only)
<hr>

## Customizing the Analysis

The analysis can be customized using additional command line parameters or using config files. The two approaches are explained in the sections below.

### Customize Using Command Line Parameters

As you know, `java -jar RepoSense.jar` takes the following parameter:

* **`-repo REPO_LOCATION`**: The URL or the disk location of the git repositories to analyze.<br>
  Example using URL: `-repo https://github.com/reposense/RepoSense.git`<br>
  Example using disk location: `-repo C:\Users\user\Desktop\GitHub\RepoSense`


In addition, there are some _optional_ extra parameters you can use to customize the analysis further:

* **`-output OUTPUT_DIRECTORY`**: Indicates where to save the report generated. Default: current directory.<br>
  Example: `-output ./foo` (in this case, the report will be in the `./foo/reposense-report` folder)
* **`-since START_DATE`**: The start date of analysis. Format: `DD/MM/YYYY`<br>
  Example:`-since 21/10/2017`
* **`-until END_DATE`**: The end date of analysis. The analysis excludes the end date. Format: `DD/MM/YYYY`<br>
  Example:`-since 21/10/2017`
* **`-formats LIST_OF_FORMATS`**: A space-separated list of file extensions that should be included in the analysis. Default: `adoc cs css fxml gradle html java js json jsp md py tag xml`<br>
  Example:`-formats css fxml gradle`
* **`--ignore-standalone-config`**: A flag to ignore the standalone config file in the repo (`-isac` as alias). This flag will not overwrite the `Ignore standalone config` field in the csv config file. Default: the standalone config file is not ignored.<br>
  Example:`--ignore-standalone-config` or `-isac`

Here's an example of a command using all parameters:<br>
`java -jar RepoSense.jar -repo https://github.com/reposense/RepoSense.git -output ./report_folder -since 21/10/2017 -until 21/11/2017 -formats java adoc js -isac`

### Customize Using csv Config Files

Another, more powerful, way to customize the analysis is by using dedicated config files. In this case you need to use the `-config` parameter instead of the `-repo` parameter when running RepoSense, as follows:

* **`-config CONFIG_DIRECTORY`**: The directory in which you have the config files.<br>
  Example:`java -jar RepoSense.jar -config  ./my_configs`

The directory used with the `-config` parameter should contain a `repo-config.csv` file and, optionally, an `author-config.csv` file, both of which are described in the sections below.

#### `repo-config.csv`

`repo-config.csv` file contains repo-level config data as follows:

* First row: column headings, ignored by RepoSense
* Second row: repo data

Here is an example:

Repository's Location|Branch|File formats|Ignore Glob List|Ignore standalone config|Ignore Commits List
---------------------|------|------------|----------------|------------------------|-------------------
`https://github.com/foo/bar.git`|`master`|`java;css`|`test/**`|`yes`|`2fb6b9b2dd9fa40bf0f9815da2cb0ae8731436c7;c5a6dc774e22099cd9ddeb0faff1e75f9cf4f151`

<br>

Column Name | Explanation
----------- | -----------
Repository's Location | The `GitHub URL` or `Disk Path` to the git repository e.g., `https://github.com/foo/bar.git` or `C:\Users\user\Desktop\GitHub\foo\bar`
[Optional] Branch | The branch to analyze in the target repository e.g., `master`. Default: the default branch of the repo
[Optional] File formats<sup>*</sup> | The file extensions to analyze. Default: `adoc;cs;css;fxml;gradle;html;java;js;json;jsp;md;py;tag;xml`
[Optional] Ignore Glob List<sup>*</sup> | The list of file path globs to ignore during analysis for each author. e.g., `test/**;temp/**`
[Optional] Ignore standalone config | To ignore the standalone config file (if any) in target repository, enter **`yes`**. If the cell is empty, the standalone config file in the repo (if any) will take precedence over configurations provided in the csv files.
[Optional] Ignore Commit List<sup>*</sup> | The list of commits to ignore during analysis. For accurate results, the commits should be provided with their full hash.

<sup>* **Multi-value column**: multiple values can be entered in this column using a semicolon `;` as the separator.</sup>

#### `author-config.csv`

Optionally, you can use a `author-config.csv` (which should be in the same directory as `repo-config.csv` file) to provide more details about the authors to analyze ([example](author-config.csv)). It should contain the following columns:

Column Name | Explanation
----------- | -----------
Repository's Location | Same as `repo-config.csv`
[Optional] Branch | The branch to analyze for this author e.g., `master`. Default: the default branch of the repo
Author's GitHub ID | GitHub username of the target author e.g., `JohnDoe`
[Optional] Author's Display Name | The name to display for the author. Default: author's GitHub username.
[Optional] Author's Git Author Name<sup>*</sup> | The meaning of _Git Author Name_ is explained in [_A Note About Git Author Name_](#a-note-about-git-author-name).
[Optional] Ignore Glob List<sup>*</sup> | Files to ignore for this author, in addition to files ignored by the patterns specified in `repo-config.csv`

<sup>* **Multi-value column**: multiple values can be entered in this column using a semicolon `;` as the separator.</sup>

If `author-config.csv` is not given and the repo has not provide author details in a standalone config file, all the authors of the repositories within the date range specified (if any) will be analyzed.

<hr>

## Analyzing Multiple Repos

This section assumes that you have read the earlier sections of the user guide.

The simplest way to analyze multiple repos in one go is to use the `-repos` parameter in place of the `-repo` parameter when running RepoSense.
* Format : `java -jar RepoSense.jar -repos REPO_LIST` <br>
* Example: `java -jar RepoSense.jar -repos https://github.com/reposense/RepoSense.git c:/myRepose/foo/bar` analyzes the two specified repos (one remote, one local) and generates one report containing details of both.

Alternatively, you can use csv config files to customize the analysis as before while specifying multiple repos to analyze.
* `repo-config.csv`: Add additional rows for the extra repos ([example](repo-config.csv))
* `author-config.csv`: Add one row for each author in each repo you want to analyze



