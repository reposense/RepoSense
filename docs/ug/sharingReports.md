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

#### One-off upload with deed.page

If you only need a link to a report you have already generated (e.g., to share it with tutors for the rest of the semester), you can upload the report folder to [deed.page](https://deed.page) with one command, without creating an account. Choose your own slug, as it becomes part of the URL:

```shell
tar -C reposense-report -czf report.tar.gz .
curl -X PUT --data-binary @report.tar.gz -H "Content-Type: application/gzip" "https://deed.page/v1/sites?slug=cs2103-team-report"
```

The response contains the URL of the published report (e.g., `https://cs2103-team-report.deed.page/`) and a `token`. Keep the token private: sending it in an `Authorization: Bearer <token>` header when you upload again replaces the report at the same URL. Note that this option does not regenerate the report for you, and reports on the free plan show a small deed.page badge. Size limits are listed in the [deed.page docs](https://deed.page/docs).

<!-- ------------------------------------------------------------------------------------------------------ -->

### Embeddable Widgets

Published reports can additionally be embedded in other websites through `iframes`. Simply click the clipboard icon to generate and copy the iframe for your desired section of the report for either a single ramp chart or a group of ramp charts. Paste the iframe in any HTML supported document to render it.

A sample iframe would look like:

```html
<iframe src="XXX" frameborder="0" width="800px" height="XXXpx"></iframe>
```

Adjust the width and height to the desired dimensions as required.
