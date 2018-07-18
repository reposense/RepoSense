package reposense.authorship;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.authorship.model.FileInfo;
import reposense.git.GitChecker;
import reposense.model.Author;
import reposense.template.GitTestTemplate;
import reposense.util.TestConstants;


public class FileInfoExtractorTest extends GitTestTemplate {
    private static final Path FILE_WITH_SPECIAL_CHARACTER = new File(FileInfoExtractorTest.class.getClassLoader()
            .getResource("FileInfoExtractorTest/fileWithSpecialCharacters.txt").getFile()).toPath();
    private static final Path FILE_WITHOUT_SPECIAL_CHARACTER = new File(FileInfoExtractorTest.class.getClassLoader()
            .getResource("FileInfoExtractorTest/fileWithoutSpecialCharacters.txt").getFile()).toPath();

    @Test
    public void extractFileInfosTest() {
        config.getAuthorAliasMap().put(TestConstants.MAIN_AUTHOR_NAME, new Author(TestConstants.MAIN_AUTHOR_NAME));
        config.getAuthorAliasMap().put(TestConstants.FAKE_AUTHOR_NAME, new Author(TestConstants.FAKE_AUTHOR_NAME));
        GitChecker.checkout(config.getRepoRoot(), TestConstants.TEST_COMMIT_HASH);
        List<FileInfo> files = FileInfoExtractor.extractFileInfos(config);
        Assert.assertEquals(files.size(), 6);
        Assert.assertTrue(isFileExistence(Paths.get("annotationTest.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("blameTest.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("newPos/movedFile.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("inMasterBranch.java"), files));
    }

    @Test
    public void generateFileInfo_fileWithSpecialCharacters_correctFileInfoGenerated() {
        FileInfo fileInfo = FileInfoExtractor.generateFileInfo(FILE_WITH_SPECIAL_CHARACTER.toString(), "");
        Assert.assertEquals(5, fileInfo.getLines().size());
    }

    @Test
    public void generateFileInfo_fileWithoutSpecialCharacters_correctFileInfoGenerated() {
        FileInfo fileInfo = FileInfoExtractor.generateFileInfo(FILE_WITHOUT_SPECIAL_CHARACTER.toString(), "");
        Assert.assertEquals(5, fileInfo.getLines().size());
    }

    private boolean isFileExistence(Path filePath, List<FileInfo> files) {
        return files.stream().anyMatch(file -> Paths.get(file.getPath()).equals(filePath));
    }
}
