---
name: docs-maintenance
description: Use when RepoSense behavior, CLI/configuration syntax, report UI, workflows, or developer responsibilities change and MarkBind user or developer documentation may need updating.
---

# Maintain RepoSense Documentation

Determine whether the change affects a user workflow, contributor workflow, configuration contract, report UI, CLI flag, generated output, test procedure, or deployment behavior. Update the narrowest relevant source document under `docs/`; do not edit generated `docs/_site/` output.

| Change | Likely documentation |
| --- | --- |
| CLI, configuration, or report interpretation | `docs/ug/` |
| Architecture, code conventions, tests, or contributor process | `docs/dg/` |
| GitHub Actions, previews, or deployment | `docs/dg/devOpsGuide.md` or `projectManagement.md` |
| Site navigation or shared layout | `docs/_markbind/` |

Follow the repository's MarkBind and Google developer-documentation conventions in `docs/dg/workflow.md` and `docs/dg/styleGuides.md`. Use `tags="production"` or `tags="dev"` only when content genuinely differs between the released and development sites. Keep command examples consistent with `build.gradle` and package scripts, and check links, anchors, code fences, and MarkBind syntax after editing.

When documentation describes behavior changed in code, state both sides of the contract: user-observable outcome and any relevant configuration, limitation, or failure behavior.
