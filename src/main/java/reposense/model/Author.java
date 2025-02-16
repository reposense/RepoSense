package reposense.model;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Represents an immutable Git Author.
 */
public final class Author {
    public static final String NAME_NO_AUTHOR_WITH_COMMITS_FOUND =
            "NO AUTHOR WITH COMMITS FOUND WITHIN THIS PERIOD OF TIME";
    private static final String UNKNOWN_AUTHOR_GIT_ID = "-";

    private static final String STANDARD_GITHUB_EMAIL_DOMAIN = "@users.noreply.github.com";
    private static final String STANDARD_GITLAB_EMAIL_DOMAIN = "@users.noreply.gitlab.com";

    private static final String MESSAGE_UNCOMMON_EMAIL_PATTERN = "The provided email, %s, uses uncommon pattern.";
    private static final String MESSAGE_UNCOMMON_GLOB_PATTERN = "The provided ignore glob, %s, uses uncommon pattern.";
    private static final String COMMON_EMAIL_REGEX =
            "^([a-zA-Z0-9_\\-\\.\\+]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
    private static final String COMMON_GLOB_REGEX = "^[-a-zA-Z0-9 _/\\\\*!{}\\[\\]!(),:.]*$";
    public static final Author UNKNOWN_AUTHOR = new Author(UNKNOWN_AUTHOR_GIT_ID);

    private final String gitId;
    private final List<String> emails;
    private final String displayName;
    private final List<String> authorAliases;
    private final List<String> ignoreGlobList;
    private final PathMatcher ignoreGlobMatcher;

    public Author(String gitId) {
        this.gitId = gitId;
        List<String> emailList = new ArrayList<>();
        addStandardGitHostEmails(emailList);
        this.emails = List.copyOf(emailList);
        this.displayName = gitId;
        this.authorAliases = new ArrayList<>();
        this.ignoreGlobList = new ArrayList<>();
        this.ignoreGlobMatcher = createPathMatcher(this.ignoreGlobList);
    }

    public Author(String gitId, List<String> emails, String displayName, List<String> authorAliases, List<String> ignoreGlobList, PathMatcher ignoreGlobMatcher) {
        this.gitId = Optional.ofNullable(gitId).orElseThrow(() -> new IllegalArgumentException("gitId cannot be null"));

        this.emails = Optional.ofNullable(emails)
                .map(e -> {
                    List<String> emailsCopy = List.copyOf(e);
                    validateEmails(emailsCopy);
                    addStandardGitHostEmails(emailsCopy);
                    return emailsCopy;
                }).orElseGet(Collections::emptyList);


        this.displayName = Optional.ofNullable(displayName)
                .filter(d -> !d.isEmpty())
                .orElse(this.gitId);
        this.authorAliases = List.copyOf(Optional.ofNullable(authorAliases).orElseGet(Collections::emptyList));
        this.ignoreGlobList = List.copyOf(Optional.ofNullable(ignoreGlobList)
                .map(i -> {
                    validateIgnoreGlobs(i);
                    return i;
                }).orElseGet(Collections::emptyList));

        this.ignoreGlobMatcher = Optional.ofNullable(ignoreGlobMatcher).orElse(createPathMatcher(this.ignoreGlobList));
    }

    public Author(StandaloneAuthor sa) {
        this.gitId = sa.getGitId();

        List<String> emailsCopy = List.copyOf(sa.getEmails());
        validateEmails(emailsCopy);
        addStandardGitHostEmails(emailsCopy);
        this.emails = emailsCopy;

        this.displayName = !sa.getDisplayName().isEmpty() ? sa.getDisplayName() : sa.getGitId();

        this.authorAliases = List.copyOf(sa.getAuthorNames());

        List<String> globList = List.copyOf(sa.getIgnoreGlobList());
        validateIgnoreGlobs(globList);
        this.ignoreGlobList = List.copyOf(globList);
        this.ignoreGlobMatcher = createPathMatcher(this.ignoreGlobList);
    }

