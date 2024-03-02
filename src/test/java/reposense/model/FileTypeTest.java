package reposense.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FileTypeTest {
    public static final List<String> DEFAULT_TEST_FORMAT_STRINGS = Arrays.asList(
            "adoc", "cs", "css", "fxml", "gradle", "html", "java", "js",
            "json", "jsp", "md", "py", "tag", "txt", "xml");
    public static final List<FileType> DEFAULT_TEST_FORMATS = FileType.convertFormatStringsToFileTypes(
            DEFAULT_TEST_FORMAT_STRINGS);
    public static final List<FileType> NO_SPECIFIED_FORMATS = Collections.emptyList();

    @Test
    public void validateFileTypeLabel_validLabel_success() {
        FileType.validateFileTypeLabel("tEsT123");
        FileType.validateFileTypeLabel("t$e's&t Me");
    }

    @Test
    public void validateFileTypeLabel_emptyLabel_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FileType.validateFileTypeLabel(""));
    }

    @Test
    public void validateFileFormat_isAlphaNumeric_success() {
        FileType.validateFileFormat("tEsT123");
    }

    @Test
    public void validateFileFormat_specialCharacters_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> FileType.validateFileFormat("$pull request"));
    }

    @Test
    public void isFileGlobMatching_matchingFormat_success() {
        FileType fileType = FileType.convertStringFormatToFileType("f1");
        Assertions.assertTrue(fileType.isFileGlobMatching("src/test/main.f1"));
    }

    @Test
    public void isFileGlobMatching_nonMatchingFormat_success() {
        FileType fileType = FileType.convertStringFormatToFileType("f1");
        Assertions.assertFalse(fileType.isFileGlobMatching("src/test/main.java"));
    }

    @Test
    public void isFileGlobMatching_matchingGroup_success() {
        FileType fileType = new FileType("test", Collections.singletonList("**/test/*"));
        Assertions.assertTrue(fileType.isFileGlobMatching("src/test/main.java"));
        Assertions.assertTrue(fileType.isFileGlobMatching("src//test/main.java"));
    }

    @Test
    public void isFileGlobMatching_nonMatchingGroup_success() {
        FileType fileType = new FileType("test", Collections.singletonList("**/test/*"));
        Assertions.assertFalse(fileType.isFileGlobMatching("test/main.java"));
    }

    @Test
    public void fileType_cloneFileType_success() throws Exception {
        FileType fileType = new FileType("test", Collections.singletonList("**/test/*"));
        Assertions.assertNotSame(fileType, fileType.clone());
    }
}
