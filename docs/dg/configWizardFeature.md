# Configuration Wizard Feature Design

**Status:** Proposed  
**Last Updated:** 26 January 2026  
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

The Configuration Wizard is an interactive web-based GUI tool that guides users through creating RepoSense configuration files (`repo-config.csv`, `author-config.csv`, `group-config.csv`, `report-config.yaml`) via intuitive forms and visual feedback instead of manual file editing.

**Invocation:**

```bash
java -jar RepoSense.jar --config-wizard
```

This command launches a local web server and automatically opens the wizard in the user's default browser at `http://localhost:9000/config-wizard`.

**Goal:** Provide an intuitive, visually-guided experience that lowers the barrier to entry for new users while supporting advanced configurations for power users.

---

## Motivation

### Current Pain Points

1. **Steep Learning Curve**
   - Users must understand exact schema, field names, and formatting before generating their first report
   - Need to reference documentation constantly for valid values and syntax

2. **Error-Prone Manual Editing**
   - CSV delimiter issues, YAML indentation errors
   - Typos in field names or values
   - Missing required fields discovered only at runtime

3. **Time-Consuming Setup**
   - Repetitive data entry for multiple repositories/authors
   - No immediate validation feedback
   - Trial-and-error cycles when configs fail to parse

4. **Poor Discoverability**
   - Users don't know what configuration options are available
   - Advanced features (overrides, multi-value fields) are hidden in docs

### Target Users

- **New users:** Getting started with first RepoSense report
- **Students:** Creating code portfolios for coursework
- **Educators:** Setting up configs for class projects
- **Teams:** Standardizing configuration across multiple repos

---

## Technology Stack

### Web-Based GUI Implementation

**Why Web GUI over CLI/TUI:**

- ✅ **Superior UX:** Visual feedback, tooltips, inline help, preview panels
- ✅ **Better discoverability:** See all options at a glance, no need to remember commands
- ✅ **Richer interactions:** Drag-and-drop, multi-select, autocomplete, syntax highlighting
- ✅ **Accessibility:** Works for non-technical users uncomfortable with command line
- ✅ **Validation feedback:** Real-time visual indicators (red/green borders, inline errors)
- ✅ **Configuration preview:** Live preview of generated CSV/YAML before saving
- ✅ **Reuses existing tech:** Leverages RepoSense's existing Vue.js frontend stack

**Backend (Java - Existing Dependencies):**

- **`argparse4j`** (v0.9.0): CLI argument parsing for `--config-wizard` flag
- **`net.freeutils.jlhttp`** (v2.6): Already in RepoSense for report server, reuse for wizard server
- **`commons-csv`** (v1.9.0): CSV file generation
- **`jackson-dataformat-yaml`** (v2.17.0): YAML file generation
- **`gson`** (v2.9.0): JSON API communication between frontend and backend

**Frontend (Vue.js - Existing Stack):**

- **Vue 3**: Already used in RepoSense frontend
- **Vite**: Already configured for dev/build
- **TypeScript**: For type-safe form validation
- **Existing UI components**: Can reuse from `frontend/src/components/`

**New Frontend Dependencies:**

- **Vuelidate** or **VeeValidate**: Form validation library
- **vue-multiselect**: For multi-select dropdowns (file formats, authors)
- **codemirror** or **monaco-editor**: Syntax highlighting for preview panels (optional)

