<frontmatter>
  title: "Process"
  header: header.md
  footer: footer.md
  siteNav: mainNav.md
  pageNav: 2
</frontmatter>

# Process

We generally follow the  [The OSS-Generic Reference Process](https://oss-generic.github.io/process/) but with the following deviations.

* We will squash the commits when merging a PR. Therefore, there is no need to follow a strict commit organization or write elaborate commit messages for each commit. There is no need to use CanIHasReview tool either. However, when pushing new commits to your PR branch, do tidy up _new_ commits (i.e., commits not yet pushed) e.g., squash noise commits.

* When syncing local branch with upstream, use _merge_ rather than _rebase_ to avoid the need to force push your branch.

## How to contribute
Here's a quick guide on how you can contribute to our repository:
1. [Look for an open issue to work on](https://github.com/reposense/RepoSense/issues), or start a fresh issue to discuss a potential feature or bug.
2. Make a fork of our repository, and create a new branch (with a meaningful name).
3. Start making your changes while following our Coding Standard for
[Java](https://oss-generic.github.io/process/codingStandards/CodingStandard-Java.html),
[JavaScript](https://docs.google.com/document/d/1gZ6WG6HBTJYHAtVkz9kzi_SUuzfXqzO-SvFnLuag2xM/pub?embedded=true),
[CSS](https://oss-generic.github.io/process/codingStandards/CodingStandard-Css.html),
and [HTML](https://oss-generic.github.io/process/codingStandards/CodingStandard-Html.html).
4. Open a pull request and propose a good commit message for your pull request by following our [commit organization](https://oss-generic.github.io/process/docs/FormatsAndConventions.html#commit-message).
5. Let us know when you are ready for a review! If you haven't heard back from us after a couple of days, do probe us by leaving a comment on the pull request.
