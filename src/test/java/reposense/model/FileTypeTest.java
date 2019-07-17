package reposense.model;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

public class FileTypeTest {
    @Test
    public void validateFileType_alphaNumeric_success() {
        FileType fileType = new FileType("tEsT123", Collections.emptyList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateFileType_nonAlphaNumeric_throwsIllegalArgumentException() {
        FileType invalidType = new FileType("tEsT123.java", Collections.emptyList());
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
    public void isFileGlobMatching_matchingGroup_sucess() {
        FileType fileType = new FileType("test", Collections.singletonList("**/test/*"));
        Assert.assertTrue(fileType.isFileGlobMatching("src/test/main.java"));
        Assert.assertTrue(fileType.isFileGlobMatching("src//test/main.java"));
    }

    @Test
    public void isFileGlobMatching_nonMatchingGroup_sucess() {
        FileType fileType = new FileType("test", Collections.singletonList("**/test/*"));
        Assert.assertFalse(fileType.isFileGlobMatching("test/main.java"));
    }
}
