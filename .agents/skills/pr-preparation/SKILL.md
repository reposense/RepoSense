---
name: pr-preparation
description: Use when preparing a RepoSense contribution or pull-request description, including issue references, test evidence, proposed squash-merge commit messages, and upstream submission checks.
---

# Prepare a RepoSense Pull Request

Prepare the change for review without assuming permission to push, open a pull request, merge, or modify an issue. Read `.github/PULL_REQUEST_TEMPLATE.md`, `docs/dg/workflow.md`, and the diff before drafting anything.

## PR-ready result

- Use `Fixes #<number>` only when the change fully resolves that issue; use `Part of #<number>` for partial work. Simple documentation fixes or clearly specified tasks do not require a new issue.
- Draft the template's proposed commit message around current behavior and the change's outcome. Keep it meaningful when squashed, and wrap lines at 72 characters as the template requests.
- Summarize behavior, scope, significant design decisions, documentation impact, and exact validation commands with their results. Identify checks not run and why.
- Check that the diff contains no debugging artifacts or unrelated generated files. New unpushed commits should be cleaned up; the project squashes merged PRs, so elaborate commit organization is not required.
- Default to a contributor fork and its branch targeting the appropriate upstream branch. Release and hot-patch work have separate branch rules in `docs/dg/projectManagement.md`.

For functional changes, use `change-validation` before declaring the PR ready. Never fabricate test results, issue status, reviewer approval, or preview URLs.
