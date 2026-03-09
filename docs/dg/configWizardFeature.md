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
          - gitId: alice
            emails:
              - alice@example.com
            displayName: Alice Thompson
            gitAuthorName:
              - alice
              - AT
            ignoreGlobList:
              - test/**
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
├── wizard/                    // Wizard app (separate entry point)
│   ├── index.html             // Wizard HTML entry point
│   ├── App.vue                // Root wizard component
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
└── vite.config.mts            // Multi-page build (main + wizard entry points)
```

**Build Output:**

```
frontend/build/
├── index.html                 // Report viewer entry
├── wizard/
│   └── index.html             // Wizard entry
└── assets/
    ├── wizard-[hash].js
    ├── wizard-[hash].css
    └── vue-vendor-[hash].js
```

### State Model

The wizard store mirrors the YAML structure directly, so assembling the final payload requires no remapping:

```typescript
// store.ts
interface Author {
  gitId: string;
  emails: string[];
  displayName: string;
  gitAuthorName: string[];
  ignoreGlobList: string[];
}

interface Branch {
  branch: string;
  blurb: string;
  ignoreGlobList: string[];
  ignoreAuthorsList: string[];
  fileSizeLimit: number | null;
  authors: Author[];
}

interface Group {
  groupName: string;
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
| `/api/validate-glob`   | POST   | Validate a glob pattern          | `{ pattern }`              | `{ valid: boolean, error?: string }`            |
| `/api/validate-date`   | POST   | Validate date format/logic       | `{ since?, until? }`       | `{ valid: boolean, error?: string }`            |
| `/api/preview`         | POST   | Generate YAML preview string     | `WizardConfig` (JSON)      | `{ yaml: string }`                              |

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

| Field            | Support | Validation          | Notes                    |
| ---------------- | ------- | ------------------- | ------------------------ |
| `gitId`          | ✅ Full | Non-empty string    | Mandatory                |
| `emails`         | ✅ Full | Email regex         | Optional, multi-value    |
| `displayName`    | ✅ Full | Free text           | Optional                 |
| `gitAuthorName`  | ✅ Full | Free text           | Optional, multi-value    |
| `ignoreGlobList` | ✅ Full | Glob syntax         | Optional, multi-value    |

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

### Phase 1: Backend Infrastructure ✅ (Partially Done)

**Scope:**

- ✅ **CLI Flag**: Add `--config-wizard` flag to `ArgsParser` and route in `RepoSense.main()`
- ✅ **Wizard Server**: `ConfigWizardServer` serves static assets and handles REST API
- ✅ **File Writer**: `ConfigFileWriter.writeReportConfig()` writes YAML via Jackson
- 🔲 **Remove CSV writers**: Delete `writeRepoConfig()`, `writeAuthorConfig()`, `writeGroupConfig()` from `ConfigFileWriter`
- 🔲 **Simplify `/api/generate`**: Accept a single unified YAML-shaped JSON body, write one file
- 🔲 **Add `/api/preview` endpoint**: Returns the YAML string for the live preview pane
- 🔲 **Add `/api/validate-glob` and `/api/validate-date` endpoints**

**Deliverable:** Backend that accepts a single YAML-shaped payload and writes `report-config.yaml`.

### Phase 2: Frontend Wizard Core 🔲

**Scope:**

- 🔲 **Remodel `store.ts`**: Change state shape from flat CSV-mirroring arrays to nested YAML-mirroring structure
- 🔲 **Update `App.vue`**: Change stepper to 4 steps (Report Settings, Repos & Branches, Groups, Review)
- 🔲 **Implement `ReportStep.vue`**: Step 1 — report title field
- 🔲 **Implement `ReposStep.vue`**: Step 2 — nested repo → branch → author card UI
- 🔲 **Implement `GroupsStep.vue`**: Step 3 — per-repo group configuration
- 🔲 **Implement `ReviewStep.vue`**: Step 4 — YAML preview, validation status, generate button
- 🔲 **Two-pane layout**: Integrate `c-resizer` for the form/preview split
- 🔲 **Live preview pane**: Call `/api/preview` on state changes, render YAML

**Deliverable:** Working 4-step wizard UI with live YAML preview.

### Phase 3: Full Field Support & Validation 🔲

**Scope:**

- 🔲 **Tag-chip inputs**: Implement reusable multi-value input component for emails, glob lists, git names
- 🔲 **Tier 1 validation**: Inline syntax feedback for all fields (URL, email, glob, branch)
- 🔲 **Tier 2 validation**: Cross-field checks (duplicate repos/branches/authors, group glob overlap)
- 🔲 **Tier 3 validation**: Call `/api/generate` in dry-run mode and surface parser errors in Step 4
- 🔲 **Success screen**: Show generated file path, copy-to-clipboard command, start-over/close options
- 🔲 **Loading states**: Spinners for URL validation, YAML preview refresh

**Deliverable:** Complete, production-ready wizard with full field coverage and polished UX.

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

1. **CSV Config File Support**

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
