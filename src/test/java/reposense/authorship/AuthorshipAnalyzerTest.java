package reposense.authorship;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import reposense.authorship.analyzer.AuthorshipAnalyzer;
import reposense.authorship.model.FileInfo;
import reposense.git.GitCheckout;
import reposense.model.Author;
import reposense.model.CommitHash;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class AuthorshipAnalyzerTest extends GitTestTemplate {

    @Test
    public void analyzeAuthorship_partialCredit_success() {
        FileInfo fileInfoFull = generateAnalyzeAuthorshipTestFileInfo("analyzeAuthorshipTest.java");
        FileInfoAnalyzer.analyzeFile(config, fileInfoFull, true);

        Assert.assertEquals(new Author(FAKE_AUTHOR_NAME), fileInfoFull.getLine(1).getAuthor());
        Assert.assertEquals(new Author(MINGYI_AUTHOR_NAME), fileInfoFull.getLine(2).getAuthor());
        Assert.assertEquals(new Author(MINGYI_AUTHOR_NAME), fileInfoFull.getLine(3).getAuthor());

        Assert.assertTrue(fileInfoFull.getLine(1).isFullCredit());

        // Partial credit given as author only appended a full stop to the line
        Assert.assertFalse(fileInfoFull.getLine(2).isFullCredit());
        Assert.assertFalse(fileInfoFull.getLine(3).isFullCredit());
    }

    @Test
    public void analyzeAuthorship_unknownAuthor_success() {
        FileInfo fileInfoFull = generateAnalyzeAuthorshipTestFileInfo("analyzeAuthorshipTest.java");
        config.getAuthorEmailsAndAliasesMap().remove(FAKE_AUTHOR_NAME);
        FileInfoAnalyzer.analyzeFile(config, fileInfoFull, true);

        // fakeAuthor is not in authorEmailsAndAliasesMap, so line has unknown author
        Assert.assertEquals(Author.UNKNOWN_AUTHOR, fileInfoFull.getLine(1).getAuthor());
        Assert.assertEquals(new Author(MINGYI_AUTHOR_NAME), fileInfoFull.getLine(2).getAuthor());
        Assert.assertEquals(new Author(MINGYI_AUTHOR_NAME), fileInfoFull.getLine(3).getAuthor());

        // Unknown author given partial credit by default
        Assert.assertFalse(fileInfoFull.getLine(1).isFullCredit());

        // Full credit given since previous author (fakeAuthor) is not recognized
        Assert.assertTrue(fileInfoFull.getLine(2).isFullCredit());
        Assert.assertTrue(fileInfoFull.getLine(3).isFullCredit());
    }

    @Test
    public void analyzeAuthorship_beforeSinceDate_success() {
        FileInfo fileInfoFull = generateAnalyzeAuthorshipTestFileInfo("analyzeAuthorshipTest.java");
        Date sinceDate = TestUtil.getSinceDate(2019, Calendar.NOVEMBER, 20);
        config.setSinceDate(sinceDate);
        FileInfoAnalyzer.analyzeFile(config, fileInfoFull, true);

        Assert.assertEquals(new Author(FAKE_AUTHOR_NAME), fileInfoFull.getLine(1).getAuthor());
        Assert.assertEquals(new Author(MINGYI_AUTHOR_NAME), fileInfoFull.getLine(2).getAuthor());
        Assert.assertEquals(new Author(MINGYI_AUTHOR_NAME), fileInfoFull.getLine(3).getAuthor());

        Assert.assertTrue(fileInfoFull.getLine(1).isFullCredit());

        // Full credit given since previous version was made in a commit before the sinceDate
        Assert.assertTrue(fileInfoFull.getLine(2).isFullCredit());
        Assert.assertTrue(fileInfoFull.getLine(3).isFullCredit());
    }

    @Test
    public void analyzeAuthorship_insideIgnoreCommitList_success() {
        FileInfo fileInfoFull = generateAnalyzeAuthorshipTestFileInfo("analyzeAuthorshipTest.java");
        config.setIgnoreCommitList(
                Collections.singletonList(new CommitHash("f874c0992645bed626de2113659ce48d7a2233dd")));
        FileInfoAnalyzer.analyzeFile(config, fileInfoFull, true);

        // Unknown author since previous version was made in commit that is ignored
        Assert.assertEquals(Author.UNKNOWN_AUTHOR, fileInfoFull.getLine(1).getAuthor());
        Assert.assertEquals(new Author(MINGYI_AUTHOR_NAME), fileInfoFull.getLine(2).getAuthor());
        Assert.assertEquals(new Author(MINGYI_AUTHOR_NAME), fileInfoFull.getLine(3).getAuthor());

        Assert.assertFalse(fileInfoFull.getLine(1).isFullCredit());

        // Full credit given since previous version was made in commit that is ignored
        Assert.assertTrue(fileInfoFull.getLine(2).isFullCredit());
        Assert.assertTrue(fileInfoFull.getLine(3).isFullCredit());
    }

    @Test
    public void analyzeAuthorship_emptyLine_success() {
        FileInfo fileInfoFull = generateAnalyzeAuthorshipTestFileInfo("analyzeAuthorshipTest1.java");
        FileInfoAnalyzer.analyzeFile(config, fileInfoFull, true);

        // Empty line is given full credit
        Assert.assertEquals(new Author(FAKE_AUTHOR_NAME), fileInfoFull.getLine(1).getAuthor());
        Assert.assertTrue(fileInfoFull.getLine(1).isFullCredit());
    }

    @Test
    public void analyzeAuthorship_matchesIgnoreGlob_success() {
        FileInfo fileInfoFull = generateAnalyzeAuthorshipTestFileInfo("analyzeAuthorshipTest1.java");
        Author fakeAuthor = config.getAuthor("fakeAuthor", "");

        // File was renamed analyzeAuthorshipTest2.java -> analyzeAuthorshipTest1.java, ignore previous file name
        fakeAuthor.appendIgnoreGlobList(Collections.singletonList("analyzeAuthorshipTest2.java"));
        FileInfoAnalyzer.analyzeFile(config, fileInfoFull, true);

        Assert.assertEquals(new Author(FAKE_AUTHOR_NAME), fileInfoFull.getLine(1).getAuthor());
        Assert.assertEquals(new Author(FAKE_AUTHOR_NAME), fileInfoFull.getLine(2).getAuthor());
        Assert.assertEquals(new Author(FAKE_AUTHOR_NAME), fileInfoFull.getLine(3).getAuthor());
        Assert.assertEquals(new Author(MINGYI_AUTHOR_NAME), fileInfoFull.getLine(4).getAuthor());

        Assert.assertTrue(fileInfoFull.getLine(1).isFullCredit());
        Assert.assertTrue(fileInfoFull.getLine(2).isFullCredit());
        Assert.assertTrue(fileInfoFull.getLine(3).isFullCredit());

        // Full credit given since previous author ignores the previous file name
        Assert.assertTrue(fileInfoFull.getLine(4).isFullCredit());
    }

    @Test
    public void analyzeAuthorship_sameAuthor_success() {
        FileInfo fileInfoFull = generateAnalyzeAuthorshipTestFileInfo("analyzeAuthorshipTest1.java");
        FileInfoAnalyzer.analyzeFile(config, fileInfoFull, true);

        Assert.assertEquals(new Author(FAKE_AUTHOR_NAME), fileInfoFull.getLine(1).getAuthor());
        Assert.assertEquals(new Author(FAKE_AUTHOR_NAME), fileInfoFull.getLine(2).getAuthor());
        Assert.assertEquals(new Author(FAKE_AUTHOR_NAME), fileInfoFull.getLine(3).getAuthor());
        Assert.assertEquals(new Author(MINGYI_AUTHOR_NAME), fileInfoFull.getLine(4).getAuthor());

        Assert.assertTrue(fileInfoFull.getLine(1).isFullCredit());
        Assert.assertTrue(fileInfoFull.getLine(2).isFullCredit());

        // Full credit given since previous author is also the current author
        Assert.assertTrue(fileInfoFull.getLine(3).isFullCredit());

        Assert.assertFalse(fileInfoFull.getLine(4).isFullCredit());
    }

    /**
     * Generates the {@code FileInfo} for test file {@code fileName}.
     */
    private FileInfo generateAnalyzeAuthorshipTestFileInfo(String fileName) {
        config.setBranch("945-FileAnalyzerTest-analyzeAuthorship");
        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());
        Date sinceDate = TestUtil.getSinceDate(2018, Calendar.JANUARY, 1);
        config.setSinceDate(sinceDate);
        FileInfo fileInfoFull = generateTestFileInfo(fileName);
        config.getAuthorEmailsAndAliasesMap().put(MINGYI_AUTHOR_NAME, new Author(MINGYI_AUTHOR_NAME));
        return fileInfoFull;
    }

    @Test
    public void getLevenshteinDistance_success()
            throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException {
        Method getLevenshteinDistance = getLevenshteinDistanceMethod();
        Assert.assertEquals(4, getLevenshteinDistance.invoke(null, "potato", "tomatoes"));
    }

    @Test
    public void getLevenshteinDistance_insertion_success()
            throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException {
        Method getLevenshteinDistance = getLevenshteinDistanceMethod();
        Assert.assertEquals(2, getLevenshteinDistance.invoke(null, "abcd", "abcdef"));
    }

    @Test
    public void getLevenshteinDistance_deletion_success()
            throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException {
        Method getLevenshteinDistance = getLevenshteinDistanceMethod();
        Assert.assertEquals(3, getLevenshteinDistance.invoke(null, "abcde", "ab"));
    }

    @Test
    public void getLevenshteinDistance_substitution_success()
            throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException {
        Method getLevenshteinDistance = getLevenshteinDistanceMethod();
        Assert.assertEquals(4, getLevenshteinDistance.invoke(null, "xxxxefg", "abcdefg"));
    }

    @Test
    public void getLevenshteinDistance_identicalStrings_success()
            throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException {
        Method getLevenshteinDistance = getLevenshteinDistanceMethod();
        Assert.assertEquals(0, getLevenshteinDistance.invoke(null, "abcdefg", "abcdefg"));
    }

    @Test
    public void getLevenshteinDistance_emptyStrings_success()
            throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException {
        Method getLevenshteinDistance = getLevenshteinDistanceMethod();
        Assert.assertEquals(0, getLevenshteinDistance.invoke(null, "", ""));
    }

    @Test
    public void getLevenshteinDistance_emptyString_success()
            throws NoSuchMethodException, SecurityException, InvocationTargetException, IllegalAccessException {
        Method getLevenshteinDistance = getLevenshteinDistanceMethod();
        Assert.assertEquals(6, getLevenshteinDistance.invoke(null, "abcdef", ""));
    }

    private Method getLevenshteinDistanceMethod() throws NoSuchMethodException, SecurityException {
        Method getLevenshteinDistance =
                AuthorshipAnalyzer.class.getDeclaredMethod("getLevenshteinDistance", String.class, String.class);
        getLevenshteinDistance.setAccessible(true);
        return getLevenshteinDistance;
    }
}
