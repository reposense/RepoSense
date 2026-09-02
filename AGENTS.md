# RepoSense Agent Guide

RepoSense is a Java 11 contribution-analysis application that generates an interactive Vue 3 report. Read this file before changing the repository, then load the relevant skill from `.agents/skills/` for architecture/design, validation, PR preparation, review, or documentation work.

## Sources of truth

- Treat `build.gradle`, `frontend/package.json`, `.github/workflows/`, and the code as authoritative for commands, dependencies, and CI behavior.
- Use `docs/dg/architecture.md`, `docs/dg/report.md`, `docs/dg/workflow.md`, and `docs/dg/styleGuides.md` for maintained project conventions. `.github/copilot-instructions.md` is a helpful overview, but verify any version- or command-specific statement against the authoritative files.
- Preserve unrelated working-tree changes. Do not create branches, push, open pull requests, publish, or release without explicit authorization.

## Architecture map

The backend flows from CLI/configuration parsing and run-configuration selection, through Git wrappers and the commits/authorship analyzers, into `ReportGenerator`, which writes report JSON. The Vue report loads and validates that JSON in `frontend/src/utils/api.ts`, then routes and shared state distribute it to views and components. `frontend/config-wizard/` is a separate Vue application for producing report configuration.

Key locations: Java production code is `src/main/java/reposense/`; unit tests are `src/test/`; system tests are `src/systemtest/`; report UI is `frontend/src/`; Cypress tests are `frontend/cypress/tests/`; user/developer documentation is `docs/`.

## Working conventions

- Follow Java 11 compatibility and local Java, TypeScript, Vue, SCSS, and MarkBind conventions. For Java, use existing Git wrapper classes and quote path arguments with `StringsUtil.addQuotesForFilePath`; use `LogsManager` for application logging.
- Add or update tests proportionately: unit tests for local logic, system tests for analysis/configuration/report-output behavior, Cypress tests for non-trivial UI behavior or report-data interactions. Update user or developer documentation when externally visible behavior changes.
- Use the platform Gradle wrapper: `./gradlew` on Unix-like systems and `./gradlew.bat` on Windows. Common checks are `test`, `systemtest`, `lintFrontend`, `checkstyleAll`, `environmentalChecks`, and `testFrontend`; select them based on the changed surface rather than running commands blindly.
