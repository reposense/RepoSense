package reposense.model;

import java.util.Arrays;

import org.junit.Test;

public class FormatTest {
    @Test
    public void validateFormats_alphaNumeric_success() {
        Format.validateFormats(Arrays.asList("java", "7z"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateFormats_nonAlphaNumeric_throwIllegalArgumentException() {
        Format.validateFormats(Arrays.asList(".java"));
    }
}