**Architecture:** Embedded web server (similar to RepoSense's report server) + Vue.js SPA

**UI Layout Strategy:** Reuse RepoSense's existing two-pane layout pattern (`c-resizer` component) with:

- **Left pane:** Configuration forms (similar to summary/charts pane)
- **Right pane:** Live config preview (similar to authorship/zoom tabs)
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
│  │  - Multi-step form wizard                           │   │
│  │  - Real-time validation                             │   │
│  │  - Config preview                                   │   │
│  └─────────────────┬───────────────────────────────────┘   │
└────────────────────┼───────────────────────────────────────┘
                     │ HTTP REST API
                     │ (localhost:9000)
                     │
┌────────────────────▼───────────────────────────────────────┐
│            Embedded Java Web Server                        │
│  ┌─────────────────────────────────────────────────────┐  │
│  │  ConfigWizardServer (extends ReportServer)          │  │
│  │  - Serves Vue.js static assets                      │  │
│  │  - REST API endpoints for validation                │  │
│  │  - File I/O for reading/writing configs            │  │
│  └─────────────────┬───────────────────────────────────┘  │
└────────────────────┼───────────────────────────────────────┘
                     │
          ┌──────────┴──────────┐
          │                     │
    ┌─────▼──────┐      ┌──────▼──────┐
    │ Validators │      │ File Writer │
    │  (Reuse    │      │   (CSV/YAML)│
    │  Parsers)  │      └─────────────┘
    └────────────┘
```

### Directory Structure

**Backend (Java):**

```
src/main/java/reposense/wizard/
├── ConfigWizardServer.java          // Embedded web server, REST API endpoints
├── ConfigWizardLauncher.java        // Initialization and startup logic
├── ConfigFileWriter.java            // Generates and writes CSV/YAML files
│
├── api/                             // REST API endpoints
│   ├── RepoConfigEndpoint.java      // POST /api/validate-repo
│   ├── AuthorConfigEndpoint.java    // POST /api/validate-author
│   ├── GroupConfigEndpoint.java     // POST /api/validate-group
│   ├── GenerateEndpoint.java        // POST /api/generate-configs
│   └── ApiResponse.java             // Common response wrapper
│
├── validators/                      // Validation logic
│   ├── ConfigValidator.java         // Validation helper, reuses RepoSense parsers
│   ├── DateValidator.java           // Date format and logic validation
│   ├── GlobPatternValidator.java    // Glob syntax validation
│   └── RepositoryValidator.java     // Git repository accessibility checks
│
└── model/                           // DTOs for API communication
    ├── RepoConfigDto.java
    ├── AuthorConfigDto.java
    └── GroupConfigDto.java
```

**Frontend (Vue.js) - Option 1: Separate App (Recommended):**

```
frontend/
├── src/                             // Existing report viewer code (unchanged)
│   ├── components/
│   ├── views/
│   └── main.ts
│
├── wizard/                          // New wizard app (separate entry point)
│   ├── index.html                   // Wizard HTML entry (Vite convention)
│   ├── vite.config.ts               // Wizard-specific Vite config (optional)
│   │
│   └── src/                         // Source code (Vue.js best practice)
│       ├── main.ts                  // Wizard entry point
│       ├── App.vue                  // Root wizard component
│       │
│       ├── router/                  // Router module (best practice: folder)
│       │   └── index.ts             // Route definitions
│       │
│       ├── store/                   // State management (best practice: folder)
│       │   └── index.ts             // Pinia store setup
│       │
│       ├── views/                   // Page-level components (Vue.js convention)
│       │   ├── RepoConfigView.vue   // Step 1: Repository config
│       │   ├── AuthorConfigView.vue // Step 2: Author config
│       │   ├── GroupConfigView.vue  // Step 3: Group config
│       │   └── ReviewView.vue       // Step 4: Review & generate
│       │
│       ├── components/              // Reusable components
│       │   ├── WizardStepper.vue
│       │   ├── WizardLayout.vue     // Main layout with c-resizer
│       │   ├── NavigationButtons.vue
│       │   ├── ValidationMessage.vue
│       │   ├── ConfigPreview.vue
│       │   │
│       │   └── fields/              // Form field components (sub-folder)
│       │       ├── RepoUrlField.vue
│       │       ├── BranchSelector.vue
│       │       ├── FileFormatPicker.vue
│       │       ├── GlobPatternInput.vue
│       │       ├── DateRangePicker.vue
│       │       ├── AuthorMultiSelect.vue
│       │       └── EmailInput.vue
│       │
│       ├── composables/             // Reusable composition functions (Vue 3 pattern)
│       │   ├── useValidation.ts     // Form validation composable
│       │   ├── useWizardState.ts    // Wizard state management
│       │   └── useConfigApi.ts      // API calls composable
│       │
│       ├── services/                // API and business logic
│       │   ├── api.ts               // API client (HTTP calls)
│       │   └── configGenerator.ts   // Config generation logic
│       │
│       ├── utils/                   // Pure utility functions
│       │   ├── validators.ts        // Validation helpers
│       │   └── formatters.ts        // CSV/YAML formatting
│       │
│       ├── types/                   // TypeScript types
│       │   ├── config.types.ts
│       │   └── api.types.ts
│       │
│       ├── assets/                  // Static assets
│       │   ├── styles/
│       │   │   ├── main.css         // Global wizard styles
│       │   │   └── variables.css    // CSS custom properties
│       │   └── images/
│       │
│       └── constants/               // Application constants
│           └── config.ts            // Default values, enums
│
├── vite.config.mts                  // Update for multi-page build
└── package.json                     // Shared dependencies
```

**Build Output:**

```
build/
└── wizard/                          // Wizard build artifacts
    ├── index.html
    ├── assets/
    │   ├── wizard-[hash].js
    │   ├── wizard-[hash].css
    │   └── vendor-[hash].js
    └── api/                         // API spec (optional)
        └── openapi.json
```

**Why Option 1 (Separate App in `frontend/wizard/`)?**

✅ **Clear separation**: Wizard code doesn't mix with report viewer
✅ **Shared tooling**: Uses same Vite, TypeScript, eslint config
✅ **Shared dependencies**: No duplication of Vue, libraries in package.json
✅ **Can reuse components**: Import from `../src/components/` or `../shared/`
✅ **Separate build target**: Can build wizard independently
✅ **Separate HTML entry**: Different title, meta tags, scripts
✅ **Low overhead**: Still one frontend project, one package.json

**Vite Config Update (`vite.config.mts`):**
(no refactoring needed)
✅ **Separate build target**: Can build wizard independently
✅ **Separate HTML entry**: Different title, meta tags, scripts
✅ **Low overhead**: Still one frontend project, one package.json
✅ **Zero refactoring**: Existing report viewer code remains untouched
rollupOptions: {
input: {
main: resolve(**dirname, "index.html"), // Report viewer
wizard: resolve(**dirname, "wizard/index.html"), // Config wizard
},
},
},
});

```

---

### Component Structure (Wizard-Specific)

**Reusing Existing RepoSense Components:**

- **`c-resizer`**: Import directly from `../src/components/c-resizer.vue` (no changes to existing files)
- **`c-error-message-box`**: Reuse for displaying high-level generation or validation errors in Step 4.
- **Layout pattern**: Same as main report view (left: forms, right: preview)
- **Component styling**: Reuse existing CSS variables and design tokens
- **No refactoring needed**: Existing report viewer files remain in their current locations

**Component Hierarchy:**
```

wizard/App.vue // Root wizard component
└── c-resizer (imported from ../src/components/c-resizer.vue)
├── template(#left)
│ └── WizardLeftPane
│ ├── WizardStepper (progress: Step 1 of 4)
│ ├── RepoConfigForm / AuthorConfigForm / GroupConfigForm / ReviewForm
│ └── NavigationButtons (Back / Next / Generate)
│
└── template(#right)
└── WizardRightPane
└── ConfigPreview (tabbed view)
├── [repo-config.csv] tab
├── [author-config.csv] tab
├── [group-config.csv] tab
└── [report-config.yaml] tab (if applicable)

````

**Import Example:**

```typescript
// wizard/App.vue
import CResizer from '../src/components/c-resizer.vue';
                  ├── [group-config.csv] tab
                  └── [report-config.yaml] tab (if applicable)
````

### Integration Points

**ArgsParser Modification:**

```java
// Add new flag
public static final String[] CONFIG_WIZARD_FLAG = new String[] {"--config-wizard"};

// In parse() method
parser.addArgument(CONFIG_WIZARD_FLAG)
    .help("Launch web-based configuration wizard")
    .action(Arguments.storeTrue());
```

**RepoSense.main() Routing:**
Launcher.launch(9000); // Handles server + browser
return;
}

        // Normal execution flow...
    }

}