    public Author(Author another, String gitId, List<String> emails, String displayName, List<String> authorAliases, List<String> ignoreGlobList, PathMatcher ignoreGlobMatcher) {
        this(
                Optional.ofNullable(gitId).orElse(another.gitId),
                Optional.ofNullable(emails).orElse(another.emails),
                Optional.ofNullable(displayName).orElse(another.displayName),
                Optional.ofNullable(authorAliases).orElse(another.authorAliases),
                Optional.ofNullable(ignoreGlobList).orElse(another.ignoreGlobList),
                Optional.ofNullable(ignoreGlobMatcher).orElse(another.ignoreGlobMatcher)
                );
    }

    public Author(Author another) {
        this.gitId = another.gitId;
        this.emails = another.emails;
        this.displayName = another.displayName;
        this.authorAliases = another.authorAliases;
        this.ignoreGlobList = another.ignoreGlobList;
        this.ignoreGlobMatcher = another.ignoreGlobMatcher;
    }

    /**
     * Checks that all the strings in the {@code emails} only contains commonly used email patterns.
     *
     * @throws IllegalArgumentException if any of the values do not meet the criteria.
     */
    private static void validateEmails(List<String> emails) {
        Pattern emailPattern = Pattern.compile(COMMON_EMAIL_REGEX);
        for (String email : emails) {
            if (!emailPattern.matcher(email).matches()) {
                throw new IllegalArgumentException(String.format(MESSAGE_UNCOMMON_EMAIL_PATTERN, email));
            }
        }
    }

    /**
     * Checks that all the strings in the {@code ignoreGlobList} only contains commonly used glob patterns.
     *
     * @throws IllegalArgumentException if any of the values do not meet the criteria.
     */
    private static void validateIgnoreGlobs(List<String> ignoreGlobList) {
        Pattern globPattern = Pattern.compile(COMMON_GLOB_REGEX);
        for (String glob : ignoreGlobList) {
            if (!globPattern.matcher(glob).matches()) {
                throw new IllegalArgumentException(String.format(MESSAGE_UNCOMMON_GLOB_PATTERN, glob));
            }
        }
    }

    private PathMatcher createPathMatcher(List<String> globList) {
        String globString = "glob:{" + String.join(",", globList) + "}";
        return FileSystems.getDefault().getPathMatcher(globString);
    }

    private void addStandardGitHostEmails(List<String> emails) {
        String standardGitHubEmail = gitId + STANDARD_GITHUB_EMAIL_DOMAIN;
        String standardGitLabEmail = gitId + STANDARD_GITLAB_EMAIL_DOMAIN;
        if (!emails.contains(standardGitHubEmail)) {
            emails.add(standardGitHubEmail);
        }
        if (!emails.contains(standardGitLabEmail)) {
            emails.add(standardGitLabEmail);
        }
    }

    // Getters remain unchanged but now return immutable collections
    public String getGitId() {
        return gitId;

    }
    public List<String> getEmails() {
        return emails;
    }

    public String getDisplayName() {
        return this.displayName;

    }
    public List<String> getAuthorAliases() {
        return List.copyOf(this.authorAliases);
    }

    public List<String> getIgnoreGlobList() {
        return List.copyOf(this.ignoreGlobList);
    }

    /**
     * Creates a new Author instance with additional ignore globs.
     */
    public Author withAdditionalIgnoreGlobs(List<String> newIgnoreGlobs) {
        validateIgnoreGlobs(newIgnoreGlobs);
        List<String> combinedGlobs = new ArrayList<>(this.ignoreGlobList);
        newIgnoreGlobs.forEach(glob -> {
            if (!combinedGlobs.contains(glob)) {
                combinedGlobs.add(glob);
            }
        });

        return new Author(
                this.gitId,
                this.emails,
                this.displayName,
                this.authorAliases,
                combinedGlobs,
                createPathMatcher(combinedGlobs)
        );
    }


    /**
     * Returns true if this author is ignoring the {@code filePath} based on its ignore glob matcher.
     */
    public boolean isIgnoringFile(Path filePath) {
        return ignoreGlobMatcher.matches(filePath);
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (this == other) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof Author)) {
            return false;
        }

        Author otherAuthor = (Author) other;
        return this.gitId.equalsIgnoreCase(otherAuthor.gitId);
    }

    @Override
    public int hashCode() {
        return gitId != null ? gitId.toLowerCase().hashCode() : 0;
    }

    @Override
    public String toString() {
        return gitId;
    }

}