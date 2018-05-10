package reposense.git;

import static reposense.util.TestConstants.FIRST_COMMIT_HASH;
import static reposense.util.TestConstants.MAIN_AUTHOR_NAME;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import reposense.dataObject.Author;
import reposense.dataObject.CommitInfo;
import reposense.template.GitTestTemplate;


public class GitLoggerTest extends GitTestTemplate {

    @Before
    @Override
    public void before() {
        super.before();
    }

    @Test
    public void withoutContentTest() {
        List<CommitInfo> commits = GitLogger.getCommits(config);
        Assert.assertEquals(commits.size(), 0);
    }

    @Test
    public void withContentTest() {
        config.getAuthorAliasMap().put(MAIN_AUTHOR_NAME, new Author(MAIN_AUTHOR_NAME));
        List<CommitInfo> commits = GitLogger.getCommits(config);
        Assert.assertNotEquals(commits.size(), 0);
        CommitInfo commit = commits.get(0);
        Assert.assertEquals(commit.getHash(), FIRST_COMMIT_HASH);
        Assert.assertEquals(commit.getAuthor(), new Author(MAIN_AUTHOR_NAME));
    }
}
