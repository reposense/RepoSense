<variable name="title">Appendix: Using `@@author` tags</variable>
<frontmatter>
  title: "Appendix: Using `@@author` tags"
</frontmatter>

{% from 'scripts/macros.njk' import embed, step with context %}

<h1 class="display-4"><md>{{ title }}</md></h1>

<div class="lead">

`@@author` tags is a rather invasive but sometimes necessary way to provide more information to RepoSense, by annotating the code being analyzed.
</div>

If you want to override the code authorship deduced by RepoSense (which is based on Git blame/log data), you can use `@@author` tags to specify certain code segments should be credited to a certain author irrespective of git history. An example scenario where this is useful is when a method was originally written by one author but a second author did some minor refactoring to it; in this case RepoSense might attribute the code to the second author while you may want to attribute the code to the first author.

There are 2 types of `@@author` tags:
- Start Tags (format: `@@author AUTHOR_GITHUB_ID`): A start tag indicates the start of a code segment written by the author identified by the `AUTHOR_GITHUB_ID`.
- End Tags (format: `@@author`): Optional. An end tag indicates the end of a code segment written by the author identified by the `AUTHOR_GITHUB_ID` of the start tag.

<box type="info" seamless>

If an end tag is not provided, the code till the next start tag (or the end of the file) will be attributed to the author specified in the start tag above. Use only when necessary to minimize polluting your code with these extra tags.
</box>

The `@@author` tags should be enclosed within a comment, using the comment syntax of the file in concern. Below are some examples:

![author tags](../images/add-author-tags.png)

Note: Remember to **commit** the files after the changes. (reason: RepoSense can see committed code only)

Special thanks to [Collate project](https://github.com/se-edu/collate) for providing the inspiration for this functionality.
