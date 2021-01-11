<variable name="title">Appendix: Troubleshooting</variable>
<frontmatter>
  title: "{{ title | safe }}"
  pageNav: 3
</frontmatter>

<h1 class="display-4"><md>{{ title }}</md></h1>

<!-- ------------------------------------------------------------------------------------------------------ -->

### Contributions missing in the ramp chart (but appear in the contribution bar and code panel)

This is probably a case of giving an incorrect author name alias (or github ID) in your [author-config file](#author-config-csv).<br>
Please refer to [A Note About Git Author Name](#a-note-about-git-author-name) above on how to find out the correct author name you are using, and how to change it.<br>
Also ensure that you have added all author name aliases that you may be using (if you are using multiple computers or have previously changed your author name).<br>
Alternatively, you may choose to configure *RepoSense* to track using your GitHub email instead in your [standalone config file](#provide-data-using-a-json-config-file) or [author-config file](#author-config-csv), which is more accurate compared to author name aliases. The associated GitHub email you are using can be found in your [GitHub settings](https://github.com/settings/emails).

<!-- ------------------------------------------------------------------------------------------------------ -->

### Contribution bar and code panel is empty (despite a non-empty ramp chart)

The contribution bar and code panel records the lines you have authored to the **latest** commit of the repository and branch you are analyzing. As such, it is possible that while you have lots of commit contributions, your final authorship contribution is low if you have only deleted lines, someone else have overwritten your code and taken authorship for it (currently, *RepoSense* does not have functionality to track overwritten lines).<br>
It is also possible that another user has overriden the authorship of your lines using the [@@author tags](#provide-data-using-author-tags).

<!-- ------------------------------------------------------------------------------------------------------ -->

### RepoSense is not using the standalone config file in my local repository

Ensure that you have committed the changes to your standalone config file first before running the analysis, as *RepoSense* is unable to detect uncommitted changes to your local repository.

<!-- ------------------------------------------------------------------------------------------------------ -->

### RepoSense fails on Windows (but works on Linux/Mac OS)

It is possible you may have some file names with [special characters](https://docs.microsoft.com/en-us/windows/desktop/FileIO/naming-a-file#naming-conventions) in them, which is disallowed in Windows OS. As such, *RepoSense* is unable to fully clone your repository, thus failing the analysis.

<!-- ------------------------------------------------------------------------------------------------------ -->

### Some files are not captured by RepoSense

The files may be [binary files](https://en.wikipedia.org/wiki/Binary_file). *RepoSense* does not analyze binary files. Common binary files include images (`.jpg`, `.png`), applications (`.exe`), zip files (`.zip`, `.rar`) and certain document types (`.docx`, `.pptx`).
