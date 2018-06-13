package reposense.git;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import reposense.analyzer.CommitInfoExtractor;
import reposense.dataobject.CommitInfo;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;


public class CommitInfoExtractorTest extends GitTestTemplate {

    @Before
    @Override
    public void before() {
        super.before();
    }

    @Test
    public void withContentTest() {
        List<CommitInfo> commits = CommitInfoExtractor.extractCommitInfos(config);
        Assert.assertFalse(commits.isEmpty());
    }

    @Test
    public void withoutContentTest() {
        Date sinceDate = TestUtil.getDate(2050, Calendar.JANUARY, 1);
        config.setSinceDate(sinceDate);

        List<CommitInfo> commits = CommitInfoExtractor.extractCommitInfos(config);
        Assert.assertTrue(commits.isEmpty());
    }
}
