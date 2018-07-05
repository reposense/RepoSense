package reposense.commits;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.commits.model.CommitInfo;
import reposense.template.GitTestTemplate;
import reposense.util.TestConstants;
import reposense.util.TestUtil;

public class CommitInfoExtractorTest extends GitTestTemplate {

    @Test
    public void withContentTest() {
        HashMap<String, List<CommitInfo>> commits = CommitInfoExtractor.extractCommitInfos(config);
        for (String doctype: TestConstants.TEST_DOCTYPES) {
            Assert.assertFalse(commits.get(doctype).isEmpty());
        }
    }

    @Test
    public void withoutContentTest() {
        Date sinceDate = TestUtil.getDate(2050, Calendar.JANUARY, 1);
        config.setSinceDate(sinceDate);
        HashMap<String, List<CommitInfo>> commits = CommitInfoExtractor.extractCommitInfos(config);
        for (String doctype: TestConstants.TEST_DOCTYPES) {
            Assert.assertTrue(commits.get(doctype).isEmpty());
        }
    }
}
