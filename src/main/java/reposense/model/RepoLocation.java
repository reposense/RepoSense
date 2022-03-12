package reposense.model;

import static reposense.util.FileUtil.isValidPath;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.util.Optional;
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

    protected static final String UNSUPPORTED_DOMAIN_NAME = "NOT_RECOGNISED";

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
    private static final Pattern DOMAIN_NAME_PATTERN = Pattern.compile("^(ww.\\.)?(.*@)?(?<domainName>[^.]*).*$");

    private static final String GROUP_REPO_NAME = "repoName";
    private static final String GROUP_PATH = "path";
    private static final String GROUP_DOMAIN_NAME = "domainName";
    private static final String GROUP_DOMAIN = "domain";

    private static final String PATH_SEPARATOR_REPLACEMENT = "-";

    private final String location;
    private final String repoName;
    private final String organization;
    private final String domainName;

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
            repoNameAndOrg = new String[] {"", "", UNSUPPORTED_DOMAIN_NAME};
        } else if (isLocalRepo(location)) {
            repoNameAndOrg = getLocalRepoNameAndOrg(location);
        } else {
            repoNameAndOrg = getRemoteRepoNameAndOrg(location);
        }

        this.repoName = repoNameAndOrg[0];
        this.organization = repoNameAndOrg[1];
        this.domainName = repoNameAndOrg[2];
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

        return new String[] {tempRepoName, tempOrganization, getDomainNameFromMatcher(actualMatcher)};
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
     */
    private static String getDomainNameFromMatcher(Matcher matcher) throws InvalidLocationException {
        String domain = matcher.group(GROUP_DOMAIN);
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
        return this.location.equals(otherLocation.location);
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }
}
