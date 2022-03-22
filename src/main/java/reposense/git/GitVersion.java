package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Contains git version related functionalities.
 * Git version is responsible for finding out the version of git the user of RepoSense is running.
 */
public class GitVersion {

    /** Regex for finding Git Version */
    private static final Pattern GIT_VERSION_PATTERN =
            Pattern.compile("git version (?<versionNumber>\\p{Digit})\\.(?<releaseNumber>\\p{Digit}+).*");
    private static final String VERSION_NUMBER_GROUP = "versionNumber";
    private static final String RELEASE_NUMBER_GROUP = "releaseNumber";

    /**
     * Get current git version of RepoSense user
     */
    public static String getGitVersion() {
        Path rootPath = Paths.get("/");
        String versionCommand = "git --version";

        return runCommand(rootPath, versionCommand);
    }

    /**
     * Returns the version number and release number from a git version {@code commandOutput}.
     * Return type is a length 2 string array with the version number at index 0, release number at index 1.
     */
    private static String[] getVersionNumberAndReleaseNumberFromString(String commandOutput) {
        Matcher matcher = GIT_VERSION_PATTERN.matcher(commandOutput);
        matcher.matches();
        return new String[] {matcher.group(VERSION_NUMBER_GROUP), matcher.group(RELEASE_NUMBER_GROUP)};
    }

    /**
     * Returns true if the {@code commandOutput} version is at least as recent as the {@code versionString} version.
     */
    protected static boolean isGitVersionOutputAtLeastVersion(String commandOutput, String versionString) {
        String[] versionStringDetails = versionString.split("\\.");
        int requiredVersionNumber = Integer.parseInt(versionStringDetails[0]);
        int requiredReleaseNumber = Integer.parseInt(versionStringDetails[1]);
        String[] gitVersionCommandOutputDetails = getVersionNumberAndReleaseNumberFromString(commandOutput);
        int actualVersionNumber = Integer.parseInt(gitVersionCommandOutputDetails[0]);
        int actualReleaseNumber = Integer.parseInt(gitVersionCommandOutputDetails[1]);
        return actualVersionNumber > requiredVersionNumber
                || actualVersionNumber == requiredVersionNumber && actualReleaseNumber >= requiredReleaseNumber;
    }

    /**
     * Returns true if the machine's Git version is at least as recent as {@code versionString}.
     */
    public static boolean isGitVersionAtLeast(String versionString) {
        return isGitVersionOutputAtLeastVersion(getGitVersion(), versionString);
    }
}
