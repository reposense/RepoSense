package reposense.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import reposense.util.AssertUtil;

public class AuthorTest {

    @Test
    public void constructor_empty_throwsIllegalArgumentException() {
        AssertUtil.assertThrows(IllegalArgumentException.class, () -> new Author(""));
    }

    @Test
    public void constructor_illegalGitHubId_throwsIllegalArgumentException() {
        AssertUtil.assertThrows(IllegalArgumentException.class, () -> new Author("lithiumlkid & echo hi"));
        AssertUtil.assertThrows(IllegalArgumentException.class, () -> new Author("lithiumlkid ; echo hi"));
        AssertUtil.assertThrows(IllegalArgumentException.class, () -> new Author("lithiumlkid | rm -rf /"));
    }

    @Test
    public void constructor_validGitHubId_success() {
        String gitId = "lithiumlkid";
        Author author = new Author(gitId);
        assertEquals(author.getGitId(), gitId);

        gitId = "LAPTOP-7KFM2KSP\\User";
        author = new Author(gitId);
        assertEquals(author.getGitId(), gitId);
    }
}
