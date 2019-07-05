package reposense.model;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a file format in {@code RepoConfiguration}.
 */
public class Format {
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
     * Returns true if the {@code relativePath}'s file type is inside {@code formatsWhiteList}.
     */
    public static boolean isInsideWhiteList(String relativePath, List<Format> formatsWhiteList) {
        return formatsWhiteList.stream().anyMatch(format -> relativePath.endsWith(format.toString()));
    }

    public static String getFileFormat(String relativePath) {
        String[] tok = relativePath.split("[./\\\\]");
        return tok[tok.length - 1];
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
