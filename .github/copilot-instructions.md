# GitHub Copilot Instructions for RepoSense

## Project Overview

**RepoSense** is a contribution analysis tool for Git repositories that generates interactive visualizations of programmer activities across single or multiple repositories. It's designed for educators and managers to track the work done in projects and analyze code contributions chronologically.

**Key Purpose:**

- Analyze Git repository contribution patterns
- Visualize programmer activities across time
- Generate interactive HTML reports with commit and authorship insights
- Support configuration-driven analysis for multiple repositories

## Technology Stack

### Backend

- **Language:** Java 11 (compatible up to Java 17)
- **Build System:** Gradle
- **Key Dependencies:**
  - Gson 2.9.0 (JSON serialization/deserialization)
  - JLHttp 2.6 (HTTP server)
  - Argparse4j 0.9.0 (CLI argument parsing)
  - Apache Commons CSV 1.9.0 (CSV parsing)
  - Jackson DataFormat YAML 2.17.0 (YAML parsing)
  - Jansi 2.4.1 (colored console output)
  - JUnit 5.8.2+ (testing)

### Frontend

- **Language:** TypeScript with Vue 3
- **Build Tool:** Vite
- **Package Manager:** npm
- **Key Dependencies:**
  - Vue 3
  - Vue Router
  - Vuex (State management)
  - ESLint and Stylelint (Linting)
  - Cypress (E2E testing)
  - Highlight.js (Code highlighting)
  - Markdown-it (Markdown parsing)

### DevOps & Documentation

- **CI/CD:** GitHub Actions
- **Documentation:** MarkBind
- **Code Coverage:** Codecov
- **Deployment:** Netlify and Surge

## Project Structure

```
RepoSense/
├── src/
│   ├── main/java/reposense/          # Main backend source code
│   │   ├── parser/                    # CLI, CSV, JSON parsing
│   │   ├── git/                       # Git command wrappers
│   │   ├── commits/                   # Commit analysis
│   │   ├── authorship/                # File authorship analysis
│   │   ├── report/                    # Report generation
│   │   ├── system/                    # OS/system interactions
│   │   ├── model/                     # Data structures
│   │   └── RepoSense.java             # Main entry point
│   ├── test/java/reposense/           # Unit tests
│   └── systemtest/java/reposense/     # System tests
├── frontend/
│   ├── src/
│   │   ├── components/                # Vue components
│   │   ├── views/                     # Page-level components
│   │   ├── router/                    # Vue Router configuration
│   │   ├── store/                     # Vuex store
│   │   ├── types/                     # TypeScript types
│   │   ├── utils/                     # Utility functions
│   │   └── main.ts                    # Entry point
│   ├── cypress/                       # E2E tests
│   └── package.json
├── docs/
│   ├── dg/                            # Developer guide
│   ├── ug/                            # User guide
│   └── _markbind/                     # MarkBind configuration
├── config/
│   ├── author-config.csv              # Author configuration template
│   ├── repo-config.csv                # Repository configuration template
│   ├── group-config.csv               # Group configuration template
│   └── report-config.yaml             # Report metadata
├── build.gradle                       # Gradle build configuration
└── .github/                           # GitHub workflows and templates
```

## Architecture Overview

RepoSense follows a modular architecture with clear separation of concerns:

### Backend Pipeline

1. **Parser** (`parser/` package)

   - `ArgsParser`: Parses CLI arguments into `CliArguments` object
   - `CsvParser`: Abstract parser for CSV configuration files (author, group, repo configs)
   - `JsonParser`: Abstract parser for JSON configuration files
   - `StandaloneConfigJsonParser`: Parses `_reposense/config.json`

2. **Git Layer** (`git/` package)

   - Wrapper classes for Git commands (blame, log, clone, branch, etc.)
   - `GitUtil`: Helper functions for Git operations
   - `GitVersion`: Ensures compatible Git version

