package reposense.authorship;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.git.CommitNotFoundException;
import reposense.git.GitChecker;
import reposense.model.Author;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;


public class FileAnalyzerTest extends GitTestTemplate {

    @Test
    public void blameTest() {
        FileResult fileResult = getFileResult("blameTest.java");
        assertFileAnalysisCorrectness(fileResult);
    }

    @Test
    public void movedFileBlameTest() {
        FileResult fileResult = getFileResult("newPos/movedFile.java");
        assertFileAnalysisCorrectness(fileResult);

    }

    @Test
    public void blameTestDateRange() throws CommitNotFoundException {
        Date sinceDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 6);
        Date untilDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 8);

        GitChecker.checkoutToDate(config.getRepoRoot(), config.getBranch(), untilDate);
        config.setSinceDate(sinceDate);
        config.setUntilDate(untilDate);

        FileResult fileResult = getFileResult("blameTest.java");
        assertFileAnalysisCorrectness(fileResult);
    }

    @Test
    public void movedFileBlameTestDateRange() throws CommitNotFoundException {
        Date sinceDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 7);
        Date untilDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 9);

        GitChecker.checkoutToDate(config.getRepoRoot(), config.getBranch(), untilDate);
        config.setSinceDate(sinceDate);
        config.setUntilDate(untilDate);

        FileResult fileResult = getFileResult("newPos/movedFile.java");
        assertFileAnalysisCorrectness(fileResult);
    }

    @Test
    public void analyzeFile_blameTestFileIgnoreFakeAuthorCommit_success() {
        FileInfo fileInfo = generateTestFileInfo("blameTest.java");
        config.setIgnoreCommitList(Collections.singletonList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018));
        FileInfoAnalyzer.analyzeFile(config, fileInfo);

        Assert.assertEquals(new Author(MAIN_AUTHOR_NAME), fileInfo.getLine(1).getAuthor());
        Assert.assertEquals(new Author(MAIN_AUTHOR_NAME), fileInfo.getLine(2).getAuthor());
        Assert.assertEquals(new Author(MAIN_AUTHOR_NAME), fileInfo.getLine(4).getAuthor());

        // line added in commit that was ignored
        Assert.assertEquals(new Author(Author.UNKNOWN_AUTHOR_GIT_ID), fileInfo.getLine(3).getAuthor());
    }

    @Test
    public void analyzeFile_blameTestFileIgnoreAllCommit_success() {
        FileInfo fileInfo = generateTestFileInfo("blameTest.java");
        config.setIgnoreCommitList(Arrays.asList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018,
                MAIN_AUTHOR_BLAME_TEST_FILE_COMMIT_06022018));
        FileInfoAnalyzer.analyzeFile(config, fileInfo);

        fileInfo.getLines().forEach(lineInfo ->
                Assert.assertEquals(new Author(Author.UNKNOWN_AUTHOR_GIT_ID), lineInfo.getAuthor()));
    }
}
