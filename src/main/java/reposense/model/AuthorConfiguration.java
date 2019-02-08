package reposense.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import reposense.system.LogsManager;

/**
 * Represents author configuration information from CSV config file for a single repository.
 */
public class AuthorConfiguration {
    public static final String DEFAULT_BRANCH = "HEAD";
    private static final Logger logger = LogsManager.getLogger(AuthorConfiguration.class);

    private RepoLocation location;
    private String branch;

    private transient List<Author> authorList = new ArrayList<>();
    private transient Map<String, Author> authorEmailsAndAliasesMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private transient Map<Author, String> authorDisplayNameMap = new HashMap<>();

    public AuthorConfiguration(RepoLocation location) {
        this(location, DEFAULT_BRANCH);
    }

    public AuthorConfiguration(RepoLocation location, String branch) {
        this.location = location;
        this.branch = location.isEmpty() ? DEFAULT_BRANCH : branch;
    }

    /**
     * Clears authors information and use the information provided from {@code standaloneConfig}.
     */
    public void update(StandaloneConfig standaloneConfig) {
        List<Author> newAuthorList = new ArrayList<>();
        Map<String, Author> newAuthorEmailsAndAliasesMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Map<Author, String> newAuthorDisplayNameMap = new HashMap<>();
        List<String> newIgnoreGlobList = standaloneConfig.getIgnoreGlobList();

        for (StandaloneAuthor sa : standaloneConfig.getAuthors()) {
            Author author = new Author(sa);
            author.appendIgnoreGlobList(newIgnoreGlobList);

            newAuthorList.add(author);
            newAuthorDisplayNameMap.put(author, author.getDisplayName());
            List<String> aliases = new ArrayList<>(author.getAuthorAliases());
            List<String> emails = new ArrayList<>(author.getEmails());
            aliases.add(author.getGitId());
            aliases.forEach(alias -> newAuthorEmailsAndAliasesMap.put(alias, author));
            emails.forEach(email -> newAuthorEmailsAndAliasesMap.put(email, author));
        }

        Format.validateFormats(standaloneConfig.getFormats());
        CommitHash.validateCommits(standaloneConfig.getIgnoreCommitList());

        // only assign the new values when all the fields in {@code standaloneConfig} pass the validations.
        setAuthorList(newAuthorList);
        setAuthorEmailsAndAliasesMap(newAuthorEmailsAndAliasesMap);
        setAuthorDisplayNameMap(newAuthorDisplayNameMap);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof AuthorConfiguration)) {
            return false;
        }

        AuthorConfiguration otherAuthorConfig = (AuthorConfiguration) other;

        return location.equals(otherAuthorConfig.location)
                && branch.equals(otherAuthorConfig.branch)
                && authorList.equals(otherAuthorConfig.authorList)
                && authorEmailsAndAliasesMap.equals(otherAuthorConfig.authorEmailsAndAliasesMap)
                && authorDisplayNameMap.equals(otherAuthorConfig.authorDisplayNameMap);
    }

    public Map<Author, String> getAuthorDisplayNameMap() {
        return authorDisplayNameMap;
    }

    public void setAuthorDisplayNameMap(Map<Author, String> authorDisplayNameMap) {
        this.authorDisplayNameMap = authorDisplayNameMap;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public List<Author> getAuthorList() {
        return authorList;
    }

    /**
     * Sets the details of {@code author} to {@code AuthorConfiguration} including the default alias, aliases
     * and display name.
     */
    private void setAuthorDetails(Author author) {
        // Set GitHub Id and its corresponding email as default
        addAuthorEmailsAndAliasesMapEntry(author, Arrays.asList(author.getGitId()));

        addAuthorEmailsAndAliasesMapEntry(author, author.getAuthorAliases());
        addAuthorEmailsAndAliasesMapEntry(author, author.getEmails());

        setAuthorDisplayName(author, author.getDisplayName());
    }

    /**
     * Propagates {@code ignoreGlobList} to {@code author}.
     */
    public static void propagateIgnoreGlobList(Author author, List<String> ignoreGlobList) {
        author.appendIgnoreGlobList(ignoreGlobList);
    }

    public void addAuthor(Author author) {
        authorList.add(author);
        setAuthorDetails(author);
    }

    public void addAuthor(Author author, List<String> ignoreGlobList) {
        addAuthor(author);
        propagateIgnoreGlobList(author, ignoreGlobList);
    }

    /**
     * Adds new authors from {@code authorList} and sets the default alias, aliases, emails and display name
     * of the new authors. Skips authors that have already been added previously.
     */
    public void addAuthors(List<Author> authorList, List<String> ignoreGlobList) {
        for (Author author : authorList) {
            if (containsAuthor(author)) {
                logger.warning(String.format(
                        "Skipping author as %s already in repository %s %s", author.getGitId(), location, branch));
                continue;
            }

            addAuthor(author, ignoreGlobList);
        }
    }

    public boolean containsAuthor(Author author) {
        return authorList.contains(author);
    }

    public void setAuthorList(List<Author> authorList) {
        this.authorList = authorList;
    }

    /**
     * Clears author mapping information and resets it with the details of current author list.
     */
    public void resetAuthorInformation(List<String> ignoreGlobList) {
        authorEmailsAndAliasesMap.clear();
        authorDisplayNameMap.clear();

        authorList.forEach(author -> {
            setAuthorDetails(author);
            propagateIgnoreGlobList(author, ignoreGlobList);
        });
    }

    public Map<String, Author> getAuthorEmailsAndAliasesMap() {
        return authorEmailsAndAliasesMap;
    }

    public void setAuthorEmailsAndAliasesMap(Map<String, Author> authorEmailsAndAliasesMap) {
        this.authorEmailsAndAliasesMap = authorEmailsAndAliasesMap;
    }

    public void setAuthorDisplayName(Author author, String displayName) {
        authorDisplayNameMap.put(author, displayName);
    }

    public void addAuthorEmailsAndAliasesMapEntry(Author author, List<String> values) {
        values.forEach(value -> authorEmailsAndAliasesMap.put(value, author));
    }

    public RepoLocation getLocation() {
        return location;
    }
}
