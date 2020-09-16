<variable name="title">Showcase</variable>
<frontmatter>
  title: "{{ title | safe }}"
</frontmatter>

<h1 class="display-3"><md>{{ title }}</md></h1>


### Case 1: Monitoring student programmers (**individual** projects)

* **Scenario:** RepoSense is used to monitor a Software Engineering course in which students build a project over 8 weeks.

* **Links:** [report](https://nus-cs2113-ay1920s2.github.io/ip-dashboard/#sort=groupTitle&groupSelect=groupByAuthors&search=&sortWithin=title&since=2020-01-13&timeframe=commit&mergegroup=false&breakdown=false) | [repo containing the settings](https://github.com/nus-cs2113-AY1920S2/ip-dashboard)

* **Example usages:**
  * To compare students based on the amount of code written, we can sort by contribution, as done in [this view](https://nus-cs2113-ay1920s2.github.io/ip-dashboard/#sort=totalCommits%20dsc&groupSelect=groupByAuthors&search=&sortWithin=title&since=2020-01-13&timeframe=day&mergegroup=false&breakdown=false).
  * [This view](https://nus-cs2113-ay1920s2.github.io/ip-dashboard/#sort=totalCommits%20dsc&groupSelect=groupByAuthors&search=andy&sortWithin=title&since=2020-01-13&timeframe=day&mergegroup=false&breakdown=false&tabOpen=true&tabType=authorship&tabAuthor=andy-aw-why&tabRepo=andy-aw-why%2Fduke%5Bmaster%5D) shows us code written by a specific student.

<!-- ------------------------------------------------------------------------------------------------------ -->

### Case 2: Monitoring student programmers (**team** projects)

* **Scenario:** Similar to case 1 above but this time students are doing team projects.

* **Links:** [report](https://nus-cs2113-ay1920s2.github.io/tp-dashboard/) | [settings](https://github.com/nus-cs2113-AY1920S2/tp-dashboard)

* **Example usages:**
  * To find the breakdown of the work done, we can tick the `breakdown by file type` checkbox, as shown in [this view](https://nus-cs2113-ay1920s2.github.io/tp-dashboard/#search=&sort=groupTitle&sortWithin=title&since=2020-03-01&timeframe=commit&mergegroup=false&groupSelect=groupByRepos&breakdown=true). After that, we can filter out certain file types by un-ticking the file type.
  * To find how teams compare in terms of total work done, we can tick the `merge group` check-box and sort groups by `Contribution`, as seen in [this view](https://nus-cs2113-ay1920s2.github.io/tp-dashboard/#search=&sort=totalCommits&sortWithin=title&since=2020-03-01&timeframe=week&mergegroup=true&groupSelect=groupByRepos&breakdown=false). Also note how <tooltip content="i.e., each ramp represents the work done by the entire team in the whole week">the `granularity` of the ramps is set to `Week`</tooltip> to reduce clutter.
  * [This view](https://nus-cs2113-ay1920s2.github.io/tp-dashboard/#search=&sort=groupTitle&sortWithin=title&since=2020-04-06&timeframe=commit&mergegroup=false&groupSelect=groupByRepos&breakdown=true) shows the activities near the submission deadline (note how some have overshot the deadline and some others show a frenzy of activities very near to the deadline).


<!-- ------------------------------------------------------------------------------------------------------ -->

### Case 3: Monitoring student programmers (**multiple** external projects)

* **Scenario:** Similar to case 1 and 2 above but this time each student works on multiple projects. Furthermore, most projects are external OSS projects not within the control of the teacher.

* **Links:** [report](https://nus-cs3281.github.io/2020-dashboard/#search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=false&groupSelect=groupByAuthors&breakdown=false&since=2019-12-01) | [settings](https://github.com/nus-cs2113-AY1920S2/tp-dashboard)

* **Example usages:**
  * [This view](https://nus-cs3281.github.io/2020-dashboard/#search=&sort=groupTitle&sortWithin=title&since=2019-12-01&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=false&tabOpen=true&tabType=zoom&zA=anubh-v&zR=CATcher-org%2FCATcher%5Bmaster%5D&zACS=118.09969788519638&zS=2019-12-01&zFS=&zU=2020-06-05&zMG=false&zFTF=commit&zFGS=groupByRepos) shows the commit messages written by a specific student.
  * Note how we can use the `group by` drop-down to organize activities around projects or individual authors.
  * Similarly, we can use the `merge all groups` check-box to see the sum of activities in a specific project or by a specific student.
