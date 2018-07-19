package reposense.authorship;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.authorship.model.FileInfo;
import reposense.git.GitChecker;
import reposense.template.GitTestTemplate;

public class FileInfoExtractorTest extends GitTestTemplate {
    private static final Path FILE_WITH_SPECIAL_CHARACTER = new File(FileInfoExtractorTest.class.getClassLoader()
            .getResource("FileInfoExtractorTest/fileWithSpecialCharacters.txt").getFile()).toPath();
    private static final Path FILE_WITHOUT_SPECIAL_CHARACTER = new File(FileInfoExtractorTest.class.getClassLoader()
            .getResource("FileInfoExtractorTest/fileWithoutSpecialCharacters.txt").getFile()).toPath();

    @Test
    public void extractFileInfos_latestCommit_success() {
        GitChecker.checkout(config.getRepoRoot(), config.getBranch());
        List<FileInfo> fileInfos = FileInfoExtractor.extractFileInfos(config);

        Assert.assertEquals(6, fileInfos.size());
        Assert.assertTrue(isFileExistence(Paths.get("annotationTest.java"), fileInfos));
        Assert.assertTrue(isFileExistence(Paths.get("blameTest.java"), fileInfos));
        Assert.assertTrue(isFileExistence(Paths.get("newPos/movedFile.java"), fileInfos));
        Assert.assertTrue(isFileExistence(Paths.get("newFile.java"), fileInfos));
        Assert.assertTrue(isFileExistence(Paths.get("inMasterBranch.java"), fileInfos));
        Assert.assertTrue(isFileExistence(Paths.get("README.md"), fileInfos));
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

    /**
     * Returns true if the {@code filePath} exists inside {@code files}.
     */
    private boolean isFileExistence(Path filePath, List<FileInfo> files) {
        return files.stream().anyMatch(file -> Paths.get(file.getPath()).equals(filePath));
    }
}
