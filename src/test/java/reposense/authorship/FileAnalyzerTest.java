package reposense.authorship;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.git.GitCheckout;
import reposense.git.exception.CommitNotFoundException;
import reposense.model.Author;
import reposense.model.CommitHash;
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

        GitCheckout.checkoutDate(config.getRepoRoot(), config.getBranch(), untilDate);
        config.setSinceDate(sinceDate);
        config.setUntilDate(untilDate);

        FileResult fileResult = getFileResult("blameTest.java");
        assertFileAnalysisCorrectness(fileResult);
    }

    @Test
    public void movedFileBlameTestDateRange() throws CommitNotFoundException {
        Date sinceDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 7);
        Date untilDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 9);

        GitCheckout.checkoutDate(config.getRepoRoot(), config.getBranch(), untilDate);
        config.setSinceDate(sinceDate);
        config.setUntilDate(untilDate);

        FileResult fileResult = getFileResult("newPos/movedFile.java");
        assertFileAnalysisCorrectness(fileResult);
    }

    @Test
    public void analyzeFile_blameTestFileIgnoreFakeAuthorCommitFullHash_success() {
        FileInfo fileInfoFull = generateTestFileInfo("blameTest.java");
        config.setIgnoreCommitList(Collections.singletonList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018));
        FileInfoAnalyzer.analyzeFile(config, fileInfoFull);

        FileInfo fileInfoShort = generateTestFileInfo("blameTest.java");
        config.setIgnoreCommitList(
                Collections.singletonList(
                        new CommitHash(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING.substring(0, 8))));
        FileInfoAnalyzer.analyzeFile(config, fileInfoShort);

        Assert.assertEquals(fileInfoFull, fileInfoShort);

        Assert.assertEquals(new Author(MAIN_AUTHOR_NAME), fileInfoFull.getLine(1).getAuthor());
        Assert.assertEquals(new Author(MAIN_AUTHOR_NAME), fileInfoFull.getLine(2).getAuthor());
        Assert.assertEquals(new Author(MAIN_AUTHOR_NAME), fileInfoFull.getLine(4).getAuthor());

        // line added in commit that was ignored
        Assert.assertEquals(Author.UNKNOWN_AUTHOR, fileInfoFull.getLine(3).getAuthor());
    }

    @Test
    public void analyzeFile_blameTestFileIgnoreAllCommit_success() {
        FileInfo fileInfoFull = generateTestFileInfo("blameTest.java");
        config.setIgnoreCommitList(Arrays.asList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018,
                MAIN_AUTHOR_BLAME_TEST_FILE_COMMIT_06022018));
        FileInfoAnalyzer.analyzeFile(config, fileInfoFull);

        FileInfo fileInfoShort = generateTestFileInfo("blameTest.java");
        config.setIgnoreCommitList(CommitHash.convertStringsToCommits(
                Arrays.asList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING.substring(0, 8),
                        MAIN_AUTHOR_BLAME_TEST_FILE_COMMIT_06022018_STRING.substring(0, 8))));
        FileInfoAnalyzer.analyzeFile(config, fileInfoShort);

        Assert.assertEquals(fileInfoFull, fileInfoShort);
        fileInfoFull.getLines().forEach(lineInfo ->
                Assert.assertEquals(Author.UNKNOWN_AUTHOR, lineInfo.getAuthor()));
    }

    @Test
    public void analyzeFile_emailWithAdditionOperator_success() {
        config.setBranch("617-FileAnalyzerTest-analyzeFile_emailWithAdditionOperator_success");
        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());
        Author author = new Author(MINGYI_AUTHOR_NAME);
        config.setAuthorList(Collections.singletonList(author));

        FileInfo fileInfo = FileInfoExtractor.generateFileInfo(config.getRepoRoot(), "pr_617.java");
        FileInfoAnalyzer.analyzeFile(config, fileInfo);

        Assert.assertEquals(1, fileInfo.getLines().size());
        fileInfo.getLines().forEach(lineInfo -> Assert.assertEquals(author, lineInfo.getAuthor()));
    }
}
