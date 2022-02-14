{% set title = "Appendix: Style guides" %}
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

## Additional Javadoc requirements:
In addition to what has been mentioned in the [**Java** coding standard](https://se-education.org/guides/conventions/java/index.html), we also stipulate the following standards for Javadoc:
* Complete sentences should start with a capital letter and end with a full-stop.
  * This applies even for one-line Javadoc. Example:
```
Not okay (No fullstop):
/**
 * Returns a {@code Date} object for {@code sinceDate}
 */
 
Should be:
/**
 * Returns a {@code Date} object for {@code sinceDate}.
 */
```
* Related to the above, each line (except for block tags) should not start with punctuation/special characters (except for the leading asterisk and whitespace). Examples:
```
Not okay (Second line, excluding the opening '/**', starts with a special character):
/**
 * Returns a {@code Date} object for {@code sinceDate}.
 * {@code sinceDate} is obtained from the user CLI.
 */
 
Should be:
/**
 * Returns a {@code Date} object for {@code sinceDate}.
 * The {@code sinceDate} is obtained from the user CLI.
 */
 
Also not okay (Second line, which is a continuation from the first line, starts with a special character):
/**
 * Returns a {@code Date} object by adjusting {@code sinceDate} to the timezone given by 
 * {@code zoneId}.
 */

Should be:
/**
 * Returns a {@code Date} object by adjusting {@code sinceDate} to the timezone 
 * given by {@code zoneId}.
 */
```
* If Javadoc is written for a method, all input parameters should be described in the Javadoc, either in the description or through `@param` tags.
  * If @param tags are used, they must be used for all parameters.

Examples:
```
Not okay (Only mentions zoneId parameter):
/**
 * Returns a {@code Date} object adjusted for timezone given by {@code zoneId}.
 */
public Date adjustTimeZone(Date sinceDate, ZoneId zoneId) {
  //Code here
}

Not okay (@param tag used only for zoneId)
/**
 * Returns a {@code Date} object by adjusting {@code sinceDate}
 * to the timezone given by {@code zoneId}.
 *
 * @param zoneId The timezone ID to adjust the sinceDate to.
 */
public Date adjustTimeZone(Date sinceDate, ZoneId zoneId) {
  //Code here
}

Okay (No @param tags):
/**
 * Returns a {@code Date} object by adjusting {@code sinceDate}
 * to the timezone given by {@code zoneId}.
 */
public Date adjustTimeZone(Date sinceDate, ZoneId zoneId) {
  //Code here
}

Okay (@param tags used for all inputs):
/**
 * Returns a {@code Date} object by adjusting {@code sinceDate}
 * to the timezone given by {@code zoneId}.
 * 
 * @param sinceDate The date prior to the timezone conversion.
 * @param zoneId The timezone ID to adjust the sinceDate to.
 */
public Date adjustTimeZone(Date sinceDate, ZoneId zoneId) {
  //Code here
}
```

* Within the main code, if an exception is thrown in a method (both header and body) for which Javadoc is written, a `@throws` tag must be used to describe how the exception is likely to arise.
  * One `@throws` tag per unique exception.
  * This requirement does not apply to test code.
* Javadoc should have a description and not just block tags.
```
Not okay (No description):
/**
 * @throws ParseException if {@code sinceDate} string cannot be parsed.
 */

Okay:
/**
 * Converts {@code sinceDate} string into a {@code Date} object.
 * 
 * @throws ParseException if {@code sinceDate} string cannot be parsed.
 */
```


