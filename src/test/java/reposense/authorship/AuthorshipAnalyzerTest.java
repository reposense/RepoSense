package reposense.authorship;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reposense.authorship.analyzer.AuthorshipAnalyzer;
import reposense.authorship.model.FileInfo;
import reposense.git.GitCheckout;
import reposense.model.Author;
import reposense.model.CommitHash;
import reposense.model.RepoConfiguration;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class AuthorshipAnalyzerTest extends GitTestTemplate {
    private static final LocalDateTime SINCE_DATE = TestUtil.getSinceDate(2018, Month.JANUARY.getValue(), 1);
    private static final LocalDateTime UNTIL_DATE = TestUtil.getUntilDate(2019, Month.DECEMBER.getValue(), 1);
    private static final String TEST_FILENAME = "analyzeAuthorshipTest.java";
    private static final String TEST1_FILENAME = "analyzeAuthorshipTest1.java";
    private static final String TEST2_FILENAME = "analyzeAuthorshipTest2.java";
    private static final String BRANCH_NAME = "945-FileAnalyzerTest-analyzeAuthorship";
    private static final CommitHash IGNORE_HASH = new CommitHash("f874c0992645bed626de2113659ce48d7a2233dd");
    private static final Author MINGYI_AUTHOR = new Author(MINGYI_AUTHOR_NAME);

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
        config.setIgnoreCommitList(List.of(IGNORE_HASH));

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
        fakeAuthor.importIgnoreGlobList(List.of(TEST2_FILENAME));

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

    private FileInfo analyzeTextFile(String relativePath) {
        FileInfoExtractor fileInfoExtractor = new FileInfoExtractor();
        FileInfo fileInfo = fileInfoExtractor.generateFileInfo(config, relativePath);

        FileInfoAnalyzer fileInfoAnalyzer = new FileInfoAnalyzer();
        fileInfoAnalyzer.analyzeTextFile(config, fileInfo, true);

        return fileInfo;
    }

    @Test
    public void getLevenshteinDistance_success()
            throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException {
        Method getLevenshteinDistance = getLevenshteinDistanceMethod();
        Assertions.assertEquals(4, getLevenshteinDistance.invoke(null, "potato", "tomatoes"));
    }

    @Test
    public void getLevenshteinDistance_insertion_success()
            throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException {
        Method getLevenshteinDistance = getLevenshteinDistanceMethod();
        Assertions.assertEquals(2, getLevenshteinDistance.invoke(null, "abcd", "abcdef"));
    }

    @Test
    public void getLevenshteinDistance_deletion_success()
            throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException {
        Method getLevenshteinDistance = getLevenshteinDistanceMethod();
        Assertions.assertEquals(3, getLevenshteinDistance.invoke(null, "abcde", "ab"));
    }

    @Test
    public void getLevenshteinDistance_substitution_success()
            throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException {
        Method getLevenshteinDistance = getLevenshteinDistanceMethod();
        Assertions.assertEquals(4, getLevenshteinDistance.invoke(null, "xxxxefg", "abcdefg"));
    }

    @Test
    public void getLevenshteinDistance_identicalStrings_success()
            throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException {
        Method getLevenshteinDistance = getLevenshteinDistanceMethod();
        Assertions.assertEquals(0, getLevenshteinDistance.invoke(null, "abcdefg", "abcdefg"));
    }

    @Test
    public void getLevenshteinDistance_emptyStrings_success()
            throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException {
        Method getLevenshteinDistance = getLevenshteinDistanceMethod();
        Assertions.assertEquals(0, getLevenshteinDistance.invoke(null, "", ""));
    }

    @Test
    public void getLevenshteinDistance_emptyString_success()
            throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException {
        Method getLevenshteinDistance = getLevenshteinDistanceMethod();
        Assertions.assertEquals(6, getLevenshteinDistance.invoke(null, "abcdef", ""));
    }

    private Method getLevenshteinDistanceMethod() throws NoSuchMethodException, SecurityException {
        Method getLevenshteinDistance =
                AuthorshipAnalyzer.class.getDeclaredMethod("getLevenshteinDistance", String.class, String.class);
        getLevenshteinDistance.setAccessible(true);
        return getLevenshteinDistance;
    }
}
