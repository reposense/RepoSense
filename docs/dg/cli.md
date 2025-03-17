{% set title = "Appendix: CLI syntax reference" %}
<frontmatter>
  title: "{{ title | safe }}"
  pageNav: 3
</frontmatter>

<h1 class="display-4"><md>{{ title }}</md></h1>

The section below provides explanations for flags used in system testing. For other flags, please visit the CLI
syntax reference under Appendix section of User Guide.

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--fresh-cloning`

**`--fresh-cloning`**: Clones the repo again if it has been cloned before.

<box type="info" seamless>

* Used in `ConfigSystemTest`.
* Some test cases performs shallow cloning while some does not. Fresh cloning ensures that the test cases that does
not perform shallow cloning will clone the repo again if the previous test case uses shallow cloning, ensuring
correctness of the analysis.

</box>
