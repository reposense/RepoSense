---
name: project-design
description: Use when explaining RepoSense architecture, locating component responsibilities, or designing a feature that spans configuration, Java analysis, report JSON, or Vue applications.
---

# Design RepoSense Changes

Use this skill to produce an evidence-based design, not a generic repository summary. Read only the developer-guide sections and code needed for the request; checked-in build and source files override stale overview material.

## Trace the affected path

Start from the user-visible behavior and determine which boundary changes:

| Concern | Read first | Follow into |
| --- | --- | --- |
| CLI or configuration | `parser/`, `model/`, `config/`, `docs/ug/` | `RunConfigurationDecider`, configuration models, reporters |
| Commit or authorship analysis | `commits/` or `authorship/`, `git/` | extractor, analyzer, aggregator, report JSON |
| Generated report contract | `report/`, `frontend/src/types/zod/`, `utils/api.ts` | affected store, router, view, component, Cypress tests |
| Report UI | `frontend/src/views/`, `components/`, `store/` | report JSON schema and existing Cypress coverage |
| Configuration wizard | `frontend/config-wizard/` | its types, store, composables, and component flow |

Describe each relevant component by its responsibility, inputs, outputs, and callers. Confirm the path against code before asserting a dependency.

## Design constraints

Preserve existing configuration compatibility unless the request explicitly permits a break. For backend changes, consider parser diagnostics, Git command wrappers, path quoting, logging, generated JSON compatibility, and concurrency in cloning/analysis. For report changes, keep Zod schemas, data loading, state, URLs/hash behavior, views, and Cypress tests consistent.

Propose the smallest design that covers behavior, errors, compatibility, documentation, and tests. State assumptions and unresolved product choices instead of inventing them.

## Useful references

- Backend: `docs/dg/architecture.md`
- Report UI and data flow: `docs/dg/report.md`
- Development workflow and test locations: `docs/dg/workflow.md`
- Coding conventions: `docs/dg/styleGuides.md`
