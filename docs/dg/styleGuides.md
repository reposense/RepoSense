<variable name="title">Appendix: Style guides</variable>
<frontmatter>
  title: "{{ title | safe }}"
</frontmatter>

<h1 class="display-4"><md>{{ title }}</md></h1>

<div class="lead">

Our coding standards are mostly based on those at [se-education.org/guides](https://se-education.org/guides).
</div>

* [**CSS** coding standard](https://se-education.org/guides/conventions/css.html)
* [**Files/folders** naming conventions](https://se-education.org/guides/conventions/files.html)
* [**HTML** coding standard](https://se-education.org/guides/conventions/html.html)
* [**Markdown/MarkBind** coding standard](https://se-education.org/guides/conventions/markdown.html)
* [**Java** coding standard](https://se-education.org/guides/conventions/java/index.html)
* [**JavaScript** coding standard](https://se-education.org/guides/conventions/javascript.html)
* **Documentation**: Follow the [**Google developer documentation style guide**](https://developers.google.com/style).

## Note on Ternary Operators:
Ternary operators can be used to shorten if-else blocks such as this:
```
Date min = new Date(Long.MIN_VALUE);
if (!commitInfos.isEmpty()) {
    min = commitInfos.get(0).getTime();
}
```

The result would look something like this:
```
Date min = commitInfos.isEmpty() ? new Date(Long.MIN_VALUE) : commitInfos.get(0).getTime();
```

To preserve readability, it is recommended that if-else blocks should only be 
converted to ternary operators if the resultant code can be kept at most 3 lines long
(in accordance to the coding standard).
