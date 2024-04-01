package reposense.authorship;

import static reposense.parser.ArgsParser.DEFAULT_ORIGINALITY_THRESHOLD;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reposense.authorship.model.FileInfo;
import reposense.git.GitCheckout;
import reposense.model.Author;
import reposense.model.CommitHash;
import reposense.model.RepoConfiguration;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class AuthorshipAnalyzerTest extends GitTestTemplate {
    private static final LocalDateTime SINCE_DATE = TestUtil.getSinceDate(2018, Month.JANUARY.getValue(), 1);
    private static final LocalDateTime UNTIL_DATE = TestUtil.getUntilDate(2024, Month.APRIL.getValue(), 30);
    private static final String TEST_FILENAME = "analyzeAuthorshipTest.java";
    private static final String TEST1_FILENAME = "analyzeAuthorshipTest1.java";
    private static final String TEST2_FILENAME = "analyzeAuthorshipTest2.java";
    private static final String TEST3_FILENAME = "analyzeAuthorshipTest3.java";
    private static final String TEST_EXCEED_THRESHOLD_FILENAME = "exceedThresholdTest.txt";
    private static final String TEST_NO_CANDIDATE_LINE_FILENAME = "noCandidateLineTest.txt";
    private static final String TEST_MULTIPLE_PARENT_FILENAME = "multipleParentTest.txt";
    private static final String BRANCH_NAME = "945-FileAnalyzerTest-analyzeAuthorship";
    private static final CommitHash IGNORE_HASH = new CommitHash("f874c0992645bed626de2113659ce48d7a2233dd");
    private static final Author MINGYI_AUTHOR = new Author(MINGYI_AUTHOR_NAME);
    private static final Author SHICHEN_AUTHOR = new Author(SHICHEN_AUTHOR_NAME);

    private RepoConfiguration config;

    @BeforeEach
    public void before() throws Exception {
        super.before();

        config = configs.get();

        config.setBranch(BRANCH_NAME);
        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());

        config.setSinceDate(SINCE_DATE);
        config.setUntilDate(UNTIL_DATE);
        config.setZoneId(TIME_ZONE_ID);

        config.addAuthorNamesToAuthorMapEntry(FAKE_AUTHOR, FAKE_AUTHOR_NAME);
        config.addAuthorNamesToAuthorMapEntry(MINGYI_AUTHOR, MINGYI_AUTHOR_NAME);
        config.addAuthorNamesToAuthorMapEntry(SHICHEN_AUTHOR, SHICHEN_AUTHOR_NAME);
    }

    @Test
    public void analyzeAuthorship_partialCredit_success() {
        FileInfo fileInfo = analyzeTextFile(TEST_FILENAME);

        Assertions.assertEquals(FAKE_AUTHOR, fileInfo.getLine(1).getAuthor());
        Assertions.assertEquals(MINGYI_AUTHOR, fileInfo.getLine(2).getAuthor());
        Assertions.assertEquals(MINGYI_AUTHOR, fileInfo.getLine(3).getAuthor());

        Assertions.assertTrue(fileInfo.getLine(1).isFullCredit());

        // Partial credit given as author only appended a full stop to the line
        Assertions.assertFalse(fileInfo.getLine(2).isFullCredit());
        Assertions.assertFalse(fileInfo.getLine(3).isFullCredit());
    }

    @Test
    public void analyzeAuthorship_unknownAuthor_success() {
        // Actually should remove fakeAuthor, but this does the same job
        config.addAuthorNamesToAuthorMapEntry(Author.UNKNOWN_AUTHOR, FAKE_AUTHOR_NAME);

        FileInfo fileInfo = analyzeTextFile(TEST_FILENAME);

        // fakeAuthor is not in authorEmailsAndAliasesMap, so line has unknown author
        Assertions.assertEquals(Author.UNKNOWN_AUTHOR, fileInfo.getLine(1).getAuthor());
        Assertions.assertEquals(MINGYI_AUTHOR, fileInfo.getLine(2).getAuthor());
        Assertions.assertEquals(MINGYI_AUTHOR, fileInfo.getLine(3).getAuthor());

        // Full credit given since analysis is not performed on unknown author
        Assertions.assertTrue(fileInfo.getLine(1).isFullCredit());

        // Full credit given since previous author (fakeAuthor) is not recognized
        Assertions.assertTrue(fileInfo.getLine(2).isFullCredit());
        Assertions.assertTrue(fileInfo.getLine(3).isFullCredit());
    }

    @Test
    public void analyzeAuthorship_beforeSinceDate_success() {
        config.setSinceDate(TestUtil.getSinceDate(2019, Month.NOVEMBER.getValue(), 20));

        FileInfo fileInfo = analyzeTextFile(TEST_FILENAME);

        Assertions.assertEquals(FAKE_AUTHOR, fileInfo.getLine(1).getAuthor());
        Assertions.assertEquals(MINGYI_AUTHOR, fileInfo.getLine(2).getAuthor());
        Assertions.assertEquals(MINGYI_AUTHOR, fileInfo.getLine(3).getAuthor());

        Assertions.assertTrue(fileInfo.getLine(1).isFullCredit());

        // Full credit given since previous version was made in a commit before the sinceDate
        Assertions.assertTrue(fileInfo.getLine(2).isFullCredit());
        Assertions.assertTrue(fileInfo.getLine(3).isFullCredit());
    }

    @Test
    public void analyzeAuthorship_insideIgnoreCommitList_success() {
        config.setIgnoreCommitList(Arrays.asList(IGNORE_HASH));

        FileInfo fileInfo = analyzeTextFile(TEST_FILENAME);

        // Unknown author since previous version was made in commit that is ignored
        Assertions.assertEquals(Author.UNKNOWN_AUTHOR, fileInfo.getLine(1).getAuthor());
        Assertions.assertEquals(MINGYI_AUTHOR, fileInfo.getLine(2).getAuthor());
        Assertions.assertEquals(MINGYI_AUTHOR, fileInfo.getLine(3).getAuthor());

        Assertions.assertTrue(fileInfo.getLine(1).isFullCredit());

        // Full credit given since previous version was made in commit that is ignored
        Assertions.assertTrue(fileInfo.getLine(2).isFullCredit());
        Assertions.assertTrue(fileInfo.getLine(3).isFullCredit());
    }

    @Test
    public void analyzeAuthorship_emptyLine_success() {
        FileInfo fileInfo = analyzeTextFile(TEST1_FILENAME);

        // Empty line is given full credit
        Assertions.assertEquals(FAKE_AUTHOR, fileInfo.getLine(1).getAuthor());
        Assertions.assertTrue(fileInfo.getLine(1).isFullCredit());
    }

    @Test
    public void analyzeAuthorship_matchesIgnoreGlob_success() {
        Author fakeAuthor = config.getAuthor("fakeAuthor", "");

        // File was renamed analyzeAuthorshipTest2.java -> analyzeAuthorshipTest1.java, ignore previous file name
        fakeAuthor.importIgnoreGlobList(Arrays.asList(TEST2_FILENAME));

        FileInfo fileInfo = analyzeTextFile(TEST1_FILENAME);

        // Full credit given since previous author ignores the previous file name
        Assertions.assertEquals(MINGYI_AUTHOR, fileInfo.getLine(4).getAuthor());
        Assertions.assertTrue(fileInfo.getLine(4).isFullCredit());
    }

    @Test
    public void analyzeAuthorship_sameAuthor_success() {
        FileInfo fileInfo = analyzeTextFile(TEST1_FILENAME);

        // Full credit given since current author is also the author of the previous version
        Assertions.assertEquals(FAKE_AUTHOR, fileInfo.getLine(3).getAuthor());
        Assertions.assertTrue(fileInfo.getLine(3).isFullCredit());
    }

    @Test
    public void analyzeAuthorship_annotatedAuthorOverride_success() {
        FileInfo fileInfo = analyzeTextFile(TEST3_FILENAME);

        // Line 1 - 4 is annotated to ming yi (myteo)
        // Partial credit given since the blamed author is not the same as the annotated author
        for (int i = 1; i <= 4; i++) {
            Assertions.assertEquals(MINGYI_AUTHOR, fileInfo.getLine(i).getAuthor());
            Assertions.assertFalse(fileInfo.getLine(i).isFullCredit());
        }

        // Line 5 - 8 is annotated to shi chen (SkyBlaise)
        // Full credit is inherited since the blamed author is the same as the annotated author
        for (int i = 5; i <= 8; i++) {
            Assertions.assertEquals(SHICHEN_AUTHOR, fileInfo.getLine(i).getAuthor());
            Assertions.assertTrue(fileInfo.getLine(i).isFullCredit());
        }
    }

    @Test
    public void analyzeAuthorship_exceedOriginalityThreshold_success() {
        FileInfo fileInfo = analyzeTextFile(TEST_EXCEED_THRESHOLD_FILENAME);

        Assertions.assertEquals(SHICHEN_AUTHOR, fileInfo.getLine(1).getAuthor());
        Assertions.assertTrue(fileInfo.getLine(1).isFullCredit());
    }

    @Test
    public void analyzeAuthorship_noCandidateLine_success() {
        FileInfo fileInfo = analyzeTextFile(TEST_NO_CANDIDATE_LINE_FILENAME);

        Assertions.assertEquals(SHICHEN_AUTHOR, fileInfo.getLine(1).getAuthor());
        Assertions.assertTrue(fileInfo.getLine(1).isFullCredit());
    }

    @Test
    public void analyzeAuthorship_multipleParent_success() {
        FileInfo fileInfo = analyzeTextFile(TEST_MULTIPLE_PARENT_FILENAME);

        Assertions.assertEquals(SHICHEN_AUTHOR, fileInfo.getLine(1).getAuthor());
        Assertions.assertTrue(fileInfo.getLine(1).isFullCredit());
    }

    private FileInfo analyzeTextFile(String relativePath) {
        FileInfoExtractor fileInfoExtractor = new FileInfoExtractor();
        FileInfo fileInfo = fileInfoExtractor.generateFileInfo(config, relativePath);

        FileInfoAnalyzer fileInfoAnalyzer = new FileInfoAnalyzer();
        fileInfoAnalyzer.analyzeTextFile(config, fileInfo, true, DEFAULT_ORIGINALITY_THRESHOLD);

        return fileInfo;
    }
}
