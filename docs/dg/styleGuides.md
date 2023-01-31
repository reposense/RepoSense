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
* **Vue Components**: Follow the [**Vue style guide**](https://vuejs.org/style-guide/), up to the **Recommended** section.
* **Documentation**: Follow the [**Google developer documentation style guide**](https://developers.google.com/style).

## Note on Ternary Operators:
Ternary operators can be used to shorten if-else blocks such as this:
```
LocalDateTime min = ARBITRARY_FIRST_COMMIT_DATE_UTC.withZoneSameInstant(zoneId).toLocalDateTime();
if (!commitInfos.isEmpty()) {
    min = commitInfos.get(0).getTime();
}
return min;
```

The result would look something like this:
```
return (commitInfos.isEmpty())
        ? ARBITRARY_FIRST_COMMIT_DATE_UTC.withZoneSameInstant(zoneId).toLocalDateTime()
        : commitInfos.get(0).getTime();
```

To preserve readability, it is recommended that if-else blocks should only be 
converted to ternary operators if the resultant code can be kept at most 3 lines long
(in accordance to the coding standard).

## Additional Javadoc requirements:
In addition to what has been mentioned in the [**Java** coding standard (SE-EDU)](https://se-education.org/guides/conventions/java/index.html) and [**Google Java Style Guide**](https://google.github.io/styleguide/javaguide.html), we also stipulate the following standards for Javadoc:
* If Javadoc is written for a method, all input parameters should be described in the Javadoc, either in the description with `@code tags` or through `@param` block tags.
  * If `@param` block tags are used, they must be used for all parameters.
  * This is not necessary (although still recommended) for methods with `@Override` annotations if Javadoc is used. However, if the method that is being overriden is part of your code and has Javadoc, all parameters must be described.

Negative Examples:
```
Not okay (Only mentions zoneId parameter):
/**
 * Returns a {@link LocalDateTime} object adjusted for timezone given by {@code zoneId}.
 */
public LocalDateTime adjustTimeZone(LocalDateTime sinceDate, ZoneId zoneId) {
    //Code here
}

Not okay (@param tag used only for zoneId)
/**
 * Returns a {@link LocalDateTime} object by adjusting {@code sinceDate}
 * to the timezone given by {@code zoneId}.
 *
 * @param zoneId The timezone ID to adjust the sinceDate to.
 */
public LocalDateTime adjustTimeZone(LocalDateTime sinceDate, ZoneId zoneId) {
    //Code here
}
```
Positive Example #1:
```
Okay (No @param tags):
/**
 * Returns a {@link LocalDateTime} object by adjusting {@code sinceDate}
 * to the timezone given by {@code zoneId}.
 */
public LocalDateTime adjustTimeZone(LocalDateTime sinceDate, ZoneId zoneId) {
    //Code here
}
```
Positive Example #2:
```
Okay (@param tags used for all inputs):
/**
 * Returns a {@link LocalDateTime} object by adjusting {@code sinceDate}
 * to the timezone given by {@code zoneId}.
 * 
 * @param sinceDate The date prior to the timezone conversion.
 * @param zoneId The timezone ID to adjust the sinceDate to.
 */
public LocalDateTime adjustTimeZone(LocalDateTime sinceDate, ZoneId zoneId) {
    //Code here
}
```

* Within the main code, if an exception is thrown in a method (both header and body) for which Javadoc is written, a `@throws` tag must be used to describe how the exception is likely to arise.
  * This requirement does not apply to test code.
  * One `@throws` tag per unique exception.
  * The order of exceptions in the `@throws` tag block should match that of the method's `throws` statement.
```
Not okay (order of exceptions in tag block and method signature do not match):
/**
 * Returns a {@link LocalDateTime} object from {@code dateString}.
 * 
 * @throws ParseException if {@code dateString} cannot be parsed.
 * @throws NullPointerException if {@code dateString} is null.
 */
public LocalDateTime parseDate(String dateString) throws NullPointerException, ParseException {
    String trimmedString = dateString.toUpperCase(); // NullPointerException may happen here.
    // Code here
}

Should be:
/**
 * Returns a {@link LocalDateTime} object from {@code dateString}.
 * 
 * @throws NullPointerException if {@code dateString} is null.
 * @throws ParseException if {@code dateString} cannot be parsed.
 */
public LocalDateTime parseDate(String dateString) throws NullPointerException, ParseException {
    String trimmedString = dateString.toUpperCase(); // NullPointerException may happen here.
    // Code here
}
```


