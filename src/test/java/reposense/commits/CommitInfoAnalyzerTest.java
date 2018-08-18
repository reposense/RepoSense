package reposense.commits;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.commits.model.CommitInfo;
import reposense.commits.model.CommitResult;
import reposense.model.Author;
import reposense.template.GitTestTemplate;

public class CommitInfoAnalyzerTest extends GitTestTemplate {
    private static final int NUMBER_EUGENE_COMMIT = 1;

    @Test
    public void analyzeCommits_allAuthorNoIgnoredCommitsNoDateRange_success() {
        config.getAuthorAliasMap().put(MAIN_AUTHOR_NAME, new Author(MAIN_AUTHOR_NAME));
        config.getAuthorAliasMap().put(FAKE_AUTHOR_NAME, new Author(FAKE_AUTHOR_NAME));
        config.getAuthorAliasMap().put(EUGENE_AUTHOR_NAME, new Author(EUGENE_AUTHOR_NAME));

        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> commitResults = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        Assert.assertEquals(commitInfos.size(), commitResults.size());
    }

    @Test
    public void analyzeCommits_fakeMainAuthorNoIgnoredCommitsNoDateRange_success() {
        config.getAuthorAliasMap().put(MAIN_AUTHOR_NAME, new Author(MAIN_AUTHOR_NAME));
        config.getAuthorAliasMap().put(FAKE_AUTHOR_NAME, new Author(FAKE_AUTHOR_NAME));

        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> commitResults = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        Assert.assertEquals(commitInfos.size() - NUMBER_EUGENE_COMMIT, commitResults.size());
    }

    @Test
    public void analyzeCommits_eugeneAuthorNoIgnoredCommitsNoDateRange_success() {
        config.getAuthorAliasMap().clear();
        config.getAuthorAliasMap().put(EUGENE_AUTHOR_NAME, new Author(EUGENE_AUTHOR_NAME));

        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> commitResults = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        Assert.assertEquals(NUMBER_EUGENE_COMMIT, commitResults.size());
    }

    @Test
    public void analyzeCommits_allAuthorSingleCommitIgnoredNoDateRange_success() {
        config.getAuthorAliasMap().put(MAIN_AUTHOR_NAME, new Author(MAIN_AUTHOR_NAME));
        config.getAuthorAliasMap().put(FAKE_AUTHOR_NAME, new Author(FAKE_AUTHOR_NAME));
        config.getAuthorAliasMap().put(EUGENE_AUTHOR_NAME, new Author(EUGENE_AUTHOR_NAME));
        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        config.setIgnoreCommitList(Collections.singletonList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018));
        List<CommitResult> commitResultsFull = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);
        config.setIgnoreCommitList(
                Collections.singletonList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018.substring(0, 8)));
        List<CommitResult> commitResultsShort = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        Assert.assertEquals(commitResultsShort, commitResultsFull);
        Assert.assertEquals(commitInfos.size() - 1, commitResultsFull.size());
    }

    @Test
    public void analyzeCommits_allAuthorMultipleCommitIgnoredNoDateRange_success() {
        config.getAuthorAliasMap().put(MAIN_AUTHOR_NAME, new Author(MAIN_AUTHOR_NAME));
        config.getAuthorAliasMap().put(FAKE_AUTHOR_NAME, new Author(FAKE_AUTHOR_NAME));
        config.getAuthorAliasMap().put(EUGENE_AUTHOR_NAME, new Author(EUGENE_AUTHOR_NAME));
        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        config.setIgnoreCommitList(
                Arrays.asList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018, EUGENE_AUTHOR_README_FILE_COMMIT_07052018));
        List<CommitResult> commitResultsFull = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);
        config.setIgnoreCommitList(Arrays.asList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018.substring(0, 8),
                EUGENE_AUTHOR_README_FILE_COMMIT_07052018.substring(0, 8)));
        List<CommitResult> commitResultsShort = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        Assert.assertEquals(commitResultsShort, commitResultsFull);
        Assert.assertEquals(commitInfos.size() - 2, commitResultsFull.size());
    }
}
