package reposense.authorship;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.git.GitCheckout;
import reposense.git.GitVersion;
import reposense.model.Author;
import reposense.model.CommitHash;
import reposense.model.FileType;
import reposense.model.RepoConfiguration;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class FileAnalyzerTest extends GitTestTemplate {

    private static final LocalDateTime BLAME_TEST_SINCE_DATE =
            TestUtil.getSinceDate(2018, Month.FEBRUARY.getValue(), 6);
    private static final LocalDateTime BLAME_TEST_UNTIL_DATE =
            TestUtil.getUntilDate(2018, Month.FEBRUARY.getValue(), 8);
    private static final LocalDateTime PREVIOUS_AUTHOR_BLAME_TEST_SINCE_DATE =
            TestUtil.getSinceDate(2018, Month.FEBRUARY.getValue(), 6);
    private static final LocalDateTime PREVIOUS_AUTHOR_BLAME_TEST_UNTIL_DATE =
            TestUtil.getUntilDate(2021, Month.AUGUST.getValue(), 7);
    private static final LocalDateTime EMAIL_WITH_ADDITION_TEST_SINCE_DATE =
            TestUtil.getSinceDate(2019, Month.MARCH.getValue(), 28);
    private static final LocalDateTime EMAIL_WITH_ADDITION_TEST_UNTIL_DATE =
            TestUtil.getUntilDate(2019, Month.MARCH.getValue(), 28);
    private static final LocalDateTime MOVED_FILE_SINCE_DATE =
            TestUtil.getSinceDate(2018, Month.FEBRUARY.getValue(), 7);
    private static final LocalDateTime MOVED_FILE_UNTIL_DATE =
            TestUtil.getUntilDate(2018, Month.FEBRUARY.getValue(), 9);
    private static final LocalDateTime SHOULD_INCLUDE_LAST_MODIFIED_IN_LINES_SINCE_DATE =
            TestUtil.getSinceDate(2018, Month.FEBRUARY.getValue(), 7);
    private static final LocalDateTime LAST_MODIFIED_DATE =
            LocalDateTime.of(2020, Month.OCTOBER.getValue(), 27, 18, 0, 7);

    private static final LocalDateTime SHOULD_INCLUDE_LAST_MODIFIED_IN_LINES_UNTIL_DATE =
            TestUtil.getUntilDate(2018, Month.FEBRUARY.getValue(), 9);
    private static final LocalDateTime ANALYZE_BINARY_FILES_SINCE_DATE =
            TestUtil.getSinceDate(2017, Month.JANUARY.getValue(), 1);
    private static final LocalDateTime ANALYZE_BINARY_FILES_UNTIL_DATE =
            TestUtil.getUntilDate(2020, Month.JANUARY.getValue(), 1);
    private static final LocalDateTime ANALYZE_LARGE_FILES_SINCE_DATE =
            TestUtil.getSinceDate(2017, Month.JANUARY.getValue(), 1);
    private static final LocalDateTime ANALYZE_LARGE_FILES_UNTIL_DATE =
            TestUtil.getUntilDate(2022, Month.MARCH.getValue(), 8);
    private static final LocalDateTime ANALYZE_FILES_EMPTY_EMAIL_COMMIT_SINCE_DATE =
            TestUtil.getSinceDate(2022, Month.FEBRUARY.getValue(), 10);
    private static final LocalDateTime ANALYZE_FILES_EMPTY_EMAIL_COMMIT_UNTIL_DATE =
            TestUtil.getUntilDate(2022, Month.FEBRUARY.getValue(), 14);

    private static final Author[] EXPECTED_LINE_AUTHORS_BLAME_TEST = {
            MAIN_AUTHOR, MAIN_AUTHOR, FAKE_AUTHOR, MAIN_AUTHOR
    };
    private static final Author[] EXPECTED_LINE_AUTHORS_MOVED_FILE = {
            MAIN_AUTHOR, MAIN_AUTHOR, MAIN_AUTHOR, MAIN_AUTHOR
    };
    private static final Author[] EXPECTED_LINE_AUTHORS_PREVIOUS_AUTHORS_BLAME_TEST = {
            MAIN_AUTHOR, MAIN_AUTHOR, FAKE_AUTHOR, MAIN_AUTHOR
    };

    private RepoConfiguration config;
    private FileInfoExtractor fileInfoExtractor = new FileInfoExtractor();
    private FileInfoAnalyzer fileInfoAnalyzer = new FileInfoAnalyzer();

    @BeforeEach
    public void before() throws Exception {
        super.before();

        config = configs.get();
        config.setZoneId(TIME_ZONE_ID);
        config.addAuthorNamesToAuthorMapEntry(new Author(MAIN_AUTHOR_NAME), MAIN_AUTHOR_NAME);
        config.addAuthorNamesToAuthorMapEntry(new Author(FAKE_AUTHOR_NAME), FAKE_AUTHOR_NAME);
        config.addAuthorNamesToAuthorMapEntry(new Author(IGNORED_AUTHOR_NAME), IGNORED_AUTHOR_NAME);
    }

    @Test
    public void blameTest() {
        config.setSinceDate(BLAME_TEST_SINCE_DATE);
        config.setUntilDate(BLAME_TEST_UNTIL_DATE);
        FileResult fileResult = getFileResult("blameTest.java");
        assertFileAnalysisCorrectness(fileResult, Arrays.asList(EXPECTED_LINE_AUTHORS_BLAME_TEST));
    }

    @Test
    public void blameWithPreviousAuthorsTest() {
        Assumptions.assumeTrue(GitVersion.isGitVersionSufficientForFindingPreviousAuthors());
        config.setSinceDate(PREVIOUS_AUTHOR_BLAME_TEST_SINCE_DATE);
        config.setUntilDate(PREVIOUS_AUTHOR_BLAME_TEST_UNTIL_DATE);
        config.setIsFindingPreviousAuthorsPerformed(true);
        config.setBranch(TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH);

        GitCheckout.checkout(config.getRepoRoot(), TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH);

        createTestIgnoreRevsFile(AUTHOR_TO_IGNORE_BLAME_COMMIT_LIST_07082021);
        FileResult fileResult = getFileResult("blameTest.java");
        removeTestIgnoreRevsFile();

        assertFileAnalysisCorrectness(fileResult, Arrays.asList(EXPECTED_LINE_AUTHORS_PREVIOUS_AUTHORS_BLAME_TEST));
    }

    @Test
    public void movedFileBlameTest() {
        config.setSinceDate(MOVED_FILE_SINCE_DATE);
        config.setUntilDate(MOVED_FILE_UNTIL_DATE);
        FileResult fileResult = getFileResult("newPos/movedFile.java");
        assertFileAnalysisCorrectness(fileResult, Arrays.asList(EXPECTED_LINE_AUTHORS_MOVED_FILE));
    }

    @Test
    public void blameTestDateRange() throws Exception {
        GitCheckout.checkoutDate(config.getRepoRoot(), config.getBranch(), BLAME_TEST_UNTIL_DATE, config.getZoneId());
        config.setSinceDate(BLAME_TEST_SINCE_DATE);
        config.setUntilDate(BLAME_TEST_UNTIL_DATE);

        FileResult fileResult = getFileResult("blameTest.java");
        assertFileAnalysisCorrectness(fileResult, Arrays.asList(EXPECTED_LINE_AUTHORS_BLAME_TEST));
    }

    @Test
    public void blameWithPreviousAuthorsTestDateRange() throws Exception {
        Assumptions.assumeTrue(GitVersion.isGitVersionSufficientForFindingPreviousAuthors());
        config.setSinceDate(PREVIOUS_AUTHOR_BLAME_TEST_SINCE_DATE);
        config.setUntilDate(PREVIOUS_AUTHOR_BLAME_TEST_UNTIL_DATE);
        config.setIsFindingPreviousAuthorsPerformed(true);
        config.setBranch(TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH);

        GitCheckout.checkout(config.getRepoRoot(), TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH);
        GitCheckout.checkoutDate(config.getRepoRoot(), config.getBranch(), PREVIOUS_AUTHOR_BLAME_TEST_UNTIL_DATE,
                config.getZoneId());

        createTestIgnoreRevsFile(AUTHOR_TO_IGNORE_BLAME_COMMIT_LIST_07082021);
        FileResult fileResult = getFileResult("blameTest.java");
        removeTestIgnoreRevsFile();

        assertFileAnalysisCorrectness(fileResult, Arrays.asList(EXPECTED_LINE_AUTHORS_PREVIOUS_AUTHORS_BLAME_TEST));
    }

    @Test
    public void movedFileBlameTestDateRange() throws Exception {
        GitCheckout.checkoutDate(config.getRepoRoot(), config.getBranch(), MOVED_FILE_UNTIL_DATE, config.getZoneId());
        config.setSinceDate(MOVED_FILE_SINCE_DATE);
        config.setUntilDate(MOVED_FILE_UNTIL_DATE);

        FileResult fileResult = getFileResult("newPos/movedFile.java");
        assertFileAnalysisCorrectness(fileResult, Arrays.asList(EXPECTED_LINE_AUTHORS_MOVED_FILE));
    }

    @Test
    public void analyzeTextFile_blameTestFileIgnoreFakeAuthorCommitFullHash_success() {
        config.setSinceDate(BLAME_TEST_SINCE_DATE);
        config.setUntilDate(BLAME_TEST_UNTIL_DATE);
        FileInfo fileInfoFull = fileInfoExtractor.generateFileInfo(config, "blameTest.java");
        config.setIgnoreCommitList(Collections.singletonList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018));
        fileInfoAnalyzer.analyzeTextFile(config, fileInfoFull);

        FileInfo fileInfoShort = fileInfoExtractor.generateFileInfo(config, "blameTest.java");
        config.setIgnoreCommitList(
                Collections.singletonList(
                        new CommitHash(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING.substring(0, 8))));
        fileInfoAnalyzer.analyzeTextFile(config, fileInfoShort);

        Assertions.assertEquals(fileInfoFull, fileInfoShort);

        Assertions.assertEquals(new Author(MAIN_AUTHOR_NAME), fileInfoFull.getLine(1).getAuthor());
        Assertions.assertEquals(new Author(MAIN_AUTHOR_NAME), fileInfoFull.getLine(2).getAuthor());
        Assertions.assertEquals(new Author(MAIN_AUTHOR_NAME), fileInfoFull.getLine(4).getAuthor());

        // line added in commit that was ignored
        Assertions.assertEquals(Author.UNKNOWN_AUTHOR, fileInfoFull.getLine(3).getAuthor());
    }

    @Test
    public void analyzeFile_blameWithPreviousAuthorsIgnoreFirstCommitThatChangedLine_assignLineToUnknownAuthor() {
        Assumptions.assumeTrue(GitVersion.isGitVersionSufficientForFindingPreviousAuthors());
        config.setSinceDate(PREVIOUS_AUTHOR_BLAME_TEST_SINCE_DATE);
        config.setUntilDate(PREVIOUS_AUTHOR_BLAME_TEST_UNTIL_DATE);
        config.setIsFindingPreviousAuthorsPerformed(true);
        config.setBranch(TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH);
        GitCheckout.checkout(config.getRepoRoot(), TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH);

        FileInfo fileInfoFull = fileInfoExtractor.generateFileInfo(config, "blameTest.java");

        config.setIgnoreCommitList(Collections.singletonList(MAIN_AUTHOR_BLAME_TEST_FILE_COMMIT_06022018));
        createTestIgnoreRevsFile(config.getIgnoreCommitList());
        fileInfoAnalyzer.analyzeTextFile(config, fileInfoFull);
        removeTestIgnoreRevsFile();

        FileInfo fileInfoShort = fileInfoExtractor.generateFileInfo(config, "blameTest.java");
        config.setIgnoreCommitList(Collections.singletonList(
                        new CommitHash(MAIN_AUTHOR_BLAME_TEST_FILE_COMMIT_06022018_STRING.substring(0, 8))));
        config.setIgnoreCommitList(createTestIgnoreRevsFile(config.getIgnoreCommitList()));
        fileInfoAnalyzer.analyzeTextFile(config, fileInfoShort);
        removeTestIgnoreRevsFile();

        Assertions.assertEquals(fileInfoFull, fileInfoShort);

        Assertions.assertEquals(new Author(IGNORED_AUTHOR_NAME), fileInfoFull.getLine(2).getAuthor());
        Assertions.assertEquals(new Author(FAKE_AUTHOR_NAME), fileInfoFull.getLine(3).getAuthor());
        Assertions.assertEquals(new Author(IGNORED_AUTHOR_NAME), fileInfoFull.getLine(4).getAuthor());

        // line added in commit that was ignored
        Assertions.assertEquals(Author.UNKNOWN_AUTHOR, fileInfoFull.getLine(1).getAuthor());
    }

    @Test
    public void analyzeTextFile_blameTestFileIgnoreAllCommit_success() {
        config.setSinceDate(BLAME_TEST_SINCE_DATE);
        config.setUntilDate(BLAME_TEST_UNTIL_DATE);
        FileInfo fileInfoFull = fileInfoExtractor.generateFileInfo(config, "blameTest.java");
        config.setIgnoreCommitList(Arrays.asList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018,
                MAIN_AUTHOR_BLAME_TEST_FILE_COMMIT_06022018));
        fileInfoAnalyzer.analyzeTextFile(config, fileInfoFull);

        FileInfo fileInfoShort = fileInfoExtractor.generateFileInfo(config, "blameTest.java");
        config.setIgnoreCommitList(CommitHash.convertStringsToCommits(
                Arrays.asList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING.substring(0, 8),
                        MAIN_AUTHOR_BLAME_TEST_FILE_COMMIT_06022018_STRING.substring(0, 8))));
        fileInfoAnalyzer.analyzeTextFile(config, fileInfoShort);

        Assertions.assertEquals(fileInfoFull, fileInfoShort);
        fileInfoFull.getLines().forEach(lineInfo ->
                Assertions.assertEquals(Author.UNKNOWN_AUTHOR, lineInfo.getAuthor()));
    }

    @Test
    public void analyzeFile_blameWithPreviousAuthorTestFileIgnoreAllCommit_success() {
        Assumptions.assumeTrue(GitVersion.isGitVersionSufficientForFindingPreviousAuthors());
        config.setSinceDate(PREVIOUS_AUTHOR_BLAME_TEST_SINCE_DATE);
        config.setUntilDate(PREVIOUS_AUTHOR_BLAME_TEST_UNTIL_DATE);
        config.setIsFindingPreviousAuthorsPerformed(true);
        config.setBranch(TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH);
        GitCheckout.checkout(config.getRepoRoot(), TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH);

        FileInfo fileInfoFull = fileInfoExtractor.generateFileInfo(config, "blameTest.java");
        config.setIgnoreCommitList(Arrays.asList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018,
                MAIN_AUTHOR_BLAME_TEST_FILE_COMMIT_06022018, AUTHOR_TO_IGNORE_BLAME_TEST_FILE_COMMIT_07082021));
        createTestIgnoreRevsFile(config.getIgnoreCommitList());
        fileInfoAnalyzer.analyzeTextFile(config, fileInfoFull);
        removeTestIgnoreRevsFile();

        FileInfo fileInfoShort = fileInfoExtractor.generateFileInfo(config, "blameTest.java");
        config.setIgnoreCommitList(CommitHash.convertStringsToCommits(
                Arrays.asList(FAKE_AUTHOR_BLAME_TEST_FILE_COMMIT_08022018_STRING.substring(0, 8),
                        MAIN_AUTHOR_BLAME_TEST_FILE_COMMIT_06022018_STRING.substring(0, 8),
                        AUTHOR_TO_IGNORE_BLAME_TEST_FILE_COMMIT_07082021_STRING.substring(0, 8))));
        createTestIgnoreRevsFile(config.getIgnoreCommitList());
        fileInfoAnalyzer.analyzeTextFile(config, fileInfoShort);
        removeTestIgnoreRevsFile();

        Assertions.assertEquals(fileInfoFull, fileInfoShort);
        fileInfoFull.getLines().forEach(lineInfo ->
                Assertions.assertEquals(Author.UNKNOWN_AUTHOR, lineInfo.getAuthor()));
    }

    @Test
    public void analyzeTextFile_blameTestFileIgnoreRangedCommit_success() {
        config.setSinceDate(BLAME_TEST_SINCE_DATE);
        config.setUntilDate(BLAME_TEST_UNTIL_DATE);
        FileInfo fileInfoFull = fileInfoExtractor.generateFileInfo(config, "blameTest.java");
        config.setIgnoreCommitList(FAKE_AUTHOR_BLAME_RANGED_COMMIT_LIST_09022018);
        fileInfoAnalyzer.analyzeTextFile(config, fileInfoFull);

        FileInfo fileInfoRanged = fileInfoExtractor.generateFileInfo(config, "blameTest.java");
        String rangedCommit = FAKE_AUTHOR_BLAME_RANGED_COMMIT_ONE_06022018_STRING + ".."
                + FAKE_AUTHOR_BLAME_RANGED_COMMIT_FOUR_08022018_STRING;
        config.setIgnoreCommitList(CommitHash.getHashes(config.getRepoRoot(), config.getBranch(),
                new CommitHash(rangedCommit)).collect(Collectors.toList()));
        fileInfoAnalyzer.analyzeTextFile(config, fileInfoRanged);

        Assertions.assertEquals(fileInfoFull, fileInfoRanged);
        fileInfoFull.getLines().forEach(lineInfo ->
                Assertions.assertEquals(Author.UNKNOWN_AUTHOR, lineInfo.getAuthor()));
    }

    @Test
    public void analyzeTextFile_blameTestFileIgnoreRangedCommitShort_success() {
        config.setSinceDate(BLAME_TEST_SINCE_DATE);
        config.setUntilDate(BLAME_TEST_UNTIL_DATE);
        FileInfo fileInfoFull = fileInfoExtractor.generateFileInfo(config, "blameTest.java");
        config.setIgnoreCommitList(FAKE_AUTHOR_BLAME_RANGED_COMMIT_LIST_09022018);
        fileInfoAnalyzer.analyzeTextFile(config, fileInfoFull);

        FileInfo fileInfoRangedShort = fileInfoExtractor.generateFileInfo(config, "blameTest.java");
        String rangedCommitShort = FAKE_AUTHOR_BLAME_RANGED_COMMIT_ONE_06022018_STRING.substring(0, 8) + ".."
                + FAKE_AUTHOR_BLAME_RANGED_COMMIT_FOUR_08022018_STRING.substring(0, 8);
        config.setIgnoreCommitList(CommitHash.getHashes(config.getRepoRoot(), config.getBranch(),
                new CommitHash(rangedCommitShort)).collect(Collectors.toList()));
        fileInfoAnalyzer.analyzeTextFile(config, fileInfoRangedShort);

        Assertions.assertEquals(fileInfoFull, fileInfoRangedShort);
        fileInfoFull.getLines().forEach(lineInfo ->
                Assertions.assertEquals(Author.UNKNOWN_AUTHOR, lineInfo.getAuthor()));
    }

    @Test
    public void analyzeTextFile_emailWithAdditionOperator_success() {
        config.setSinceDate(EMAIL_WITH_ADDITION_TEST_SINCE_DATE);
        config.setUntilDate(EMAIL_WITH_ADDITION_TEST_UNTIL_DATE);
        config.setBranch("617-FileAnalyzerTest-analyzeFile_emailWithAdditionOperator_success");
        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());
        Author author = new Author(MINGYI_AUTHOR_NAME);
        config.setAuthorList(Collections.singletonList(author));

        FileInfo fileInfo = fileInfoExtractor.generateFileInfo(config, "pr_617.java");
        fileInfoAnalyzer.analyzeTextFile(config, fileInfo);

        Assertions.assertEquals(1, fileInfo.getLines().size());
        fileInfo.getLines().forEach(lineInfo -> Assertions.assertEquals(author, lineInfo.getAuthor()));
    }

    @Test
    public void analyzeTextFile_shouldIncludeLastModifiedDateInLines_success() {
        config.setSinceDate(SHOULD_INCLUDE_LAST_MODIFIED_IN_LINES_SINCE_DATE);
        config.setUntilDate(SHOULD_INCLUDE_LAST_MODIFIED_IN_LINES_UNTIL_DATE);
        config.setIsLastModifiedDateIncluded(true);
        config.setBranch("1345-FileAnalyzerTest-analyzeFile_shouldIncludeLastModifiedDateInLines_success");
        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());
        Author author = new Author(JAMES_AUTHOR_NAME);
        config.setAuthorList(Collections.singletonList(author));

        FileInfo fileInfo = fileInfoExtractor.generateFileInfo(config, "includeLastModifiedDateInLinesTest.java");
        fileInfoAnalyzer.analyzeTextFile(config, fileInfo);

        Assertions.assertEquals(4, fileInfo.getLines().size());
        fileInfo.getLines().forEach(lineInfo ->
                Assertions.assertEquals(LAST_MODIFIED_DATE, lineInfo.getLastModifiedDate()));
    }

    @Test
    public void analyzeTextFile_fileExceedingFileSizeLimit_success() {
        config.setSinceDate(ANALYZE_LARGE_FILES_SINCE_DATE);
        config.setUntilDate(ANALYZE_LARGE_FILES_UNTIL_DATE);
        config.setBranch("1647-FileAnalyzerTest-analyzeTextFile_fileExceedingFileSizeLimit_success");
        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());

        FileInfo fileInfo = fileInfoExtractor.generateFileInfo(config, "largeFile.json");
        fileInfoAnalyzer.analyzeTextFile(config, fileInfo);

        Assertions.assertEquals(46902, fileInfo.getLines().size());
        Assertions.assertEquals(fileInfo.getFileSize() > config.getFileSizeLimit(), fileInfo.exceedsFileLimit());
    }

    @Test
    public void analyzeBinaryFile_shouldSetLinesToBeEmpty_success() {
        config.setSinceDate(ANALYZE_BINARY_FILES_SINCE_DATE);
        config.setUntilDate(ANALYZE_BINARY_FILES_UNTIL_DATE);
        config.setBranch("728-FileInfoExtractorTest-getNonBinaryFilesList_directoryWithBinaryFiles_success");
        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());
        List<FileInfo> binaryFileInfos = fileInfoExtractor.extractBinaryFileInfos(config);

        for (FileInfo binaryFileInfo: binaryFileInfos) {
            fileInfoAnalyzer.analyzeBinaryFile(config, binaryFileInfo);
            Assertions.assertEquals(0, binaryFileInfo.getLines().size());
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
            Assertions.assertNull(fileInfoAnalyzer.analyzeBinaryFile(config, binaryFileInfo));
        }
    }

    @Test
    public void analyzeFile_filesWithEmptyEmailCommit_success() {
        config.setSinceDate(ANALYZE_FILES_EMPTY_EMAIL_COMMIT_SINCE_DATE);
        config.setUntilDate(ANALYZE_FILES_EMPTY_EMAIL_COMMIT_UNTIL_DATE);
        config.setBranch("1636-FileAnalyzerTest-analyzeFile_filesWithEmptyEmailCommit_success");
        config.setAuthorList(Arrays.asList(new Author("chan-j-d")));
        List<String> relevantFileFormats = Arrays.asList("txt", "png");
        config.setFormats(FileType.convertFormatStringsToFileTypes(relevantFileFormats));
        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());

        List<FileInfo> fileInfos = fileInfoExtractor.extractTextFileInfos(config);
        FileInfo textFileInfo = fileInfos.get(0);
        FileInfo binaryFileInfo = new FileInfo("empty-email-commit-binary-file.png");

        Assertions.assertNotNull(fileInfoAnalyzer.analyzeTextFile(config, textFileInfo));
        Assertions.assertNotNull(fileInfoAnalyzer.analyzeBinaryFile(config, binaryFileInfo));
    }
}
