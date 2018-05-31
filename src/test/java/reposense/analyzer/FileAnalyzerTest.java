package reposense.analyzer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.dataobject.Author;
import reposense.dataobject.FileInfo;
import reposense.git.GitChecker;
import reposense.template.GitTestTemplate;
import reposense.util.TestConstants;


public class FileAnalyzerTest extends GitTestTemplate {
    @Test
    public void allFileTest() {
        config.getAuthorAliasMap().put(TestConstants.MAIN_AUTHOR_NAME, new Author(TestConstants.MAIN_AUTHOR_NAME));
        config.getAuthorAliasMap().put(TestConstants.FAKE_AUTHOR_NAME, new Author(TestConstants.FAKE_AUTHOR_NAME));
        GitChecker.checkout(config.getRepoRoot(), TestConstants.TEST_COMMIT_HASH);
        List<FileInfo> files = FileAnalyzer.analyzeAllFiles(config);
        Assert.assertEquals(files.size(), 4);
        Assert.assertTrue(isFileExistence(Paths.get("annotationTest.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("blameTest.java"), files));
        Assert.assertTrue(isFileExistence(Paths.get("newPos/movedFile.java"), files));
        Assert.assertFalse(isFileExistence(Paths.get("inMasterBranch.java"), files)); //empty file
    }

    private boolean isFileExistence(Path filePath, List<FileInfo> files) {
        return files.stream().anyMatch(file -> Paths.get(file.getPath()).equals(filePath));
    }
}
