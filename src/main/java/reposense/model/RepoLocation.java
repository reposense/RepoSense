package reposense.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.parser.InvalidLocationException;
import reposense.report.ErrorSummary;
import reposense.util.StringsUtil;
import reposense.util.SystemUtil;

/**
 * Represents a repository location.
 */
public class RepoLocation {
    private static final String MESSAGE_INVALID_LOCATION = "%s is an invalid location.";
    private static final String MESSAGE_INVALID_REMOTE_URL = "%s is an invalid remote URL.";

    private static final Pattern GIT_REPOSITORY_LOCATION_PATTERN =
            Pattern.compile("^(ssh|git|https?|ftps?)://[^/]*?/(?<path>.*?)/?(?<repoName>[^/]+?)(/?\\.git)?/?$");
    private static final Pattern SCP_LIKE_SSH_REPOSITORY_LOCATION_PATTERN =
            Pattern.compile("^.*?:(?<path>[^/].*?)??/??(?<repoName>[^/]+?)(\\.git)?/?$");
    private static final Pattern LOCAL_REPOSITORY_NON_WINDOWS_LOCATION_PATTERN =
            Pattern.compile("^(file://)?(?<path>.*?)/?(?<repoName>[^/]+?)(/?\\.git)?/?$");
    private static final Pattern LOCAL_REPOSITORY_WINDOWS_LOCATION_PATTERN =
            Pattern.compile("^(?<path>.*?)\\\\?(?<repoName>[^\\\\]+?)(\\\\?\\.git)?\\\\?$");

    private final String location;
    private final String repoName;
    private final String organization;

    /**
     * @throws InvalidLocationException if {@code location} cannot be represented by a {@code URL} or {@code Path}.
     */
    public RepoLocation(String location) throws InvalidLocationException {
        if (SystemUtil.isWindows()) {
            location = StringsUtil.removeTrailingBackslash(location);
        }

        this.location = location;

        String[] repoNameAndOrg;
        if (location.isEmpty()) {
            repoNameAndOrg = new String[] {"", ""};
        } else if (isLocalRepo(location)) {
            repoNameAndOrg = getLocalRepoNameAndOrg(location);
        } else {
            repoNameAndOrg = getRemoteRepoNameAndOrg(location);
        }

        this.repoName = repoNameAndOrg[0];
        this.organization = repoNameAndOrg[1];
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
     * Returns true if {@code repoArgument} is a valid local repository argument.
     * This implementation follows directly from the {@code git clone}
     * <a href="https://git-scm.com/docs/git-clone#_git_urls">specification</a>.
     */
    public static boolean isLocalRepo(String repoArgument) {
        if (!repoArgument.contains(":")) {
            return true;
        }

        boolean hasSlashBeforeFirstColon = repoArgument.split(":", 2)[0].contains("/");
        if (hasSlashBeforeFirstColon) {
            return true;
        }

        String[] urlProtocolDetails = repoArgument.split("://", 2);
        if (urlProtocolDetails[0].equals("file")) {
            return true;
        }

        return false;
    }

    private String[] getLocalRepoNameAndOrg(String location) throws InvalidLocationException {
        boolean isWindows = SystemUtil.isWindows();
        if (isWindows) {
            location = location.replace("file://", "");
            location = location.replaceAll("/", "\\\\");
        }
        Pattern localRepoPattern = isWindows
                ? LOCAL_REPOSITORY_WINDOWS_LOCATION_PATTERN
                : LOCAL_REPOSITORY_NON_WINDOWS_LOCATION_PATTERN;
        Matcher localRepoMatcher = localRepoPattern.matcher(location);

        if (!localRepoMatcher.matches()) {
            ErrorSummary.getInstance().addErrorMessage(location,
                    String.format(MESSAGE_INVALID_LOCATION, location));
            throw new InvalidLocationException(String.format(MESSAGE_INVALID_LOCATION, location));
        }

        String tempRepoName = localRepoMatcher.group("repoName");
        String fileSeparator = isWindows ? "\\\\" : "/";
        String tempOrganization = localRepoMatcher.group("path").replaceAll(fileSeparator, "-");

        return new String[] {tempRepoName, tempOrganization};
    }

    private String[] getRemoteRepoNameAndOrg(String location) throws InvalidLocationException {
        Matcher remoteRepoMatcher = GIT_REPOSITORY_LOCATION_PATTERN.matcher(location);
        Matcher sshRepoMatcher = SCP_LIKE_SSH_REPOSITORY_LOCATION_PATTERN.matcher(location);

        boolean isNormalUrl = remoteRepoMatcher.matches();
        if (isNormalUrl) {
            try {
                new URI(location);
            } catch (URISyntaxException e) {
                ErrorSummary.getInstance().addErrorMessage(location,
                        String.format(MESSAGE_INVALID_REMOTE_URL, location));
                throw new InvalidLocationException(String.format(MESSAGE_INVALID_REMOTE_URL, location));
            }
        }
        boolean isValidRemoteRepoUrl = remoteRepoMatcher.matches() || sshRepoMatcher.matches();
        if (!isValidRemoteRepoUrl) {
            ErrorSummary.getInstance().addErrorMessage(location,
                    String.format(MESSAGE_INVALID_REMOTE_URL, location));
            throw new InvalidLocationException(String.format(MESSAGE_INVALID_REMOTE_URL, location));
        }

        Matcher actualMatcher = remoteRepoMatcher.matches() ? remoteRepoMatcher : sshRepoMatcher;
        String tempRepoName = actualMatcher.group("repoName");
        String tempOrganization = actualMatcher.group("path").replaceAll("/", "-");

        return new String[] {tempRepoName, tempOrganization};
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
