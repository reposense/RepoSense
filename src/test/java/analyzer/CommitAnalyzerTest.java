package analyzer;

import dataObject.Author;
import dataObject.CommitInfo;
import dataObject.FileInfo;
import git.GitLogger;
import org.junit.Assert;
import org.junit.Test;
import template.GitTestTemplate;
import util.TestConstants;

import java.util.List;

/**
 * Created by matanghao1 on 12/2/18.
 */
public class CommitAnalyzerTest extends GitTestTemplate{

    @Test
    public void commitTest(){
        config.getAuthorAliasMap().put(TestConstants.MAIN_AUTHOR_NAME,new Author(TestConstants.MAIN_AUTHOR_NAME));
        config.getAuthorAliasMap().put(TestConstants.FAKE_AUTHOR_NAME,new Author(TestConstants.FAKE_AUTHOR_NAME));
        config.getAuthorList().add(new Author(TestConstants.MAIN_AUTHOR_NAME));
        config.getAuthorList().add(new Author(TestConstants.FAKE_AUTHOR_NAME));

        CommitInfo commit = getCommit(TestConstants.TEST_COMMIT_HASH);
        CommitAnalyzer.aggregateFileInfos(config,commit);
        Assert.assertEquals(4,commit.getFileinfos().size());
        Assert.assertEquals(2,commit.getAuthorContributionMap().keySet().size());
        Assert.assertEquals(new Integer(14),commit.getAuthorContributionMap().get(new Author(TestConstants.MAIN_AUTHOR_NAME)));
        Assert.assertEquals(new Integer(7),commit.getAuthorContributionMap().get(new Author(TestConstants.FAKE_AUTHOR_NAME)));
    }

    private CommitInfo getCommit(String hash){
        List<CommitInfo> commits = GitLogger.getCommits(config);
        for (CommitInfo commit:commits){
            if (commit.getHash().equals(hash)) return commit;
        }
        return null;
    }

}
