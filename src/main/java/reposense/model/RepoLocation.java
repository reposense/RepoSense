package reposense.model;

import static reposense.util.FileUtil.isValidPath;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reposense.git.GitRemote;
import reposense.parser.InvalidLocationException;
import reposense.report.ErrorSummary;
import reposense.util.StringsUtil;
import reposense.util.SystemUtil;

/**
 * Represents a repository location.
 */
public class RepoLocation {
    protected static final String UNSUPPORTED_DOMAIN_NAME = "NOT_RECOGNIZED";

    private static final String MESSAGE_INVALID_LOCATION = "%s is an invalid location.";
    private static final String MESSAGE_INVALID_REMOTE_URL = "%s is an invalid remote URL.";

    private static final String REPO_NAME_REGEX = "(?<repoName>[^/]+?)(/?\\.git)?/?";
    private static final String PATH_TO_REPO_REGEX = "(?<path>.*?)/?" + REPO_NAME_REGEX;
    private static final Pattern GIT_REPOSITORY_LOCATION_PATTERN =
            Pattern.compile("^(ssh|git|https?|ftps?)://(?<domain>[^/]*?)/" + PATH_TO_REPO_REGEX + "$");
    private static final Pattern SCP_LIKE_SSH_REPOSITORY_LOCATION_PATTERN =
            Pattern.compile("^(.*@)?(?<domain>.*?):(?<path>[^/].*?)??/??" + REPO_NAME_REGEX + "$");
    private static final Pattern LOCAL_REPOSITORY_NON_WINDOWS_LOCATION_PATTERN =
            Pattern.compile("^(file://)?" + PATH_TO_REPO_REGEX + "$");
    private static final Pattern LOCAL_REPOSITORY_WINDOWS_LOCATION_PATTERN =
            Pattern.compile("^" + PATH_TO_REPO_REGEX.replaceAll("/", "\\\\\\\\") + "$");
    private static final Pattern DOMAIN_NAME_PATTERN = Pattern.compile("^(ww.\\.)?+(.*@)?(?<domainName>[^.]+)\\..+$");

    private static final String GROUP_REPO_NAME = "repoName";
    private static final String GROUP_PATH = "path";
    private static final String GROUP_DOMAIN_NAME = "domainName";
    private static final String GROUP_DOMAIN = "domain";

    private static final String PATH_SEPARATOR_REPLACEMENT = "-";

    // Used for remote link generation (serialized)
    private final String location;
    private final String repoName;
    private final String organization;
    private final String domainName;

    // Used for generation of local repository report output directory
    private final transient String outputFolderRepoName;
    private final transient String outputFolderOrganization;

    /**
     * Creates {@link RepoLocation} based on the {@code location}, which is represented by a {@code URL}
     * or {@link Path}.
     *
     * @throws InvalidLocationException if {@code location} cannot be represented by a {@code URL} or {@link Path}.
     */
    public RepoLocation(String location) throws InvalidLocationException {
        if (SystemUtil.isWindows()) {
            location = StringsUtil.removeTrailingBackslash(location);
        }

        this.location = location;
        String[] remoteRepoNameAndOrg;
        String[] outputFolderRepoNameAndOrg;
        if (location.isEmpty()) {
            remoteRepoNameAndOrg = new String[] {"", "", UNSUPPORTED_DOMAIN_NAME};
            outputFolderRepoNameAndOrg = remoteRepoNameAndOrg;
        } else if (isLocalRepo(location)) {
            outputFolderRepoNameAndOrg = getLocalRepoNameAndOrg(location);

            Map<String, String> remotes = GitRemote.getRemotes(location);
            String newLocation = GitRemote.getAvailableRemoteLocation(remotes).orElse(location);

            remoteRepoNameAndOrg = remotes.size() == 0
                ? outputFolderRepoNameAndOrg
                : getRemoteRepoNameAndOrg(newLocation);
        } else {
            remoteRepoNameAndOrg = getRemoteRepoNameAndOrg(location);
            outputFolderRepoNameAndOrg = remoteRepoNameAndOrg;
        }

        this.repoName = remoteRepoNameAndOrg[0];
        this.organization = remoteRepoNameAndOrg[1];
        this.domainName = remoteRepoNameAndOrg[2];

        this.outputFolderRepoName = outputFolderRepoNameAndOrg[0];
        this.outputFolderOrganization = outputFolderRepoNameAndOrg[1];
    }

