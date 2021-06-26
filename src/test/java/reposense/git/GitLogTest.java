package reposense.git;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import reposense.model.Author;
import reposense.model.FileType;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class GitLogTest extends GitTestTemplate {

    @Test
    public void gitLog_existingFormats_hasContent() {
        String content = GitLog.get(config, getAlphaAllAliasAuthor());
        Assert.assertFalse(content.isEmpty());
    }

    @Test
    public void gitLog_nonExistingFormats_noContent() {
        config.setFormats(FileType.convertFormatStringsToFileTypes(Collections.singletonList("py")));
        String content = GitLog.get(config, getAlphaAllAliasAuthor());
        Assert.assertTrue(content.isEmpty());
    }

    @Test
    public void gitLog_includeAllJavaFiles_success() {
        config.setFormats(FileType.convertFormatStringsToFileTypes(Collections.singletonList("java")));
        String content = GitLog.get(config, getAlphaAllAliasAuthor());
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(9, content));
    }

    @Test
    public void gitLog_fakeAuthorNameOnly_success() {
        Author fakeAuthorName = new Author(FAKE_AUTHOR_NAME);

        String content = GitLog.get(config, fakeAuthorName);
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(5, content));
    }

    @Test
    public void gitLog_authorNameIncorrectCase_success() {
        Author fakeAuthorName = new Author(FAKE_AUTHOR_NAME.toUpperCase());

        String content = GitLog.get(config, fakeAuthorName);
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(5, content));
    }

    @Test
    public void gitLog_fakeAuthorNameWithSpecialCharacter_success() {
        Author fakeAuthorWithSpecialCharacter = new Author(FAKE_AUTHOR_NAME.replace("fake", "#()!"));

        String content = GitLog.get(config, fakeAuthorWithSpecialCharacter);
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(5, content));
    }

    @Test
    public void gitLog_includeAllJavaFilesAuthorIgnoreMovedFile_success() {
        config.setFormats(FileType.convertFormatStringsToFileTypes(Collections.singletonList("java")));
        Author ignoreMovedFileAuthor = getAlphaAllAliasAuthor();
        ignoreMovedFileAuthor.setIgnoreGlobList(Collections.singletonList("**movedFile.java"));

        String content = GitLog.getWithFiles(config, ignoreMovedFileAuthor);
        Assert.assertTrue(TestUtil.compareNumberFilesChanged(4, content));
    }

    @Test
    public void gitLog_authorIgnoreAllJavaFiles_success() {
        config.setFormats(FileType.convertFormatStringsToFileTypes(Collections.singletonList("java")));
        Author ignoreAllJavaFilesAuthor = getAlphaAllAliasAuthor();
        ignoreAllJavaFilesAuthor.setIgnoreGlobList(Collections.singletonList("*.java"));

        String content = GitLog.getWithFiles(config, ignoreAllJavaFilesAuthor);
        Assert.assertTrue(TestUtil.compareNumberFilesChanged(0, content));
    }

    @Test
    public void gitLog_validIgnoreGlobs_success() {
        Author author = getAlphaAllAliasAuthor();

        author.setIgnoreGlobList(Collections.singletonList("annotationTest.java"));
        String content = GitLog.getWithFiles(config, author);
        Assert.assertTrue(TestUtil.compareNumberFilesChanged(6, content));

        author.setIgnoreGlobList(Collections.singletonList("**Test**"));
        content = GitLog.getWithFiles(config, author);
        Assert.assertTrue(TestUtil.compareNumberFilesChanged(5, content));

        author.setIgnoreGlobList(Collections.singletonList("README.md"));
        content = GitLog.getWithFiles(config, author);
        Assert.assertTrue(TestUtil.compareNumberFilesChanged(6, content));

        author.setIgnoreGlobList(Collections.singletonList("**.java"));
        content = GitLog.getWithFiles(config, author);
        Assert.assertTrue(TestUtil.compareNumberFilesChanged(1, content));

        author.setIgnoreGlobList(Collections.singletonList("./newPos"));
        content = GitLog.getWithFiles(config, author);
        Assert.assertTrue(TestUtil.compareNumberFilesChanged(6, content));
    }

    @Test
    public void gitLog_invalidIgnoreGlobs_filtered() {
        Author author = getAlphaAllAliasAuthor();

        author.setIgnoreGlobList(Collections.singletonList("../testrepo-Alpha"));
        String content = GitLog.getWithFiles(config, author);
        Assert.assertTrue(TestUtil.compareNumberFilesChanged(7, content));

        author.setIgnoreGlobList(Collections.singletonList("../*.java"));
        content = GitLog.getWithFiles(config, author);
        Assert.assertTrue(TestUtil.compareNumberFilesChanged(7, content));

        author.setIgnoreGlobList(Collections.singletonList("/newPos"));
        content = GitLog.getWithFiles(config, author);
        Assert.assertTrue(TestUtil.compareNumberFilesChanged(7, content));

        author.setIgnoreGlobList(Collections.singletonList(".."));
        content = GitLog.getWithFiles(config, author);
        Assert.assertTrue(TestUtil.compareNumberFilesChanged(7, content));
    }

    @Test
    public void gitLog_authorWithAllCharactersRegexAlias_emptyResult() {
        Author authorWithAllCharactersRegexAlias = new Author("none");
        authorWithAllCharactersRegexAlias.setAuthorAliases(Collections.singletonList(".*"));

        String content = GitLog.get(config, authorWithAllCharactersRegexAlias);
        Assert.assertTrue(content.isEmpty());
    }

    @Test
    public void gitLog_sinceDateInFuture_noContent() {
        Date date = TestUtil.getSinceDate(2050, Calendar.JANUARY, 1);
        config.setSinceDate(date);
        String content = GitLog.get(config, getAlphaAllAliasAuthor());
        Assert.assertTrue(content.isEmpty());
    }

    @Test
    public void gitLog_untilDateBeforeAnyCommit_noContent() {
        Date date = TestUtil.getUntilDate(2010, Calendar.JANUARY, 1);
        config.setUntilDate(date);
        config.setSinceDate(null);
        String content = GitLog.get(config, getAlphaAllAliasAuthor());
        Assert.assertTrue(content.isEmpty());
    }

    @Test
    public void gitLog_emailWithAdditionOperator_success() {
        config.setBranch("617-FileAnalyzerTest-analyzeFile_emailWithAdditionOperator_success");
        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());
        Author author = new Author(MINGYI_AUTHOR_NAME);
        config.setAuthorList(Collections.singletonList(author));

        String content = GitLog.get(config, author);
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(1, content));
    }
}
