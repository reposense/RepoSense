package reposense.model;

import java.util.ArrayList;
import java.util.List;

public class FormatList {
    private static final String FORMAT_VALIDATION_REGEX = "[A-Za-z0-9]+";
    private static final String MESSAGE_ILLEGAL_FORMATS = "The provided formats, %s, contains illegal characters.";

    private List<String> formats = new ArrayList<>();

    public List<String> getFormats() {
        return formats;
    }

    public void setFormats(List<String> formats) {
        validateFormats(formats);
        this.formats = formats;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (this == other) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof FormatList)) {
            return false;
        }

        FormatList otherList = (FormatList) other;
        return this.formats.equals(otherList.formats);
    }

    @Override
    public int hashCode() {
        return formats.hashCode();
    }

    /**
     * Checks that all the strings in the {@code formats} are in valid formats.
     * @throws IllegalArgumentException if any of the values do not meet the criteria.
     */
    public static void validateFormats(List<String> formats) throws IllegalArgumentException {
        for (String format: formats) {
            if (!isValidFormat(format)) {
                throw new IllegalArgumentException(String.format(MESSAGE_ILLEGAL_FORMATS, format));
            }
        }
    }

    /**
     * Returns true if the given {@code value} is a valid format.
     */
    private static boolean isValidFormat(String value) throws IllegalArgumentException {
        return value.matches(FORMAT_VALIDATION_REGEX);
    }
}
