package reposense.authorship;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.git.GitCheckout;
import reposense.model.Author;
import reposense.model.CommitHash;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;


public class FileAnalyzerTest extends GitTestTemplate {
    private static final Date BLAME_TEST_SINCE_DATE = TestUtil.getSinceDate(2018, Calendar.FEBRUARY, 6);
    private static final Date BLAME_TEST_UNTIL_DATE = TestUtil.getUntilDate(2018, Calendar.FEBRUARY, 8);
    private static final Date EMAIL_WITH_ADDITION_TEST_SINCE_DATE =
            TestUtil.getSinceDate(2019, Calendar.MARCH, 28);
    private static final Date EMAIL_WITH_ADDITION_TEST_UNTIL_DATE =
            TestUtil.getUntilDate(2019, Calendar.MARCH, 28);
    private static final Date MOVED_FILE_SINCE_DATE = TestUtil.getSinceDate(2018, Calendar.FEBRUARY, 7);
    private static final Date MOVED_FILE_UNTIL_DATE = TestUtil.getUntilDate(2018, Calendar.FEBRUARY, 9);
    private static final Date SHOULD_INCLUDE_LAST_MODIFIED_IN_LINES_SINCE_DATE =
            TestUtil.getSinceDate(2018, Calendar.FEBRUARY, 7);
    private static final Date LAST_MODIFIED_DATE = new Calendar
            .Builder()
            .setDate(2020, 9, 27)
            .setTimeOfDay(18, 0, 7)
            .build()
            .getTime();
    private static final Date SHOULD_INCLUDE_LAST_MODIFIED_IN_LINES_UNTIL_DATE =
            TestUtil.getUntilDate(2018, Calendar.FEBRUARY, 9);
    private static final String TIME_ZONE_ID_STRING = "Asia/Singapore";


    @Before
    public void before() throws Exception {
        super.before();
        config.setZoneId(TIME_ZONE_ID_STRING);
    }

    @Test
    public void blameTest() {
        config.setSinceDate(BLAME_TEST_SINCE_DATE);
        config.setUntilDate(BLAME_TEST_UNTIL_DATE);
        FileResult fileResult = getFileResult("blameTest.java");
        assertFileAnalysisCorrectness(fileResult);
    }

    @Test
    public void movedFileBlameTest() {
        config.setSinceDate(MOVED_FILE_SINCE_DATE);
        config.setUntilDate(MOVED_FILE_UNTIL_DATE);
        FileResult fileResult = getFileResult("newPos/movedFile.java");
        assertFileAnalysisCorrectness(fileResult);

    }

    @Test
    public void blameTestDateRange() throws Exception {
        GitCheckout.checkoutDate(config.getRepoRoot(), config.getBranch(), BLAME_TEST_UNTIL_DATE);
        config.setSinceDate(BLAME_TEST_SINCE_DATE);
        config.setUntilDate(BLAME_TEST_UNTIL_DATE);

        FileResult fileResult = getFileResult("blameTest.java");
        assertFileAnalysisCorrectness(fileResult);
    }

    @Test
    public void movedFileBlameTestDateRange() throws Exception {
        GitCheckout.checkoutDate(config.getRepoRoot(), config.getBranch(), MOVED_FILE_UNTIL_DATE);
        config.setSinceDate(MOVED_FILE_SINCE_DATE);
        config.setUntilDate(MOVED_FILE_UNTIL_DATE);

        FileResult fileResult = getFileResult("newPos/movedFile.java");
        assertFileAnalysisCorrectness(fileResult);
    }

