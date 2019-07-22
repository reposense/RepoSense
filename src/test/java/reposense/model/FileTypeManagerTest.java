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
        fileTypeManager = new FileTypeManager();
    }

    @Test
    public void getFileType_withCustomGroups_getsCorrectGroupLabel() {
        fileTypeManager.setGroups(Arrays.asList(
                new FileType("code", Collections.singletonList("src/main/**")),
                new FileType("docs", Arrays.asList("backend/docs/**", "src/docs/**"))
        ));
        Assert.assertEquals("docs", fileTypeManager.getFileType("src/docs/a/b/c/def.md"));
        Assert.assertEquals("code", fileTypeManager.getFileType("src/main/a/b/c/def.java"));
        Assert.assertEquals("other", fileTypeManager.getFileType("src/main.java"));
    }

    @Test
    public void getFileType_noCustomGroups_returnsCorrectFormat() {
        Assert.assertEquals("makefile", fileTypeManager.getFileType("src/build/makefile"));
        Assert.assertEquals("cpp", fileTypeManager.getFileType("src/main/main.cpp"));
    }

    @Test
    public void isInsideFormatsWhiteList_whitelistedFormat_success() {
        Assert.assertTrue(FileTypeManager.isInsideFormatsWhiteList("test.py", FileTypeTest.DEFAULT_TEST_FORMATS));
    }

    @Test
    public void isInsideFormatsWhiteList_notWhitelistedFormat_success() {
        Assert.assertFalse(FileTypeManager.isInsideFormatsWhiteList("test.cpp", FileTypeTest.DEFAULT_TEST_FORMATS));
    }
}
