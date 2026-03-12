# Configuration Wizard Feature Design

**Status:** In Progress
**Last Updated:** 9 March 2026
**Feature Type:** Web-Based GUI Tool

---

## Table of Contents

- [Overview](#overview)
- [Motivation](#motivation)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [User Experience](#user-experience)
- [Input Validation](#input-validation)
- [Field Support](#field-support)
- [Implementation Plan](#implementation-plan)
- [Future Enhancements](#future-enhancements)

---

## Overview

The Configuration Wizard is an interactive web-based GUI tool that guides users through creating a RepoSense `report-config.yaml` file via intuitive forms and visual feedback, instead of manual file editing.

**Invocation:**

```bash
java -jar RepoSense.jar --config-wizard
```

This command launches a local web server and automatically opens the wizard in the user's default browser at `http://localhost:9000/config-wizard`.

**Output:** A single `report-config.yaml` file written to `./generated-configs/`, which encodes all repository, branch, author, and group configuration in a unified hierarchical structure.

**Goal:** Provide an intuitive, visually-guided experience that lowers the barrier to entry for new users while supporting advanced configurations for power users.

---

## Motivation

### Current Pain Points

1. **Steep Learning Curve**
   - Users must understand exact schema, field names, and formatting before generating their first report
   - Need to reference documentation constantly for valid values and syntax

2. **Error-Prone Manual Editing**
   - YAML indentation errors
   - Typos in field names or values
   - Missing required fields discovered only at runtime

3. **Time-Consuming Setup**
   - Repetitive data entry for multiple repositories/authors
   - No immediate validation feedback
   - Trial-and-error cycles when configs fail to parse

4. **Poor Discoverability**
   - Users don't know what configuration options are available
   - Advanced features (per-branch author lists, group globs, blurbs) are hidden in docs

### Target Users

- **New users:** Getting started with their first RepoSense report
- **Students:** Creating code portfolios for coursework
- **Educators:** Setting up configs for class projects
- **Teams:** Standardizing configuration across multiple repos

---

## Technology Stack

### Web-Based GUI Implementation

**Why Web GUI over CLI/TUI:**

- ✅ **Superior UX:** Visual feedback, tooltips, inline help, preview panels
- ✅ **Better discoverability:** See all options at a glance, no need to remember commands
- ✅ **Richer interactions:** Multi-select, expandable cards, syntax highlighting
- ✅ **Accessibility:** Works for non-technical users uncomfortable with command line
- ✅ **Validation feedback:** Real-time visual indicators (red/green borders, inline errors)
- ✅ **Configuration preview:** Live preview of generated YAML before saving
- ✅ **Reuses existing tech:** Leverages RepoSense's existing Vue.js frontend stack

**Backend (Java - Existing Dependencies):**

- **`argparse4j`** (v0.9.0): CLI argument parsing for `--config-wizard` flag
- **`net.freeutils.jlhttp`** (v2.6): Already in RepoSense for report server, reused for wizard server
- **`jackson-dataformat-yaml`** (v2.17.0): YAML file generation
- **`gson`** (v2.9.0): JSON API communication between frontend and backend

**Frontend (Vue.js - Existing Stack):**

- **Vue 3**: Already used in RepoSense frontend
- **Vite**: Already configured for dev/build
- **TypeScript**: For type-safe form validation
- **Existing UI components**: Can reuse from `frontend/src/components/`

**Architecture:** Embedded web server (similar to RepoSense's report server) + Vue.js SPA

**UI Layout Strategy:** Reuse RepoSense's existing two-pane layout pattern (`c-resizer` component) with:

- **Left pane:** Configuration forms (wizard steps)
- **Right pane:** Live `report-config.yaml` preview
- **Resizable divider:** Users can adjust pane sizes
- **Collapsible right pane:** Close preview to focus on form fields

---

## Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    User's Browser                           │
│  ┌─────────────────────────────────────────────────────┐   │
│  │         Vue.js Config Wizard SPA                     │   │
│  │  - 4-step form wizard                               │   │
│  │  - Real-time validation                             │   │
│  │  - Live report-config.yaml preview                  │   │
│  └─────────────────┬───────────────────────────────────┘   │
└────────────────────┼───────────────────────────────────────┘
                     │ HTTP REST API
                     │ (localhost:9000)
                     │
┌────────────────────▼───────────────────────────────────────┐
│            Embedded Java Web Server                        │
│  ┌─────────────────────────────────────────────────────┐  │
│  │  ConfigWizardServer                                 │  │
│  │  - Serves Vue.js static assets                      │  │
│  │  - REST API endpoints for validation                │  │
│  │  - File I/O for writing report-config.yaml          │  │
│  └─────────────────┬───────────────────────────────────┘  │
└────────────────────┼───────────────────────────────────────┘
                     │
          ┌──────────┴──────────┐
          │                     │
    ┌─────▼──────┐      ┌──────▼──────┐
    │ Validators │      │ File Writer │
    │  (Reuse    │      │   (YAML)    │
    │  Parsers)  │      └─────────────┘
    └────────────┘
```

### YAML Output Structure

The wizard produces a single `report-config.yaml` file. All repo, branch, author, and group data is nested within this one file:

```yaml
title: RepoSense Report
repos:
  - repo: https://github.com/user/repo.git
    groups:
      - group-name: frontend
        globs:
          - src/frontend/**
      - group-name: backend
        globs:
          - src/main/**
    branches:
      - branch: main
        blurb: Main development branch
        ignore-glob-list:
          - node_modules/**
          - build/**
        ignore-authors-list:
          - bot-user
        file-size-limit: 500000
        authors:
          - author-git-host-id: alice
            author-emails:
              - alice@example.com
            author-display-name: Alice Thompson
            author-git-author-name:
              - alice
              - AT
```

### Directory Structure

**Backend (Java):**

```
src/main/java/reposense/wizard/
├── ConfigWizardServer.java    // Embedded web server, REST API endpoints
└── ConfigFileWriter.java      // Generates and writes report-config.yaml
```

**Frontend (Vue.js) — Separate App:**

```
frontend/
├── src/                       // Existing report viewer code (unchanged)
│
├── config-wizard/             // Wizard app (separate entry point)
│   ├── index.html             // Wizard HTML entry point
│   ├── App.vue                // Root wizard component, two-pane layout, YAML preview
│   ├── main.ts                // Wizard entry point
│   ├── store.ts               // Reactive wizard state (YAML-shaped)
│   │
│   └── components/
│       ├── WizardStep.vue     // Shared step wrapper (nav buttons, title)
│       ├── ReportStep.vue     // Step 1: Report settings (title)
│       ├── ReposStep.vue      // Step 2: Repos, branches, and authors
│       ├── GroupsStep.vue     // Step 3: Groups (per repo)
│       └── ReviewStep.vue     // Step 4: Review & generate
│
└── vite.config.mts            // Multi-page build (main + config-wizard entry points)
```

**Build Output:**

```
frontend/build/
├── index.html                 // Report viewer entry
├── config-wizard/
│   └── index.html             // Wizard entry (served at /config-wizard)
└── assets/
    ├── config-wizard-[hash].js
    ├── config-wizard-[hash].css
    └── vue-vendor-[hash].js
```

### State Model

The wizard store mirrors the YAML structure directly, so assembling the final payload requires no remapping:

```typescript
// store.ts
interface Author {
  'author-git-host-id': string;
  'author-display-name': string;
  'author-emails': string[];
  'author-git-author-name': string[];
}

interface Branch {
  branch: string;
  blurb: string;
  'ignore-glob-list': string[];
  'ignore-authors-list': string[];
  'file-size-limit': number | null;
  authors: Author[];
}

interface Group {
  'group-name': string;
  globs: string[];
}

interface Repo {
  repo: string;
  groups: Group[];
  branches: Branch[];
}

interface WizardConfig {
  title: string;
  repos: Repo[];
}
```

### Integration Points

**ArgsParser (`--config-wizard` flag):**

```java
public static final String[] CONFIG_WIZARD_FLAGS = new String[] { "--config-wizard" };

// In parse() method:
parser.addArgument(CONFIG_WIZARD_FLAGS)
    .help("Launch web-based configuration wizard")
    .action(Arguments.storeTrue());
```

**RepoSense.main() routing:**

```java
if (cliArguments.isConfigWizard()) {
    ConfigWizardServer.startWizard(SERVER_PORT_NUMBER);
    return;
}
// Normal execution flow...
```

**REST API Endpoints:**

| Endpoint               | Method | Purpose                          | Request                    | Response                                        |
| ---------------------- | ------ | -------------------------------- | -------------------------- | ----------------------------------------------- |
| `/api/validate`        | POST   | Validate a repo location         | `{ location }`             | `{ valid: boolean, error?: string }`            |
| `/api/generate`        | POST   | Write `report-config.yaml`       | `WizardConfig` (JSON)      | `{ success: boolean, path: string }`            |
| `/api/validate-glob`   | POST   | Validate glob pattern syntax     | `{ pattern }`              | `{ valid: boolean, error?: string }`            |
| `/api/validate-date`   | POST   | Validate date format/logic       | `{ since?, until? }`       | `{ valid: boolean, error?: string }`            |
| `/api/preview`         | POST   | Generate YAML preview string     | `WizardConfig` (JSON)      | `{ yaml: string }`                              |
| `/api/validate-config` | POST   | Parse config via RepoSense parser | `WizardConfig` (JSON)      | `{ valid: boolean, error?: string }`            |

### Reusing Existing Parsers for Validation

**Key Principle:** Don't reimplement validation logic — call existing parsers.

```java
class ConfigValidator {
    // Validate repo location by calling the actual RepoSense model
    static ValidationResult validateRepoLocation(String location) {
        try {
            new RepoLocation(location);
            return ValidationResult.success();
        } catch (Exception e) {
            return ValidationResult.error(e.getMessage());
        }
    }

    // Reuse existing date validation argument types
    static boolean validateSinceDate(String date) {
        try {
            new SinceDateArgumentType().convert(null, null, date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
```

---

## User Experience

### Wizard Step Flow (4 Steps)

```
Step 1: Report Settings
  └─ Report title

Step 2: Repos & Branches
  └─ For each repo:
      ├─ Repo URL (validated)
      └─ For each branch:
          ├─ Branch name
          ├─ Blurb (optional)
          ├─ Ignore glob list
          ├─ Ignore authors list
          ├─ File size limit
          └─ Authors:
              ├─ Git Host ID
              ├─ Emails
              ├─ Display name
              ├─ Git author names
              └─ Ignore glob list

Step 3: Groups
  └─ For each repo (from Step 2):
      └─ For each group:
          ├─ Group name
          └─ Glob patterns

Step 4: Review & Generate
  └─ YAML preview
  └─ Validation status
  └─ Generate button → writes report-config.yaml
```

### Two-Pane Layout

```
┌──────────────────────────────────────────────────────────────────────────────┐
│ RepoSense Configuration Wizard                                  [Step 2 of 4] │
├─────────────────────────────────────────────────────┬────────────────────────┤
│                                                     │                        │
│  🗂 Repos & Branches                                │  📄 Preview            │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━   │  ━━━━━━━━━━━━━━━━━━━   │
│                                                     │                        │
│  ┌─ Repository #1 ───────────────────────────────┐ │  report-config.yaml    │
│  │                                               │ │  ━━━━━━━━━━━━━━━━━━    │
│  │  Repository URL *                             │ │                        │
│  │  ┌─────────────────────────────────────────┐ │ │  title: RepoSense ...  │
│  │  │ https://github.com/user/repo.git        │ │ │  repos:                │
│  │  └─────────────────────────────────────────┘ │ │    - repo: https://... │
│  │  ✓ Repository accessible                      │ │      branches:         │
│  │                                               │ │        - branch: main  │
│  │  ┌─ Branch: main ────────────────────────┐   │ │          authors: ...  │
│  │  │  Blurb (optional)       [____________]│   │ │                        │
│  │  │  Ignore Glob List       [+ Add]       │   │ │               [Copy]   │
│  │  │  ┌───────────────────────────────┐   │   │ │                        │
│  │  │  │ node_modules/**          [×]  │   │   │ │  ℹ Updates in          │
│  │  │  └───────────────────────────────┘   │   │ │    real-time           │
│  │  │                                       │   │ │                        │
│  │  │  Authors                 [+ Add]      │   │ │                        │
│  │  │  ┌─ alice ────────────────────────┐  │   │ │                        │
│  │  │  │ Display Name  [Alice Thompson] │  │   │ │                        │
│  │  │  │ Emails        [alice@...]      │  │   │ │                        │
│  │  │  │ Git Names     [alice; AT]      │  │   │ │                        │
│  │  │  └────────────────────────────────┘  │   │ │                        │
│  │  └───────────────────────────────────────┘   │ │                        │
│  │                              [+ Add Branch]  │ │                        │
│  └───────────────────────────────────────────────┘ │                        │
│                            [+ Add Repository]       │                        │
│                                                     │                        │
│  ┌───────────┐                    ┌──────────────┐  │                        │
│  │ ← Back    │                    │ Next Step →  │  │                        │
│  └───────────┘                    └──────────────┘  │                        │
│                                                     │                        │
├─────────────────────────────────────────────────────┴────────────────────────┤
│                             ← Drag to resize →                               │
└──────────────────────────────────────────────────────────────────────────────┘
```

**Key features:**
- **Left pane:** Step-by-step forms with expandable repo/branch/author cards
- **Right pane:** Live `report-config.yaml` preview, updates as user types
- **Resizable divider:** Using the existing `c-resizer` component
- **Collapsible preview:** Click `[⊗]` to close preview and maximise form area

### Step 1: Report Settings

```
┌────────────────────────────────────────────────────────────┐
│  RepoSense Configuration Wizard               [Step 1 of 4]│
├────────────────────────────────────────────────────────────┤
│                                                             │
│  📋 Report Settings                                         │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  │
│                                                             │
│  Report Title                                               │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ RepoSense Report                                    │  │
│  └─────────────────────────────────────────────────────┘  │
│  ℹ Displayed as the heading of the generated report        │
│                                                             │
│                                      ┌──────────────────┐  │
│                                      │   Next Step →    │  │
│                                      └──────────────────┘  │
└────────────────────────────────────────────────────────────┘
```

### Step 2: Repos & Branches

The main step. Each repo card is expandable and contains one or more branch sub-cards. Each branch card contains author sub-cards.

```
┌────────────────────────────────────────────────────────────┐
│  RepoSense Configuration Wizard               [Step 2 of 4]│
├────────────────────────────────────────────────────────────┤
│  🗂 Repos & Branches                                        │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  │
│                                                             │
│  ┌─ Repository #1 ────────────────────────────[Remove]────┐ │
│  │  URL * [https://github.com/user/repo.git]  ✓ Valid    │ │
│  │                                                        │ │
│  │  ┌─ Branch: main ──────────────────────────────────┐  │ │
│  │  │  Blurb           [________________________]      │  │ │
│  │  │  Ignore Glob List         [node_modules/** ×]   │  │ │
│  │  │                           [+ Add Pattern]        │  │ │
│  │  │  Ignore Authors List      [bot-user ×]           │  │ │
│  │  │                           [+ Add Author]         │  │ │
│  │  │  File Size Limit (bytes)  [500000      ]         │  │ │
│  │  │                                                  │  │ │
│  │  │  Authors                                         │  │ │
│  │  │  ┌─ alice ────────────────────────────[Remove]─┐ │  │ │
│  │  │  │ Git Host ID   [alice              ]         │ │  │ │
│  │  │  │ Display Name  [Alice Thompson     ]         │ │  │ │
│  │  │  │ Emails        [alice@example.com ×]  [+ Add]│ │  │ │
│  │  │  │ Git Names     [alice ×] [AT ×]       [+ Add]│ │  │ │
│  │  │  │ Ignore Globs  [test/**  ×]            [+ Add]│ │  │ │
│  │  │  └──────────────────────────────────────────────┘ │  │ │
│  │  │                              [+ Add Author]        │  │ │
│  │  └────────────────────────────────────────────────────┘  │ │
│  │                                [+ Add Branch]            │ │
│  └────────────────────────────────────────────────────────── ┘ │
│                                   [+ Add Repository]        │
│                                                             │
│  ┌────────────┐                        ┌────────────────┐  │
│  │ ← Back     │                        │  Next Step →   │  │
│  └────────────┘                        └────────────────┘  │
└────────────────────────────────────────────────────────────┘
```

### Step 3: Groups

Groups are configured per repo. The repos from Step 2 are listed; each has its own group cards.

```
┌────────────────────────────────────────────────────────────┐
│  RepoSense Configuration Wizard               [Step 3 of 4]│
├────────────────────────────────────────────────────────────┤
│  📁 Groups                                                  │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  │
│  ℹ Groups classify files in a repo into named categories.  │
│                                                             │
│  ┌─ github.com/user/repo ──────────────────────────────┐  │
│  │                                                      │  │
│  │  ┌─ Group: frontend ────────────────────[Remove]──┐ │  │
│  │  │  Glob patterns  [src/frontend/** ×]  [+ Add]  │ │  │
│  │  └───────────────────────────────────────────────┘ │  │
│  │                                                      │  │
│  │  ┌─ Group: backend ─────────────────────[Remove]──┐ │  │
│  │  │  Glob patterns  [src/main/** ×]      [+ Add]  │ │  │
│  │  └───────────────────────────────────────────────┘ │  │
│  │                              [+ Add Group]          │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌────────────┐  ┌──────────────┐  ┌────────────────────┐ │
│  │ ← Back     │  │  Skip Groups │  │     Next Step →    │ │
│  └────────────┘  └──────────────┘  └────────────────────┘ │
└────────────────────────────────────────────────────────────┘
```

### Step 4: Review & Generate

```
┌────────────────────────────────────────────────────────────┐
│  RepoSense Configuration Wizard               [Step 4 of 4]│
├────────────────────────────────────────────────────────────┤
│  📊 Review & Generate                                       │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  │
│                                                             │
│  Summary:                                                   │
│    • 1 repository configured                               │
│    • 2 branches configured                                 │
│    • 3 authors configured                                  │
│    • 2 file groups defined                                 │
│                                                             │
│  ┌─ Preview ───────────────────────────────────────────┐  │
│  │  [report-config.yaml]                               │  │
│  ├─────────────────────────────────────────────────────┤  │
│  │  title: RepoSense Report                            │  │
│  │  repos:                                             │  │
│  │    - repo: https://github.com/user/repo.git         │  │
│  │      branches:                                      │  │
│  │        - branch: main                               │  │
│  │          authors:                                   │  │
│  │            - gitId: alice                           │  │
│  │  ...                                        [Copy]  │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                             │
│  🔍 Validating configuration...                             │
│    ✓ report-config.yaml: Valid                             │
│                                                             │
│  Output Location: ./generated-configs/                      │
│                                                             │
│  ┌─────────────────┐              ┌──────────────────────┐ │
│  │ ← Back          │              │  Generate Config ✓   │ │
│  └─────────────────┘              └──────────────────────┘ │
└────────────────────────────────────────────────────────────┘
```

**Success Screen:**

```
┌────────────────────────────────────────────────────────────┐
│  ✓ Configuration Generated Successfully!                    │
├────────────────────────────────────────────────────────────┤
│                                                             │
│  📁 Generated file in ./generated-configs/:                 │
│    ✓ report-config.yaml                                    │
│                                                             │
│  Next Steps:                                                │
│    1. Review the generated file                            │
│    2. Run RepoSense:                                        │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ java -jar RepoSense.jar \                   [Copy]  │  │
│  │   --repo-config ./generated-configs/               │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────────┐  ┌──────────────────────────────┐   │
│  │  Start Over      │  │  Close Wizard                │   │
│  └──────────────────┘  └──────────────────────────────┘   │
└────────────────────────────────────────────────────────────┘
```

---

## Input Validation

### Three-Tier Validation Strategy

#### **Tier 1: Syntax Validation (Immediate)**

Validates format and data type as the user enters values.

| Field              | Validation                     | Error Message                         |
| ------------------ | ------------------------------ | ------------------------------------- |
| Repository URL     | Valid `RepoLocation` format    | "Invalid repository location format"  |
| Email              | Standard email regex           | "Invalid email format"                |
| File Size Limit    | Positive integer only          | "Must be a positive number (bytes)"   |
| Glob Pattern       | Valid glob syntax              | "Invalid glob: unmatched bracket"     |
| Branch             | Non-empty, no spaces           | "Branch name cannot be empty"         |
| Group Name         | Non-empty, unique per repo     | "Group name must be unique per repo"  |

#### **Tier 2: Semantic Validation (Context-Aware)**

Validates logical consistency and cross-field relationships.

| Validation          | Check                              | Action                                              |
| ------------------- | ---------------------------------- | --------------------------------------------------- |
| Duplicate repo URLs | Same URL added twice               | "Repository already added"                          |
| Duplicate branches  | Same branch name in a repo         | "Branch already configured for this repo"           |
| Duplicate authors   | Same gitId in a branch             | "Author already added to this branch"               |
| Empty branch list   | Repo has no branches configured    | Warn: "Add at least one branch to this repository"  |
| Glob overlap        | Group globs overlap across groups  | Warn: "Files may be counted in multiple groups"     |

#### **Tier 3: Parser Validation (Final Check)**

Validates the complete config by running it through the actual RepoSense YAML parser before writing.

**Process:**

1. Assemble YAML structure from wizard state
2. Write to a temp file
3. Call `ReportConfigYamlParser.parse()` (or equivalent)
4. Catch any parse exceptions
5. Display parser error messages to the user
6. Allow user to go back and fix issues
7. On success, write file to `./generated-configs/report-config.yaml`

**Example Error Recovery:**

```
✓ Validating configuration...
  ✗ report-config.yaml: "Branch 'feature' not found in repository"

→ Options:
  › Fix the branch name
    Remove this branch configuration
    Continue anyway (skip validation)
```

---

## Field Support

### report-config.yaml — Complete Field Coverage

#### Top-Level Fields

| Field    | Support | Validation           | Notes    |
| -------- | ------- | -------------------- | -------- |
| `title`  | ✅ Full | Free text            | Optional |
| `repos`  | ✅ Full | Array, min 1 element | Mandatory |

#### Per-Repo Fields (`repos[i]`)

| Field      | Support | Validation              | Notes                   |
| ---------- | ------- | ----------------------- | ----------------------- |
| `repo`     | ✅ Full | `RepoLocation` check    | Mandatory               |
| `groups`   | ✅ Full | Array structure         | Optional (Step 3)       |
| `branches` | ✅ Full | Array, min 1 branch     | Mandatory               |

#### Per-Group Fields (`repos[i].groups[j]`)

| Field        | Support | Validation                   | Notes                          |
| ------------ | ------- | ---------------------------- | ------------------------------ |
| `group-name` | ✅ Full | Non-empty, unique per repo   | Mandatory                      |
| `globs`      | ✅ Full | Glob syntax validation       | Mandatory, multi-value         |

#### Per-Branch Fields (`repos[i].branches[j]`)

| Field                 | Support | Validation             | Notes                            |
| --------------------- | ------- | ---------------------- | -------------------------------- |
| `branch`              | ✅ Full | Non-empty string       | Optional (defaults to HEAD)      |
| `blurb`               | ✅ Full | Markdown text          | Optional                         |
| `ignore-glob-list`    | ✅ Full | Glob syntax validation | Optional, multi-value            |
| `ignore-authors-list` | ✅ Full | Non-empty strings      | Optional, multi-value            |
| `file-size-limit`     | ✅ Full | Positive integer       | Optional, bytes                  |
| `authors`             | ✅ Full | Array structure        | Optional                         |

#### Per-Author Fields (`repos[i].branches[j].authors[k]`)

| Field                    | Support | Validation          | Notes                    |
| ------------------------ | ------- | ------------------- | ------------------------ |
| `author-git-host-id`     | ✅ Full | Non-empty string    | Mandatory                |
| `author-emails`          | ✅ Full | Email regex         | Optional, multi-value    |
| `author-display-name`    | ✅ Full | Free text           | Optional                 |
| `author-git-author-name` | ✅ Full | Free text           | Optional, multi-value    |

### Multi-Value Field UI

Multi-value fields (globs, emails, git author names, etc.) use a tag-chip input:

```
Emails                                                  [+ Add]
┌──────────────────────────────────────────────────────────┐
│ alice@example.com [×]   alice.t@company.com [×]          │
└──────────────────────────────────────────────────────────┘
```

Users type a value and press Enter or click `[+ Add]` to append. The `[×]` button removes a tag.

---

## Implementation Plan

### Phase 1: Backend Infrastructure ✅ (Complete)

**Scope:**

- ✅ **CLI Flag**: Add `--config-wizard` flag to `ArgsParser` and route in `RepoSense.main()`
- ✅ **Wizard Server**: `ConfigWizardServer` serves static assets and handles REST API
- ✅ **File Writer**: `ConfigFileWriter.writeReportConfig()` writes YAML via Jackson
- ✅ **Remove CSV writers**: Deleted `writeRepoConfig()`, `writeAuthorConfig()`, `writeGroupConfig()` and `toYes()` from `ConfigFileWriter`; removed `commons-csv` import
- ✅ **Simplify `/api/generate`**: Accepts a single unified YAML-shaped JSON body, writes one `report-config.yaml` file
- ✅ **Add `/api/preview` endpoint**: Accepts the same payload, returns serialised YAML string for the live preview pane
- ✅ **Update browser launch URL**: `ConfigWizardServer` now opens `/config-wizard` instead of `/wizard/`
- 🔲 **Add `/api/validate-glob` and `/api/validate-date` endpoints** (deferred to Phase 3 alongside Tier 2 validation)

**Deliverable:** Backend that accepts a single YAML-shaped payload and writes `report-config.yaml`. ✅

### Phase 2: Frontend Wizard Core ✅ (Complete)

**Scope:**

- ✅ **Remodel `store.ts`**: State shape now mirrors the YAML hierarchy (`repos → branches → authors`, `repos → groups`)
- ✅ **Update `App.vue`**: 4-step stepper, two-pane layout with inline draggable resizer (note: `c-resizer` was not reused as it is tightly coupled to the main app's Vuex store)
- ✅ **Implement `ReportStep.vue`**: Step 1 — report title field
- ✅ **Implement `ReposStep.vue`**: Step 2 — nested repo → branch → author card UI with URL validation
- ✅ **Implement `GroupsStep.vue`**: Step 3 — per-repo group configuration with Skip option
- ✅ **Implement `ReviewStep.vue`**: Step 4 — summary, YAML snippet, generate button, success/error state
- ✅ **Two-pane layout**: Inline CSS flexbox resizer in `App.vue` (drag to adjust left/right pane widths)
- ✅ **Live preview pane**: Watches `store.config`, debounces calls to `/api/preview`, renders YAML in dark-themed right pane
- ✅ **New entry point**: All wizard source files moved to `frontend/config-wizard/`; `vite.config.mts` updated to build to `build/config-wizard/`, served at `/config-wizard`

**Deliverable:** Working 4-step wizard UI with live YAML preview. ✅

### Phase 3: Full Field Support & Validation ✅ (Complete)

**Scope:**

- ✅ **Tag-chip inputs**: `TagChipInput.vue` reusable component — add on Enter/comma/semicolon, remove on ×, duplicate tags silently rejected, emits `tag-added` for per-tag validation; replaces semicolon-separated string fields in `ReposStep.vue` and `GroupsStep.vue`
- ✅ **`/api/validate-glob` endpoint**: Syntactic glob validation using `FileSystems.getDefault().getPathMatcher("glob:" + pattern)`; catches `PatternSyntaxException`; does not check against actual repo file paths (see Future Enhancements)
- ✅ **`/api/validate-config` endpoint**: Writes wizard payload to a temp file, calls `ReportConfigYamlParser.parse()`, returns parser exceptions as error messages; temp file always deleted in `finally`
- ✅ **Tier 1 validation**: URL via `/api/validate` on blur; glob via `/api/validate-glob` on tag-add; email regex on frontend on tag-add; branch name and Git Host ID space-check inline
- ✅ **Tier 2 validation**: Duplicate repo URLs, duplicate branch names per repo, duplicate author IDs per branch, group name uniqueness per repo — all checked in `onNext()` before saving to store
- ✅ **Tier 3 validation**: Runs automatically on `onMounted` when Step 4 is entered; Generate button is disabled while validating or invalid; parser errors surface with a "Dismiss and generate anyway" escape hatch that re-enables the button
- ✅ **Loading states**: `isPreviewLoading` flag in `App.vue` shows "updating..." in preview header during YAML refresh; URL validation shows "Validating..." text

**Deliverable:** Complete, production-ready wizard with full field coverage and polished UX. ✅

### Phase 4: Testing & Documentation 🔲

**Scope:**

- 🔲 **Backend unit tests**: `ConfigFileWriter`, `/api/generate`, `/api/validate` endpoints
- 🔲 **Frontend component tests**: Each step component with valid and invalid inputs
- 🔲 **Cypress E2E tests**: Full wizard flow from launch to generated file
- 🔲 **User Guide**: Create `docs/ug/configWizard.md`
- 🔲 **Update CLI docs**: Add `--config-wizard` to `docs/ug/cli.md`

**Deliverable:** Fully verified feature with comprehensive documentation.

**Total Estimated Time:** ~16–20 days

---

## Future Enhancements

### Short-term (Within 6 months)

1. **UI Styling — Match Main RepoSense App**

   The wizard currently uses Vue's `#42b983` green as its accent colour and `Segoe UI` as its font, neither of which are used in the main RepoSense report viewer. To make the wizard feel like a native part of the product, apply the following in two sub-steps:

   - **Step A — Font:** Import `Titillium Web` (the main app's font) via Google Fonts in `config-wizard/index.html` and update `font-family` in `App.vue`.
   - **Step B — Colours:** Replace the `#42b983` accent throughout the wizard with values from the existing MUI colour palette already defined in `frontend/src/styles/_colors.scss`. Define CSS custom properties (`--color-primary`, `--color-border`, `--color-error`, etc.) at the root of `App.vue` mapping to the chosen MUI hex values, then update all usages in one pass. Suggested candidates: `blue-grey-500` (`#607D8B`) or `teal-500` (`#009688`) as the primary accent — both are present in the MUI palette and consistent with the main app's aesthetic.

   **Files affected:** `config-wizard/index.html`, `config-wizard/App.vue` (CSS custom properties and usages), `config-wizard/components/*.vue` (any hardcoded colour values).

2. **CSV Config File Support**

   Add a mode toggle so users can choose YAML or CSV output. The YAML-only implementation lays the groundwork (validation logic, server, step structure) that CSV support can be layered on top of.

2. **Config Edit Mode**

   ```bash
   java -jar RepoSense.jar --config-wizard --edit ./generated-configs/
   ```

   Load an existing `report-config.yaml` into the wizard, edit fields, regenerate.

3. **Standardize Shared Resources**

   Formalize a `frontend/shared/` directory for TypeScript interfaces and utility functions used by both the report viewer and the wizard.

4. **Import from Git Hosting Platforms**

   Button in UI: "Import from GitHub" — OAuth flow to access user's repos, auto-populate repo URLs and detect collaborators.

5. **Shareable Wizard Links**

   Export wizard state as a URL parameter (base64 encoded) to share pre-filled configs with teammates.

### Long-term (6+ months)

1. **Author ID Existence Validation**

   The wizard currently only checks that `author-git-host-id` is non-empty. A future enhancement could verify that the author has actually committed to the target repository:

   1. After the repo URL and branch are confirmed, run `git log --format='%an' | sort -u` against the cloned repo
   2. Check if the provided Git Host ID appears in the author list
   3. Warn: "No commits found for this author in the repository"

   This is deferred for the same reason as glob file-path matching — it requires the repo to be cloned first. A lighter alternative for GitHub repos would be calling `https://api.github.com/users/{username}` to check if the account exists, but this only works for GitHub (not GitLab, Bitbucket, or self-hosted), is subject to rate limiting (60 unauthenticated requests/hour), and only confirms account existence rather than actual commits to the repo. Like glob matching, this should be a warning only — users may legitimately add authors before they have made their first commit.

2. **Glob Pattern Matching Against Actual Repo Files**

   `/api/validate-glob` currently only checks that a glob pattern is syntactically valid. A future enhancement could warn the user if a pattern matches no files in the target repository:

   1. After a repo URL and branch are confirmed, run `git ls-tree -r HEAD --name-only` against the cloned repo
   2. Test the glob pattern against the resulting file list
   3. Warn: "This pattern matches 0 files in the repository"

   This is deferred because it requires the repo to already be cloned (slow, network-dependent) and only makes sense after Step 2's URL validation has passed. It is also strictly a warning — the user should still be able to proceed with a zero-match pattern if intentional.

1. **Hosted Cloud Version**

   Deploy wizard to `reposense.org/config-wizard` — no local installation required, direct download of generated YAML.

2. **Validation Report View**

   Upload an existing `report-config.yaml`, show a visual report of errors, warnings, and suggestions. Fix issues directly in the UI.

3. **Configuration Comparison**

   Side-by-side diff view before overwriting an existing config file.

---

## Related Resources

- **User Guide:** [Customizing Reports](../ug/customizingReports.md)
- **Config Format Spec:** [Config Files Format](../ug/configFiles.md)
- **CLI Reference:** [CLI Syntax](../ug/cli.md)
- **Parser Implementation:** `src/main/java/reposense/parser/`

---

## Discussion & Updates

### Change Log

| Date         | Change                                                                        |
| ------------ | ----------------------------------------------------------------------------- |
| 26 Jan 2026  | Initial design document created                                               |
| 26 Jan 2026  | Updated to web-based GUI approach (from CLI/TUI)                              |
| 26 Jan 2026  | Added two-pane layout using `c-resizer`, separate `frontend/wizard/` app      |
| 9 Mar 2026   | Scoped to YAML-only output (`report-config.yaml`); removed CSV support        |
| 9 Mar 2026   | Redesigned step flow to mirror YAML hierarchy (Option B, 4-step wizard)       |
| 9 Mar 2026   | Phase 1 complete: removed CSV writers, simplified `/api/generate`, added `/api/preview`, updated browser URL to `/config-wizard` |
| 9 Mar 2026   | Phase 2 complete: new `frontend/config-wizard/` app with 4-step wizard, YAML-shaped store, two-pane layout, live preview pane  |
| 12 Mar 2026  | Phase 3 complete: `TagChipInput.vue`, `/api/validate-glob`, `/api/validate-config`, Tier 1/2/3 validation, loading states |
| 12 Mar 2026  | Tier 3 validation moved to `onMounted` in `ReviewStep.vue`; Generate button disabled until valid; `WizardStep.vue` gains `nextDisabled` prop |
| 12 Mar 2026  | Bug fix: empty branch string now converted to `null` in `ReposStep.vue` so parser correctly defaults to `"HEAD"`; `ConfigFileWriter` configured with `NON_NULL` serialisation to omit null fields from generated YAML; `TagChipInput` rejects duplicate tags |
| 11 Mar 2026   | Bug fix: author fields in `store.ts` and `ReposStep.vue` were using incorrect YAML keys (`gitId`, `displayName`, etc.); corrected to match `ReportAuthorDetails` annotations (`author-git-host-id`, `author-display-name`, `author-emails`, `author-git-author-name`); removed non-existent author-level `ignoreGlobList` field |

### Decision Log

| Decision                                                  | Rationale                                                                                                  | Date        |
| --------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- | ----------- |
| ~~CLI/TUI~~ → **Web GUI**                                 | Better UX, visual feedback, leverages existing Vue.js stack                                                | 26 Jan 2026 |
| Reuse existing parsers for validation                     | Avoid duplication, ensure consistency with main RepoSense                                                  | 26 Jan 2026 |
| Embedded web server approach                              | No separate deployment, works locally, direct file access                                                  | 26 Jan 2026 |
| Separate app in `frontend/wizard/`                        | Zero impact on report viewer code, shared Vite tooling                                                     | 26 Jan 2026 |
| Auto-detection on user request only                       | Mitigate loading time issues for large repositories                                                        | 26 Jan 2026 |
| **YAML-only output** (no CSV)                             | Simpler scope, YAML is more expressive and hierarchical — CSV support can be added later                   | 9 Mar 2026  |
| **4-step flow mirroring YAML hierarchy** (Option B)       | Avoids awkward CSV-to-YAML remapping; store state directly mirrors output structure                        | 9 Mar 2026  |
| Store state shaped as YAML, not flat CSV arrays           | No remapping needed on generate; frontend state IS the YAML structure                                      | 9 Mar 2026  |

### Open Questions

1. Should the wizard server stay alive after config generation, or prompt the user to close?
2. How to handle very large repos (100+ authors) during any future auto-detection?
3. Should validation be strict (reject invalid) or permissive (warn but allow)?
4. Should the `/api/preview` endpoint generate YAML server-side, or should the frontend format it client-side?

---

**Document End**
