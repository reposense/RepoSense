package reposense.authorship;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.git.GitCheckout;
import reposense.git.GitClone;
import reposense.model.Author;
import reposense.model.CommitHash;
import reposense.model.FileTypeTest;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.parser.InvalidLocationException;
import reposense.template.GitTestTemplate;
import reposense.util.FileUtil;
import reposense.util.TestUtil;


public class FileAnalyzerTest extends GitTestTemplate {
    private static final Date BLAME_TEST_SINCE_DATE = TestUtil.getSinceDate(2018, Calendar.FEBRUARY, 6);
    private static final Date BLAME_TEST_UNTIL_DATE = TestUtil.getUntilDate(2018, Calendar.FEBRUARY, 8);
    private static final Date PREVIOUS_AUTHOR_BLAME_TEST_SINCE_DATE =
            TestUtil.getSinceDate(2018, Calendar.FEBRUARY, 6);
    private static final Date PREVIOUS_AUTHOR_BLAME_TEST_UNTIL_DATE =
            TestUtil.getUntilDate(2021, Calendar.AUGUST, 3);
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
    private static final Date ANALYZE_BINARY_FILES_SINCE_DATE =
            TestUtil.getSinceDate(2017, Calendar.JANUARY, 1);
    private static final Date ANALYZE_BINARY_FILES_UNTIL_DATE =
            TestUtil.getUntilDate(2020, Calendar.JANUARY, 1);
    private static final String TIME_ZONE_ID_STRING = "Asia/Singapore";