    public boolean isEmpty() {
        return location.isEmpty();
    }

    public String getRepoName() {
        return outputFolderRepoName;
    }

    public String getOrganization() {
        return outputFolderOrganization;
    }

    public String getDomainName() {
        return domainName;
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

        // checks if it is a URL file protocol
        String urlProtocol = repoArgument.split("://", 2)[0];
        if (urlProtocol.equals("file")) {
            return true;
        }

        // catch disk drive arguments such as C:\
        if (SystemUtil.isWindows() && isValidPath(repoArgument)) {
            return true;
        }

        return false;
    }

    /**
     * Returns a best-guess repo name and organization from the given local repo {@code location}.
     * The return is a length-2 string array with the repo name at index 0 and organization at index 1.
     *
     * @throws InvalidLocationException if the location specified is not a proper local repository.
     */
    private String[] getLocalRepoNameAndOrg(String location) throws InvalidLocationException {
        boolean isWindows = SystemUtil.isWindows();
        if (isWindows) {
            location = location.replaceAll("file://", "");
            location = location.replaceAll("/", "\\\\");
            location = location.replaceAll("[|:]", "-");
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

        String tempRepoName = localRepoMatcher.group(GROUP_REPO_NAME);
        String tempOrganization = getOrganizationFromMatcher(localRepoMatcher);
        return new String[] {tempRepoName, tempOrganization, UNSUPPORTED_DOMAIN_NAME};
    }

    /**
     * Returns a best-guess repo name and organization from the given remote repo {@code location}.
     * The return is a length-2 string array with the repo name at index 0 and organization at index 1.
     *
     * @throws InvalidLocationException if the location specified is not a proper remote repository.
     */
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

        // priority for standard URL matches over SSH as SSH matcher will normally also match standard URL
        Matcher actualMatcher = remoteRepoMatcher.matches() ? remoteRepoMatcher : sshRepoMatcher;
        String tempRepoName = actualMatcher.group(GROUP_REPO_NAME);
        String tempOrganization = getOrganizationFromMatcher(actualMatcher);

        return new String[] {tempRepoName, tempOrganization,
                getDomainNameFromDomain(actualMatcher.group(GROUP_DOMAIN))};
    }

    /**
     * Returns the organization string from the {@code matcher} if one exists.
     * If no match was found for it, returns an empty string instead.
     */
    private static String getOrganizationFromMatcher(Matcher matcher) {
        return Optional.ofNullable(matcher.group(GROUP_PATH))
                .map(s -> Paths.get(s).normalize().toString())
                .map(s -> s.replaceAll(Pattern.quote(FileSystems.getDefault().getSeparator()),
                        PATH_SEPARATOR_REPLACEMENT))
                .orElse("");
    }

    /**
     * Returns the domain name of the URL from the {@code matcher} if it is one of the recognised ones.
     * Returns {@code UNRECOGNISED_DOMAIN_NAME} if it is a local repo or not recognised.
     *
     * @throws InvalidLocationException if the domain specified is not a proper domain name.
     */
    public static String getDomainNameFromDomain(String domain) throws InvalidLocationException {
        Matcher domainNameMatcher = DOMAIN_NAME_PATTERN.matcher(domain);
        if (!domainNameMatcher.matches()) {
            throw new InvalidLocationException(MESSAGE_INVALID_REMOTE_URL);
        }
        String domainName = domainNameMatcher.group(GROUP_DOMAIN_NAME);
        return isSupportedDomainName(domainName)
                ? domainName
                : UNSUPPORTED_DOMAIN_NAME;
    }

    private static boolean isSupportedDomainName(String domainName) {
        return SupportedDomainUrlMap.isSupportedDomainName(domainName);
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
        return this.location.equals(otherLocation.location)
                && this.repoName.equals(otherLocation.repoName)
                && this.organization.equals(otherLocation.organization)
                && this.domainName.equals(otherLocation.domainName);
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }
}
