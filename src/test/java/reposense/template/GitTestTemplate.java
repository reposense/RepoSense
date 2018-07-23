package reposense.template;

import java.io.File;
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
import reposense.parser.ArgsParser;
import reposense.parser.InvalidLocationException;
import reposense.system.CommandRunner;
import reposense.util.FileUtil;
import reposense.util.TestConstants;

public class GitTestTemplate {
    protected static final String TEST_ORG = "reposense";
    protected static final String TEST_REPO = "testrepo-Alpha";
    protected static final String TEST_REPO_GIT_LOCATION = "https://github.com/" + TEST_ORG + "/" + TEST_REPO + ".git";
    protected static final String LOCAL_TEST_REPO_ADDRESS = FileUtil.REPOS_ADDRESS
            + File.separator + TEST_ORG + "_" + TEST_REPO + "_" + "master" + File.separator + TEST_REPO;
    protected static final String DISK_REPO_DISPLAY_NAME = "testrepo-Alpha_master";
    protected static RepoConfiguration config;

    @Before
    public void before() throws InvalidLocationException {
        config = new RepoConfiguration(TEST_REPO_GIT_LOCATION, "master");
        config.setFormats(ArgsParser.DEFAULT_FORMATS);
    }

    @BeforeClass
    public static void beforeClass() throws GitDownloaderException, IOException, InvalidLocationException {
        deleteRepos();
        config = new RepoConfiguration(TEST_REPO_GIT_LOCATION, "master");
        config.setFormats(ArgsParser.DEFAULT_FORMATS);
        GitDownloader.downloadRepo(config);
    }

    @AfterClass
    public static void afterClass() throws IOException {
        deleteRepos();
    }

    @After
    public void after() {
        CommandRunner.checkout(LOCAL_TEST_REPO_ADDRESS, "master");
    }

    private static void deleteRepos() throws IOException {
        FileUtil.deleteDirectory(FileUtil.REPOS_ADDRESS);
    }

    public FileInfo generateTestFileInfo(String relativePath) {
        FileInfo fileInfo = FileInfoExtractor.generateFileInfo(LOCAL_TEST_REPO_ADDRESS, relativePath);

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
