package reposense.authorship;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.authorship.model.FileInfo;
import reposense.git.GitChecker;
import reposense.model.Author;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;


public class FileInfoExtractorTest extends GitTestTemplate {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "resources", "FileInfoExtractorTest");
    private static final Path FILE_WITH_SPECIAL_CHARACTER = TEST_DATA_FOLDER.resolve("fileWithSpecialCharacters.txt");
    private static final Path FILE_WITHOUT_SPECIAL_CHARACTER = TEST_DATA_FOLDER
            .resolve("fileWithoutSpecialCharacters.txt");

    private static final String WINDOWS_ILLEGAL_FILE_NAME_BRANCH = "windows-illegal-filename";
    private static final String EDITED_FILE_INFO_BRANCH = "getEditedFileInfos-test";
    private static final String FEBRUARY_EIGHT_COMMIT_HASH = "768015345e70f06add2a8b7d1f901dc07bf70582";
    private static final String OCTOBER_SEVENTH_COMMIT_HASH = "b28dfac5bd449825c1a372e58485833b35fdbd50";

    @Test
    public void extractFileInfosTest() {
        config.getAuthorAliasMap().put(MAIN_AUTHOR_NAME, new Author(MAIN_AUTHOR_NAME));
        config.getAuthorAliasMap().put(FAKE_AUTHOR_NAME, new Author(FAKE_AUTHOR_NAME));
        GitChecker.checkout(config.getRepoRoot(), TEST_COMMIT_HASH);
        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);
        Assert.assertEquals(6, files.size());
        Assert.assertTrue(isFileExistence(Paths.get("README.md"), files));
        Assert.assertTrue(isFileExistence(Paths.get("annotationTest.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("blameTest.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("newPos/movedFile.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("inMasterBranch.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("newFile.java"), files));
    }

    @Test
    public void extractFileInfos_sinceDateFebrauaryNineToLatestCommit_success() {
        Date date = TestUtil.getDate(2018, Calendar.FEBRUARY, 9);
        config.setSinceDate(date);

        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);
        Assert.assertEquals(4, files.size());

        // files edited within commit range
        Assert.assertTrue(isFileExistence(Paths.get("README.md"), files));
        Assert.assertTrue(isFileExistence(Paths.get("newPos/movedFile.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("annotationTest.java"), files));

        // files not edited within commit range
        Assert.assertFalse(isFileExistence(Paths.get("inMasterBranch.java"), files));
        Assert.assertFalse(isFileExistence(Paths.get("blameTest.java"), files));
        Assert.assertFalse(isFileExistence(Paths.get("newFile.java"), files));
    }

    @Test
    public void extractFileInfos_windowsIllegalFileNameBranch_success() {
        GitChecker.checkout(config.getRepoRoot(), WINDOWS_ILLEGAL_FILE_NAME_BRANCH);
        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);

        if (TestUtil.isWindows()) {
            Assert.assertEquals(6, files.size());
        } else {
            Assert.assertEquals(7, files.size());
            Assert.assertTrue(isFileExistence(Paths.get("windows:Illegal?Characters!File(Name).java"), files));
        }
    }

    @Test
    public void extractFileInfos_sinceDateAfterLatestCommit_emptyResult() {
        Date date = TestUtil.getDate(2050, 12, 31);
        config.setSinceDate(date);

        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);
        Assert.assertTrue(files.isEmpty());
    }

    @Test
    public void extractFileInfos_untilDateBeforeFirstCommit_emptyResult() {
        Date date = TestUtil.getDate(2015, 12, 31);
        config.setUntilDate(date);

        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);
        Assert.assertTrue(files.isEmpty());
    }

    @Test
    public void getEditedFileInfos_editFileInfoBranchSinceFebrauryEight_success() {
        GitChecker.checkout(config.getRepoRoot(), EDITED_FILE_INFO_BRANCH);
        List<FileInfo> files = FileInfoExtractor.getEditedFileInfos(config, FEBRUARY_EIGHT_COMMIT_HASH);

        Assert.assertEquals(3, files.size());
        Assert.assertTrue(isFileExistence(Paths.get("README.md"), files));
        Assert.assertTrue(isFileExistence(Paths.get("annotationTest.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("newPos/movedFile.java"), files));

        // file renamed without changing content, not included
        Assert.assertFalse(isFileExistence(Paths.get("renamedFile.java"), files));
    }

    @Test
    public void getEditedFileInfos_editFileInfoBranchSinceFirstCommit_success() {
        GitChecker.checkout(config.getRepoRoot(), EDITED_FILE_INFO_BRANCH);
        List<FileInfo> files = FileInfoExtractor.getEditedFileInfos(config, FIRST_COMMIT_HASH);

        Assert.assertEquals(5, files.size());

        // empty file created, not included
        Assert.assertFalse(isFileExistence(Paths.get("inMasterBranch.java"), files));
    }

    @Test
    public void getEditedFileInfos_windowsIllegalFileNameBranchSinceOctoberFifteen_success() {
        GitChecker.checkout(config.getRepoRoot(), WINDOWS_ILLEGAL_FILE_NAME_BRANCH);
        List<FileInfo> files = FileInfoExtractor.getEditedFileInfos(config, OCTOBER_SEVENTH_COMMIT_HASH);

        if (TestUtil.isWindows()) {
            Assert.assertTrue(files.isEmpty());
        } else {
            Assert.assertEquals(1, files.size());
            Assert.assertTrue(isFileExistence(Paths.get("windows:Illegal?Characters!File(Name).java"), files));
        }
    }

    @Test
    public void generateFileInfo_fileWithSpecialCharacters_correctFileInfoGenerated() {
        FileInfo fileInfo = FileInfoExtractor.generateFileInfo(".", FILE_WITH_SPECIAL_CHARACTER.toString());
        Assert.assertEquals(5, fileInfo.getLines().size());
    }

    @Test
    public void generateFileInfo_fileWithoutSpecialCharacters_correctFileInfoGenerated() {
        FileInfo fileInfo = FileInfoExtractor.generateFileInfo(".", FILE_WITHOUT_SPECIAL_CHARACTER.toString());
        Assert.assertEquals(5, fileInfo.getLines().size());
    }

    private boolean isFileExistence(Path filePath, List<FileInfo> files) {
        return files.stream().anyMatch(file -> Paths.get(file.getPath()).equals(filePath));
    }
}
