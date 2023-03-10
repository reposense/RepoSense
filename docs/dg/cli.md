{% set title = "Appendix: CLI syntax reference" %}
<frontmatter>
  title: "{{ title | safe }}"
  pageNav: 3
</frontmatter>

<h1 class="display-4"><md>{{ title }}</md></h1>

The section below provides explanations for flags used in system testing. For other flags, please visit the CLI
syntax reference under Appendix section of User Guide.

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--test-mode`

**`--test-mode`**: Enables test mode behavior.

<box type="info" seamless>

* Used in `ConfigSystemTest`.
* Some test cases requires `AuthorConfiguration.hasAuthorConfigFile` to be set to `false` to pass. The exact
reason is unknown as the test cases are pretty old to track back. Tracing this shows that there is something to do
with `AnnotatorAnalyzer.findAuthorInLine()` where `AuthorConfiguration.hasAuthorConfigFile()` is called.
* Can be used for behaviors specific to test code.
  * E.g. `--fresh-cloning`. Fresh cloning is always `false` when running RepoSense normally, and is only used in
  system tests.

</box>

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--fresh-cloning`

**`--fresh-cloning`**: Clones the repo again if it has been cloned before.

<box type="info" seamless>

* Used in `ConfigSystemTest`.
* Some test cases performs shallow cloning while some does not. Fresh cloning ensures that the test cases that does
not perform shallow cloning will clone the repo again if the previous test case uses shallow cloning, ensuring
correctness of the analysis.
* Requires `--test-mode` flag to be enabled.

</box>