    private static final Author[] EXPECTED_LINE_AUTHORS_BLAME_TEST = {
            MAIN_AUTHOR, MAIN_AUTHOR, FAKE_AUTHOR, MAIN_AUTHOR
    };
    private static final Author[] EXPECTED_LINE_AUTHORS_MOVED_FILE = {
            MAIN_AUTHOR, MAIN_AUTHOR, MAIN_AUTHOR, MAIN_AUTHOR
    };
    private static final Author[] EXPECTED_LINE_AUTHORS_PREVIOUS_AUTHORS_BLAME_TEST = {
            MAIN_AUTHOR, MAIN_AUTHOR, FAKE_AUTHOR, MAIN_AUTHOR
    };

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
        assertFileAnalysisCorrectness(fileResult, Arrays.asList(EXPECTED_LINE_AUTHORS_BLAME_TEST));
    }

    @Test
    public void movedFileBlameTest() {
        config.setSinceDate(MOVED_FILE_SINCE_DATE);
        config.setUntilDate(MOVED_FILE_UNTIL_DATE);
        FileResult fileResult = getFileResult("newPos/movedFile.java");
        assertFileAnalysisCorrectness(fileResult, Arrays.asList(EXPECTED_LINE_AUTHORS_MOVED_FILE));
    }

    @Test
    public void blameWithPreviousAuthorsTest() throws Exception {
        /*
         Once PR gets merged in at testrepo alpha, uncomment below

         config.setSinceDate(PREVIOUS_AUTHOR_BLAME_TEST_SINCE_DATE);
         config.setUntilDate(PREVIOUS_AUTHOR_BLAME_TEST_UNTIL_DATE);
         config.setIsFindingPreviousAuthorsPerformed(true);
         config.setBranch(TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH);
         GitCheckout.checkout(config.getRepoRoot(), TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH);
        */

//        blameWithPreviousAuthorsTestRepoConfig.setSinceDate(PREVIOUS_AUTHOR_BLAME_TEST_SINCE_DATE);
//        blameWithPreviousAuthorsTestRepoConfig.setUntilDate(PREVIOUS_AUTHOR_BLAME_TEST_UNTIL_DATE);
//        blameWithPreviousAuthorsTestRepoConfig.setIsFindingPreviousAuthorsPerformed(true);
//
//        createTestIgnoreRevsFile(AUTHOR_TO_IGNORE_BLAME_COMMIT_LIST_03082021);
//        FileResult fileResult = getFileResult(blameWithPreviousAuthorsTestRepoConfig, "blameTest.java");
//        removeTestIgnoreRevsFile();
//
//        assertFileAnalysisCorrectness(fileResult, Arrays.asList(EXPECTED_LINE_AUTHORS_PREVIOUS_AUTHORS_BLAME_TEST));
    }

    @Test
    public void blameTestDateRange() throws Exception {
        GitCheckout.checkoutDate(config.getRepoRoot(), config.getBranch(), BLAME_TEST_UNTIL_DATE);
        config.setSinceDate(BLAME_TEST_SINCE_DATE);
        config.setUntilDate(BLAME_TEST_UNTIL_DATE);

        FileResult fileResult = getFileResult("blameTest.java");
        assertFileAnalysisCorrectness(fileResult, Arrays.asList(EXPECTED_LINE_AUTHORS_BLAME_TEST));
    }

    @Test
    public void movedFileBlameTestDateRange() throws Exception {
        GitCheckout.checkoutDate(config.getRepoRoot(), config.getBranch(), MOVED_FILE_UNTIL_DATE);
        config.setSinceDate(MOVED_FILE_SINCE_DATE);
        config.setUntilDate(MOVED_FILE_UNTIL_DATE);

        FileResult fileResult = getFileResult("newPos/movedFile.java");
        assertFileAnalysisCorrectness(fileResult, Arrays.asList(EXPECTED_LINE_AUTHORS_MOVED_FILE));
    }

    @Test
    public void analyzeFile_blameTestFileIgnoreFakeAuthorCommitFullHash_success() {
        config.setSinceDate(BLAME_TEST_SINCE_DATE);
        config.setUntilDate(BLAME_TEST_UNTIL_DATE);
        FileInfo fileInfoFull = generateTestFileInfo("blameTest.java");
        config.setIgnoreCommitList(Collections.singletonList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018));
        FileInfoAnalyzer.analyzeTextFile(config, fileInfoFull);

        FileInfo fileInfoShort = generateTestFileInfo("blameTest.java");
        config.setIgnoreCommitList(
                Collections.singletonList(
                        new CommitHash(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING.substring(0, 8))));
        FileInfoAnalyzer.analyzeTextFile(config, fileInfoShort);

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
        FileInfoAnalyzer.analyzeTextFile(config, fileInfoFull);

        FileInfo fileInfoShort = generateTestFileInfo("blameTest.java");
        config.setIgnoreCommitList(CommitHash.convertStringsToCommits(
                Arrays.asList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING.substring(0, 8),
                        MAIN_AUTHOR_BLAME_TEST_FILE_COMMIT_06022018_STRING.substring(0, 8))));
        FileInfoAnalyzer.analyzeTextFile(config, fileInfoShort);

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
        FileInfoAnalyzer.analyzeTextFile(config, fileInfoFull);

        FileInfo fileInfoRanged = generateTestFileInfo("blameTest.java");
        String rangedCommit = FAKE_AUTHOR_BLAME_RANGED_COMMIT_ONE_06022018_STRING + ".."
                + FAKE_AUTHOR_BLAME_RANGED_COMMIT_FOUR_08022018_STRING;
        config.setIgnoreCommitList(CommitHash.getHashes(config.getRepoRoot(), config.getBranch(),
                new CommitHash(rangedCommit)).collect(Collectors.toList()));
        FileInfoAnalyzer.analyzeTextFile(config, fileInfoRanged);

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
        FileInfoAnalyzer.analyzeTextFile(config, fileInfoFull);

        FileInfo fileInfoRangedShort = generateTestFileInfo("blameTest.java");
        String rangedCommitShort = FAKE_AUTHOR_BLAME_RANGED_COMMIT_ONE_06022018_STRING.substring(0, 8) + ".."
                + FAKE_AUTHOR_BLAME_RANGED_COMMIT_FOUR_08022018_STRING.substring(0, 8);
        config.setIgnoreCommitList(CommitHash.getHashes(config.getRepoRoot(), config.getBranch(),
                new CommitHash(rangedCommitShort)).collect(Collectors.toList()));
        FileInfoAnalyzer.analyzeTextFile(config, fileInfoRangedShort);

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
        FileInfoAnalyzer.analyzeTextFile(config, fileInfo);

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
        FileInfoAnalyzer.analyzeTextFile(config, fileInfo);

        Assert.assertEquals(4, fileInfo.getLines().size());
        fileInfo.getLines().forEach(lineInfo ->
                Assert.assertEquals(LAST_MODIFIED_DATE, lineInfo.getLastModifiedDate()));
    }

    @Test
    public void analyzeBinaryFile_shouldSetLinesToBeEmpty_success() {
        config.setSinceDate(ANALYZE_BINARY_FILES_SINCE_DATE);
        config.setUntilDate(ANALYZE_BINARY_FILES_UNTIL_DATE);
        config.setBranch("728-FileInfoExtractorTest-getNonBinaryFilesList_directoryWithBinaryFiles_success");
        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());
        List<FileInfo> binaryFileInfos = FileInfoExtractor.extractBinaryFileInfos(config);

        for (FileInfo binaryFileInfo: binaryFileInfos) {
            FileInfoAnalyzer.analyzeBinaryFile(config, binaryFileInfo);
            Assert.assertEquals(0, binaryFileInfo.getLines().size());
        }
    }

    @Test
    public void analyzeBinaryFile_nonExistingFilePath_success() {
        config.setSinceDate(ANALYZE_BINARY_FILES_SINCE_DATE);
        config.setUntilDate(ANALYZE_BINARY_FILES_UNTIL_DATE);
        config.setBranch("728-FileInfoExtractorTest-getNonBinaryFilesList_directoryWithBinaryFiles_success");
        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());

        List<FileInfo> binaryFileInfos = Arrays.asList(new FileInfo("/nonExistingJpgPicture.jpg"),
                new FileInfo("/nonExistingPngPicture.png"));

        for (FileInfo binaryFileInfo: binaryFileInfos) {
            Assert.assertNull(FileInfoAnalyzer.analyzeBinaryFile(config, binaryFileInfo));
        }
    }
}
