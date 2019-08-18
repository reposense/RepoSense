package reposense.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class FileTypeTest {
    public static final List<String> DEFAULT_TEST_FORMAT_STRINGS = Arrays.asList(
            "adoc", "cs", "css", "fxml", "gradle", "html", "java", "js",
            "json", "jsp", "md", "py", "tag", "txt", "xml");
    public static final List<FileType> DEFAULT_TEST_FORMATS = FileType.convertFormatStringsToFileTypes(
            DEFAULT_TEST_FORMAT_STRINGS);
    public static final List<FileType> NO_SPECIFIED_FORMATS = Collections.emptyList();

    @Test
    public void validateFileType_alphaNumeric_success() {
        FileType.validateFileType("tEsT123");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateFileType_nonAlphaNumeric_throwsIllegalArgumentException() {
        FileType.validateFileType("tEsT123.java");
    }

    @Test
    public void isFileGlobMatching_matchingFormat_success() {
        FileType fileType = FileType.convertStringFormatToFileType("f1");
        Assert.assertTrue(fileType.isFileGlobMatching("src/test/main.f1"));
    }

    @Test
    public void isFileGlobMatching_nonMatchingFormat_success() {
        FileType fileType = FileType.convertStringFormatToFileType("f1");
        Assert.assertFalse(fileType.isFileGlobMatching("src/test/main.java"));
    }

    @Test
    public void isFileGlobMatching_matchingGroup_success() {
        FileType fileType = new FileType("test", Collections.singletonList("**/test/*"));
        Assert.assertTrue(fileType.isFileGlobMatching("src/test/main.java"));
        Assert.assertTrue(fileType.isFileGlobMatching("src//test/main.java"));
    }

    @Test
    public void isFileGlobMatching_nonMatchingGroup_success() {
        FileType fileType = new FileType("test", Collections.singletonList("**/test/*"));
        Assert.assertFalse(fileType.isFileGlobMatching("test/main.java"));
    }
}
