{% set title = "Appendix: CLI syntax reference" %}
<frontmatter>
  title: "{{ title | safe }}"
  pageNav: 3
</frontmatter>

<h1 class="display-4"><md>{{ title }}</md></h1>

<div class="lead">

The command `java -jar RepoSense.jar` takes several flags.
</div>

<box>

**Examples**:

An example of a command using most parameters:<br>
`java -jar RepoSense.jar --repos https://github.com/reposense/RepoSense.git --output ./report_folder --since 31/1/2017 --until 31/12/2018 --formats java adoc xml --view --ignore-standalone-config --last-modified-date --timezone UTC+08 --find-previous-authors --analyze-authorship --originality-threshold 0.66`

Same command as above but using most parameters in alias format:<br>
`java -jar RepoSense.jar -r https://github.com/reposense/RepoSense.git -o ./report_folder -s 31/1/2017 -u 31/12/2018 -f java adoc xml -v -i -l -t UTC+08 -F -A -ot 0.66`
</box>

The section below provides explanations for each of the flags.

<!-- --------------------------◘---------------------------------------------------------------------------- -->

### `--analyze-authorship`, `-A`

**`--analyze-authorship`**: Performs further analysis to distinguish between partial and full credit attribution for
lines of code assigned to the author.

* Default: this feature is turned **_off_** by default and the author will receive partial credits for all lines of
  code, as the code lines are at least partial credit but may not qualify for full credit.
* Alias: `-A` (upper case)
* Example: `--analyze-authorship` or `-A`

<box type="info" seamless>

A darker background colour represents full credit, while a lighter background colour represents partial credit.

If the code is attributed to a different author by the user via `@@author` tag, then the new author will be given
partial credit.
</box>

<!-- --------------------------◘---------------------------------------------------------------------------- -->

### `--config`, `-c`

<div id="section-config">

**`--config CONFIG_DIRECTORY`**: Specifies that config files located in `CONFIG_DIRECTORY` should be used to customize the report.
* Parameter: `CONFIG_DIRECTORY` The directory containing the config files.
* Alias: `-c`
* Example: `java -jar RepoSense.jar --config  ./config`

<box type="info" seamless>

* Cannot be used with `--repos`. The `--repos` flag will take precedence over this flag.
* If both `--repos` and `--config` are not specified, RepoSense looks for config files in the `./config` directory.
</box>
</div>

