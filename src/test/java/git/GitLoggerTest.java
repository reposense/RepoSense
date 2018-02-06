package git;

import dataObject.Author;
import dataObject.CommitInfo;
import dataObject.RepoConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import template.GitTemplate;
import util.TestConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static util.TestConstants.*;

/**
 * Created by matanghao1 on 7/2/18.
 */
public class GitLoggerTest extends GitTemplate {
    RepoConfiguration config;

    @Before
    public void before(){
        config = new RepoConfiguration(TEST_ORG,TEST_REPO,"master");


    }

    @Test
    public void withoutContentTest(){
        List<CommitInfo> commits = GitLogger.getCommits(config);
        Assert.assertEquals(commits.size(),0);
    }

    @Test
    public void withContentTest(){
        HashMap<String,Author> aliasMap = new HashMap<>();
        aliasMap.put(MAIN_AUTHOR_NAME,new Author(MAIN_AUTHOR_NAME));
        config.setAuthorAliasMap(aliasMap);
        List<CommitInfo> commits = GitLogger.getCommits(config);
        System.out.println(config.getRepoRoot());
        Assert.assertNotEquals(commits.size(),0);
        CommitInfo commit = commits.get(0);
        Assert.assertEquals(commit.getHash(),FIRST_COMMIT_HASH);
        Assert.assertEquals(commit.getAuthor(),new Author(MAIN_AUTHOR_NAME));
    }
}
