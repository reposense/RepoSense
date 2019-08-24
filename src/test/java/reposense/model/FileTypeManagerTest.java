package reposense.model;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FileTypeManagerTest {
    private FileTypeManager fileTypeManager;

    @Before
    public void setUp() {
        fileTypeManager = new FileTypeManager(Collections.emptyList());
    }

    @Test
    public void getFileType_withCustomGroups_getsCorrectGroupLabel() {
        fileTypeManager.setGroups(Arrays.asList(
                new FileType("code", Collections.singletonList("src/main/**")),
                new FileType("docs", Arrays.asList("backend/docs/**", "src/docs/**"))
        ));
        Assert.assertEquals("docs", fileTypeManager.getFileType("src/docs/a/b/c/def.md").toString());
        Assert.assertEquals("code", fileTypeManager.getFileType("src/main/a/b/c/def.java").toString());
        Assert.assertEquals("other", fileTypeManager.getFileType("src/main.java").toString());
    }

    @Test
    public void getFileType_noCustomGroups_returnsCorrectFormat() {
        Assert.assertEquals("makefile", fileTypeManager.getFileType("src/build/makefile").toString());
        Assert.assertEquals("cpp", fileTypeManager.getFileType("src/main/main.cpp").toString());
    }

    @Test
    public void isInsideFormatsWhiteList_whitelistedFormat_success() {
        fileTypeManager.setFormats(FileTypeTest.DEFAULT_TEST_FORMATS);
        Assert.assertTrue(fileTypeManager.isInsideWhitelistedFormats("test.py"));
    }

    @Test
    public void isInsideFormatsWhiteList_notWhitelistedFormat_success() {
        fileTypeManager.setFormats(FileTypeTest.DEFAULT_TEST_FORMATS);
        Assert.assertFalse(fileTypeManager.isInsideWhitelistedFormats("test.cpp"));
    }
}
