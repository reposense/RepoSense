# RepoSense - Developer Guide
Thank you for your interest in contributing to RepoSense!
- [Setting up](#setting-up)
  - [Prerequisites](#prerequisites)
  - [Setting up the project in your computer using IntelliJ](#setting-up-the-project-in-your-computer-using-intellij)
  - [Verifying the setup](#verifying-the-setup)
  - [Configuring the coding style](#configuring-the-coding-style)
- [Architecture](#architecture)
  - [Parser](#parserconfigparser)
  - [Git](#gitgitdownloader)
  - [CommitsReporter](#commitsreporter)
  - [AuthorshipReporter](#authorshipreporter)
  - [ReportGenerator](#reportgeneratormain)
  - [System](#system)
  - [Model](#model)
- [HTML Dashboard](#html-dashboard)

## Setting up

### Prerequisites
1. **JDK `1.8.0_60`** or later.
1. **git `2.14`** or later on the command line.
 > Type `git --version` on your OS terminal and ensure that you have the correct version of **git**.

### Setting up the project in your computer using IntelliJ
1. Fork this repo, and clone the fork to your computer.
1. Open *IntelliJ* (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project dialog first).
1. Set up the correct *JDK* version for *Gradle*.
    1. Click `Configure` > `Project Defaults` > `Project Structure`.
    1. Click `New…​` and find the directory of the *JDK*.
1. Click `Import Project`.
1. Locate the `build.gradle` file and select it. Click `OK`.
1. Ensure that the selected version of `Gradle JVM` matches our prerequisite.
1. Click `OK` to accept the all the other default settings.

### Verifying the setup
1. Ensure that *Gradle* builds without error by running the command `gradlew clean build`, and ensure that it finishs with a `BUILD SUCCESSFUL` message.
1. Run the tests to ensure that they all pass by running the command `gradlew test functional`, and ensure that it finishs with a `BUILD SUCCESSFUL` message.
  > Ensure that you are on the project root directory when using the `gradlew` commands.

### Configuring the coding style
This project follows [oss-generic coding standards](https://oss-generic.github.io/process/docs/CodingStandards.html). *IntelliJ’s* default style is mostly compliant with our *Java* coding convention but it uses a different import order from ours. To rectify,

1. Go to `File` > `Settings…`​ (*Windows/Linux*), or `IntelliJ IDEA` > `Preferences…`​ (*macOS*).
1. Select `Editor` > `Code Style` > `Java`.
1. Click on the `Imports` tab to set the order
   * For `Class count to use import with '*'` and `Names count to use static import with '*'`: Set to `999` to prevent IntelliJ from contracting the import statements
   * For `Import Layout`, follow this image below:
   ![import-order](images/import-order.png)

Optionally, you can follow the [Using Checkstyle](UsingCheckstyle.md) document to configure *Intellij* to check style-compliance as you write code.

### Before writing code
1. Do check out our [process guide](../docs/Process.md) before submitting any PR with your changes.

## Architecture

 ![architecture](images/architecture.png)
*Figure 1. Overall architecture of RepoSense*

### Parser(ConfigParser)
`Parser` contains two classes:
 * [`ArgsParser`](/src/main/java/reposense/parser/ArgsParser.java): Parses the user-supplied command line arguments into a `CliArguments` object.
 * [`CsvParser`](/src/main/java/reposense/parser/CsvParser.java): Parses the the user-supplied CSV config file into a list of `RepoConfiguration` for each repository to analyze.


### Git(GitDownloader)
`Git` contains the wrapper classes for respective *git* commands.
 * [`GitDownloader`](/src/main/java/reposense/git/GitDownloader.java): Wrapper class for `git clone` functionality. Clones the repository from *GitHub* into a temporary folder in order to run the analysis.
 * [`GitChecker`](/src/main/java/reposense/git/GitChecker.java): Wrapper class for `git checkout` functionality. Checks out the repository by branch name or commit hash.


### CommitsReporter
[`CommitsReporter`](/src/main/java/reposense/commits/CommitsReporter.java) is responsible for analyzing the **commit** history and generating a [`CommitContributionSummary`](/src/main/java/reposense/commits/model/CommitContributionSummary.java) for each repository. `CommitContributionSummary` contains information such as each author's daily and weekly contribution and the variance of their contribution. `CommitsReporter`,
 1. uses [`CommitInfoExtractor`](/src/main/java/reposense/commits/CommitInfoExtractor.java) to run the `git log` command, which generates the statistics of each commit made within date range.
 1. generates a [`CommitInfo`](/src/main/java/reposense/commits/model/CommitInfo.java) for each commit, which contains the `infoLine` and `statLine`.
 1. uses [`CommitInfoAnalyzer`](/src/main/java/reposense/commits/CommitInfoAnalyzer.java) to extract the relevant data from `CommitInfo` into a [`CommitResult`](/src/main/java/reposense/commits/model/CommitResult.java), such as the number of line insertions and deletions in the commit and the author of the commit.
 1. uses [`CommitResultAggregator`](/src/main/java/reposense/commits/CommitResultAggregator.java) to aggregate all `CommitResult` into a [`CommitContributionSummary`](/src/main/java/reposense/commits/model/CommitContributionSummary.java).


### AuthorshipReporter
[`AuthorshipReporter`](/src/main/java/reposense/authorship/AuthorshipReporter.java) is responsible for analyzing the white listed **files**, traces the original author for each line of text/code, and generating an [`AuthorshipSummary`](/src/main/java/reposense/authorship/model/AuthorshipSummary.java) for each repository. `AuthorshipSummary` contains the analysis results of the white listed files and the amount of line contributions each author made. `AuthorshipReporter`,
 1. uses [`FileInfoExtractor`](/src/main/java/reposense/authorship/FileInfoExtractor.java) to traverse the repository to find all relevant files.
 1. generates a [`FileInfo`](/src/main/java/reposense/authorship/model/FileInfo.java) for each relevant file, which contains the path to the file and a list of [`LineInfo`](/src/main/java/reposense/authorship/model/LineInfo.java) representing each line of the file.
 1. uses [`FileInfoAnalyzer`](/src/main/java/reposense/authorship/FileInfoAnalyzer.java) to analyze each file, using `git blame` or annotations, and finds the `Author` for each `LineInfo`.
 1. generates a [`FileResult`](/src/main/java/reposense/authorship/model/FileResult.java) for each file, which consolidates the authorship results into a *Map* of each author's line contribution to the file.
 1. uses [`FileResultAggregator`](/src/main/java/reposense/authorship/FileResultAggregator.java) to aggregate all `FileResult` into an `AuthorshipSummary`.


### ReportGenerator(Main)
[`ReportGenerator`](/src/main/java/reposense/report/ReportGenerator.java),
 1. uses `GitDownloader` API to download the repository from *GitHub*.
 1. copies the template files into the designated output directory.
 1. uses `CommitReporter` and `AuthorshipReporter` to produce the commit and authorship summary respectively.
 1. generates the `JSON` files needed to generate the `HTML` dashboard.


### System
`System` contains the classes that interact with the Operating System and external processes.
 * [`CommandRunner`](/src/main/java/reposense/system/CommandRunner.java) creates processes that executes commands on the terminal. It consists of many *git* commands.
 * [`LogsManager`](/src/main/java/reposense/system/LogsManager.java) uses the `java.util.logging` package for logging. The `LogsManager` class is used to manage the logging levels and logging destinations. Log messages are output through: `Console` and to a `.log` file.
 * [`DashboardServer`](/src/main/java/reposense/system/DashboardServer.java) starts a server to display the dashboard on the browser. It depends on the `net.freeutils.httpserver` package.


### Model
`Model` holds the data structures that are commonly used by the different aspects of *RepoSense*.
 * [`Author`](/src/main/java/reposense/model/Author.java) stores the `GitHub ID` of an author. Any contributions or commits made by the author, using his/her `GitHub ID` or aliases, will be attributed to the same `Author` object. It is used by `AuthorshipReporter` and `CommitsReporter` to attribute the commit and file contributions to the respective authors.
 * [`CliArguments`](/src/main/java/reposense/model/CliArguments.java) stores the parsed command line arguments supplied by the user. It contains the configuration settings such as the CSV config file to read from, the directory to output the report to, and date range of commits to analyze. These configuration settings are passed into `RepoConfiguration`.
 * [`RepoConfiguration`](/src/main/java/reposense/model/RepoConfiguration.java) stores the configuration information from the CSV config file for a single repository, which are the repository's orgarization, name, branch, list of authors to analyse, date range to analyze commits and files from `CliArguments`.
 These configuration information are used by:
    - `GitDownloader` to determine which repository to download from and which branch to check out to.
    - `AuthorshipReporter` and `CommitsReporter` to determine the range of commits and files to analyze.
    - `ReportGenerator` to determine the directory to output the report.


## HTML Dashboard
The source files for the dashboard is located in `frontend/src` and is built by [spuild](https://github.com/ongspxm/spuild2) before being packaged into the JAR file to be extracted as part of the report.

### Javascript Files
- **api.js** - loading and parsing of the dashboard content
- **main.js** - main controller that handles the loading of the content into different modules
- **v_summary.js** - module that supports the ramp chart view
- **v_authorship.js** - module that supports the authorship view

### Overall Structure
The main Vue object (`window.app`) is responsible for the loading of the dashboard (through `summary.json`). Its `repos` attribute is tied to the global `window.REPOS`, and is passed into the various other modules when the information is needed.

`window.app` is broken down into two main parts, the summary view and the tabs view. The basic design is to have a summary view (with the ramp charts) as the main dashboard, and other modules display additional information in the tabbed interface which is displayed on the right.

**overall architecture**

![overall architecture](images/architecture.png)

### Loading of dashboard information
When the dashboard is first loaded, the main vue object tries to retreive the `summary.json` file in order to determine the right `commits.json` files to load into memory. `api.js` handles the loading of the file, and approriately gets the relevant file information, depending on whether the network files is available or a report archive have to be used.

Once the relevant `commit.json` files hash been loaded, all the repo information will be passed into `v_summary` to be loaded in the summary view as the releveant ramp charts.

### Activating additional view modules
Most activity or actions should happen within the module itself, but in the case where there is a need to control the tab view from the module, an event is emitted from the module to main vue object (`window.app`), which then handles the data receive and pass it along to the relevant modules.

An additional thing to note is the event handler should handle the switching of the tabs as well, activating the display of the rights tabs just that the relevant tabs are being displayed at all times.

### Hash link
Other than the global main vue object, another global variable we have is the `window.hashParams`. This object is reponsible for generating the relevant permalink for a specific view of the summary module for the dashboard.
