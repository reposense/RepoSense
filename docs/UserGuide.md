# RepoSense - User Guide

## Quick Start
1. Ensure that you have the necessary [dependencies](#dependencies).
2. Read up on [How to Generate Dashboard](#how-to-generate-dashboard).
3. Fill up the [CSV Config File](#csv-config-file).
4. Perform the execution to generate the [dashboard](#dashboard).


## Dependencies
1. **JDK `1.8.0_60`**  or later.
2. **Git** on the command line.
3. **findstr** for Windows, **grep** for macOS or Linux on the command line.
   * Check that the tool exist on your OS terminal by typing it's name on your terminal and ensure that it does not output messages such as `not found` or `not recognized`.


## How to Generate Dashboard
1. Download the latest executable Jar on our [release](https://github.com/reposense/RepoSense/releases/latest).
   * Alternatively, you can compile the executable Jar yourself by following our [build from source guide](Build.md).
2. Execute it on the OS terminal. <br>
Usage: `java -jar RepoSense.jar -config CSV_CONFIG_FILE_PATH [-output OUTPUT_DIRECTORY] [-since DD/MM/YYYY] [-until DD/MM/YYYY]`

3. The dashboard will be generated in the designated OUTPUT_DIRECTORY, or current working directory otherwise, upon successful compilation as index.html.

Sample usage:
```
$ java -jar RepoSense.jar -config CSV_path.csv -output output_path/ -since 01/10/2017 -until 01/11/2017
```

Argument List:
- config : Mandatory. The path to the CSV config file.
- output : Optional. The path to the dashboard generated. If not provided, it will be generated in the current directory.
- since : Optional. start date of analysis. Format: `DD/MM/YYYY`
- until : Optional. end date of analysis. Format: `DD/MM/YYYY`

```
Note:
The contribution calculation is based on the daily commits made within 00:00 to 23:59 in GMT+8.
```

### Other option:
 1. Clone this repository (or [download as zip](https://github.com/reposense/RepoSense/archive/master.zip))
 2. Execute the following command on the OS terminal inside the project directory.<br>
Usage: `gradlew run -Dargs="-config CSV_CONFIG_FILE_PATH [-output OUTPUT_DIRECTORY] [-since DD/MM/YYYY] [-until DD/MM/YYYY]"` <br>

Sample usage:
```
$ gradlew run -Dargs="-config CSV_path.csv -output output_path/ -since 01/10/2017 -until 01/11/2017"
```

`-Dargs="..."` uses the same argument format as mentioned above.


## CSV Config File
The `CSV Config File` contains the list of repositories, and the corresponding target authors to track contribution of.
[sample.csv](../sample.csv) is an example of a configuration file setup. It should contain the following columns:

Column Name | Explanation
----------- | -----------
Organization | Organization of the target repository
Repository | Name of the target repository
Branch | The branch to analyse in the target repository
Author's GitHub ID | GitHub ID of the target contributor in the repository
Author's Display Name | Optional Field. The value of this field, if not empty, will be displayed in the dashboard instead of author's GitHub ID.
[Optional] Author's Git Author Name | Detailed explanation below

#### Git Author Name
`Git Author Name` refers to the customizable author's display name set in the local `.gitconfig` file.
It is displayed as author name as opposed to the `GitHub ID` in the entries.
For example, in the Git Log's display:
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
**ActualGitHubId** and **ConfiguredAuthorName** are both `Git Author Name` of the same author.

By default, Git uses the authors' `GitHub ID` as their `Git Author Name`.
However, this is not always the case. Many Git users customize their `Git Author Name`.

To fix this, authors can use the following command to reset their `Git Author Name` to `GitHub ID` before contributing:
```
git config --global user.name “YOUR_GITHUB_ID_HERE”
```
For more information, do visit this [FAQ](https://www.git-tower.com/learn/git/faq/change-author-name-email) on changing Git Author Identity.

If an author's `Git Author Name` is not the same as his `GitHub ID`, the `Git Author Name` needs to be filled into the CSV config file for accurate consolidation.
In the event that the author has more than one `Git Author Name`, multiple values can be entered in the `Git Author Name` column by using a semicolon `;` separator.
For example,`Alice;Bob`.

### [Optional] Preparation of Repositories
#### Contribution Tags
Although RepoSense's contribution analysis is quite accurate, authors can use annotations to ensure that RepoSense correctly recognizes their contribution.
Special thanks to [Collate](https://github.com/se-edu/collate) for providing the inspiration for this functionality.

There are 2 types of tags:
- Start Tags (`@@author YOUR_GITHUB_ID`)
- End Tags (`@@author`)

Below are some examples (by the courtesy of Collate's User Guide):

![author tags](images/add-author-tags.png)

You can use `Start Tags` to mark the start of your contribution. The author specified in the `Start Tags` will be recognized by RepoSense as the author for all lines between a `Start Tag` and the next `End Tag`. If RepoSense cannot find a matching `End Tag` for a `Start Tag` in the same file, it will assume that all lines between the `Start Tag` to the end of the file is authored by the author specified in the `Start Tag`.


## Dashboard
The `Dashboard` is written in HTML and Javascript as static pages - readable by majority of web browsers, and easily deploy-able in most hosting platforms (such as [GitHub Pages](https://pages.github.com/)).

Below is an example of how the Dashboard looks like:

![dashboard](images/dashboard.png)

It consists of three main parts: [Tool Bar](#tool-bar), [Chart Panel](#chart-panel) and [Code Panel](#code-panel).

### Tool Bar
The `Tool Bar` at the top provides a set of filters that control the chart panel. From right to left, the filters are:
- Sort : Users can sort by:
    - Total Contribution : The amount of lines, written by the author, in the repository.
    - Variance : The variance of contribution of all commits.
        - It indicates the code consistency of an author.
        - It helps to determine whether the author has been contributing regularly or procrastinating.
        - This can be useful for instructors of student projects, e.g. [Addressbook](https://github.com/se-edu/addressbook-level4).
    - Author Name : The Author's GitHub ID or Display Name configured in the `CSV Config File`.
    - Team Name : The name of the organization of the repository.
- Interval : Interval refers to amount of time one single ramp represents.
    - Users can choose two modes of time intervals:
        - daily
        - weekly
- Period : The time period that the ramp charts display.
- Group : Checkbox.
    - If checked, authors that have contributed to the same repository will be displayed next to each other.
    - This overrides the sort function.
- Search : Filters the author and repository by keywords.
    - Multiple keywords can be used, separated by spaces.
    - The keywords are logically connected with OR operators.
- Bookmark : The star icon at the top right corner.
    - Clicking on it will generates a link to the report with all the tooltip settings.
    - This link will be copied to user’s clipboard.

### Chart Panel
The `Chart Panel` contains two type of indicators:
- [Ramp Chart](#ramp-chart)
- [Total Contribution Bar](#total-contribution-bars)

#### Ramp Chart
To illustrate frequency and amount of contribution in the same graph, and to allow easy comparison between each entry, we implemented a new type of visualization.
This is referred as the `Ramp Charts`.

Below are a few examples:

![Ramp Charts](images/rampchart.png)
Each light blue bar represents the contribution timeline of an individual author for a specific repository. Each row, there are several **ramps**.
- The area of the ramp is proportional to the amount of contribution the author did at that time period.
- The position of the right edge of the ramp (perpendicular to the blue bar) is dependent on the time period that ramp represents.
- To make comparison between two authors easier, the color of the ramps that represent different authors' contributions at the same time period are the same.
- The timelines (blue bar) of the charts should be aligned, so that the comparison of contribution between two authors are easier.
- There is no limit to the area of the ramp. If the contribution for a time period is too large compared to the rest of the time period, it is going to overlap with the neighbor ramps. Thus, the ramps are transparent, so they will not cover their neighbors.
- When user hovers the pointer over a ramp, as shown in the above Figure, total amount of contribution over the time period will be shown.
- Clicking on the ramp will redirect user to the GitHub page, which contains all the commits within the fixed time period.

#### Total Contribution Bars
The total amount of code contributed is represented by the **red bars**, and the length of these red bars is proportional to the total contribution of the corresponding author.
Hovering over the bar shows the exact amount of contribution.
If the author contributes **too much** compared to other authors, there will be multiple red bars in his `Chart Panel`.


### Code Panel
The `Code Panel` allows users to review contributers' code, showing all the lines written by the selected author.
Clicking on the name of the author, in the `Chart Panel`, will display the `Code Panel` on the right.

Below is the list of features in this panel:
- Files that contain author's contribution will be shown in this panel, sorted by the number of lines written.
- Clicking the file title will show/hide the file content.
- The lines that are **NOT** written by the selected author are hidden in collapsable boxes.
  - User can click on the boxes to display the hidden lines for context.
  - These lines will be highlighted in a different color, gray.