    @Test
    public void analyzeFile_blameTestFileIgnoreFakeAuthorCommitFullHash_success() {
        config.setSinceDate(BLAME_TEST_SINCE_DATE);
        config.setUntilDate(BLAME_TEST_UNTIL_DATE);
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
        config.setSinceDate(BLAME_TEST_SINCE_DATE);
        config.setUntilDate(BLAME_TEST_UNTIL_DATE);
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
    public void analyzeFile_blameTestFileIgnoreRangedCommit_success() {
        config.setSinceDate(BLAME_TEST_SINCE_DATE);
        config.setUntilDate(BLAME_TEST_UNTIL_DATE);
        FileInfo fileInfoFull = generateTestFileInfo("blameTest.java");
        config.setIgnoreCommitList(FAKE_AUTHOR_BLAME_RANGED_COMMIT_LIST_09022018);
        FileInfoAnalyzer.analyzeFile(config, fileInfoFull);

        FileInfo fileInfoRanged = generateTestFileInfo("blameTest.java");
        String rangedCommit = FAKE_AUTHOR_BLAME_RANGED_COMMIT_ONE_06022018_STRING + ".."
                + FAKE_AUTHOR_BLAME_RANGED_COMMIT_FOUR_08022018_STRING;
        config.setIgnoreCommitList(CommitHash.getHashes(config.getRepoRoot(), config.getBranch(),
                new CommitHash(rangedCommit)).collect(Collectors.toList()));
        FileInfoAnalyzer.analyzeFile(config, fileInfoRanged);

        Assert.assertEquals(fileInfoFull, fileInfoRanged);
        fileInfoFull.getLines().forEach(lineInfo ->
                Assert.assertEquals(Author.UNKNOWN_AUTHOR, lineInfo.getAuthor()));
    }

    @Test
    public void analyzeFile_blameTestFileIgnoreRangedCommitShort_success() {
        config.setSinceDate(BLAME_TEST_SINCE_DATE);
        config.setUntilDate(BLAME_TEST_UNTIL_DATE);
        FileInfo fileInfoFull = generateTestFileInfo("blameTest.java");
        config.setIgnoreCommitList(FAKE_AUTHOR_BLAME_RANGED_COMMIT_LIST_09022018);
        FileInfoAnalyzer.analyzeFile(config, fileInfoFull);

        FileInfo fileInfoRangedShort = generateTestFileInfo("blameTest.java");
        String rangedCommitShort = FAKE_AUTHOR_BLAME_RANGED_COMMIT_ONE_06022018_STRING.substring(0, 8) + ".."
                + FAKE_AUTHOR_BLAME_RANGED_COMMIT_FOUR_08022018_STRING.substring(0, 8);
        config.setIgnoreCommitList(CommitHash.getHashes(config.getRepoRoot(), config.getBranch(),
                new CommitHash(rangedCommitShort)).collect(Collectors.toList()));
        FileInfoAnalyzer.analyzeFile(config, fileInfoRangedShort);

        Assert.assertEquals(fileInfoFull, fileInfoRangedShort);
        fileInfoFull.getLines().forEach(lineInfo ->
                Assert.assertEquals(Author.UNKNOWN_AUTHOR, lineInfo.getAuthor()));
    }

    @Test
    public void analyzeFile_emailWithAdditionOperator_success() {
        config.setSinceDate(EMAIL_WITH_ADDITION_TEST_SINCE_DATE);
        config.setUntilDate(EMAIL_WITH_ADDITION_TEST_UNTIL_DATE);
        config.setBranch("617-FileAnalyzerTest-analyzeFile_emailWithAdditionOperator_success");
        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());
        Author author = new Author(MINGYI_AUTHOR_NAME);
        config.setAuthorList(Collections.singletonList(author));

        FileInfo fileInfo = FileInfoExtractor.generateFileInfo(config.getRepoRoot(), "pr_617.java");
        FileInfoAnalyzer.analyzeFile(config, fileInfo);

        Assert.assertEquals(1, fileInfo.getLines().size());
        fileInfo.getLines().forEach(lineInfo -> Assert.assertEquals(author, lineInfo.getAuthor()));
    }

    @Test
    public void analyzeFile_shouldIncludeLastModifiedDateInLines_success() {
        config.setSinceDate(SHOULD_INCLUDE_LAST_MODIFIED_IN_LINES_SINCE_DATE);
        config.setUntilDate(SHOULD_INCLUDE_LAST_MODIFIED_IN_LINES_UNTIL_DATE);
        config.setIsLastModifiedDateIncluded(true);
        config.setBranch("1345-FileAnalyzerTest-analyzeFile_shouldIncludeLastModifiedDateInLines_success");
        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());
        Author author = new Author(JAMES_AUTHOR_NAME);
        config.setAuthorList(Collections.singletonList(author));

        FileInfo fileInfo = FileInfoExtractor.generateFileInfo(config.getRepoRoot(),
                "includeLastModifiedDateInLinesTest.java");
        FileInfoAnalyzer.analyzeFile(config, fileInfo);

        Assert.assertEquals(4, fileInfo.getLines().size());
        fileInfo.getLines().forEach(lineInfo ->
                Assert.assertEquals(LAST_MODIFIED_DATE, lineInfo.getLastModifiedDate()));
    }
}