````

**ConfigWizardServer File Serving:**

```java
// Serves from build/wizard/ directory
ConfigWizardServer server = new ConfigWizardServer(9000);
server.serveStaticFiles("/wizard", Paths.get("build/wizard"));
server.start();           logger.info("Press Ctrl+C to stop the server.");

            // Keep server alive
            server.waitForShutdown();
            return;
        }

        // Normal execution flow...
    }
}
````

**REST API Endpoints:**

| Endpoint                  | Method | Purpose                     | Request                                   | Response                                        |
| ------------------------- | ------ | --------------------------- | ----------------------------------------- | ----------------------------------------------- |
| `/api/validate-repo`      | POST   | Validate repo URL, branch   | `{url, branch}`                           | `{valid: boolean, errors: [], suggestions: []}` |
| `/api/auto-detect-branch` | POST   | Auto-detect default branch  | `{url}`                                   | `{branch: string}`                              |
| `/api/analyze-repo`       | POST   | Get file types, authors     | `{url, branch}`                           | `{fileTypes: [], authors: []}`                  |
| `/api/validate-author`    | POST   | Validate author config      | `{gitId, emails, ...}`                    | `{valid: boolean, errors: []}`                  |
| `/api/validate-glob`      | POST   | Validate glob pattern       | `{pattern}`                               | `{valid: boolean, error: string}`               |
| `/api/validate-date`      | POST   | Validate date format/logic  | `{since, until}`                          | `{valid: boolean, error: string}`               |
| `/api/generate-configs`   | POST   | Generate all config files   | `{repoConfig, authorConfig, groupConfig}` | `{success: boolean, files: []}`                 |
| `/api/preview-config`     | POST   | Preview CSV/YAML output     | `{type, data}`                            | `{preview: string}`                             |
| `/api/check-standalone`   | POST   | Check for standalone config | `{url}`                                   | `{exists: boolean, config: {}}`                 |

### Reusing Existing Parsers for Validation

**Key Principle:** Don't reimplement validation logic—call existing parsers!

```java
class ConfigValidator {
    // Validate by calling actual RepoSense parsers
    static ValidationResult validateRepoConfig(Path csvPath) {
        try {
            new RepoConfigCsvParser(csvPath).parse();
            return ValidationResult.success();
        } catch (InvalidCsvException | ParseException e) {
            return ValidationResult.error(e.getMessage());
        }
    }

