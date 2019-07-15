package reposense.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a file format in {@code RepoConfiguration}.
 */
public class Format {
    public static final List<String> DEFAULT_FORMAT_STRINGS = Arrays.asList(
            "adoc", "cs", "css", "fxml", "gradle", "html", "java", "js",
            "json", "jsp", "md", "py", "tag", "txt", "xml");
    public static final List<Format> DEFAULT_FORMATS = convertStringsToFormats(DEFAULT_FORMAT_STRINGS);
    private static final String FORMAT_VALIDATION_REGEX = "[A-Za-z0-9]+";
    private static final String MESSAGE_ILLEGAL_FORMATS = "The provided format, %s, contains illegal characters.";

    private String format;

    public Format(String format) {
        validateFormat(format);
        this.format = format;
    }

    @Override
    public String toString() {
        return format;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (this == other) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof Format)) {
            return false;
        }

        Format otherFormat = (Format) other;
        return this.format.equals(otherFormat.format);
    }

    @Override
    public int hashCode() {
        return format.hashCode();
    }

    /**
     * Checks that all the strings in the {@code formats} are in valid formats.
     * @throws IllegalArgumentException if any of the values do not meet the criteria.
     */
    public static void validateFormats(List<String> formats) throws IllegalArgumentException {
        formats.forEach(Format::validateFormat);
    }

    /**
     * Converts all the strings in {@code formats} into {@code Format} objects. Returns null if {@code formats} is null.
     * @throws IllegalArgumentException if any of the strings are in invalid formats.
     */
    public static List<Format> convertStringsToFormats(List<String> formats) throws IllegalArgumentException {
        if (formats == null) {
            return null;
        }

        return formats.stream()
                .map(Format::new)
                .collect(Collectors.toList());
    }

    /**
     * Checks that {@code value} is in a valid format.
     * @throws IllegalArgumentException if {@code value} does not meet the criteria.
     */
    private static void validateFormat(String value) throws IllegalArgumentException {
        if (!value.matches(FORMAT_VALIDATION_REGEX)) {
            throw new IllegalArgumentException(String.format(MESSAGE_ILLEGAL_FORMATS, value));
        }
    }
}
