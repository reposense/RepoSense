package reposense.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.system.LogsManager;

/**
 * Represents author configuration information from CSV config file for a single repository.
 */
public class AuthorConfiguration implements Cloneable {
    public static final String DEFAULT_BRANCH = "HEAD";
    public static final boolean DEFAULT_HAS_AUTHOR_CONFIG_FILE = false;
    private static final Logger logger = LogsManager.getLogger(AuthorConfiguration.class);
    private static final Pattern EMAIL_PLUS_OPERATOR_PATTERN =
            Pattern.compile("^(?<prefix>.+)\\+(?<suffix>.*)(?<domain>@.+)$");

    private boolean hasAuthorConfigFile = DEFAULT_HAS_AUTHOR_CONFIG_FILE;

    private RepoLocation location;
    private String branch;

    private transient List<Author> authorList = new ArrayList<>();
    private transient Map<String, Author> authorNamesToAuthorMap = new HashMap<>();
    private transient Map<String, Author> authorEmailsToAuthorMap = new HashMap<>();
    private transient Map<Author, String> authorDisplayNameMap = new HashMap<>();

    public AuthorConfiguration(RepoLocation location) {
        this(location, DEFAULT_BRANCH);
    }

    public AuthorConfiguration(RepoLocation location, String branch) {
        this.location = location;
        this.branch = location.isEmpty() ? DEFAULT_BRANCH : branch;
    }

    /**
     * Clears authors information and use the information provided from {@code standaloneConfig}. Also updates each
     * author's {@code ignoreGlobList}.
     */
    public void update(StandaloneConfig standaloneConfig, List<String> ignoreGlobList) {
        List<Author> newAuthorList = new ArrayList<>();
        Map<String, Author> newAuthorNamesToAuthorMap = new HashMap<>();
        Map<String, Author> newAuthorEmailsToAuthorMap = new HashMap<>();
        Map<Author, String> newAuthorDisplayNameMap = new HashMap<>();

        for (StandaloneAuthor sa : standaloneConfig.getAuthors()) {
            Author author = new Author(sa);
            author.importIgnoreGlobList(ignoreGlobList);

            newAuthorList.add(author);
            newAuthorDisplayNameMap.put(author, author.getDisplayName());
            List<String> aliases = new ArrayList<>(author.getAuthorAliases());
            List<String> emails = new ArrayList<>(author.getEmails());
            aliases.add(author.getGitId());
            aliases.forEach(alias -> {
                checkDuplicateAliases(newAuthorNamesToAuthorMap, alias, author.getGitId());
                newAuthorNamesToAuthorMap.put(alias, author);
            });
            emails.forEach(email -> newAuthorEmailsToAuthorMap.put(email, author));
        }

        setAuthorList(newAuthorList);
        setAuthorNamesToAuthorMap(newAuthorNamesToAuthorMap);
        setAuthorEmailsToAuthorMap(newAuthorEmailsToAuthorMap);
        setAuthorDisplayNameMap(newAuthorDisplayNameMap);
    }