3. **Analysis Layer**

   - **`CommitsReporter`**: Analyzes commit history
     - Uses `CommitInfoExtractor` → `CommitInfoAnalyzer` → `CommitResultAggregator`
     - Produces `CommitContributionSummary` with daily/weekly contribution stats
   - **`AuthorshipReporter`**: Analyzes file-level authorship
     - Uses `FileInfoExtractor` → `FileInfoAnalyzer` → `FileResultAggregator`
     - Produces `AuthorshipSummary` with line contribution counts

4. **Report Generation** (`report/` package)

   - `ReportGenerator`: Main orchestrator
     - Clones repositories (multi-threaded, default 4 threads)
     - Analyzes repositories (multi-threaded, default CPU core count)
     - Generates JSON files for report visualization

5. **System Layer** (`system/` package)
   - `CommandRunner`: Executes terminal commands
   - `LogsManager`: Logging using java.util.logging
   - `ReportServer`: HTTP server for report viewing

### Frontend Architecture

- **App Structure**: Single Page Application (SPA) with client-side routing
- **State Management**: Vuex for centralized store
- **Routing**: Vue Router for navigation
- **Components**: Reusable Vue components for report visualization
- **Styling**: SCSS/CSS with consistent theming

## Coding Standards & Conventions

### Java Code Style

