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
    public void Author_validEmails_success() {
        List<String> emails = new ArrayList<>();
        emails.add("tester@test.net");
        emails.add("developer@example.com");
        System.out.println(emails);
        Author author = new Author("Tester", emails, null, null, null, null);
        System.out.println(emails);
        // The additional 2 emails comes from the Standard GitHub & Gitlab Emails.
        Assertions.assertEquals(emails.size() + 2, author.getEmails().size());
        Assertions.assertTrue(author.getEmails().containsAll(emails));

        emails.add("Tester@users.noreply.github.com");
        author = new Author("Tester", emails, null, null, null, null);
        // The additional 1 email comes from the Gitlab email as the standard GitHub has already been included.
        Assertions.assertEquals(emails.size() + 1, author.getEmails().size());
        Assertions.assertTrue(author.getEmails().containsAll(emails));

        emails.add("Tester@users.noreply.gitlab.com");
        author = new Author("Tester", emails, null, null, null, null);
        Assertions.assertEquals(emails.size(), author.getEmails().size());
        Assertions.assertTrue(author.getEmails().containsAll(emails));
    }

    @Test
    public void Author_throwIllegalArgumentException() {
        String[] emails = new String[] {"this.”is\\ invalid”@example.com", "developer@example.com"};
        AssertUtil.assertThrows(IllegalArgumentException.class, () -> new Author("Tester", Arrays.asList(emails), null, null, null, null));
        AssertUtil.assertThrows(IllegalArgumentException.class, () -> new Author("Tester", Collections.singletonList(""), null, null, null, null));
    }

    @Test
    public void Author_validGlobRegex_success() {
        String[] ignoreGlobs = new String[] {"**.adoc", "collated/**"};
        String[] testPaths = new String[] {"docs/UserGuide.adoc", "collated/codeeong.md"};

        Author author = new Author("Tester", null, null, null, Arrays.asList(ignoreGlobs), null);
        Assertions.assertEquals(2, author.getIgnoreGlobList().size());
        Assertions.assertTrue(author.getIgnoreGlobList().containsAll(Arrays.asList(ignoreGlobs)));
        Arrays.stream(testPaths).forEach(value ->
                Assertions.assertTrue(author.isIgnoringFile(Paths.get(value))));
    }

    @Test
    public void setIgnoreGlobList_quoteInGlobPattern_throwIllegalArgumentException() {
        Author author = new Author("Tester");
        String[] ignoreGlobs = new String[] {"**.adoc", "collated/**\""};

        Assertions.assertThrows(IllegalArgumentException.class, () -> new Author("Tester", null, null, null, Arrays.asList(ignoreGlobs), null));
    }

    @Test
    public void setIgnoreGlobList_semicolonInGlobPattern_throwIllegalArgumentException() {
        Author author = new Author("Tester");
        String[] ignoreGlobs = new String[] {"**.adoc; echo hi", "collated/**"};

        Assertions.assertThrows(IllegalArgumentException.class, () -> new Author("Tester", null, null, null, Arrays.asList(ignoreGlobs), null));
    }

    @Test
    public void appendIgnoreGlobList_validGlobRegex_success() {
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


        Author author = new Author("Tester", null, null, null, Arrays.asList(ignoreGlobs), null);
        Author authorWithAdditionalIgnoreGlobs = author.withAdditionalIgnoreGlobs(Arrays.asList(moreIgnoreGlobs));

        Assertions.assertEquals(4, authorWithAdditionalIgnoreGlobs.getIgnoreGlobList().size());
        Assertions.assertTrue(authorWithAdditionalIgnoreGlobs.getIgnoreGlobList().containsAll(ignoreGlobList));
        Arrays.stream(testPaths).forEach(value ->
                Assertions.assertTrue(authorWithAdditionalIgnoreGlobs.isIgnoringFile(Paths.get(value))));
    }

    @Test
    public void appendIgnoreGlobList_appendOrOperator_throwIllegalArgumentException() {
        Author author = new Author("Tester");
        String[] ignoreGlobs = new String[] {"**[!(.md)] | rm -rf /", "C:\\Program Files\\**"};

        Assertions.assertThrows(IllegalArgumentException.class, () -> new Author("Tester", null, null, null, Arrays.asList(ignoreGlobs), null));
    }
}
