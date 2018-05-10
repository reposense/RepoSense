package reposense.analyzer;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.dataObject.Author;
import reposense.dataObject.FileInfo;
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
        Assert.assertTrue(isFileExistence("annotationTest.java", files));
        Assert.assertTrue(isFileExistence("blameTest.java", files));
        Assert.assertTrue(isFileExistence("newPos" + File.separator + "movedFile.java", files));
        Assert.assertFalse(isFileExistence("inMasterBranch.java", files)); //empty file
    }

    private boolean isFileExistence(String filePath, List<FileInfo> files) {
        for (FileInfo file : files) {
            if (file.getPath().equals(filePath)) {
                return true;
            }
        }
        return false;
    }
}