    // Reuse existing date validation
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

### Progressive Disclosure Strategy

Instead of overwhelming users with 30+ fields, use staged prompts:

#### **Stage 1: Quick Start (Essential Fields)**

```
Repository URL → Branch → Author Git Host ID
```

Generates minimal working config (covers 80% of use cases).

#### **Stage 2: Common Options (Interactive)**

````
? Would you like to configure advanced options? (y/n)
  → File formats
  → Ignore patterns
  → Date ranges
``` (Two-Pane Layout)**

````

┌──────────────────────────────────────────────────────────────────────────────────────────┐
│ RepoSense Configuration Wizard [Step 1 of 4] │
├──────────────────────────────────────────────────────────────────────────────────────────┤
│ │ │
│ 📁 Repository Configuration │ 📄 Preview │
│ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │ ━━━━━━━━━━━━━━━━━━━━━━━━━ │
│ │ │
│ Configuration Mode: │ [repo-config.csv] [⊗] │
│ ( ) Quick (•) Standard ( ) Advanced │ ━━━━━━━━━━━━━━━━━━━━━━━━━ │
│ │ │
│ Repository URL \* │ Repository's Location,... │
│ ┌────────────────────────────────────────────────┐ │ https://github.com/user/... │
│ │ https://github.com/user/repo.git │ │ repo.git,main,all │
│ └────────────────────────────────────────────────┘ │ │
│ ✓ Repository accessible │ │
│ │ [Copy]│
│ Branch │ │
│ ┌────────────────────────────────────────────────┐ │ ℹ Updates in real-time as │
│ │ main [Auto-detect ↻] │ │ you fill the form │
│ └────────────────────────────────────────────────┘ │ │
│ ℹ Auto-detected from repository │ │
│ │ │
│ File Formats │ │
│ [✓] All formats (default) │ │
│ [ ] Specific formats │ │
│ │ │
│ Ignore Patterns (Glob) [+ Add]│ │
│ ┌────────────────────────────────────────────────┐ │ │
│ │ test/** [×] │ ││ │
│ │ **.log [×] │ ││ │
│ └────────────────────────────────────────────────┘ │ [Resizable │
│ 💡 Suggested: node_modules/**, build/** [Add] │ Divider] │
│ ││ │
│ 📅 Date Range │ │
│ (•) Default (last 1 month) │ │
│ ( ) Custom range │ │
│ │ │
│ ⚙️ Advanced Options [Expand ▼] │ │
│ │ │
│ ┌──────────────┐ ┌────────────────────────────────┐ │ │
│ │ Add Repo [+] │ │ Next Step → │ │ │
│ └──────────────┘ └────────────────────────────────┘ │ │
└──────────────────────────────────────────────────────────┴────────────────────────────────┘
│← Drag to resize →│

```

**Key Features:**
- **Left pane (form fields):** Similar to c-summary pane in main report
- **Right pane (preview):** Similar to c-authorship/c-zoom tabs
- **Resizable divider:** Users can drag to adjust pane widths (using c-resizer)
- **Collapsible preview:** Click `[⊗]` to close preview and maximize form area
- **Real-time updates:** Preview updates as user types/selects┌─────────────────────────────────────────────────────┐  │
│  │ main                        [Auto-detect ↻]         │  │
│  └─────────────────────────────────────────────────────┘  │
│  ℹ Auto-detected from repository                           │
│                                                             │
│  File Formats                                               │
│    [✓] All formats (default)                               │
│    [ ] Specific formats                                    │
│                                                             │
│  Ignore Patterns (Glob)                              [+ Add]│
│  ┌─────────────────────────────────────────────────────┐  │
│  │ test/**                                          [×] │  │
│  │ **.log                                           [×] │  │
│  └─────────────────────────────────────────────────────┘  │
│  💡 Suggested: node_modules/**, build/**              [Add]│
│                                                             │
│  📅 Date Range                                              │
│    (•) Default (last 1 month)                              │
│    ( ) Custom range                                        │
│                                                             │
│  ⚙️ Advanced Options                             [Expand ▼]│
│                                                             │
│  ┌────────────────────┐  ┌────────────────────────────┐   │
│  │ Add Repository [+] │  │              Next Step → │   │
│  └────────────────────┘  └────────────────────────────┘   │
└────────────────────────────────────────────────────────────┘
```

#### **Step 2: Author Configuration**

```
┌────────────────────────────────────────────────────────────┐
│  RepoSense Configuration Wizard               [Step 2 of 4]│
├────────────────────────────────────────────────────────────┤
│                                                             │
│  👤 Author Configuration                                    │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  │
│                                                             │
│  🔍 Analyzing repository...                                 │
│  ✓ Found 3 authors                                         │
│                                                             │
│  Select Authors to Include:                                │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ [✓] alice (150 commits) alice@example.com          │  │
│  │ [✓] bob (80 commits) bob@example.com               │  │
│  │ [ ] charlie (45 commits) charlie@example.com       │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                             │
│  ── Alice ───────────────────────────────────────────────  │
│                                                             │
│  Display Name                   Git Host ID                │
│  ┌─────────────────────────┐   ┌─────────────────────┐    │
│  │ Alice Thompson          │   │ alice               │    │
│  └─────────────────────────┘   └─────────────────────┘    │
│                                                             │
│  Git Author Names (used in commits)                 [+ Add]│
│  ┌─────────────────────────────────────────────────────┐  │
│  │ alice                                            [×] │  │
│  │ AT                                               [×] │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                             │
│  Email Addresses                                     [+ Add]│
│  ┌─────────────────────────────────────────────────────┐  │
│  │ alice@example.com                                [×] │  │
│  │ alice.t@company.com                              [×] │  │
│  └─────────────────────────────────────────────────────┘  │
│  ✓ 2 valid emails                                          │
│                                                             │
│  ── Bob ─────────────────────────────────────────────────  │
│  (Using defaults)                                          │
│                                                             │
│  ┌─────────────────┐  ┌──────────────┐  ┌─────────────┐  │
│  │ ← Back          │  │ Skip Authors │  │ Next Step → │  │
│  └─────────────────┘  └──────────────┘  └─────────────┘  │
└────────────────────────────────────────────────────────────┘
```

#### **Step 4: Review & Generate**

```
┌────────────────────────────────────────────────────────────┐
│  RepoSense Configuration Wizard               [Step 4 of 4]│
├────────────────────────────────────────────────────────────┤
│                                                             │
│  📊 Review & Generate                                       │
│  ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  │
│                                                             │
│  Summary:                                                   │
│    • 1 repository configured                               │
│    • 2 authors selected                                    │
│    • 2 file groups defined                                 │
│                                                             │
│  ┌─ Preview ──────────────────────────────────────────┐   │
│  │  [repo-config.csv] [author-config.csv] [group...]  │   │
│  ├─────────────────────────────────────────────────────┤   │
│  │ Repository's Location,Branch,File formats           │   │
│  │ https://github.com/user/repo.git,main,all           │   │
│  │                                                      │   │
│  │                                              [Copy]  │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  🔍 Validating configuration...                             │
│    ✓ repo-config.csv: Valid                                │
│    ✓ author-config.csv: Valid                              │
│    ✓ group-config.csv: Valid                               │
│                                                             │
│  Output Location:                                           │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ ./config/                               [Browse...] │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌─────────────────┐              ┌──────────────────────┐│
│  │ ← Back          │              │ Generate Configs ✓   ││
│  └─────────────────┘              └──────────────────────┘│
└────────────────────────────────────────────────────────────┘
```

**Success Screen:**

```
┌────────────────────────────────────────────────────────────┐
│  ✓ Configuration Generated Successfully!                    │
├────────────────────────────────────────────────────────────┤
│                                                             │
│  📁 Generated files in ./config/:                           │
│    ✓ repo-config.csv (1 repository)                        │
│    ✓ author-config.csv (2 authors)                         │
│    ✓ group-config.csv (2 groups)                           │
│                                                             │
│  ℹ Setup Instructions:                                     │
│    Place these files in your desired project directory to   │
│    maintain your configuration.                            │
│                                                             │
│  Next Steps:                                                │
│    1. Review generated files                               │
│    2. Run RepoSense:                                        │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐  │
│  │ java -jar RepoSense.jar --config ./config/  [Copy] │  │
│  └─────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌──────────────────┐  ┌──────────────────────────────┐   │
│  │ Create Another   │  │ Close Wizard                 │   │
│  └──────────────────┘  └──────────────────────────────┘   │
└────────────────────────────────────────────────────────────┘
```

### Visual Design System

**Color Palette:**

- **Two-pane layout:** Reuses `c-resizer` component from main RepoSense report
- **Left pane:** Form fields with step-by-step wizard flow
- **Right pane:** Tabbed preview (repo-config.csv, author-config.csv, group-config.csv tabs)
- **Input Fields:** Border turns green on valid, red on invalid
- **Tooltips:** Hover over field labels for detailed help
- **Progress Indicator:** Top of page shows steps (1 → 2 → 3 → 4)
- **Live Preview:** Right panel shows generated CSV/YAML in real-time (auto-updates)
- **Collapsible Sections:** Advanced options collapsed by default
- **Multi-select Tags:** Selected items shown as removable chips
- **Loading States:** Spinner for async operations (validation, repo analysis)
- **Resizable divider:** Drag to adjust left/right pane sizes
- **Close preview:** Click to collapse right pane, focus on form

**Benefits of Reusing Existing Layout:**

- ✅ **Familiar UX:** Users already know how to interact with two-pane layout
- ✅ **Consistent design:** Matches existing RepoSense report aesthetics
- ✅ **Less code:** Reuse `c-resizer`, don't reinvent the wheel
- ✅ **Responsive:** Same responsive behavior as main report
- ✅ **Accessibility:** Inherits existing accessibility features

- **Input Fields:** Border turns green on valid, red on invalid
- **Tooltips:** Hover over field labels for detailed help
- **Progress Indicator:** Top of page shows steps (1 → 2 → 3 → 4)
- **Live Preview:** Right panel shows generated CSV/YAML in real-time
- **Collapsible Sections:** Advanced options collapsed by default
- **Multi-select Tags:** Selected items shown as removable chips
- **Loading States:** Spinner for async operations (validation, repo analysis)

---

## Input Validation

### Three-Tier Validation Strategy

#### **Tier 1: Syntax Validation (Immediate)**

Validates format, data type, length as user enters values.

**Examples:**

| Field           | Validation                              | Error Message                           |
| --------------- | --------------------------------------- | --------------------------------------- |
| Repository URL  | `^https?://.*\.git$`                    | "URL must be HTTP(S) and end with .git" |
| Date            | `DD/MM/YYYY` or `DD/MM/YYYY'T'HH:mm:ss` | "Invalid date format. Use DD/MM/YYYY"   |
| Email           | Standard email regex                    | "Invalid email format"                  |
| File Size Limit | Numeric only, no units                  | "Must be a number (bytes)"              |
| Glob Pattern    | Valid glob syntax                       | "Invalid glob: unmatched bracket"       |
| Branch          | Non-empty, no spaces                    | "Branch name cannot be empty"           |

#### **Tier 2: Semantic Validation (Context-Aware)**

Validates logical consistency and cross-field relationships.

**Examples:**

| Validation         | Check                       | Action                                         |
| ------------------ | --------------------------- | ---------------------------------------------- |
| Date Logic         | Since < Until               | Reject: "Until date must be after Since date"  |
| CSV vs CLI Dates   | CSV dates within CLI range  | Error: "CSV date exceeds CLI --until flag"     |
| Branch Existence   | `git ls-remote` check       | Suggest: "Branch not found. Use 'main'? (y/n)" |
| File Size Logic    | Warn if > 100MB             | "Warning: Unusually large limit (100MB+)"      |
| Override Detection | Check for standalone config | "Standalone config found. Override? (y/n)"     |
| Multi-value Dupes  | Check for duplicate values  | Remove dupes, warn user                        |
| Glob Breadth       | Pattern is `**` only        | "Warning: Pattern matches all files"           |

**Date Range Validation (4 Scenarios from Docs):**

```
Scenario 1: Both CSV and CLI dates provided
  → Validate: CSV within CLI boundaries

Scenario 2: CSV full, CLI partial
  → Use CSV dates, UI adjusts to CSV range

Scenario 3: Both CSV absent
  → Use CLI or defaults (last 1 month)

Scenario 4: One CSV date missing
  → Fill with CLI value or default
```

#### **Tier 3: Parser Validation (Final Check)**

Validates complete config by calling actual RepoSense parsers.

**Process:**

1. Generate temp config files in memory or temp directory
2. Call `RepoConfigCsvParser.parse()`, `AuthorConfigCsvParser.parse()`, etc.
3. Catch any `ParseException`, `InvalidCsvException`, `InvalidHeaderException`
4. Display error messages from parsers
5. Allow user to go back and fix issues
6. Retry validation
7. On success, write files to `./config/`

**Example Error Recovery:**

```
✓ Validating configuration...
  ✓ repo-config.csv: parsed successfully
  ✗ author-config.csv: "Author 'alice' specified but not found in any repository"
  ✓ group-config.csv: parsed successfully

? Options:
  › Fix author configuration
    Skip author config (continue without it)
    Exit wizard
```

---

## Field Support

### Complete Field Coverage by Config Type

#### **repo-config.csv (14 columns)**

| Column                     | Support | Validation Method             | Notes                                    |
| -------------------------- | ------- | ----------------------------- | ---------------------------------------- |
| Repository Location        | ✅ Full | URL regex, git ls-remote      | Mandatory                                |
| Branch                     | ✅ Full | git branch -r check           | Default: auto-detect                     |
| File Formats               | ✅ Full | Extension format (dot prefix) | Multi-value, overrideable                |
| Find Previous Authors      | ✅ Full | yes/no boolean                | Simple toggle                            |
| Ignore Glob List           | ✅ Full | Glob syntax validation        | Multi-value, overrideable                |
| Ignore Standalone Config   | ✅ Full | yes/no boolean                | Show warning if standalone exists        |
| Ignore Commits List        | ✅ Full | SHA format, range syntax      | Multi-value, overrideable                |
| Ignore Authors List        | ✅ Full | Free text (Git Author Names)  | Multi-value, overrideable                |
| Shallow Cloning            | ✅ Full | yes/no boolean                | Warn about --last-modified-date conflict |
| File Size Limit            | ✅ Full | Numeric validation            | Overrideable, bytes only                 |
| Ignore File Size Limit     | ✅ Full | yes/no boolean                | If yes, skip next 2 fields               |
| Skip Ignored File Analysis | ✅ Full | yes/no boolean                | Conditional on prev field                |
| Since Date                 | ✅ Full | SinceDateArgumentType         | Optional time component                  |
| Until Date                 | ✅ Full | UntilDateArgumentType         | Optional time component                  |

#### **author-config.csv (7 columns)**

| Column                 | Support | Validation Method        | Notes                 |
| ---------------------- | ------- | ------------------------ | --------------------- |
| Repository Location    | ✅ Full | Match with repo-config   | Multi-value, optional |
| Branch                 | ✅ Full | Match with repo branches | Optional              |
| Author Git Host ID     | ✅ Full | Non-empty string         | Mandatory             |
| Author Emails          | ✅ Full | Email regex              | Multi-value, optional |
| Author Display Name    | ✅ Full | Free text                | Optional              |
| Author Git Author Name | ✅ Full | Match git log history    | Multi-value, optional |
| Ignore Glob List       | ✅ Full | Glob syntax              | Multi-value, additive |

**Advanced Syntax Support:**

- Wildcard repo locations: `*`
- Author inheritance patterns (see `authorConfigSyntax.md`)

#### **group-config.csv (3 columns)**

| Column              | Support | Validation Method          | Notes                  |
| ------------------- | ------- | -------------------------- | ---------------------- |
| Repository Location | ✅ Full | Match with repo-config     | Optional               |
| Group Name          | ✅ Full | Non-empty, unique per repo | Mandatory              |
| Globs               | ✅ Full | Glob syntax validation     | Multi-value, mandatory |

**Validation:** Ensure files only belong to one group per repo.

#### **report-config.yaml (Nested Structure)**

| Field                                  | Support | Validation Method | Notes                   |
| -------------------------------------- | ------- | ----------------- | ----------------------- |
| title                                  | ✅ Full | Free text         | Optional                |
| repos[]                                | ✅ Full | Array structure   | Mandatory if using YAML |
| repos[].repo                           | ✅ Full | URL validation    | Mandatory               |
| repos[].groups[]                       | ✅ Full | Group structure   | Optional                |
| repos[].branches[]                     | ✅ Full | Array structure   | Mandatory               |
| repos[].branches[].branch              | ✅ Full | Branch name       | Optional                |
| repos[].branches[].blurb               | ✅ Full | Markdown text     | Optional                |
| repos[].branches[].authors[]           | ✅ Full | Author structure  | Optional                |
| repos[].branches[].ignore-authors-list | ✅ Full | Array of names    | Optional                |
| repos[].branches[].ignore-glob-list    | ✅ Full | Glob array        | Optional                |
| repos[].branches[].file-size-limit     | ✅ Full | Numeric           | Optional                |

### Multi-Value Field Handling

**Three Input Approaches:**

**Approach 1: Delimiter-based (Simple)**

```
? File formats (separate with ;): .java;.md;.vue
```

**Approach 2: Loop-based (Better UX)**

```
? File format #1: .java
? Add another? (y/n): y
? File format #2: .md
? Add another? (y/n): n
→ Stored: .java;.md
```

**Approach 3: Interactive List (Best UX, More Complex)**

```
Current formats: [.java, .md]
? (a)dd / (r)emove / (d)one: a
? Format to add: .vue
Current formats: [.java, .md, .vue]
? (a)dd / (r)emove / (d)one: d
→ Stored: .java;.md;.vue
```

**Implementation:** Start with Approach 2, add Approach 3 if time permits.

### Overrideable Column Handling

**Detection:**

1. Check if `_reposense/config.json` exists in target repo
2. Parse standalone config to show current values
3. Prompt user whether to override
4. If yes, prepend `override:` to CSV value

**Example:**

```
⚠ Standalone config detected in repo
  File formats: [.java, .py]
  Ignore patterns: [test/**, docs/**]

? Override file formats? (y/n): y
? New formats: .java;.md;.vue
  → Writing to CSV: override:.java;.md;.vue

? Override ignore patterns? (y/n): n
  → Using standalone config value
```

### Conditional Field Logic

**Dependencies:**

```
If "Ignore File Size Limit" = yes:
  → Skip "File Size Limit"
  → Skip "Skip Ignored File Analysis"
  → Display: "ℹ File size settings disabled (ignoring limit)"

If "Shallow Cloning" = yes:
  → Display warning: "⚠ Incompatible with --last-modified-date flag"

If CSV dates provided + CLI dates present:
  → Validate CSV within CLI boundaries
  → Show error if out of range
```

---

## Smart Defaults & Auto-Detection

### Repository-Level Intelligence

**Auto-Detect Default Branch:**

```bash
git symbolic-ref refs/remotes/origin/HEAD
→ refs/remotes/origin/main
```

**Analyze File Types:**

```bash
git ls-tree -r HEAD --name-only | sed 's/.*\.//' | sort | uniq -c | sort -rn | head -5
→ Suggests: .java (45%), .md (30%), .xml (15%)
```

**Detect Common Ignore Patterns:**

- Always suggest: `node_modules/**`, `.git/**`, `build/**`
- Detect from `.gitignore` if exists

### Author-Level Intelligence

**List All Git Author Names:**

```bash
git log --format='%an' | sort -u
→ alice, bob, Alice Thompson, AT
```

**List All Emails:**

```bash
git log --format='%ae' | sort -u
→ alice@example.com, alice.t@company.com
```

**Commit Count per Author:**

```bash
git shortlog -sn
→ 150 alice
→  80 bob
→  45 charlie
```

**Wizard Flow:**

```
Analyzing repository...
✓ Found 3 authors:
  1. alice (150 commits) - alice@example.com
  2. bob (80 commits) - bob@example.com
  3. charlie (45 commits) - charlie@example.com

? Use detected authors? (y/n): y
? Configure details for each author? (y/n): y
```

---

## Implementation Plan

### Phase 1: Backend Infrastructure

**Estimated Time:** 3-4 days

**Scope:**

- ✅ **Wizard Server**: Implement `ConfigWizardServer` (extending `ReportServer`) to serve static assets and REST API.
- ✅ **CLI Flag**: Add `--config-wizard` flag and initialize server in `RepoSense.main()`.
- ✅ **Frontend Setup**: 
  - Create separate app entry point in `frontend/wizard/` (main.ts, index.html).
  - Update `vite.config.mts` for multi-page build.
  - No refactoring or moving of existing RepoSense frontend files.
- ✅ **Shared Tooling**: Reuse existing Vite, TypeScript, and ESLint configurations.

**Deliverable:** A functional backend and skeleton frontend that can be launched via CLI.

### Phase 2: Core Frontend Wizard

**Estimated Time:** 4-5 days

**Scope:**

- ✅ **Component Reuse**: 
  - Import `c-resizer` and `c-file-type-checkboxes` directly from `../src/components/`.
  - Reuse `muicss` and design tokens from `../src/styles/`.
- ✅ **Wizard Layout**: Build `App.vue` using `c-resizer` with the two-pane pattern (Left: Forms, Right: Preview).
- ✅ **State & Routing**: Implement wizard state management and step-based routing.
- ✅ **Step 1 & 2 Views**: Implement `RepoConfigView` and `AuthorConfigView` with real-time preview logic.
- ✅ **API Integration**: Create service layer to communicate with the backend REST API.

**Deliverable:** Working multi-step wizard UI that reuses existing UI components.

### Phase 3: Full Field Support & Advanced Features

**Estimated Time:** 5-6 days

**Scope:**

- ✅ **Complete All Fields**: Implement forms and validation for all `repo-config.csv`, `author-config.csv`, and `group-config.csv` fields.
- ✅ **Auto-detection (On Request)**: Implement triggers for branch detection, file types, and author listing that only run when user-initiated.
- ✅ **Advanced Validation**: Add cross-field validation logic and "Tier 2" semantic checks.
- ✅ **Success Screen**: Build the generation success screen with setup instructions and the close/continue server prompt.
- ✅ **Visual Polish**: Integrate `c-error-message-box` for high-level errors and add tooltips/loading states.

**Deliverable:** A complete, production-ready wizard with full feature parity and polished UX.

### Phase 4: Testing & Documentation

**Estimated Time:** 4-5 days

**Scope:**

- ✅ **Automated Testing**: Implement backend unit tests, frontend component tests, and full E2E flows using Cypress.
- ✅ **User/Dev Docs**: Update User Guide (`ug/configWizard.md`) and Developer Guide (`dg/configWizardFeature.md`).
- ✅ **Release Notes**: Draft release announcement and update site navigation.

**Deliverable:** Fully verified feature with comprehensive documentation for users and developers.

**Total Estimated Time:** ~18-22 days (3.5-4.5 weeks)

**Note:** Reduced from initial estimate by reusing existing `c-resizer` and frontend infrastructure. Documentation time increased to ensure proper integration with existing docs and comprehensive user guidance.

---

## Future Enhancements

### Short-term (Within 6 months)

1. **Config Edit Mode**

   ```bash
   java -jar RepoSense.jar --config-wizard --edit ./config/
   ```

   Load existing configs into wizard UI, edit fields, regenerate.
   Pre-populate form fields from existing CSV/YAML files.

2. **Standardize Shared Resources**
   - Formalize a `frontend/shared/` directory for TypeScript interfaces and utility functions (e.g., date formatting) used by both the report viewer and the wizard.
   - Centralize validation logic (e.g., repository URL regex) to ensure consistency across the application.

3. **Import from Git Hosting Platforms**
   - Button in UI: "Import from GitHub"
   - OAuth flow to access user's repositories
   - Auto-populate repo URLs, detect collaborators
   - One-click multi-repo setup

4. **Shareable Wizard Links**
   - Export wizard state as URL parameter (base64 encoded)
   - Share link with teammates: `http://localhost:9000/config-wizard?state=...`
   - Import wizard state from link

5. **Validation Report View**
   - New route: `/config-wizard/validate`
   - Upload existing configs, show validation results
   - Visual report of errors, warnings, suggestions
   - Fix issues directly in UI

6. **Configuration Comparison**
   - Side-by-side diff view before overwriting existing configs
   - Show added/removed/modified fields
   - Cherry-pick changes to apply

### Long-term (6+ months)

1. **Hosted Cloud Version**
   - Deploy wizard to reposense.org/config-wizard
   - No local installation required
   - Direct download of generated configs

2. **Advanced Analytics Dashboard**
   - Preview what the generated report will look like
   - Show coverage: which files/authors will be analyzed

---

## Related Resources

- **User Guide:** [Customizing Reports](../ug/customizingReports.md)
- **Config Format Spec:** [Config Files Format](../ug/configFiles.md)
- **CLI Reference:** [CLI Syntax](../ug/cli.md)
- **Parser Implementation:** `src/main/java/reposense/parser/`

---

## Discussion & Updates

### Change Log

| Date                            | Change                                                                                 | Author      |
| ------------------------------- | -------------------------------------------------------------------------------------- | ----------- |
| 26 Jan 2026                     | Initial design document created (CLI/TUI approach)                                     | -           |
| Reuse c-resizer two-pane layout | Familiar UX, consistent design, less code to write, responsive behavior already tested | 26 Jan 2026 |
| 26 Jan 2026                     | Updated to web-based GUI approach                                                      | -           |

### Open Questions

1. Should wizard support editing existing configs in addition to creation?
2. Should auto-detection features run automatically or only on user request (loading time consideration)? → **Only on user request**
3. How to handle very large repos (100+ authors) in auto-detection without performance issues?
4. Should validation be strict (reject invalid) or permissive (warn but allow)?
5. Should the wizard server stay alive after config generation, or auto-shutdown? → **Prompt user to close or continue**
6. How to handle simultaneous wizard instances (multiple users on same machine)? Use random ports?
7. Should config preview be client-side (JavaScript formatting) or server-side (call actual generators)?
8. Where should shared validation logic and types reside to best support future features while maintaining separation of concerns?

### Decision Log

| Decision                                            | Rationale                                                                                   | Date        |
| --------------------------------------------------- | ------------------------------------------------------------------------------------------- | ----------- |
| ~~CLI/TUI over web GUI~~ → **Web GUI over CLI/TUI** | Better UX, visual feedback, easier for non-technical users, leverages existing Vue.js stack | 26 Jan 2026 |
| Reuse existing parsers for validation               | Avoid duplication, ensure consistency with main RepoSense                                   | 26 Jan 2026 |
| Progressive disclosure (3 stages)                   | Balance simplicity for beginners with power for experts                                     | 26 Jan 2026 |
| Embedded web server approach                        | No separate deployment, works locally, direct file access like CLI                          | 26 Jan 2026 |
| Multi-step wizard (4 steps)                         | Organized flow, less overwhelming than single page form                                     | 26 Jan 2026 |
| Separate app in frontend/wizard/                    | Avoid refactoring existing code, leverage component reuse, zero impact on report viewer     | 29 Jan 2026 |
| Auto-detection on user request                      | Mitigate loading time issues for large repositories                                         | 29 Jan 2026 |
| Server lifecycle prompt                             | Inform user of file location and project setup before offering to close or continue         | 29 Jan 2026 |

---

**Document End**
| Vue.js best practice structure with src/ directory | Standard conventions: views/, composables/, services/, assets/, proper separation of concerns | 29 Jan 2026 |
