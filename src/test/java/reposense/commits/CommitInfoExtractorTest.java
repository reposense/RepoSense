package reposense.commits;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reposense.commits.model.CommitInfo;
import reposense.model.RepoConfiguration;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class CommitInfoExtractorTest extends GitTestTemplate {
    private RepoConfiguration config;
    private CommitInfoExtractor commitInfoExtractor = new CommitInfoExtractor();

    @BeforeEach
    public void before() throws Exception {
        super.before();
        config = configs.get();
    }

    @Test
    public void withContentTest() {
        List<CommitInfo> commits = commitInfoExtractor.extractCommitInfos(config);
        Assertions.assertFalse(commits.isEmpty());
    }

    @Test
    public void withoutContentTest() {
        LocalDateTime sinceDate = TestUtil.getSinceDate(2050, Month.JANUARY.getValue(), 1);
        config.setSinceDate(sinceDate);

        List<CommitInfo> commits = commitInfoExtractor.extractCommitInfos(config);
        Assertions.assertTrue(commits.isEmpty());
    }
}
