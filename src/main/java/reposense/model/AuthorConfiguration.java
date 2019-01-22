package reposense.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class AuthorConfiguration {
    private transient List<Author> authorList = new ArrayList<>();
    private transient TreeMap<String, Author> authorEmailsAndAliasesMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private transient Map<Author, String> authorDisplayNameMap = new HashMap<>();

    public Map<Author, String> getAuthorDisplayNameMap() {
        return authorDisplayNameMap;
    }

    public void setAuthorDisplayNameMap(Map<Author, String> authorDisplayNameMap) {
        this.authorDisplayNameMap = authorDisplayNameMap;
    }

    public List<Author> getAuthorList() {
        return authorList;
    }

    /**
     * Sets the details of {@code author} to {@code RepoConfiguration} including the default alias, alias
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
     * Propagate the {@code IgnoreGlobList} of {@code RepoConfiguration} to {@code author}.
     */
    private void propagateIgnoreGlobList(Author author, List<String> ignoreGlobList) {
        author.appendIgnoreGlobList(ignoreGlobList);
    }

    public void addAuthor(Author author, List<String> ignoreGlobList) {
        authorList.add(author);
        setAuthorDetails(author);
        propagateIgnoreGlobList(author, ignoreGlobList);
    }

    public boolean containsAuthor(Author author) {
        return authorList.contains(author);
    }

    public void setAuthorList(List<Author> authorList) {
        this.authorList = authorList;
    }

    public void resetAuthorInformation(List<String> ignoreGlobList) {
        authorEmailsAndAliasesMap.clear();
        authorDisplayNameMap.clear();

        authorList.forEach(author -> {
            setAuthorDetails(author);
            propagateIgnoreGlobList(author, ignoreGlobList);
        });
    }

    public TreeMap<String, Author> getAuthorEmailsAndAliasesMap() {
        return authorEmailsAndAliasesMap;
    }

    public void setAuthorEmailsAndAliasesMap(TreeMap<String, Author> authorEmailsAndAliasesMap) {
        this.authorEmailsAndAliasesMap = authorEmailsAndAliasesMap;
    }

    public void setAuthorDisplayName(Author author, String displayName) {
        authorDisplayNameMap.put(author, displayName);
    }

    public void addAuthorEmailsAndAliasesMapEntry(Author author, List<String> values) {
        values.forEach(value -> authorEmailsAndAliasesMap.put(value, author));
    }
}
