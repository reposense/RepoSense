package reposense.model;

import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.parser.InvalidLocationException;
import reposense.util.StringsUtil;
import reposense.util.SystemUtil;

/**
 * Represents a repository location.
 */
public class RepoLocation {
    private static final String GIT_LINK_SUFFIX = ".git";
    private static final Pattern GIT_REPOSITORY_LOCATION_PATTERN =
            Pattern.compile("^(ssh|git|https?)://.*?/(?<path>.+)/(?<repoName>.+?)(\\.git)?$");
    private static final Pattern SCP_LIKE_SSH_REPOSITORY_LOCATION_PATTERN =
            Pattern.compile("^.*?:(?<path>.+)/(?<repoName>.+?)(\\.git)?$");

    private final String location;
    private final String repoName;
    private String path;

    /**
     * @throws InvalidLocationException if {@code location} cannot be represented by a {@code URL} or {@code Path}.
     */
    public RepoLocation(String location) throws InvalidLocationException {
        if (SystemUtil.isWindows()) {
            location = StringsUtil.removeTrailingBackslash(location);
        }
        this.location = location;
        Matcher matcher = GIT_REPOSITORY_LOCATION_PATTERN.matcher(location);

        if (isLocalRepo(location)) {
            repoName = Paths.get(location).getFileName().toString().replace(GIT_LINK_SUFFIX, "");
        } else if (matcher.matches()) {
            path = matcher.group("path");
            repoName = matcher.group("repoName");
        } else {
            repoName = "UNABLE_TO_DETERMINE";
        }
    }

    public boolean isEmpty() {
        return location.isEmpty();
    }

    public String getRepoName() {
        return repoName;
    }

    public String getOrganization() {
        return path;
    }

    /**
     * Returns true if {@code repoArgument} is a valid local repository argument.
     * This implementation follows directly from the {@code git clone}
     * <a href="https://git-scm.com/docs/git-clone#_git_urls">specification</a>.
     *
     * Throws an in
     */
    private boolean isLocalRepo(String repoArgument) {
        boolean containsColon = repoArgument.contains(":");
        if (!containsColon) {
            return true;
        }

        boolean hasSlashBeforeFirstColon = repoArgument.split(":", 2)[0].contains("/");
        if (hasSlashBeforeFirstColon) {
            return true;
        }

        boolean isUrlFileType = repoArgument.split(":", 2)[0].equals("file");
        if (isUrlFileType) {
            return true;
        }

        return false;
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