    /**
     * Checks if {@code alias} of author with {@code gitId} is already being used by another author in
     * {@code authorDetailsToAuthorMap} and generates warnings.
     */
    public void checkDuplicateAliases(Map<String, Author> authorDetailsToAuthorMap, String alias, String gitId) {
        if (authorDetailsToAuthorMap.containsKey(alias)) {
            logger.warning(String.format(
                    "Duplicate alias %s found. The alias will belong to the last author - %s", alias, gitId));
        }
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
                && authorNamesToAuthorMap.equals(otherAuthorConfig.authorNamesToAuthorMap)
                && authorEmailsToAuthorMap.equals(otherAuthorConfig.authorEmailsToAuthorMap)
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
     * Sets the details of {@code author} to {@link AuthorConfiguration} including the default alias, aliases
     * and display name.
     */
    private void setAuthorDetails(Author author) {
        // Set Git Id and its corresponding email as default
        addAuthorNamesToAuthorMapEntry(author, author.getGitId());
        addAuthorNamesToAuthorMapEntry(author, author.getAuthorAliases());

        addAuthorEmailsToAuthorMapEntry(author, author.getEmails());

        setAuthorDisplayName(author, author.getDisplayName());
    }

    /**
     * Propagates {@code ignoreGlobList} to {@code author}.
     */
    public static void propagateIgnoreGlobList(Author author, List<String> ignoreGlobList) {
        author.importIgnoreGlobList(ignoreGlobList);
    }

    /**
     * Adds author to the {@code authorList}.
     */
    public void addAuthor(Author author) {
        authorList.add(author);
        setAuthorDetails(author);
    }

    /**
     * Adds {@code author} to the {@code authorList}, and propagates {@code ignoreGlobList} to the {@code author}.
     */
    public void addAuthor(Author author, List<String> ignoreGlobList) {
        addAuthor(author);
        propagateIgnoreGlobList(author, ignoreGlobList);
    }

    /**
     * Removes the authors provided in {@code ignoredAuthorsList} from the author list.
     */
    public void removeIgnoredAuthors(List<String> ignoredAuthorsList) {
        for (String author : ignoredAuthorsList) {
            Author authorToRemove = null;
            if (authorEmailsToAuthorMap.containsKey(author)) {
                authorToRemove = authorEmailsToAuthorMap.get(author);
            } else if (authorNamesToAuthorMap.containsKey(author.toLowerCase())) {
                authorToRemove = authorNamesToAuthorMap.get(author.toLowerCase());
            }

            if (authorToRemove != null) {
                removeAuthorInformation(authorToRemove);
            }
        }
    }

    /**
     * Removes all information of the {@code author} from the configs.
     * Precondition: {@code author} must be present in {@code authorDetailsToAuthorMap}.
     *
     * @param author the author to be removed.
     */
    public void removeAuthorInformation(Author author) {
        authorList.remove(author);
        authorDisplayNameMap.remove(author);
        authorNamesToAuthorMap.remove(author.getGitId().toLowerCase());

        List<String> aliases = author.getAuthorAliases();
        aliases.forEach(alias -> authorNamesToAuthorMap.remove(alias.toLowerCase()));

        List<String> emails = author.getEmails();
        emails.forEach(email -> authorEmailsToAuthorMap.remove(email));
    }

    /**
     * Adds new authors from {@code authorList}.
     * Also sets the default alias, aliases, emails and display name as well as {@code ignoreGlobList} of the new
     * authors. Skips the authors that have already been added previously.
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
     * Clears author mapping information.
     */
    public void clear() {
        clearAuthorDetailsToAuthorMap();
        authorDisplayNameMap.clear();
    }

    /**
     * Clears author details mapping information.
     */
    public void clearAuthorDetailsToAuthorMap() {
        authorNamesToAuthorMap.clear();
        authorEmailsToAuthorMap.clear();
    }

    /**
     * Resets author mapping information with the details of current author list.
     */
    public void buildFromAuthorList() {
        authorList.forEach(this::setAuthorDetails);
    }

    public Map<String, Author> getAuthorNamesToAuthorMap() {
        return authorNamesToAuthorMap;
    }

    public void setAuthorNamesToAuthorMap(Map<String, Author> authorNamesToAuthorMap) {
        this.authorNamesToAuthorMap = new HashMap<>();
        authorNamesToAuthorMap.forEach((name, author) -> this.authorNamesToAuthorMap.put(name.toLowerCase(), author));
    }

    public Map<String, Author> getAuthorEmailsToAuthorMap() {
        return authorEmailsToAuthorMap;
    }

    public void setAuthorEmailsToAuthorMap(Map<String, Author> authorEmailsToAuthorMap) {
        this.authorEmailsToAuthorMap = authorEmailsToAuthorMap;
    }

    public void setAuthorDisplayName(Author author, String displayName) {
        authorDisplayNameMap.put(author, displayName);
    }

    /**
     * Adds {@code name} as alias of {@code author} into the map.
     */
    public void addAuthorNamesToAuthorMapEntry(Author author, String name) {
        String nameInLowerCase = name.toLowerCase();
        checkDuplicateAliases(authorNamesToAuthorMap, nameInLowerCase, author.getGitId());
        authorNamesToAuthorMap.put(nameInLowerCase, author);
    }

    /**
     * Adds {@code names} as aliases of {@code author} into the map.
     */
    public void addAuthorNamesToAuthorMapEntry(Author author, List<String> names) {
        names.stream()
                .map(String::toLowerCase)
                .forEach(name -> {
                    checkDuplicateAliases(authorNamesToAuthorMap, name, author.getGitId());
                    authorNamesToAuthorMap.put(name, author);
                });
    }

    /**
     * Adds {@code emails} as aliases of {@code author} into the map.
     */
    public void addAuthorEmailsToAuthorMapEntry(Author author, List<String> emails) {
        emails.forEach(email -> {
            checkDuplicateAliases(authorEmailsToAuthorMap, email, author.getGitId());
            authorEmailsToAuthorMap.put(email, author);
        });
    }

    public RepoLocation getLocation() {
        return location;
    }

    /**
     * Attempts to find matching {@link Author} given a {@code name} and an {@code email}.
     * If no matching {@link Author} is found, {@link Author#UNKNOWN_AUTHOR} is returned.
     */
    public Author getAuthor(String name, String email) {
        if (authorNamesToAuthorMap.containsKey(name.toLowerCase())) {
            return authorNamesToAuthorMap.get(name.toLowerCase());
        }

        if (authorEmailsToAuthorMap.containsKey(email)) {
            return authorEmailsToAuthorMap.get(email);
        }

        Matcher matcher = EMAIL_PLUS_OPERATOR_PATTERN.matcher(email);
        if (matcher.matches()) {
            return authorEmailsToAuthorMap.getOrDefault(matcher.group("suffix") + matcher.group("domain"),
                    Author.UNKNOWN_AUTHOR);
        }

        return Author.UNKNOWN_AUTHOR;
    }

    public boolean isDefaultBranch() {
        return this.branch.equals(DEFAULT_BRANCH);
    }

    public boolean containsName(String name) {
        return authorNamesToAuthorMap.containsKey(name.toLowerCase()) || authorEmailsToAuthorMap.containsKey(name);
    }

    public void setHasAuthorConfigFile(boolean hasAuthorConfigFile) {
        this.hasAuthorConfigFile = hasAuthorConfigFile;
    }

    public boolean hasAuthorConfigFile() {
        return hasAuthorConfigFile;
    }

    /**
     * Creates an identical deep copy of this {@code AuthorConfiguration} object.
     *
     * @return Deep copy of this {@code AuthorConfiguration} object.
     * @throws CloneNotSupportedException if the cloning operation fails.
     */
    @Override
    public AuthorConfiguration clone() throws CloneNotSupportedException {
        AuthorConfiguration clone = (AuthorConfiguration) super.clone();
        clone.location = this.location.clone();
        clone.authorList = new ArrayList<>(this.authorList);
        clone.authorNamesToAuthorMap = new HashMap<>();
        clone.authorEmailsToAuthorMap = new HashMap<>();
        clone.authorDisplayNameMap = new HashMap<>();

        for (Map.Entry<String, Author> entry : this.authorNamesToAuthorMap.entrySet()) {
            clone.authorNamesToAuthorMap.put(entry.getKey(), entry.getValue().clone());
        }

        for (Map.Entry<String, Author> entry : this.authorEmailsToAuthorMap.entrySet()) {
            clone.authorEmailsToAuthorMap.put(entry.getKey(), entry.getValue().clone());
        }

        for (Map.Entry<Author, String> entry : this.authorDisplayNameMap.entrySet()) {
            clone.authorDisplayNameMap.put(entry.getKey().clone(), entry.getValue());
        }

        return clone;
    }
}
