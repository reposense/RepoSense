# RepoSense - Developer Guide
Thank you for your interest in contributing to RepoSense!
- [Setting up](#setting-up)
- [Architecture](#architecture)
  - [Parser](#parser)
  - [Git](#git)
  - [AuthorshipReporter](#authorshipreporter)
  - [CommitsReporter](#commitsreporter)
  - [ReportGenerator](#reportgenerator)
  - [System](#system)
  - [Model](#model)
- [HTML Dashboard](#html-dashboard)

## Setting up

### Prerequisites
1. **JDK `1.8.0_60`** or later.
2. **git** on the command line.
3. **findstr** for *Windows*, **grep** for *macOS* or *Linux* on the command line.
 > Check that the tools exist on your OS terminal by typing its name on the terminal and ensure that the terminal does not output messages such as `not found` or `not recognized`.

### Setting up the project in your computer using IntelliJ
1. Fork this repo, and clone the fork to your computer.
2. Open *IntelliJ* (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project dialog first).
3. Set up the correct *JDK* version for *Gradle*.
4. Click `Configure` > `Project Defaults` > `Project Structure`.
5. Click `New…​` and find the directory of the *JDK*.
6. Click `Import Project`.
7. Locate the `build.gradle` file and select it. Click `OK`.
8. Ensure that the selected version of `Gradle JVM` matches our prerequisite.
9. Click `OK` to accept the all the other default settings.

### Verifying the setup
1. Ensure that *Gradle* build without error.
2. Run the tests to ensure they all pass.
   1. On the project root directory, run the command `gradlew clean build functional` and ensure the build is successful. Note that the `clean build` and `functional` command may be required to be run separately.

### Configuring the coding style
This project follows [oss-generic coding standards](https://oss-generic.github.io/process/docs/CodingStandards.html). *IntelliJ’s* default style is mostly compliant with our *Java* coding convention but it uses a different import order from ours. To rectify,

1. Go to `File` > `Settings…`​ (*Windows/Linux*), or `IntelliJ IDEA` > `Preferences…`​ (*macOS*).
2. Select `Editor` > `Code Style` > `Java`.
3. Click on the `Imports` tab to set the order
   * For `Class count to use import with '*'` and `Names count to use static import with '*'`: Set to `999` to prevent IntelliJ from contracting the import statements
   * For `Import Layout`: The order is `import static all other imports`, `import java.*`, `import javax.*`, `import org.*`, `import com.*`, `import all other imports`. Add a ``<blank line>`` between each `import`.

Optionally, you can follow the [Using Checkstyle](UsingCheckstyle.md) document to configure *Intellij* to check style-compliance as you write code.

### Before writing code
1. Do check out our [process guide](../docs/Process.md) before submitting any PR with your changes.

## Architecture

 ![architecture](images/architecture.png)
*Figure 1. Overall architecture of RepoSense*

### Parser
`Parser` contains two classes:
 * [`ArgsParser`](/src/main/java/reposense/parser/ArgsParser.java): Parses the user-supplied command line arguments into a `CliArguments` object.
 * [`CsvParser`](/src/main/java/reposense/parser/CsvParser.java): Parses the the user-supplied CSV config file and produces a list of `RepoConfiguration` for each repository to analyze.


### Git
`Git` contains the wrapper classes for respective *git* commands.
 * [`GitDownloader`](/src/main/java/reposense/git/GitDownloader.java): Wrapper class for `git clone` functionality. Clones the repository from *GitHub* into a temporary folder in order to run the analysis.
 * [`GitChecker`](/src/main/java/reposense/git/GitChecker.java): Wrapper class for `git checkout` functionality. Checks out the repository by branch name or commit hash.


### CommitsReporter
[`CommitsReporter`](/src/main/java/reposense/commits/CommitsReporter.java) is responsible for analyzing and generating a summary of the **commit** history for each repository. `CommitsReporter`,
 1. Uses [`CommitInfoExtractor`](/src/main/java/reposense/commits/CommitInfoExtractor.java) to run the git log command to generate the statistics of each commit made within date range.
 2. Generates a [`CommitInfo`](/src/main/java/reposense/commits/model/CommitInfo.java) for each relevant file, which contains the `infoLine` and `statLine`.
 3. Uses [`CommitInfoAnalyzer`](/src/main/java/reposense/commits/CommitInfoAnalyzer.java) to extract the relevant data from `CommitInfo` into a [`CommitResult`](/src/main/java/reposense/commits/model/CommitResult.java), such as number of line insertions and deletions in the commit.
 4. Uses [`CommitResultAggregator`](/src/main/java/reposense/commits/CommitResultAggregator.java) to aggregate all `CommitResult` into a [`CommitContributionSummary`](/src/main/java/reposense/commits/model/CommitContributionSummary.java).


### AuthorshipReporter
[`AuthorshipReporter`](/src/main/java/reposense/authorship/AuthorshipReporter.java) is responsible for analyzing and generating a summary of every relevant **file** in each repository. `AuthorshipReporter`,
 1. Uses [`FileInfoExtractor`](/src/main/java/reposense/authorship/FileInfoExtractor.java) to traverse the repository to find all relevant files.
 2. Generates a [`FileInfo`](/src/main/java/reposense/authorship/model/FileInfo.java) for each relevant file, which contains the path to the file and a list of [`LineInfo`](/src/main/java/reposense/authorship/model/LineInfo.java) representing each line of the file.
 3. Uses [`FileInfoAnalyzer`](/src/main/java/reposense/authorship/FileInfoAnalyzer.java) to analyze each file, using `git blame` or annotations, and finds the `Author` for each `LineInfo`.
 4. Generates a [`FileResult`](/src/main/java/reposense/authorship/model/FileResult.java) for each file, which consolidates the authorship results into a *Map* of each author's line contribution to the file.
 5. Uses [`FileResultAggregator`](/src/main/java/reposense/authorship/FileResultAggregator.java) to aggregate all `FileResult` into an [`AuthorshipSummary`](/src/main/java/reposense/authorship/model/AuthorshipSummary.java).



### ReportGenerator
[`ReportGenerator`](/src/main/java/reposense/report/ReportGenerator.java),
 1. Uses `GitDownloader` API to download the repository from *GitHub*.
 2. Copies the template files into the designated output directory.
 3. Uses `CommitReporter` and `AuthorshipReporter` to produce the commit and authorship summary respectively.
 4. Generates the `JSON` files needed to generate the `HTML` dashboard.


### System
`System` contains the classes that interact with the Operating System and external processes.
 * [`CommandRunner`](/src/main/java/reposense/system/CommandRunner.java) creates processes that executes commands on the terminal. It consists of many *git* commands.
 * [`LogsManager`](/src/main/java/reposense/system/LogsManager.java) uses the `java.util.logging` package for logging. The `LogsManager` class is used to manage the logging levels and logging destinations. Log messages are output through: `Console` and to a `.log` file.


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

 ![Dashboard](images/dashboard-architeture.png)

The dashboard contains two main parts: data `JSONs`(generated by `ReportGenerator`), and the static Dashboard template.

 * As shown in the graph above, there will be one summary `JSON`(`summary.json`) containing the summary information of all repositories, and one set of repo detail `JSON`(`commits.json` and `authorship.json`) for each repository, containing the contribution information of each specified author to that repository.

  * The dashboard template is a set of `HTML` + `CSS` + `Javascript`, which is located in `template/`. When *RepoSense* is built using *Gradle*, the content will be zipped into `template/` and placed under `src/main/resources/`, which will then be copied into the `.jar` file. Whenever *RepoSense* generates a new report, the template will be unzipped to the target location along with the generated `JSON` data files.
