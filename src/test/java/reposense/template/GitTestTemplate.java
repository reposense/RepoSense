package reposense.template;

import static reposense.util.TestConstants.TEST_ORG;
import static reposense.util.TestConstants.TEST_REPO;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import reposense.authorship.FileInfoAnalyzer;
import reposense.authorship.FileInfoExtractor;
import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.authorship.model.LineInfo;
import reposense.git.GitDownloader;
import reposense.git.GitDownloaderException;
import reposense.model.Author;
import reposense.model.RepoConfiguration;
import reposense.system.CommandRunner;
import reposense.util.Constants;
import reposense.util.FileUtil;
import reposense.util.TestConstants;

public class GitTestTemplate {

    protected RepoConfiguration config;

    @Before
    public void before() {
        config = new RepoConfiguration(TEST_ORG, TEST_REPO, "master");
    }

    @BeforeClass
    public static void beforeClass() throws GitDownloaderException, IOException {
        deleteRepos();
        GitDownloader.downloadRepo(TEST_ORG, TEST_REPO, "master");
    }

    @AfterClass
    public static void afterClass() throws IOException {
        deleteRepos();
    }

    @After
    public void after() {
        CommandRunner.checkout(TestConstants.LOCAL_TEST_REPO_ADDRESS, "master");
    }

    private static void deleteRepos() throws IOException {
        FileUtil.deleteDirectory(Constants.REPOS_ADDRESS);
    }

    public FileInfo generateTestFileInfo(String relativePath) {
        FileInfo fileInfo = FileInfoExtractor.generateFileInfo(TestConstants.LOCAL_TEST_REPO_ADDRESS, relativePath);

        config.getAuthorAliasMap().put(TestConstants.MAIN_AUTHOR_NAME, new Author(TestConstants.MAIN_AUTHOR_NAME));
        config.getAuthorAliasMap().put(TestConstants.FAKE_AUTHOR_NAME, new Author(TestConstants.FAKE_AUTHOR_NAME));

        return fileInfo;
    }

    public FileResult getFileResult(String relativePath) {
        FileInfo fileinfo = generateTestFileInfo(relativePath);
        return FileInfoAnalyzer.analyzeFile(config, fileinfo);
    }

    public void assertFileAnalysisCorrectness(FileResult fileResult) {
        for (LineInfo line : fileResult.getLines()) {
            if (line.getContent().startsWith("fake")) {
                Assert.assertEquals(line.getAuthor(), new Author(TestConstants.FAKE_AUTHOR_NAME));
            } else {
                Assert.assertNotEquals(line.getAuthor(), new Author(TestConstants.FAKE_AUTHOR_NAME));
            }
        }
    }
}
