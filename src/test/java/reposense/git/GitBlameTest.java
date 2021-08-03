package reposense.git;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import reposense.model.CommitHash;
import reposense.model.FileTypeTest;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.template.GitTestTemplate;

public class GitBlameTest extends GitTestTemplate {

    protected static final String TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH =
            "1565-GitBlameTest-blameWithPreviousAuthorsRaw_validFile_success";
    protected static final List<CommitHash> IGNORECOMMITLIST_TEST = new ArrayList<>();
    protected static final Pattern IGNORED_AUTHOR_PATTERN = Pattern.compile("(FH-30)");

    static {
        IGNORECOMMITLIST_TEST.add(new CommitHash("15748a25791d8ac78522481c87f0e033b4a76fa7"));
    }

    @Test
    public void blameRaw_validFile_success() {
        String content = GitBlame.blame(config.getRepoRoot(), "blameTest.java");
        Assert.assertFalse(content.isEmpty());
    }

    @Test(expected = RuntimeException.class)
    public void blameRaw_nonExistentFile_throwsRunTimeException() {
        GitBlame.blame(config.getRepoRoot(), "nonExistentFile");
    }

    @Test
    public void blameWithPreviousAuthorsRaw_validFile_success() throws Exception {
        RepoConfiguration blameWithPreviousAuthorsTestRepoConfig = new RepoConfiguration(
                new RepoLocation("https://github.com/FH-30/testrepo-Alpha.git"),
                TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH);
        blameWithPreviousAuthorsTestRepoConfig.setFormats(FileTypeTest.DEFAULT_TEST_FORMATS);
        GitClone.clone(blameWithPreviousAuthorsTestRepoConfig);

        // Once PR gets merged in at testrepo alpha delete the rest above and uncomment below
        // GitCheckout.checkout(config.getRepoRoot(), TEST_REPO_BLAME_WITH_PREVIOUS_AUTHORS_BRANCH);

        createTestIgnoreRevsFile(IGNORECOMMITLIST_TEST);
        String content = GitBlame.blameWithPreviousAuthors(blameWithPreviousAuthorsTestRepoConfig.getRepoRoot(),
                "blameTest.java");
        removeTestIgnoreRevsFile();
        Matcher ignoredAuthorMatcher = IGNORED_AUTHOR_PATTERN.matcher(content);
        Assert.assertFalse(ignoredAuthorMatcher.find());
    }

    @Test(expected = RuntimeException.class)
    public void blameWithPreviousAuthorsRaw_nonExistentFile_throwsRunTimeException() {
        GitBlame.blameWithPreviousAuthors(config.getRepoRoot(), "nonExistentFile");
    }
}
