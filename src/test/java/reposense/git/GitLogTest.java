package reposense.git;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reposense.model.Author;
import reposense.model.FileType;
import reposense.model.RepoConfiguration;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;
import reposense.util.TimeUtil;

public class GitLogTest extends GitTestTemplate {
    private RepoConfiguration config;

    @BeforeEach
    public void before() throws Exception {
        super.before();
        config = configs.get();
    }

    @Test
    public void gitLog_existingFormats_hasContent() {
        String content = GitLog.get(config, getAlphaAllAliasAuthor());
        Assertions.assertFalse(content.isEmpty());
    }

    @Test
    public void gitLog_nonExistingFormats_noContent() {
        config.setFormats(FileType.convertFormatStringsToFileTypes(Collections.singletonList("py")));
        String content = GitLog.get(config, getAlphaAllAliasAuthor());
        Assertions.assertTrue(content.isEmpty());
    }

    @Test
    public void gitLog_includeAllJavaFiles_success() {
        config.setFormats(FileType.convertFormatStringsToFileTypes(Collections.singletonList("java")));
        String content = GitLog.get(config, getAlphaAllAliasAuthor());
        Assertions.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(12, content));
    }

    @Test
    public void gitLog_fakeAuthorNameOnly_success() {
        Author fakeAuthorName = new Author(FAKE_AUTHOR_NAME);

        String content = GitLog.get(config, fakeAuthorName);
        Assertions.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(8, content));
    }

    @Test
    public void gitLog_authorNameIncorrectCase_success() {
        Author fakeAuthorName = new Author(FAKE_AUTHOR_NAME.toUpperCase());

        String content = GitLog.get(config, fakeAuthorName);
        Assertions.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(8, content));
    }

    @Test
    public void gitLog_fakeAuthorNameWithSpecialCharacter_success() {
        Author fakeAuthorWithSpecialCharacter = new Author(FAKE_AUTHOR_NAME.replace("fake", "#()!"));

        String content = GitLog.get(config, fakeAuthorWithSpecialCharacter);
        Assertions.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(8, content));
    }

    @Test
    public void gitLog_includeAllJavaFilesAuthorIgnoreMovedFile_success() {
        config.setFormats(FileType.convertFormatStringsToFileTypes(Collections.singletonList("java")));
        Author ignoreMovedFileAuthor = getAlphaAllAliasAuthor();
        ignoreMovedFileAuthor = new Author(
                ignoreMovedFileAuthor,
                null,
                null,
                null,
                null,
                Collections.singletonList("**movedFile.java"),
                null);

        String content = GitLog.getWithFiles(config, ignoreMovedFileAuthor);
        Assertions.assertTrue(TestUtil.compareNumberFilesChanged(4, content));
    }

    @Test
    public void gitLog_authorIgnoreAllJavaFiles_success() {
        config.setFormats(FileType.convertFormatStringsToFileTypes(Collections.singletonList("java")));
        Author ignoreAllJavaFilesAuthor = getAlphaAllAliasAuthor();
        ignoreAllJavaFilesAuthor = new Author(
                ignoreAllJavaFilesAuthor,
                null,
                null,
                null,
                null,
                Collections.singletonList("*.java"),
                null);


        String content = GitLog.getWithFiles(config, ignoreAllJavaFilesAuthor);
        Assertions.assertTrue(TestUtil.compareNumberFilesChanged(0, content));
    }

    @Test
    public void gitLog_validIgnoreGlobs_success() {
        Author author = getAlphaAllAliasAuthor();
        author = new Author(
                author,
                null,
                null,
                null,
                null,
                Collections.singletonList("annotationTest.java"),
                null);

        String content = GitLog.getWithFiles(config, author);
        Assertions.assertTrue(TestUtil.compareNumberFilesChanged(6, content));

        author = new Author(
                author,
                null,
                null,
                null,
                null,
                Collections.singletonList("**Test**"),
                null);
        content = GitLog.getWithFiles(config, author);
        Assertions.assertTrue(TestUtil.compareNumberFilesChanged(5, content));

        author = new Author(
                author,
                null,
                null,
                null,
                null,
                Collections.singletonList("README.md"),
                null);
        content = GitLog.getWithFiles(config, author);
        Assertions.assertTrue(TestUtil.compareNumberFilesChanged(6, content));

        author = new Author(
                author,
                null,
                null,
                null,
                null,
                Collections.singletonList("**.java"),
                null);
        content = GitLog.getWithFiles(config, author);
        Assertions.assertTrue(TestUtil.compareNumberFilesChanged(1, content));

        author = new Author(
                author,
                null,
                null,
                null,
                null,
                Collections.singletonList("./newPos"),
                null);
        content = GitLog.getWithFiles(config, author);
        Assertions.assertTrue(TestUtil.compareNumberFilesChanged(6, content));
    }

    @Test
    public void gitLog_invalidIgnoreGlobs_filtered() {
        Author author = getAlphaAllAliasAuthor();

        author = new Author(
                author,
                null,
                null,
                null,
                null,
                Collections.singletonList("../testrepo-Alpha"),
                null);
        String content = GitLog.getWithFiles(config, author);
        Assertions.assertTrue(TestUtil.compareNumberFilesChanged(7, content));

        author = new Author(
                author,
                null,
                null,
                null,
                null,
                Collections.singletonList("../*.java"),
                null);
        content = GitLog.getWithFiles(config, author);
        Assertions.assertTrue(TestUtil.compareNumberFilesChanged(7, content));

        author = new Author(
                author,
                null,
                null,
                null,
                null,
                Collections.singletonList("/newPos"),
                null);
        content = GitLog.getWithFiles(config, author);
        Assertions.assertTrue(TestUtil.compareNumberFilesChanged(7, content));

        author = new Author(
                author,
                null,
                null,
                null,
                null,
                Collections.singletonList(".."),
                null);
        content = GitLog.getWithFiles(config, author);
        Assertions.assertTrue(TestUtil.compareNumberFilesChanged(7, content));
    }

    @Test
    public void gitLog_authorWithAllCharactersRegexAlias_emptyResult() {
        Author authorWithAllCharactersRegexAlias = new Author("none", null, null, Collections.singletonList(".*"), null, null);

        String content = GitLog.get(config, authorWithAllCharactersRegexAlias);
        Assertions.assertTrue(content.isEmpty());
    }

    @Test
    public void gitLog_sinceDateInFuture_noContent() {
        LocalDateTime date = TimeUtil.getSinceDate(
                LocalDateTime.of(2050, Month.JANUARY, 1, 0, 0));
        config.setSinceDate(date);
        String content = GitLog.get(config, getAlphaAllAliasAuthor());
        Assertions.assertTrue(content.isEmpty());
    }

    @Test
    public void gitLog_invalidSinceDateInFuture_noContent() {
        LocalDateTime date = TimeUtil.getSinceDate(
                LocalDateTime.of(2100, Month.JANUARY, 1, 0, 0));
        config.setSinceDate(date);
        String content = GitLog.get(config, getAlphaAllAliasAuthor());
        Assertions.assertTrue(content.isEmpty());
    }

    @Test
    public void gitLog_untilDateBeforeAnyCommit_noContent() {
        LocalDateTime date = TimeUtil.getUntilDate(
                LocalDateTime.of(2010, Month.JANUARY, 1, 0, 0));
        config.setUntilDate(date);
        config.setSinceDate(null);
        String content = GitLog.get(config, getAlphaAllAliasAuthor());
        Assertions.assertTrue(content.isEmpty());
    }

    @Test
    public void gitLog_invalidUntilDateBeforeAnyCommit_noContent() {
        LocalDateTime date = TimeUtil.getUntilDate(
                LocalDateTime.of(1969, Month.JANUARY, 1, 0, 0));
        config.setUntilDate(date);
        config.setSinceDate(null);
        String content = GitLog.get(config, getAlphaAllAliasAuthor());
        Assertions.assertTrue(content.isEmpty());
    }

    @Test
    public void gitLog_emailWithAdditionOperator_success() {
        config.setBranch("617-FileAnalyzerTest-analyzeFile_emailWithAdditionOperator_success");
        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());
        Author author = new Author(MINGYI_AUTHOR_NAME);
        config.setAuthorList(Collections.singletonList(author));

        String content = GitLog.get(config, author);
        Assertions.assertTrue(TestUtil.compareNumberExpectedCommitsToGitLogLines(1, content));
    }

    @Test
    public void gitLog_getParentOfInitialCommit_noContent() {
        String content = GitLog.getParentCommits(config.getRepoRoot(), ROOT_COMMIT_HASH);
        Assertions.assertTrue(content.isEmpty());
    }

    @Test
    public void gitLog_getParentOfSecondCommit_initialCommit() {
        String content = GitLog.getParentCommits(config.getRepoRoot(), SECOND_COMMIT_HASH);
        Assertions.assertEquals(ROOT_COMMIT_HASH, content);
    }

    @Test
    public void gitLog_getParentOfMergeCommit_success() {
        config.setBranch("945-GitLogTest-getParentCommit_mergeCommit_success");
        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());

        String content = GitLog.getParentCommits(config.getRepoRoot(), MERGE_COMMIT_HASH);
        Assertions.assertEquals(MERGE_COMMIT_PARENTS_HASHES, Arrays.asList(content.split(" ")));
    }

    @Test
    public void gitLog_getParentOfNonExistentCommit_throwsRunTimeException() {
        Assertions.assertThrows(RuntimeException.class, () -> GitLog.getParentCommits(config.getRepoRoot(),
                NONEXISTENT_COMMIT_HASH));
    }
}
