package reposense.model;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.util.AssertUtil;

public class AuthorTest {

    @Test
    public void setEmail_validEmails_success() {
        List<String> emails = new ArrayList<>();
        emails.add("tester@test.net");
        emails.add("developer@example.com");

        Author author = new Author("Tester");
        author.setEmails(emails);
        // The additional 2 emails comes from the Standard GitHub & Gitlab Emails.
        Assertions.assertEquals(emails.size() + 2, author.getEmails().size());
        Assertions.assertTrue(author.getEmails().containsAll(emails));

        emails.add("Tester@users.noreply.github.com");
        author.setEmails(emails);
        // The additional 1 email comes from the Gitlab email as the standard GitHub has already been included.
        Assertions.assertEquals(emails.size() + 1, author.getEmails().size());
        Assertions.assertTrue(author.getEmails().containsAll(emails));

        emails.add("Tester@users.noreply.gitlab.com");
        author.setEmails(emails);
        Assertions.assertEquals(emails.size(), author.getEmails().size());
        Assertions.assertTrue(author.getEmails().containsAll(emails));
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
        Assertions.assertEquals(2, author.getIgnoreGlobList().size());
        Assertions.assertTrue(author.getIgnoreGlobList().containsAll(Arrays.asList(ignoreGlobs)));
        Arrays.stream(testPaths).forEach(value ->
                Assertions.assertTrue(author.isIgnoringFile(Paths.get(value))));
    }

    @Test
    public void setIgnoreGlobList_quoteInGlobPattern_throwIllegalArgumentException() {
        Author author = new Author("Tester");
        String[] ignoreGlobs = new String[] {"**.adoc", "collated/**\""};

        Assertions.assertThrows(IllegalArgumentException.class, () -> author.setIgnoreGlobList(
                Arrays.asList(ignoreGlobs)));
    }

    @Test
    public void setIgnoreGlobList_semicolonInGlobPattern_throwIllegalArgumentException() {
        Author author = new Author("Tester");
        String[] ignoreGlobs = new String[] {"**.adoc; echo hi", "collated/**"};

        Assertions.assertThrows(IllegalArgumentException.class, () -> author.setIgnoreGlobList(
                Arrays.asList(ignoreGlobs)));
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

        Assertions.assertEquals(4, author.getIgnoreGlobList().size());
        Assertions.assertTrue(author.getIgnoreGlobList().containsAll(ignoreGlobList));
        Arrays.stream(testPaths).forEach(value ->
                Assertions.assertTrue(author.isIgnoringFile(Paths.get(value))));
    }

    @Test
    public void appendIgnoreGlobList_appendOrOperator_throwIllegalArgumentException() {
        Author author = new Author("Tester");
        String[] ignoreGlobs = new String[] {"**[!(.md)] | rm -rf /", "C:\\Program Files\\**"};

        Assertions.assertThrows(IllegalArgumentException.class, () -> author.importIgnoreGlobList(
                Arrays.asList(ignoreGlobs)));
    }
}
