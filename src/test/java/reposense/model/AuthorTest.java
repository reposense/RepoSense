package reposense.model;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.util.AssertUtil;

public class AuthorTest {

    @Test
    public void setEmail_validEmails_success() {
        Author author = new Author("Tester");
        String[] emails = new String[] {"tester@test.net", "developer@example.com"};

        author.setEmails(Arrays.asList(emails));

        // The additional 1 email comes from the Standard GitHub Email.
        Assert.assertEquals(emails.length + 1, author.getEmails().size());

        Assert.assertTrue(author.getEmails().containsAll(Arrays.asList(emails)));
    }

    @Test
    public void setEmail_invalidEmails_throwIllegalArgumentException() {
        Author author = new Author("Tester");
        String[] emails = new String[] {"this.”is\\ invalid”@example.com", "developer@example.com"};

        AssertUtil.assertThrows(IllegalArgumentException.class, () -> author.setEmails(Arrays.asList(emails)));
        AssertUtil.assertThrows(IllegalArgumentException.class, () -> author.setEmails(Collections.singletonList("")));
    }

    @Test
    public void setIgnoreGlobList_validGlobRegex_success() {
        Author author = new Author("Tester");
        String[] ignoreGlobs = new String[] {"**.adoc", "collated/**"};
        String[] testPaths = new String[] {"docs/UserGuide.adoc", "collated/codeeong.md"};

        author.setIgnoreGlobList(Arrays.asList(ignoreGlobs));
        Assert.assertEquals(2, author.getIgnoreGlobList().size());
        Assert.assertTrue(author.getIgnoreGlobList().containsAll(Arrays.asList(ignoreGlobs)));
        Arrays.stream(testPaths).forEach(value ->
                Assert.assertTrue(author.isIgnoringFile(Paths.get(value))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIgnoreGlobList_quoteInGlobPattern_throwIllegalArgumentException() {
        Author author = new Author("Tester");
        String[] ignoreGlobs = new String[] {"**.adoc", "collated/**\""};

        author.setIgnoreGlobList(Arrays.asList(ignoreGlobs));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setIgnoreGlobList_semicolonInGlobPattern_throwIllegalArgumentException() {
        Author author = new Author("Tester");
        String[] ignoreGlobs = new String[] {"**.adoc; echo hi", "collated/**"};

        author.setIgnoreGlobList(Arrays.asList(ignoreGlobs));
    }

    @Test
    public void appendIgnoreGlobList_validGlobRegex_success() {
        Author author = new Author("Tester");
        String[] ignoreGlobs = new String[] {"**.adoc", "collated/**"};
        String[] moreIgnoreGlobs = new String[] {"**[!(.md)]", "C:\\\\Program Files\\\\**"};
        String[] testPaths = new String[] {
            "docs/UserGuide.adoc",
            "collated/codeeong.md",
            "C:\\Program Files\\RepoSense\\README.md",
            "/this/is/not/a/md/file.txt"
        };
        List<String> ignoreGlobList = new ArrayList<>(Arrays.asList(ignoreGlobs));
        ignoreGlobList.addAll(Arrays.asList(moreIgnoreGlobs));

        author.setIgnoreGlobList(Arrays.asList(ignoreGlobs));
        author.importIgnoreGlobList(Arrays.asList(moreIgnoreGlobs));

        Assert.assertEquals(4, author.getIgnoreGlobList().size());
        Assert.assertTrue(author.getIgnoreGlobList().containsAll(ignoreGlobList));
        Arrays.stream(testPaths).forEach(value ->
                Assert.assertTrue(author.isIgnoringFile(Paths.get(value))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void appendIgnoreGlobList_appendOrOperator_throwIllegalArgumentException() {
        Author author = new Author("Tester");
        String[] ignoreGlobs = new String[] {"**[!(.md)] | rm -rf /", "C:\\Program Files\\**"};

        author.importIgnoreGlobList(Arrays.asList(ignoreGlobs));
    }
}
