package reposense.authorship;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Collections;
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

    @Test
    public void extractFileInfosTest() {
        config.getAuthorAliasMap().put(MAIN_AUTHOR_NAME, new Author(MAIN_AUTHOR_NAME));
        config.getAuthorAliasMap().put(FAKE_AUTHOR_NAME, new Author(FAKE_AUTHOR_NAME));
        GitChecker.checkout(config.getRepoRoot(), TEST_COMMIT_HASH);
        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);
        Assert.assertEquals(files.size(), 6);
        Assert.assertTrue(isFileExistence(Paths.get("annotationTest.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("blameTest.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("newPos/movedFile.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("inMasterBranch.java"), files));
    }

    @Test
    public void extractFileInfos_sinceDateFebrauaryNineToLatestCommit_success() {
        Date date = TestUtil.getDate(2018, Calendar.FEBRUARY, 9);
        config.setSinceDate(date);

        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);
        Assert.assertEquals(3, files.size());

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
    public void extractFileInfos_sinceDateAfterLatestCommit_emptyResult() {
        Date date = TestUtil.getDate(2050, 12, 31);
        config.setSinceDate(date);

        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);
        Assert.assertTrue(files.isEmpty());
    }

    @Test
    public void extractFileInfos_ignoreAllJavaFiles_success() {
        config.setIgnoreGlobList(Collections.singletonList("**.java"));

        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);
        Assert.assertEquals(1, files.size());
    }

    @Test
    public void extractFileInfos_ignoreRootDirectoryJavaFiles_success() {
        config.setIgnoreGlobList(Collections.singletonList("*.java"));

        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);
        Assert.assertEquals(2, files.size());
    }

    @Test
    public void extractFileInfos_ignoreNewPosDirectory_success() {
        config.setIgnoreGlobList(Collections.singletonList("newPos/**"));

        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);
        Assert.assertEquals(5, files.size());
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
