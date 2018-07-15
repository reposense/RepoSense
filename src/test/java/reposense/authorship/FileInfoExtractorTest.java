package reposense.authorship;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.authorship.model.FileInfo;
import reposense.git.GitChecker;
import reposense.template.GitTestTemplate;

public class FileInfoExtractorTest extends GitTestTemplate {
    private static final String ANNOTATION_AND_MOVED_FILE_NOT_CREATED_COMMIT_HASH = "8e4ca1d";

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "resources", "FileInfoExtractorTest");
    private static final Path FILE_WITH_SPECIAL_CHARACTER = TEST_DATA_FOLDER.resolve("fileWithSpecialCharacters.txt");
    private static final Path FILE_WITHOUT_SPECIAL_CHARACTER = TEST_DATA_FOLDER
            .resolve("fileWithoutSpecialCharacters.txt");

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
    public void extractFileInfos_differentCommit_differentFileInfos() {
        GitChecker.checkout(config.getRepoRoot(), ANNOTATION_AND_MOVED_FILE_NOT_CREATED_COMMIT_HASH);
        config.setLastCommitHash(ANNOTATION_AND_MOVED_FILE_NOT_CREATED_COMMIT_HASH);
        List<FileInfo> oldFileInfos = FileInfoExtractor.extractFileInfos(config);

        GitChecker.checkout(config.getRepoRoot(), config.getBranch());
        List<FileInfo> normalFileInfos = FileInfoExtractor.extractFileInfos(config);

        Assert.assertNotEquals(oldFileInfos, normalFileInfos);
    }

    @Test
    public void incrementFileInfos_oldCommitToLatestCommit_success() {
        GitChecker.checkout(config.getRepoRoot(), ANNOTATION_AND_MOVED_FILE_NOT_CREATED_COMMIT_HASH);
        config.setLastCommitHash(ANNOTATION_AND_MOVED_FILE_NOT_CREATED_COMMIT_HASH);
        List<FileInfo> oldFileInfos = FileInfoExtractor.extractFileInfos(config);

        Assert.assertEquals(4, oldFileInfos.size());
        Assert.assertTrue(isFileExistence(Paths.get("inMasterBranch.java"), oldFileInfos));
        Assert.assertTrue(isFileExistence(Paths.get("blameTest.java"), oldFileInfos));
        Assert.assertTrue(isFileExistence(Paths.get("newFile.java"), oldFileInfos));
        Assert.assertTrue(isFileExistence(Paths.get("README.md"), oldFileInfos));


        // files not yet added in old commit
        Assert.assertFalse(isFileExistence(Paths.get("annotationTest.java"), oldFileInfos));
        Assert.assertFalse(isFileExistence(Paths.get("newPos/movedFile.java"), oldFileInfos));

        // increment old file infos to latest commit hash
        GitChecker.checkout(config.getRepoRoot(), config.getBranch());
        List<FileInfo> updatedFileInfos = FileInfoExtractor.updateFileInfos(config, oldFileInfos);

        Assert.assertEquals(6, updatedFileInfos.size());
        Assert.assertTrue(isFileExistence(Paths.get("inMasterBranch.java"), updatedFileInfos));
        Assert.assertTrue(isFileExistence(Paths.get("blameTest.java"), updatedFileInfos));
        Assert.assertTrue(isFileExistence(Paths.get("newFile.java"), updatedFileInfos));
        Assert.assertTrue(isFileExistence(Paths.get("README.md"), updatedFileInfos));

        // new files added in latest commit
        Assert.assertTrue(isFileExistence(Paths.get("annotationTest.java"), updatedFileInfos));
        Assert.assertTrue(isFileExistence(Paths.get("newPos/movedFile.java"), updatedFileInfos));
    }

    @Test
    public void extractAndIncrementFileInfos_latestCommitHash_sameFileInfos() {
        GitChecker.checkout(config.getRepoRoot(), ANNOTATION_AND_MOVED_FILE_NOT_CREATED_COMMIT_HASH);
        config.setLastCommitHash(ANNOTATION_AND_MOVED_FILE_NOT_CREATED_COMMIT_HASH);
        List<FileInfo> oldFileInfos = FileInfoExtractor.extractFileInfos(config);

        GitChecker.checkout(config.getRepoRoot(), config.getBranch());
        List<FileInfo> normalFileInfos = FileInfoExtractor.extractFileInfos(config);
        List<FileInfo> updatedFileInfos = FileInfoExtractor.updateFileInfos(config, oldFileInfos);

        Assert.assertEquals(normalFileInfos, updatedFileInfos);
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

    /**
     * Returns true if the {@code filePath} exists inside {@code files}.
     */
    private boolean isFileExistence(Path filePath, List<FileInfo> files) {
        return files.stream().anyMatch(file -> Paths.get(file.getPath()).equals(filePath));
    }
}
