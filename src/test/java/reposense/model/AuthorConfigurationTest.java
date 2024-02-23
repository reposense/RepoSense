package reposense.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reposense.parser.exceptions.InvalidLocationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthorConfigurationTest {
    private static final List<String> authorNames = Arrays.asList("fake", "fake2", "fake3");
    private static final List<String> aliases = Arrays.asList("hello", "world", "!!!");
    private static final List<String> emails = Arrays.asList("author1@email.com", "author2@email.com", "author3@email.com");
    private static final List<String> displayNames = Arrays.asList("author1", "author2", "author3");

    @Test
    public void clone_cloneAuthorConfiguration_success() throws Exception {
        AuthorConfiguration authorConfiguration = this.setUpAuthorConfiguration();
        AuthorConfiguration clonedAuthorConfiguration = authorConfiguration.clone();

        // test for object equality deeply
        List<Author> originalAuthorList = authorConfiguration.getAuthorList();
        List<Author> clonedAuthorList = clonedAuthorConfiguration.getAuthorList();
        Map<String, Author> authorNameToAuthorOriginal = authorConfiguration.getAuthorNamesToAuthorMap();
        Map<String, Author> authorNameToAuthorOther = clonedAuthorConfiguration.getAuthorNamesToAuthorMap();
        Map<String, Author> authorEmailToAuthorOriginal = authorConfiguration.getAuthorEmailsToAuthorMap();
        Map<String, Author> authorEmailToAuthorOther = clonedAuthorConfiguration.getAuthorEmailsToAuthorMap();
        Map<Author, String> authorDisplayAuthorOriginal = authorConfiguration.getAuthorDisplayNameMap();
        Map<Author, String> authorDisplayAuthorOther = clonedAuthorConfiguration.getAuthorDisplayNameMap();

        Assertions.assertNotSame(originalAuthorList, clonedAuthorList);
        Assertions.assertNotSame(authorConfiguration.getLocation(), clonedAuthorConfiguration.getLocation());
        Assertions.assertNotSame(authorNameToAuthorOriginal, authorNameToAuthorOther);
        Assertions.assertNotSame(authorDisplayAuthorOriginal, authorDisplayAuthorOther);
        Assertions.assertNotSame(authorEmailToAuthorOriginal, authorEmailToAuthorOther);

        for (int i = 0; i < originalAuthorList.size(); i++) {
            Assertions.assertNotSame(originalAuthorList.get(i), clonedAuthorList.get(i));
        }

        for (String s : displayNames) {
            Assertions.assertNotSame(authorNameToAuthorOriginal.get(s), authorNameToAuthorOther.get(s));
        }

        for (String s : emails) {
            Assertions.assertNotSame(authorEmailToAuthorOriginal.get(s), authorEmailToAuthorOther.get(s));
        }
    }

    private List<Author> setUpAuthors() {
        List<Author> authors = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Author a = new Author(authorNames.get(i));
            a.setAuthorAliases(Arrays.asList(aliases.get(i)));
            a.setEmails(Arrays.asList(emails.get(i)));
            a.setDisplayName(displayNames.get(i));
            authors.add(a);
        }

        return authors;
    }

    private AuthorConfiguration setUpAuthorConfiguration() {
        try {
            AuthorConfiguration authorConfiguration = new AuthorConfiguration(
                    new RepoLocation("location"), "branch"
            );
            List<Author> authors = this.setUpAuthors();

            Map<String, Author> nametoAuthorMap = new HashMap<>();
            Map<String, Author> emailToAuthorMap = new HashMap<>();
            Map<Author, String> authorToDisplayNameMap = new HashMap<>();

            for (Author author : authors) {
                nametoAuthorMap.put(author.getDisplayName(), author);
                emailToAuthorMap.put(author.getEmails().get(0), author);
                authorToDisplayNameMap.put(author, author.getDisplayName());
            }

            authorConfiguration.setAuthorList(authors);
            authorConfiguration.setAuthorEmailsToAuthorMap(emailToAuthorMap);
            authorConfiguration.setAuthorNamesToAuthorMap(nametoAuthorMap);
            authorConfiguration.setAuthorDisplayNameMap(authorToDisplayNameMap);
            return authorConfiguration;
        } catch (InvalidLocationException ex) {
            throw new AssertionError("Test failed: Location was invalid and used");
        }
    }

}
