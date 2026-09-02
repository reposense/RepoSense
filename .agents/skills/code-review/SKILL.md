---
name: code-review
description: Use when reviewing RepoSense changes for bugs, regressions, architecture-contract violations, unsafe Git/process handling, missing tests, or pull-request readiness.
---

# Review RepoSense Changes

Review the diff and its affected call paths before commenting. Report findings first, ordered by severity, with file and line references; then state test gaps or assumptions. Do not make changes unless the request asks for fixes.

## Review the relevant contracts

| Area | Look for |
| --- | --- |
| Java and Git operations | Java 11 compatibility, existing wrapper reuse, quoted path arguments, meaningful parsing errors, `LogsManager`, resource cleanup, and safe concurrency |
| Analysis and report generation | extractor-analyzer-aggregator consistency, configuration compatibility, deterministic output, error summaries, and valid report JSON |
| Report frontend | Zod/data-loader contract, Vuex/shared state, router/hash behavior, component responsibilities, responsive behavior, and Cypress coverage |
| Configuration wizard | schema/type consistency, validation, generated YAML compatibility, and separate-app build behavior |
| Documentation and delivery | MarkBind/user-guide updates for visible changes, selected checks, PR template requirements, and absence of unrelated artifacts |

Treat `build.gradle`, `frontend/package.json`, source code, and workflow files as the current operational truth. Use the developer guide to understand intent, then verify implementation details in code. A clean diff or passing local unit tests alone is not evidence that report JSON or user-facing behavior remains compatible.

Read `docs/dg/architecture.md`, `docs/dg/report.md`, `docs/dg/styleGuides.md`, and `docs/dg/workflow.md` as needed.
