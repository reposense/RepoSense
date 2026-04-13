{% set title = "Using the Config Wizard" %}
<frontmatter>
  title: "{{ title | safe }}"
  pageNav: 3
</frontmatter>

<h1 class="display-4"><md>{{ title }}</md></h1>

<div class="lead">

The Config Wizard is an interactive browser-based GUI that guides you through creating a `report-config.yaml` file without writing YAML by hand.
</div>

## Starting the Wizard

Run the following command in your terminal:

```
java -jar RepoSense.jar --config-wizard
```

RepoSense will start a local web server and automatically open the wizard in your default browser at `http://localhost:9000/config-wizard`.

<box type="info" seamless>

* No internet connection is required — the wizard runs entirely on your local machine.
* The wizard does not analyse any repositories. Its sole purpose is to produce a `report-config.yaml` file that you can then pass to RepoSense using `--config`.
</box>

---

## Step 1: Report Settings

Enter a **Report Title** for your generated report. This text will appear as the main heading when you open the report in a browser.

The title defaults to `RepoSense Report` if left blank.

Click **Next →** to proceed.

---

## Step 2: Repos & Branches

Add the repositories you want to analyse.

### Repository URL

Enter the remote URL or local path of the Git repository.

* Remote example: `https://github.com/reposense/RepoSense.git`
* Local example: `/Users/alice/projects/my-project`

After you leave the field (on blur), the wizard validates the URL against RepoSense's location rules and shows a green tick if it is valid.

<box type="info" seamless>

Only valid Git repository locations are accepted. URLs that are not recognised as remote URLs or existing local paths will be rejected with an inline error.
</box>

### Branches

Each repository has at least one branch entry. You can add more by clicking **+ Add Branch**. For each branch you can optionally configure:

| Field | Description | Default |
|-------|-------------|---------|
| Branch name | The Git branch to analyse. | `HEAD` (repository default branch) |
| Since / Until | Date range for the analysis window. | Last month / today |
| Ignore glob list | File path patterns to exclude from analysis. | _(none)_ |
| Ignore authors list | Author IDs to exclude from analysis. | _(none)_ |
| File size limit | Maximum file size (bytes) to include. | 1 000 000 |
| Blurb | Optional description text for the branch. | _(none)_ |

### Authors

Within each branch you can add authors to constrain which contributors are included in the report. For each author you can specify:

| Field | Description |
|-------|-------------|
| Git host ID | The author's username on the Git hosting platform (e.g. GitHub username). **Required.** |
| Display name | Override the name shown in the report. |
| Emails | Additional email addresses associated with the author. |
| Git author names | Additional `user.name` values used by the author. |

Click **+ Add Author** inside a branch card to add a new author entry.

Click **Next →** when all repositories are configured.

---

## Step 3: Groups (Optional)

Groups classify files in a repository into named categories (e.g. `frontend`, `backend`, `tests`). Each group is defined by:

* **Group Name** — a short label shown in the report.
* **Glob Patterns** — one or more file path patterns (e.g. `src/frontend/**`) that determine which files belong to the group.

Groups are **optional**. Click **Skip** to leave all repositories ungrouped, or click **Next →** after defining at least one group per repository.

<box type="info" seamless>

* Group names within a single repository must be unique.
* Each group must have at least one glob pattern.
* Glob patterns are validated against Java's `PathMatcher` syntax. An inline error is shown for invalid patterns.
</box>

---

## Step 4: Review & Generate

The final step shows a summary of your configuration and performs full validation before you generate the file.

### Configuration Summary

A table shows the total number of repositories, branches, authors, and groups you have configured.

### YAML Preview

The right-hand pane displays a live YAML preview of the complete `report-config.yaml` that will be generated. You can copy the full YAML to your clipboard using the **Copy** button at the top of the preview pane.

### Validation

When this step loads, the wizard automatically sends your configuration to the RepoSense parser for a full validation check (Tier 3). The result is displayed below the summary:

* **✓ Configuration is valid** — you may proceed to generation.
* **✗ Validation failed** — an inline error explains the problem. You can go back to fix it, or click **Dismiss and generate anyway** if you are confident the configuration is correct.

The **Generate Config** button is disabled until validation completes or is dismissed.

### Generating the File

Click **Generate Config** to write the file to disk. On success, the wizard shows:

* The path of the generated file (e.g. `./generated-configs/report-config.yaml`).
* The exact command to run your report:

  ```
  java -jar RepoSense.jar --config <path-to-generated-configs-folder>
  ```

Use **Copy command** to copy this command to your clipboard, then paste it in your terminal to generate the report.

Click **Close Wizard** to shut down the local server and close the browser window.

---

## Generated Output

The wizard writes a single file:

```
./generated-configs/report-config.yaml
```

This file uses the same format as a manually authored `report-config.yaml`. See [Report Config Format](reportConfig.md) for the full field reference.

<box type="info" seamless>

If you run the wizard again, the existing file will be overwritten. Keep a copy or rename the file if you want to preserve previous configurations.
</box>

---

## Quitting the Wizard

Click the **Quit** button in the top-right corner of the wizard at any time to shut down the server and close the browser tab.

Alternatively, press **Ctrl + C** (or equivalent) in the terminal where you launched RepoSense.

---

## Troubleshooting

**The browser did not open automatically.**

Manually navigate to `http://localhost:9000/config-wizard`.

**Port 9000 is already in use.**

Stop the process using port 9000 and try again. Future versions of the wizard may allow configuring the port via a flag.

**"Validation failed" on Step 4 but the config looks correct.**

Click **Dismiss and generate anyway**. This bypasses Tier 3 validation and writes the file directly. You can then inspect the YAML and fix any issues manually before running `--config`.

---

## Related Pages

* [Report Config Format](reportConfig.md) — full `report-config.yaml` field reference
* [CLI Syntax Reference](cli.md) — all RepoSense command-line flags
* [Customizing Reports](customizingReports.md) — advanced report customization