Refer to the [Advanced Customization](./customizingReports.html#advanced-customization-using-reposense-configuration-files) section for details on the config files.

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--formats`, `-f`

**`--formats LIST_OF_FORMATS`**: Specifies which file extensions to be included in the analysis.
* Parameter: `LIST_OF_FORMATS` A space-separated list of file extensions that should be included in the analysis.<br>
  Default: all file formats
* Alias: `-f`
* Example:`--formats css fxml gradle` or `-f css fxml gradle`

<box type="info" seamless>

Binary file formats, such as `jpg`, `png`,`exe`,`zip`, `rar`, `docx`, and `pptx`, all will be labelled as the file type `binary` in the generated report.
</box>

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--find-previous-authors`, `-F`

**`--find-previous-authors`**: Utilizes Git blame's ignore revisions functionality, RepoSense will attempt to blame the line changes caused by commits in the ignore commit list to the previous authors who altered those lines (if available).
* Default: RepoSense will assume that no authors are responsible for the code changes in the lines altered by commits in the ignore commit list.
* Alias: `-F` (uppercase F)
* Example:`--find-previous-authors` or `-F`

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--help`, `-h`

**`--help`**: Shows the help message.
* Alias: `-h`

<box type="info" seamless>

Cannot be used with any other flags. This flag takes precedence over all other flags.
</box>
<!-- ------------------------------------------------------------------------------------------------------ -->

### `--ignore-standalone-config`, `-i`

**`--ignore-standalone-config`**: Specifies that the standalone config file in the repo should be ignored.
* Default: the standalone config file is not ignored
* Alias: `-i`
* Example:`--ignore-standalone-config` or `-i`

<box type="info" seamless>

This flag overrides the `Ignore standalone config` field in the CSV config file.
</box>

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--ignore-filesize-limit`, `-I`

**`--ignore-filesize-limit`**: Specifies that the file size limit (both default and user-defined) should be ignored during the analysis.
* Default: the file size limit is not ignored
* Alias: `-I`
* Example:`--ignore-filesize-limit` or `-I`

<box type="info" seamless>

All files are subject to a default file size limit or a custom size limit set by the user. Most files should not be
affected by the default size limit. This flag can be useful for including files in your report that are affected by
the size limit. Note that this flag may result in large report sizes and/or slower report generation.
</box>

<box type="info" seamless>

This flag overrides the `Ignore file size limit` field in the CSV config file.
</box>

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--last-modified-date`, `-l`

**`--last-modified-date`**: Specifies that the last modified date of each line of code should be added to `authorship.json`.
* Default: the last modified date of each line of code will not be added to `authorship.json`
* Alias: `-l` (lowercase L)
* Example:`--last-modified-date` or `-l`

<box type="info" seamless>

* Cannot be used with `--shallow-cloning`. This may result in an incorrect last modified date.
* The last modified dates will be in the same timezone specified with the `--timezone` flag.
</box>

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--originality-threshold`, `-ot`

**`--originality-threshold [VALUE]`**: Specifies the cut-off point for partial and full credit
in `--analyze-authorship`. Author will be given full credit if their contribution exceeds this threshold, else partial
credit is given.

* Parameter: `VALUE` Optional. Acceptable range: [0.0, 1.0].<br>
  Default: `0.51`
* Alias: `-ot`
* Example: `--originality-threshold 0.66` or `-ot 0.66`

<box type="info" seamless>

* Requires `--analyze-authorship` flag.
* An author's contribution, or `originality score`, is calculated using Levenshtein Distance (Edit Distance) algorithm.
  We compare the difference between current code line and its previous versions.
</box>

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--output`, `-o`

**`--output OUTPUT_DIRECTORY`**: Indicates where to save the report generated.
* Parameter: `OUTPUT_DIRECTORY` The location for the generated `reposense-report` folder.<br>
  Default: current directory
* Alias: `-o`
* Example: `--output ./foo` or `-o ./foo` (the report will be in the `./foo/reposense-report` folder)

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--period`, `-p`

**`--period PERIOD`**: Specifies the period of analysis window.
* Parameter: `PERIOD` The period of analysis window, in the format `nd` (for n days) or `nw` (for n weeks). It is used to calculate end date if only start date is specified, or calculate start date if only end date is specified.
* Alias: `-p`
* Example: `--period 30d` or `--period 4w`

<box type="info" seamless>

* If both start date and end date are not specified, the date of generating the report will be taken as the end date.
* May analyze the incorrect date range if used with `--since d1`. The program will throw a warning.
* Cannot be used with both `--since` and `--until`. The program will throw an exception.
</box>

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--portfolio`, `-P`

**`--portfolio`**: Generates an optimised report for code portfolio pages.
* Default: generates the regular report
* Alias: `-P` (uppercase P)
* Example: `--portfolio` or `-P`
* **Portfolio Mode** simplifies the dashboard for showcasing individual contributions (e.g., personal portfolio websites, resume links).

See [Portfolio Mode UI](portfolioMode.md) for details on the differences.
<!-- ------------------------------------------------------------------------------------------------------ -->

### `--repo`, `--repos`, `-r`

**`--repo REPO_LOCATION`**: Specifies which repositories to analyze.
* Parameter: `REPO_LOCATION` A list of URLs or the disk location of the Git repositories to analyze, separated by spaces.
* Alias: `-r`
* Examples:
  * `--repos https://github.com/reposense/RepoSense.git`
  * `--repo https://github.com/reposense/RepoSense.git c:/myRepose/foo/bar`: analyzes the two specified repos (one remote, one local) and generates one report containing details of both.

<box type="info" seamless>

* Cannot be used with `--config`. This flag takes precedence over `--config`.
* If both `--repos` and `--config` are not specified, RepoSense will look for config files in the default `./config` directory.
</box>
<!-- ------------------------------------------------------------------------------------------------------ -->

### `--shallow-cloning`, `-S`

**`--shallow-cloning`**: Clones repositories using Git's shallow cloning functionality, which can significantly reduce the time taken to clone large repositories. However, the flag should not be used for smaller repositories where the `.git` file is smaller than 500 MB, as it would create overhead.
* Default: RepoSense does not clone repositories using Git's shallow cloning functionality.
* Alias: `-S` (uppercase S)
* Example:`--shallow-cloning` or `-S`

<box type="info" seamless>

Cannot be used with `--last-modified-date`. This may result in an incorrect last modified date.
</box>

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--since`, `-s`

<div id="section-since-date">

**`--since START_DATE`**: Specifies the start date for the period to be analyzed.
* Parameter: `START_DATE` The first day of the period to be analyzed (with optional time specification), in one of the following formats:<br>
  ```
  1. DD/MM/YYYY
  2. DD/MM/YYYY'T'HH:mm
  3. DD/MM/YYYY'T'HH:mm:ss
  ```
  Default:
  - If `START_DATE` is not specified, it defaults to one month before the current date at `00:00:00`.
  - If hours/ minutes/ seconds are not provided, each will default to `00`.
* Alias: `-s`
* Example:`--since 21/10/2017T03:09` or `-s 21/10/2017T03:09`

<box type="info" seamless>

* If the start date is not specified, only commits made one month before the end date (if specified) or the date of generating the report, will be captured and analyzed.
* If `d1` is specified as the start date (`--since d1` or `-s d1`), then the program will search for the earliest commit date of all repositories and use that as the start date.
* If `d1` is specified together with `--period`, then the program will warn that the date range being analyzed may be incorrect.
</box>
</div>

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--text`, `-T`

**`--text`**: Refreshes text content (intro.md/title.md, repo-blurbs.md and author-blurbs.md) without reanalyzing repository data.
* Alias: `-T` (uppercase T)

<box type="info" seamless>

* This flag is used to update only the text content (intro.md/title.md, repo-blurbs.md and author-blurbs.md) of the report. The new report will be generated with the existing data from the previous report.
* Ensure that there is an existing valid report generated before using this flag.
* Cannot be used with any other flags except from `--view`, `--assets` and `--config`.
</box>

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--timezone`, `-t`

**`--timezone ZONE_ID`**: Indicates the timezone to be used for the analysis.
* Parameter: `ZONE_ID` The timezone in the format `ZONE_ID[±hh[mm]]`.<br>
  Default: system's default timezone
* Alias: `-t`
* Example:`--timezone UTC+08` or `-t UTC-1030`

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--until`, `-u`

<div id="section-until-date">

**`--until END_DATE`**: Specifies the end date of the analysis period.
* Parameter: `END_DATE` The last date of the period to be analyzed (with optional time specification), in one of the following formats:<br>
  ```
  1. DD/MM/YYYY
  2. DD/MM/YYYY'T'HH:mm
  3. DD/MM/YYYY'T'HH:mm:ss
  ```
Default:
- If `END_DATE` is not specified, it defaults to the current date at `23:59:59`.
- If hours/ minutes/ seconds are not provided, they will default to `23`, `59`, and `59`, respectively.
* Alias: `-u`
* Example:`--until 21/10/2017T01:02:00` or `-u 21/10/2017T01:02:00`

<box type="info" seamless>

Note: If the end date is not specified, the date of generating the report will be taken as the end date.
</box>
</div>

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--version`, `-V`

**`--version`**: Shows the version of RepoSense.
* Alias: `-V` (upper case)

<box type="info" seamless>

Cannot be used with any other flags. This flag takes precedence over all other flags other than `--help`.
</box>
<!-- ------------------------------------------------------------------------------------------------------ -->

### `--view`, `-v`

**`--view [REPORT_FOLDER]`**: Specifies that the report should be opened in the default browser.
* Parameter: `REPORT_FOLDER` Optional. If specified, no analysis will be performed and the report specified by the argument will be opened.<br>
  Default: `./reposense-report`
* Alias: `-v`
* Example:`--view` or `-v`
