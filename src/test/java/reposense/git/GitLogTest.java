package reposense.git;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import reposense.model.Author;
import reposense.model.Format;
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
        config.setFormats(Collections.singletonList(new Format("py")));
        String content = GitLog.get(config, getAlphaAllAliasAuthor());
        Assert.assertTrue(content.isEmpty());
    }

    @Test
    public void gitLog_includeAllJavaFiles_success() {
        config.setFormats(Collections.singletonList(new Format("java")));
        String content = GitLog.get(config, getAlphaAllAliasAuthor());
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(8, content));
    }

    @Test
    public void gitLog_fakeAuthorNameOnly_success() {
        Author fakeAuthorName = new Author(FAKE_AUTHOR_NAME);

        String content = GitLog.get(config, fakeAuthorName);
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(4, content));
    }

    @Test
    public void gitLog_authorNameIncorrectCase_success() {
        Author fakeAuthorName = new Author(FAKE_AUTHOR_NAME.toUpperCase());

        String content = GitLog.get(config, fakeAuthorName);
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(4, content));
    }

    @Test
    public void gitLog_fakeAuthorNameWithSpecialCharacter_success() {
        Author fakeAuthorWithSpecialCharacter = new Author(FAKE_AUTHOR_NAME.replace("fake", "#()!"));

        String content = GitLog.get(config, fakeAuthorWithSpecialCharacter);
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(4, content));
    }

    @Test
    public void gitLog_includeAllJavaFilesAuthorIgnoreMovedFile_success() {
        config.setFormats(Collections.singletonList(new Format("java")));
        Author ignoreMovedFileAuthor = getAlphaAllAliasAuthor();
        ignoreMovedFileAuthor.setIgnoreGlobList(Collections.singletonList("**movedFile.java"));

        String content = GitLog.get(config, ignoreMovedFileAuthor);
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(6, content));
    }

    @Test
    public void gitLog_authorIgnoreAllJavaFiles_success() {
        Author ignoreAllJavaFilesAuthor = getAlphaAllAliasAuthor();
        ignoreAllJavaFilesAuthor.setIgnoreGlobList(Collections.singletonList("*.java"));

        String content = GitLog.get(config, ignoreAllJavaFilesAuthor);
        Assert.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(2, content));
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
        Date date = TestUtil.getDate(2050, Calendar.JANUARY, 1);
        String content = GitLog.get(config, getAlphaAllAliasAuthor(), date, null);
        Assert.assertTrue(content.isEmpty());
    }

    @Test
    public void gitLog_untilDateBeforeAnyCommit_noContent() {
        Date date = TestUtil.getDate(2010, Calendar.JANUARY, 1);
        String content = GitLog.get(config, getAlphaAllAliasAuthor(), null, date);
        Assert.assertTrue(content.isEmpty());
    }
}
