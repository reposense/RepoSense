{% set title = "Sharing reports" %}
<frontmatter>
title: "{{ title | safe }}"
pageNav: 3
</frontmatter>

{% from 'scripts/macros.njk' import embed with context %}

<h1 class="display-4"><md>{{ title }}</md></h1>

<div class="lead">

**Often, you would want to share the RepoSense report with others.** For example, a teacher using RepoSense for a programming class might want to share the report privately with tutors or publish it so that everyone can see it.

</div>

The sections below explain various ways of sharing a RepoSense report.

<!-- ------------------------------------------------------------------------------------------------------ -->

### Share privately

To share a RepoSense report privately, simply find a way to share the folder containing the report (by default, it will be in a folder named `reposense-report`). For example, you can zip that folder and share it with the intended recipients.

You can point the recipients to the [_**Using reports**_](usingReports.html) section for guidance on how to view reports.

<!-- ------------------------------------------------------------------------------------------------------ -->

### Publish on the web

As RepoSense reports are in a web page format, you can publish a report by simply uploading it onto any web hosting service. Given below are several options that not only allow to publish reports, but **also enable various levels of automating** the entire process (e.g., automatically update the report daily).

{{ embed("Appendix: **Using RepoSense with GitHub Actions**", "withGithubActions.md") }}

{{ embed("Appendix: **Using RepoSense with Netlify**", "withNetlify.md") }}

<!-- ------------------------------------------------------------------------------------------------------ -->

### Embeddable Widgets

Published reports can additionally be embedded in other websites through `iframes`. Simply click the clipboard icon to generate and copy the iframe for your desired section of the report for either a single ramp chart or a group of ramp charts. Paste the iframe in any HTML supported document to render it.

A sample iframe would look like:

```html
<iframe src="XXX" frameborder="0" width="800px" height="XXXpx"></iframe>
```

Adjust the width and height to the desired dimensions as required.
