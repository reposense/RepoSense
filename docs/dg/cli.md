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

* Used for `ConfigSystemTest`.

</box>

<!-- ------------------------------------------------------------------------------------------------------ -->

### `--fresh-cloning`

**`--fresh-cloning`**: Clones the repo again if it has been cloned before.

<box type="info" seamless>

* Used for `ConfigSystemTest`.
* Requires `--test-mode` to be enabled.

</box>
