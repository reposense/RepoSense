---
name: change-validation
description: Use before handing off RepoSense functional changes to select, run, and report the appropriate Gradle, lint, Cypress, style, and environment checks; also use when a check fails.
---

# Validate RepoSense Changes

Inspect the request and changed paths before choosing checks. Use the platform Gradle wrapper: `./gradlew` on Unix-like systems or `./gradlew.bat` on Windows. Run tests from the repository root unless the command says otherwise.

| Changed surface | Baseline validation |
| --- | --- |
| Java production or unit-test code | `test`; add `checkstyleAll` for Java changes |
| Parser, Git, analysis, report generation, or configuration behavior | `test systemtest`; add targeted fixtures when behavior crosses repository/report boundaries |
| Vue report or config wizard | `lintFrontend`; add `testFrontend` for non-trivial user-facing or data-contract changes |
| Java and frontend behavior together | `test systemtest lintFrontend testFrontend` |
| Documentation only | inspect rendered MarkBind syntax and links when practical; do not claim functional tests ran |
| Any submitted code change | `environmentalChecks` before PR handoff |

`clean build` is a broad build check, not a substitute for selected tests. Use it when release packaging, dependency/build changes, or a requested full build makes it relevant.

## Handle failures

Do not weaken tests, suppress a linter, or delete fixtures merely to obtain a pass. Capture the command, failing test/check, concise error, affected path, and whether the failure plausibly predates the change. Then provide a remediation plan that names the suspected boundary and the smallest next diagnostic or fix. Re-run the failed check after a correction and distinguish completed checks from checks not run.

## Windows environmental-check exception

On Windows, `environmentalChecks` may report `no newline at EOF` from `check-eof-newline.bat` for newly created LF-only text files even when their final byte is `LF`. When this is the only environmental-check failure, `git diff --cached --check` passes, and byte inspection confirms the affected files end in `LF`, record it as a local checker limitation instead of repeatedly rewriting the files. Do not waive actual `CRLF`, trailing-whitespace, other validation failures, or a matching CI failure; CI remains the deciding result.

Read `docs/dg/workflow.md` for test locations and `build.gradle` for task dependencies before changing validation commands.
