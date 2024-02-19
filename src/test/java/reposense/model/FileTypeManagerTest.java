package reposense.model;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FileTypeManagerTest {
    private FileTypeManager fileTypeManager;

    @BeforeEach
    public void setUp() {
        fileTypeManager = new FileTypeManager(Collections.emptyList());
    }

    @Test
    public void getFileType_withCustomGroups_getsCorrectGroupLabel() {
        fileTypeManager.setGroups(Arrays.asList(
                new FileType("code", Collections.singletonList("src/main/**")),
                new FileType("docs", Arrays.asList("backend/docs/**", "src/docs/**"))
        ));
        Assertions.assertEquals("docs", fileTypeManager.getFileType("src/docs/a/b/c/def.md").toString());
        Assertions.assertEquals("code", fileTypeManager.getFileType("src/main/a/b/c/def.java").toString());
        Assertions.assertEquals("other", fileTypeManager.getFileType("src/main.java").toString());
    }

    @Test
    public void getFileType_noCustomGroups_returnsCorrectFormat() {
        // Files with standard format should return the format name itself
        Assertions.assertEquals("makefile", fileTypeManager.getFileType("src/build/makefile").toString());
        Assertions.assertEquals("cpp", fileTypeManager.getFileType("src/main/main.cpp").toString());
        Assertions.assertEquals("7z", fileTypeManager.getFileType("src/main/archive.7z").toString());

        // Files that are not of the standard format should return "other".
        Assertions.assertEquals("other", fileTypeManager.getFileType("Backup File").toString());
        Assertions.assertEquals("other", fileTypeManager.getFileType("duke.j@va").toString());
    }

    @Test
    public void isInsideFormatsWhiteList_whitelistedFormat_success() {
        fileTypeManager.setFormats(FileTypeTest.DEFAULT_TEST_FORMATS);
        Assertions.assertTrue(fileTypeManager.isInsideWhitelistedFormats("test.py"));
    }

    @Test
    public void isInsideFormatsWhiteList_notWhitelistedFormat_success() {
        fileTypeManager.setFormats(FileTypeTest.DEFAULT_TEST_FORMATS);
        Assertions.assertFalse(fileTypeManager.isInsideWhitelistedFormats("test.cpp"));
    }

    @Test
    public void fileTypeManager_cloneFileTypeManager_success() throws Exception {
        Assertions.assertNotSame(
                this.fileTypeManager.clone(), this.fileTypeManager.clone()
        );
    }
}
