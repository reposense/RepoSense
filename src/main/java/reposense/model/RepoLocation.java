package reposense.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.parser.InvalidLocationException;

/**
 * Represents a repository location.
 */
public class RepoLocation {
    private static final String GIT_LINK_SUFFIX = ".git";
    private static final Pattern GIT_REPOSITORY_LOCATION_PATTERN =
            Pattern.compile("^.*github.com\\/(?<org>.+?)\\/(?<repoName>.+?)\\.git$");

    private final String location;
    private final String repoName;
    private String organization;

    /**
     * @throws InvalidLocationException if {@code location} cannot be represented by a {@code URL} or {@code Path}.
     */
    public RepoLocation(String location) throws InvalidLocationException {
        verifyLocation(location);
        this.location = location;
        Matcher matcher = GIT_REPOSITORY_LOCATION_PATTERN.matcher(location);

        if (matcher.matches()) {
            organization = matcher.group("org");
            repoName = matcher.group("repoName");
        } else {
            repoName = Paths.get(location).getFileName().toString().replace(GIT_LINK_SUFFIX, "");
        }
    }

    public boolean isEmpty() {
        return location.isEmpty();
    }

    public String getRepoName() {
        return repoName;
    }

    public String getOrganization() {
        return organization;
    }

    /**
     * Verifies {@code location} can be presented as a {@code URL} or {@code Path}.
     * @throws InvalidLocationException if otherwise.
     */
    private void verifyLocation(String location) throws InvalidLocationException {
        boolean isValidPathLocation = false;
        boolean isValidGitUrl = false;

        try {
            Path pathLocation = Paths.get(location);
            isValidPathLocation = Files.exists(pathLocation);
        } catch (InvalidPathException ipe) {
            // Ignore exception
        }

        try {
            new URL(location);
            isValidGitUrl = location.endsWith(GIT_LINK_SUFFIX);
        } catch (MalformedURLException mue) {
            // Ignore exception
        }

        if (!isValidPathLocation && !isValidGitUrl) {
            throw new InvalidLocationException(location + " is an invalid location.");
        }
    }

    @Override
    public String toString() {
        return location;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (this == other) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof RepoLocation)) {
            return false;
        }

        RepoLocation otherLocation = (RepoLocation) other;
        return this.location.equals(otherLocation.location);
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }
}
