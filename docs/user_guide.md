## Quick Start
1. Ensure that you have the necessary [dependencies](#dependencies)
2. Read up on [How to Generate Dashboard](#how-to-generate-dashboard)
3. Fill up the CSV [configuration file](#csv-config-file)
4. Perform the execution to generate the [dashboard](#dashboard)


## Dependencies
1. **JDK `1.8.0_60`**  or later
2. **Git** on the command line
3. **findstr** for Windows, **grep** for macOS or Linux on the command line
   * Ensure that you're able to use these tools on the OS terminal.


## How to Generate Dashboard
1. Download the latest executable Jar on our [release](https://github.com/reposense/RepoSense/releases/latest).
   * Alternatively, you can compile the executable Jar yourself by following our [build from source guide](Build.md).
2. Execute it on the OS terminal. <br>
Usage: `java -jar RepoSense.jar -config CSV_CONFIG_FILE_PATH [-output OUTPUT_DIRECTORY] [-since DD/MM/YYYY] [-until DD/MM/YYYY]`
3. The dashboard can be in the folder designated in OUTPUT_DIRECTORY, or current working directory otherwise, as index.html.

Sample usage:
```
$ java -jar RepoSense.jar -config CSV_path.csv -output output_path/ -since 01/10/2017 -until 01/11/2017
```
Argument List:
- config: Mandatory. The path to the CSV config file.
- output: Optional. The path to the dashboard generated. If not provided, it will be generated in the current directory.
- since : Optional. start date of analysis. Format: `DD/MM/YYYY`
- until : Optional. end date of analysis. Format: `DD/MM/YYYY`

```
Note:
The contribution calculation is base on the daily commits made within 00:00 to 23:59 in GMT+8.
```


## CSV Config File
The `CSV Config File` control the list of repositories and the target author.
[Sample_full.csv](../sample_full.csv) is an example configuration file setup. It should contain the following columns:

Column Name | Explanation
----------- | -----------
Organization | Organization of the target repository
Repository | Name of the target repository
branch | The branch to analyse in the target repository
Author's GitHub ID | GitHub ID of the target contributor in the repository
Author's Display Name | Optional Field. The value of this field, if not empty, will be displayed in the dashboard instead of author's GitHub ID.
[Optional] Author's Git Author Name | Detailed explanation below

#### Git Author Name
`Git Author Name` refers to the customizable author's display name set in the local `.gitconfig` file.
It is displayed as author name as opposed to the `GitHub ID` in the entries.
For example, in Git Log output:
```
...
commit cd7f610e0becbdf331d5231887d8010a689f87c7
Author: actualGitHubId <ma.tanghao@dhs.sg>
Date:   Fri Feb 9 19:14:41 2018 +0800

    moved

commit e3f699fd4ef128eebce98d5b4e5b3bb06a512f49
Author: configuredAuthorName <ma.tanghao@dhs.sg>
Date:   Fri Feb 9 19:13:13 2018 +0800

    new
 ...
```
**actualGitHubId** and **configuredAuthorName** are both `Git Author Name`.

By default, git uses the authors' `GitHub ID` as their `Git Author Name`.
However, this is not always the case. Many Git users customize their `Git Author Name`.

To fix this, authors can use the following command to reset their `Git Author Name` to `GitHub ID` before contributing:
```
git config --global user.name “YOUR_GitHub_ID_HERE”
```
For more details, do checkout this [faq](https://www.git-tower.com/learn/git/faq/change-author-name-email) on changing Git Author Identity.

If an author's `Git Author Name` is not the same as his `GitHub ID`, the `Git Author Name` needs to be filled into the CSV config file for accurate consolidation.
If more than one `Git Author Name` is used, they can separate them by using the semicolon `;` operator within the `Git Author Name` column.

### [Optional] Preparation of Repositories
#### Contribution Tags
Although RepoSense's contribution analysis is quite accurate, authors can use annotations to ensure that RepoSense correctly recognizes their contribution.
Special thanks to [Collate](https://github.com/se-edu/collate) for providing the inspiration for this functionality.

There are 2 types of tags:
- Start Tags (`@@author YOUR_GITHUB_ID`)
- End Tags (`@@author`)

Below are some examples (by the courtesy of Collate's User Guide):

![author tags](images/add-author-tags.png)

You can use start tags to mark the start of your contribution. The author specified in the start tag will be recognized by RepoSense as the author for all lines between a start tag and the next end tag. If RepoSense cannot find a matching End Tag for a Start Tag in the same file, it will assume that all lines between the Start Tag to the end of the file is authored by the author specified in the Start Tag.


## Dashboard
The `Dashboard` is written in HTML and Javascript as static pages - readable by majority of web browsers, and easily deploy-able in most hosting platforms (such as [GitHub Pages](https://pages.github.com/)).
Below is an example of how the Dashboard looks like:

![dashboard](images/dashboard.png)

It consists of three main parts: [Tool Bar](#tool-bar), [Chart Panel](#chart-panel) and [Code Panel](#code-panel).

### Tool Bar
The `Tool Bar` at the top provides a set of filters that control the chart panel. From right to left, the filters are:
- Sorting: Users can sort by:
	- Total Contribution: the amount of lines written by the author did in the latest version of the project
	- Variance: The variance of contribution of all commits. This indicates whether the author is contributing regularly or just rushing before deadlines and milestones. This can be useful for instructors of student projects, e.g. [Addressbook](https://github.com/se-edu/addressbook-level4).
	- Author Name
	- Team Name: The name of the organization of the repository
- Interval: Interval refers to amount of time one single ramp represents. Users can choose two modes of time intervals: weekly (7 days)  and daily (1 day).
- Period: The time period that the ramp charts display.
- Group By Repo: Checkbox. If checked, the author that contributed to the same repo will be displayed next to each other, no matter what the sorting element is.
- Search: Only display the author whose name or the repository’s name match the keyword. The user can separate keywords by spaces, and the keywords are logically connected with OR operators.
- Bookmarking: By clicking the hyperlink icon on the top right corner, a link to the report with all the tooltip settings will be generated and copied to user’s clipboard.

### Chart Panel
The `Chart Panel` contains two type of indicators:
- ramp chart
- total contribution bar

#### Ramp Chart
To illustrate frequency and amount of contribution in the same graph, and also allow easy comparison between each entry, we implemented a new type of visualization.
This is referred as the `Ramp Charts`.

Below are a few examples:

![Ramp Charts](images/rampchart.png)
Each light blue bar represents the contribution timeline of an individual author for a specific repository. On each row, there are several ‘ramps’.
- The area of the ramp is proportional to the amount of contribution the author did at that time period.
- The position of the right edge of the ramp (perpendicular to the blue bar) is dependent on the time period that ramp represents.
- To make comparison between two authors easier, the color of the ramps that represent different authors' contributions at the same time period are the same.
- The timelines (blue bar) of the charts should be aligned, so that the comparison of contribution between two authors are easier.
- There is no limit to the area of the ramp. If the contribution for a time period is too large compared to the rest of the time period, it is going to overlap with the neighbor ramps. Thus, the ramps are transparent, so they will not cover their neighbors.
- As Figure shown, when the user hovers the mouse a ramp, the time period and the exact amount of contribution will be shown.
- If you click on a ramp, a GitHub page containing the commits in that period of time will be opened

#### Total Contribution Bars
The total amount of code contributed is represented by the **red bars**, and the length of these red bars is proportional to the total contribution of the corresponding author.
Hovering over the bar shows the exact amount of contribution.
If the author contributes **too much** compared to other authors in the report, there will be multiple red bars for him.


### Code Panel
The `Code Panel` allows users to review contributers' code in the report, showing all the lines written by the selected author.
Clicking on the name of the author, in the `Chart Panel`, will display the `Code Panel` on the right.

Below is the list of features in this panel:
- Files that contain author's contribution will be shown in this panel, sorted by the number of lines written.
- Clicking the file title will show/hide the file content.
- The lines that are NOT written by the selected author will be marked in gray and displayed to provide context for the user
- Segments of codes that are not written by the selected author are by default collapsed. User can click on the segments to display them, if necessary.
