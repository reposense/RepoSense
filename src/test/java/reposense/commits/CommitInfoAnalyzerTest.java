package reposense.commits;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import reposense.commits.model.CommitInfo;
import reposense.commits.model.CommitResult;
import reposense.model.Author;
import reposense.model.CommitHash;
import reposense.parser.InvalidLocationException;
import reposense.template.GitTestTemplate;

public class CommitInfoAnalyzerTest extends GitTestTemplate {
    private static final int NUMBER_EUGENE_COMMIT = 1;
    private static final int NUMBER_MINGYI_COMMIT = 1;
    private static final int NUMBER_EMPTY_MESSAGE_COMMIT = 1;

    @Before
    public void before() throws InvalidLocationException {
        super.before();
        config.getAuthorEmailsAndAliasesMap().clear();
    }

    @Test
    public void analyzeCommits_allAuthorNoIgnoredCommitsNoDateRange_success() {
        config.getAuthorEmailsAndAliasesMap().put(MAIN_AUTHOR_NAME, new Author(MAIN_AUTHOR_NAME));
        config.getAuthorEmailsAndAliasesMap().put(FAKE_AUTHOR_NAME, new Author(FAKE_AUTHOR_NAME));
        config.getAuthorEmailsAndAliasesMap().put(EUGENE_AUTHOR_NAME, new Author(EUGENE_AUTHOR_NAME));

        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> commitResults = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        Assert.assertEquals(commitInfos.size(), commitResults.size());
    }

    @Test
    public void analyzeCommits_fakeMainAuthorNoIgnoredCommitsNoDateRange_success() {
        config.getAuthorEmailsAndAliasesMap().put(MAIN_AUTHOR_NAME, new Author(MAIN_AUTHOR_NAME));
        config.getAuthorEmailsAndAliasesMap().put(FAKE_AUTHOR_NAME, new Author(FAKE_AUTHOR_NAME));

        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> commitResults = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        Assert.assertEquals(commitInfos.size() - NUMBER_EUGENE_COMMIT, commitResults.size());
    }

    @Test
    public void analyzeCommits_eugeneAuthorNoIgnoredCommitsNoDateRange_success() {
        config.getAuthorEmailsAndAliasesMap().put(EUGENE_AUTHOR_NAME, new Author(EUGENE_AUTHOR_NAME));

        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> commitResults = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        Assert.assertEquals(NUMBER_EUGENE_COMMIT, commitResults.size());
    }

    @Test
    public void analyzeCommits_allAuthorSingleCommitIgnoredNoDateRange_success() {
        config.getAuthorEmailsAndAliasesMap().put(MAIN_AUTHOR_NAME, new Author(MAIN_AUTHOR_NAME));
        config.getAuthorEmailsAndAliasesMap().put(FAKE_AUTHOR_NAME, new Author(FAKE_AUTHOR_NAME));
        config.getAuthorEmailsAndAliasesMap().put(EUGENE_AUTHOR_NAME, new Author(EUGENE_AUTHOR_NAME));
        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        config.setIgnoreCommitList(Collections.singletonList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018));
        List<CommitResult> commitResultsFull = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);
        config.setIgnoreCommitList(
                Collections.singletonList(
                        new CommitHash(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING.substring(0, 8))));
        List<CommitResult> commitResultsShort = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        Assert.assertEquals(commitResultsShort, commitResultsFull);
        Assert.assertEquals(commitInfos.size() - 1, commitResultsFull.size());
    }

    @Test
    public void analyzeCommits_allAuthorMultipleCommitIgnoredNoDateRange_success() {
        config.getAuthorEmailsAndAliasesMap().put(MAIN_AUTHOR_NAME, new Author(MAIN_AUTHOR_NAME));
        config.getAuthorEmailsAndAliasesMap().put(FAKE_AUTHOR_NAME, new Author(FAKE_AUTHOR_NAME));
        config.getAuthorEmailsAndAliasesMap().put(EUGENE_AUTHOR_NAME, new Author(EUGENE_AUTHOR_NAME));
        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        config.setIgnoreCommitList(
                Arrays.asList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018, EUGENE_AUTHOR_README_FILE_COMMIT_07052018));
        List<CommitResult> commitResultsFull = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);
        config.setIgnoreCommitList(CommitHash.convertStringsToCommits(Arrays.asList(
                FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING.substring(0, 8),
                EUGENE_AUTHOR_README_FILE_COMMIT_07052018_STRING.substring(0, 8))));
        List<CommitResult> commitResultsShort = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        Assert.assertEquals(commitResultsShort, commitResultsFull);
        Assert.assertEquals(commitInfos.size() - 2, commitResultsFull.size());
    }

    @Test
    public void analyzeCommits_noCommitMessage_success() {
        config.setBranch("empty-commit-message");
        config.getAuthorEmailsAndAliasesMap().clear();
        config.getAuthorEmailsAndAliasesMap().put(YONG_AUTHOR_NAME, new Author(YONG_AUTHOR_NAME));

        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> commitResults = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);
        commitResults.removeIf(s -> !s.getMessageTitle().isEmpty());

        Assert.assertEquals(NUMBER_EMPTY_MESSAGE_COMMIT, commitResults.size());
    }

    @Test
    public void analyzeCommits_emailWithAdditionOperator_success() {
        config.setBranch("617-FileAnalyzerTest-analyzeFile_emailWithAdditionOperator_success");
        Author author = new Author(MINGYI_AUTHOR_NAME);
        config.setAuthorList(Collections.singletonList(author));

        List<CommitInfo> commitInfos = CommitInfoExtractor.extractCommitInfos(config);
        List<CommitResult> commitResults = CommitInfoAnalyzer.analyzeCommits(commitInfos, config);

        Assert.assertEquals(NUMBER_MINGYI_COMMIT, commitResults.size());
    }
}
