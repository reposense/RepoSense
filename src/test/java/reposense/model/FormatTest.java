package reposense.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class FormatTest {
    public static final List<Format> DEFAULT_TEST_FORMATS = Format.convertStringsToFormats(Arrays.asList(
            "adoc", "cs", "css", "fxml", "gradle", "html", "java", "js",
            "json", "jsp", "md", "py", "tag", "txt", "xml"));
    public static final List<Format> NO_SPECIFIED_FORMATS = Format.convertStringsToFormats(Collections.emptyList());

    @Test
    public void validateFormats_alphaNumeric_success() {
        Format.validateFormats(Arrays.asList("java", "7z"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateFormats_nonAlphaNumeric_throwIllegalArgumentException() {
        Format.validateFormats(Arrays.asList(".java"));
    }
}
