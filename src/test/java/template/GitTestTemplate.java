package template;

import analyzer.FileInfoGenerator;
import dataObject.Author;
import dataObject.FileInfo;
import dataObject.LineInfo;
import dataObject.RepoConfiguration;
import git.GitBlamer;
import git.GitCloner;
import git.GitClonerException;
import org.junit.*;
import system.CommandRunner;
import util.Constants;
import util.FileUtil;
import util.TestConstants;

import java.io.File;
import java.util.HashMap;

import static util.TestConstants.TEST_ORG;
import static util.TestConstants.TEST_REPO;

/**
 * Created by matanghao1 on 6/2/18.
 */
public class GitTestTemplate {

    protected RepoConfiguration config;

    @Before
    public void before(){
        config = new RepoConfiguration(TEST_ORG,TEST_REPO,"master");
    }

    @BeforeClass
    public static void beforeClass() throws GitClonerException {
        deleteRepos();
        GitCloner.downloadRepo(TEST_ORG, TEST_REPO,"master");
    }

    @AfterClass
    public static void afterClass(){
        deleteRepos();
    }

    @After
    public void after(){
        CommandRunner.checkOut(TestConstants.LOCAL_TEST_REPO_ADDRESS,"master");
    }

    private static void deleteRepos(){
        FileUtil.deleteDirectory(Constants.REPOS_ADDRESS);
    }


    public FileInfo getBlamedFileInfo(String relativePath){
        FileInfo fileinfo = FileInfoGenerator.generateFileInfo(TestConstants.LOCAL_TEST_REPO_ADDRESS, relativePath);

        config.getAuthorAliasMap().put(TestConstants.MAIN_AUTHOR_NAME,new Author(TestConstants.MAIN_AUTHOR_NAME));
        config.getAuthorAliasMap().put(TestConstants.FAKE_AUTHOR_NAME,new Author(TestConstants.FAKE_AUTHOR_NAME));
        GitBlamer.aggregateBlameInfo(fileinfo,config);
        return fileinfo;
    }

    public boolean checkBlameInfoCorrectness(FileInfo fileinfo){
        for (LineInfo line:fileinfo.getLines()){
            if (line.getContent().startsWith("fake")){
                Assert.assertEquals(line.getAuthor(),new Author(TestConstants.FAKE_AUTHOR_NAME));
            } else {
                Assert.assertNotEquals(line.getAuthor(),new Author(TestConstants.FAKE_AUTHOR_NAME));
            }
        }
        return true;
    }

}