- Follow [SE-EDU Java Conventions](https://se-education.org/guides/conventions/java/)
- Use Checkstyle for compliance (run `gradlew checkstyleAll`)
- **Javadoc Requirements:**
  - All parameters must be documented (either in description or `@param` tags)
  - If using `@param` tags, use them for ALL parameters
  - All thrown exceptions must have `@throws` tags (excluding test code)
  - Match exception order in `@throws` tags to method signature
  - Ternary operators should be converted from if-else only if result fits on 3 lines max

### JavaScript/TypeScript Style

- Follow [SE-EDU JavaScript Conventions](https://se-education.org/guides/conventions/javascript/)
- Follow [TypeScript ESLint Recommended Rules](https://typescript-eslint.io/rules/)
- Use semicolons as delimiters in TypeScript interfaces and types
- Lint with: `npm run lint` (or `npm run lintfix` for auto-fixes)
- Run `gradlew lintFrontend` from project root

### Vue Components

- Follow [Vue 3 Style Guide](https://vuejs.org/style-guide/) up to Recommended section
- Use `<template>`, `<script>`, `<style>` sections in single-file components
- Scope styles with `scoped` attribute

### Documentation

- Follow [Google Developer Documentation Style Guide](https://developers.google.com/style)
- Update docs when adding/changing features
- Docs are in MarkBind format (see `docs/` folder)

### File/Folder Naming

- Follow [SE-EDU File Naming Conventions](https://se-education.org/guides/conventions/files/)
- Java files: PascalCase (e.g., `GitBlame.java`, `CommitsReporter.java`)
- Test files: `*Test.java` or `*Tests.java`

## Development Workflow

### Setting Up Development Environment

**Prerequisites:**

- JDK 11.0.21+9 to 17 (verify with `java -version`)
- Node.js 20 or latest minor version of 23 (verify with `node -v`)
- Git 2.23+ (verify with `git --version`)
- Recommended IDE: IntelliJ IDEA

**Setup Steps:**

1. Fork and clone the repository
2. Configure IntelliJ for correct JDK (use [this tutorial](https://se-education.org/guides/tutorials/intellijJdk.html))
3. Import as Gradle project (use [this tutorial](https://se-education.org/guides/tutorials/intellijImportGradleProject.html))
4. Run `./gradlew clean build` to verify setup
5. Run `./gradlew test systemtest` to run tests
6. Configure IntelliJ code style (use [this tutorial](https://se-education.org/guides/tutorials/intellijCodeStyle.html))

### Building & Running

**Build Commands:**

```bash
./gradlew clean build           # Full build
./gradlew run                    # Run with config files in config/
./gradlew run -Dargs="FLAGS"    # Run with custom flags
./gradlew run -Dargs="--view"   # Generate report and open in browser
```

**Testing:**

```bash
./gradlew test                   # Unit tests
./gradlew systemtest             # System tests
./gradlew test systemtest        # Both
```

**Code Quality:**

```bash
./gradlew checkstyleAll          # Check Java style (main, test, systemtest)
./gradlew checkstyleMain         # Check main code style only
./gradlew lintFrontend           # Lint frontend code
npm run lintfix                  # Auto-fix frontend lint errors (in frontend/)
./gradlew environmentalChecks    # Check file endings, line endings, whitespace
```

### Frontend Development

**Hot Reload Development:**

1. Generate report data: `./gradlew run`
2. Start hot reload server: `./gradlew hotReloadFrontend`
3. Browser opens automatically with live reloading

**Debugging:**

- Use Vue.js DevTools Chrome extension
- In Chrome: Allow access to file URLs in extension settings
- Open report, press F12, select Vue tab
- Available in development mode

**Testing Frontend:**

```bash
# Run E2E tests with Cypress
./gradlew frontendTest

# Write tests in: frontend/cypress/tests/
# Support file: frontend/cypress/support.js
```

### Code Review Guidelines for Copilot

When generating code, ensure:

- Thread safety when dealing with concurrent operations (cloning, analysis threads)
- Proper exception handling with meaningful error messages
- Null checks for user-provided inputs and configurations
- Use of `StringsUtil::addQuotesForFilePath` for any file path arguments in Git commands
- Consistent logging via `LogsManager` for debugging
- Java 11 compatibility (avoid newer language features)

### Debugging Backend

**IntelliJ Debugger Setup:**

1. Set breakpoints in `RepoSense.java` (main class) at:
   - `ArgsParser.parse(args)`: CLI argument parsing
   - `getRepoConfigurations()`: CSV file parsing
   - `getReportConfigurations()`: JSON file parsing
   - `ReportGenerator.generateReposReport()`: Main analysis
2. Right-click run button → Modify Run Configuration
3. Add flags to Program arguments (e.g., `--since 16/12/2021 --until 18/12/2022`)
4. Use [IntelliJ debugging guide](https://www.jetbrains.com/help/idea/debugging-your-first-java-application.html)

## Key Components & Responsibilities

### Backend Components

**Model Package** (`reposense/model/`)

- `Author`: Git ID with alias support
- `CliArguments`: Parsed command-line arguments
- `RepoConfiguration`: Per-repository analysis configuration
- `FileTypeManager`: File formats and custom groups

**Commits Analysis** (`reposense/commits/`)

- `CommitsReporter`: Orchestrates commit analysis
- `CommitInfoExtractor`: Runs `git log` commands
- `CommitInfoAnalyzer`: Extracts statistics from git output
- `CommitResultAggregator`: Aggregates into summary

**Authorship Analysis** (`reposense/authorship/`)

- `AuthorshipReporter`: Orchestrates authorship analysis
- `FileInfoExtractor`: Traverses repository files
- `FileInfoAnalyzer`: Uses `git blame` or annotations
- `FileResultAggregator`: Aggregates into summary

**Git Operations** (`reposense/git/`)

- Multiple Git command wrappers (clone, blame, log, branch, etc.)
- `GitUtil`: Helper functions
- **Important:** Use `StringsUtil::addQuotesForFilePath` for path arguments

**System Layer** (`reposense/system/`)

- `CommandRunner`: Process execution
- `LogsManager`: Centralized logging
- `ReportServer`: HTTP server for report viewing

### Frontend Components

**Key Directories:**

- `components/`: Reusable UI components (c-ramp, c-segment, etc.)
- `views/`: Page-level components (c-home, c-widget, c-summary, c-authorship, c-zoom)
- `router/`: Vue Router configuration
- `store/`: Vuex state management (accessed via `window.REPOS`)
- `types/`: TypeScript interfaces and types
- `utils/`: Utility functions including `api.ts` (data loader)
- `mixin/`: Vue mixins for shared logic

**Core Files:**

- `main.ts`: Sets up plugins and 3rd party components
- `app.vue`: Renders `router-view`, main application entry point
- `api.ts`: Loads and parses JSON report files (summary.json, commits.json, authorship.json)
- `c-home.vue`: Renders main report (resizer, summary, authorship, zoom)
- `c-summary.vue`: Loads ramp charts from commits.json
- `c-authorship.vue`: Displays file-level authorship from authorship.json
- `c-zoom.vue`: Filters and displays commits from selected ramp range

**Frontend Data Flow:**

- Loads `summary.json` → parses via `api.ts` → populates `window.REPOS`
- Passes data to components via Vuex store
- Components render based on Vuex state and hash parameters for permalinks
- Supports ZIP archive fallback for local file access (security limitation)

## Important Conventions & Best Practices

### Configuration Files

**CSV Configuration Files** (in `config/` by default):

- `author-config.csv`: Maps git emails to author names and display names
- `repo-config.csv`: Specifies repositories to analyze, branches, date ranges
- `group-config.csv`: Groups files by custom categories

**JSON Configuration** (`_reposense/config.json`):

- Alternative to CSV files
- Embedded in repository being analyzed
- Used for per-repository settings

**Report Configuration** (`report-config.yaml`):

- Report title and metadata
- Standalone configuration

### Multi-threading

- **Cloning:** Default 4 threads (configurable with `--cloning-threads`)
- **Analysis:** Default CPU core count (configurable with `--analysis-threads`)
- Design with concurrency in mind

### Path Handling

**Critical:** Always use `StringsUtil::addQuotesForFilePath` when constructing commands with file paths to safely handle special characters in Bash/CMD.

```java
// Example
String command = "git show " + StringsUtil.addQuotesForFilePath(filePath);
```

### Logging

- Use `LogsManager` for all logging
- Supports both console and `.log` file output
- Configurable logging levels

## Common Development Tasks

### Adding a New CLI Flag

1. Add flag constant to `ArgsParser`: `public static final String[] FLAG_NAME = {"--flag-name", "-f"};`
2. Add argument parser setup in `getArgumentParser()` method
3. Add field to `CliArguments` model
4. Parse and use in appropriate reporter or generator class
5. Update docs in `docs/ug/cli.md`
6. Add tests to verify functionality

### Modifying Git Analysis

1. Consider changes to `CommitsReporter` or `AuthorshipReporter`
2. May need to update Git wrapper classes in `git/` package
3. Ensure changes to model classes (in `reposense/model/`)
4. Add/update tests
5. Test with various repository scenarios

### Adding Frontend Feature

1. Create Vue components in `components/` or `views/`
2. Add route in `router/` if needed
3. Update Vuex store if state needed
4. Add TypeScript types in `types/`
5. Lint with `npm run lint` and `gradlew lintFrontend`
6. Add E2E tests in `cypress/tests/`
7. Update documentation

### Writing Tests

**Backend:**

- Unit tests: `src/test/java/reposense/`
- System tests: `src/systemtest/java/reposense/`
- Use JUnit 5 (Jupiter API)

**Frontend:**

- E2E tests: `frontend/cypress/tests/`
- Use Cypress framework

## Important Files to Know

- `src/main/java/reposense/RepoSense.java`: Application entry point
- `src/main/java/reposense/report/ReportGenerator.java`: Main orchestration logic
- `build.gradle`: Build configuration and dependencies
- `frontend/src/main.ts`: Frontend entry point
- `docs/dg/architecture.md`: Detailed architecture documentation
- `docs/dg/styleGuides.md`: Complete style guide requirements

## Developer Guide Reference

Refer to these essential developer guides located in `docs/dg/`:

- **Architecture Guide** (`docs/dg/architecture.md`): Deep dive into backend pipeline (Parser → Git → Analysis → Report Generation)
  - Details about Parser, Git layer, CommitsReporter, AuthorshipReporter, ReportGenerator, System layer
- **HTML Report Guide** (`docs/dg/report.md`): Frontend architecture, Vue components, JSON data structures
  - Vue lifecycle, report architecture, TypeScript/Vue files, JSON report files, data loader (api.ts)
  - Components: c-home, c-widget, c-summary, c-authorship, c-zoom, c-ramp, c-segment
- **Learning Basics** (`docs/dg/learningBasics.md`): Step-by-step backend and frontend learning with debugging tips
  - Backend learning path, debugging setup, hands-on tasks
- **Style Guides** (`docs/dg/styleGuides.md`): Complete Java/TypeScript/Vue coding standards and Javadoc requirements
  - Java conventions, TypeScript formatting, Javadoc requirements, ternary operator rules
- **Workflow Guide** (`docs/dg/workflow.md`): Development process, testing, linting, and code style checks
  - Running the app, debugging frontend, testing frontend with Cypress, code quality checks
- **DevOps Guide** (`docs/dg/devOpsGuide.md`): GitHub Actions CI/CD, Codecov, Surge.sh deployment
  - GitHub Actions workflows, continuous integration, report/documentation previews, GitHub Pages
- **Project Management** (`docs/dg/projectManagement.md`): Release process, hot patching, production website deployment
  - Merging PRs, making releases, deploying production website, hot patching

## External Resources

- **GitHub Repository:** https://github.com/reposense/RepoSense
- **SE-EDU Guides:** https://se-education.org/guides/ (for Java, TypeScript, File naming conventions)

## Technical Guidelines for Code Generation

### Concurrency Patterns

- Use thread pools for parallel operations (cloning: 4 threads, analysis: CPU cores)
- Synchronize access to shared data structures with appropriate locking mechanisms
- Handle `InterruptedException` properly in threaded operations

### Git Command Construction

- Always wrap file paths with `StringsUtil.addQuotesForFilePath()` to handle special characters
- Use existing Git wrapper classes (GitBlame, GitLog, etc.) rather than direct CommandRunner calls
- Validate Git version compatibility using `GitVersion` class

### Error Handling

- Catch and log all exceptions via `LogsManager` with context information
- Throw checked exceptions with descriptive messages for parser errors
- Use `ParseException` for configuration parsing failures
- Never swallow exceptions silently
- Include stack traces in logs for debugging

### Data Structure Patterns

- Immutable model classes for data transfer (Author, CliArguments, RepoConfiguration)
- Use `Map<Author, Integer>` patterns for author contribution aggregations
- Avoid mutable shared state; use builders for complex object construction
- Use streams for functional-style data processing where applicable

### Testing Requirements

- **Unit tests:** `src/test/java/reposense/` - Test individual components in isolation
- **System tests:** `src/systemtest/java/reposense/` - Test end-to-end workflows with real repositories
- **Frontend E2E tests:** `frontend/cypress/tests/` - Test user interactions and report rendering
- Use JUnit 5 (Jupiter API) with appropriate assertions and parametrized tests
- Mock external Git operations in unit tests to avoid slow I/O
- All tests run via GitHub Actions CI/CD on Ubuntu, macOS, and Windows
- Frontend tests run on separate Cypress job for parallel execution

## Implementation Patterns by Component

### When Working on Backend (Git Analysis, Reporting)

- Follow the Reporter pattern: Extractor → Analyzer → Aggregator
- Use `CliArguments` to access all command-line settings
- Create immutable model objects to pass data between layers
- Handle batch operations with proper resource cleanup
- Leverage existing utilities in `GitUtil` and `StringsUtil`

**Example flow:**

```java
// Extract data from git commands
List<CommitInfo> infos = extractor.extract(repoPath, dateRange);
// Analyze individual items
List<CommitResult> results = infos.stream()
    .map(analyzer::analyze)
    .collect(toList());
// Aggregate results
CommitContributionSummary summary = aggregator.aggregate(results);
```

### When Adding CLI Arguments

1. Add constant flags array to `ArgsParser`
2. Register with `ArgumentParser` in `getArgumentParser()` method
3. Add field to `CliArguments` model class
4. Access via `CliArguments` object throughout application
5. Validate argument values in the parser, not in consumers

### When Modifying Git Operations

- Extend existing Git wrapper classes or create new ones in `git/` package
- Use Git output parsing utilities consistently
- Handle both Windows and Unix path formats
- Test with edge cases: special characters, spaces, unicode in paths

### When Working on Frontend (Vue Components, Reports)

- Use TypeScript for all new code (strict type checking)
- Store report data in Vuex when shared between multiple components
- Use computed properties for derived state
- Add proper TypeScript types in `types/` directory
- Implement responsive design for mobile and desktop views

### When Adding New Report Section

1. Create Vue component in `components/` or `views/`
2. Define TypeScript interfaces in `types/`
3. Add route to Vue Router if full page
4. Add state management to Vuex store if needed
5. Add E2E test in `cypress/tests/`

### When Modifying Configuration

- Support both CSV and JSON formats for consistency
- Validate configuration values in parser with clear error messages
- Update `RepoConfiguration` or `AuthorConfiguration` models
- Add configuration examples in documentation
- Consider backwards compatibility with older config versions

## CI/CD and DevOps Practices

### GitHub Actions Workflows

- **Integration (`integration.yml`):** Runs unit tests, system tests, and frontend tests on JDK 11 (Ubuntu/macOS/Windows)
- **Frontend Tests (`cypress`):** Separate Cypress job for parallel E2E test execution
- **Surge.sh Previews:** Automatically deploys report and docs previews for pull requests
- **GitHub Pages (`gh-pages.yml`):** Auto-deploys MarkBind docs to GitHub Pages on master commits
- **Stale PRs (`stale.yml`):** Auto-closes inactive pull requests

### Pre-PR Requirements

1. **Code Quality Checks:**

   - `gradlew checkstyleAll` must pass
   - `gradlew lintFrontend` must pass
   - `gradlew environmentalChecks` (file endings, line endings, whitespace)

2. **Testing:**

   - `gradlew test systemtest` - All backend tests must pass
   - `gradlew frontendTest` - Frontend tests must pass
   - Coverage tracked via Codecov

3. **Build Validation:**
   - `gradlew clean build` must succeed
   - JAR generation: `gradlew shadowJar`

### Deployment Process

- **Dev Website:** Auto-deployed to https://reposense.github.io/RepoSense on master commits
- **Production Website:** Manual deployment to https://reposense.org after release
  - Requires `markbind build` and `markbind deploy` on release branch
  - Must update CNAME file in release branch
- **Report Previews:** Surge.sh hosts pull request preview links
- **Release Branch:** Uses semantic versioning (MAJOR.MINOR for features, MAJOR.MINOR.PATCH for fixes)

## Release and Project Management

### Making a Release

**Prerequisites:**

- JDK 11 (NOT other major versions like 12 or 13)
- `JAVA_HOME` environment variable correctly set
- Local and upstream `release` branch synced with `master`

**Release Steps:**

1. Use semantic versioning: `MAJOR.MINOR` for features, `MAJOR.MINOR.PATCH` for bug fixes
2. Generate JAR: Run `gradlew shadowJar` on release branch
3. Create release on GitHub with JAR file attached
4. Deploy production website: `cd docs` → `markbind build` → `markbind deploy`

### Hot Patching After Release

For critical bugs found in production:

1. Switch to `release` branch
2. Implement fixes and create PR to upstream `release` branch
3. After merging, release new version
4. Merge `release` branch back into `master` with separate PR

---

**Last Updated:** January 2025
**Project:** RepoSense - Contribution Analysis Tool
