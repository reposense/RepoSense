package reposense.commits;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.commits.model.CommitInfo;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class CommitInfoExtractorTest extends GitTestTemplate {

    @Test
    public void withContentTest() {
        List<CommitInfo> commits = CommitInfoExtractor.extractCommitInfos(config);
        Assert.assertFalse(commits.isEmpty());
    }

    @Test
    public void withoutContentTest() {
        LocalDateTime sinceDate = TestUtil.getSinceDate(2050, Month.JANUARY.getValue(), 1);
        config.setSinceDate(sinceDate);

        List<CommitInfo> commits = CommitInfoExtractor.extractCommitInfos(config);
        Assert.assertTrue(commits.isEmpty());
    }
}
