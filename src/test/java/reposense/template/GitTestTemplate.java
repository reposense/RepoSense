package reposense.template;

import static reposense.util.TestConstants.TEST_ORG;
import static reposense.util.TestConstants.TEST_REPO;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import reposense.analyzer.FileInfoGenerator;
import reposense.dataObject.Author;
import reposense.dataObject.FileInfo;
import reposense.dataObject.LineInfo;
import reposense.dataObject.RepoConfiguration;
import reposense.git.GitBlamer;
import reposense.git.GitCloner;
import reposense.git.GitClonerException;

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
    public static void beforeClass() throws GitClonerException {
        deleteRepos();
        GitCloner.downloadRepo(TEST_ORG, TEST_REPO, "master");
    }

    @AfterClass
    public static void afterClass() {
        deleteRepos();
    }

    @After
    public void after() {
        CommandRunner.checkOut(TestConstants.LOCAL_TEST_REPO_ADDRESS, "master");
    }

    private static void deleteRepos() {
        FileUtil.deleteDirectory(Constants.REPOS_ADDRESS);
    }


    public FileInfo getBlamedFileInfo(String relativePath) {
        FileInfo fileinfo = FileInfoGenerator.generateFileInfo(TestConstants.LOCAL_TEST_REPO_ADDRESS, relativePath);

        config.getAuthorAliasMap().put(TestConstants.MAIN_AUTHOR_NAME, new Author(TestConstants.MAIN_AUTHOR_NAME));
        config.getAuthorAliasMap().put(TestConstants.FAKE_AUTHOR_NAME, new Author(TestConstants.FAKE_AUTHOR_NAME));
        GitBlamer.aggregateBlameInfo(fileinfo, config);
        return fileinfo;
    }

    public boolean checkBlameInfoCorrectness(FileInfo fileinfo) {
        for (LineInfo line : fileinfo.getLines()) {
            if (line.getContent().startsWith("fake")) {
                Assert.assertEquals(line.getAuthor(), new Author(TestConstants.FAKE_AUTHOR_NAME));
            } else {
                Assert.assertNotEquals(line.getAuthor(), new Author(TestConstants.FAKE_AUTHOR_NAME));
            }
        }
        return true;
    }

}
