package reposense.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;

import reposense.git.GitLog;
import reposense.model.Author;
import reposense.model.RepoConfiguration;

/**
 * Contains testing related functionalities.
 */
public class TestUtil {
    private static final int[] END_OF_DAY_TIME = {23, 59, 59};
    private static final int[] START_OF_DAY_TIME = {0, 0, 0};
    private static final String MESSAGE_COMPARING_FILES = "Comparing files %s & %s\n";

    private static final String MESSAGE_LINE_CONTENT_DIFFERENT = "Content different at line number %d:\n"
            + "<< %s\n"
            + ">> %s\n";

    private static final String MESSAGE_LINES_LENGTH_DIFFERENT = "The files' lines count do not match.";
    private static final String MOVED_FILE_INDICATION = "=> ";
    private static final int STAT_FILE_PATH_INDEX = 2;

    /**
     * Returns true if the contents of the files at {@code expected} and {@code actual} are the same.
     * Also prints out error message if the lines count are different,
     * else prints out the first line of content difference (if any).
     */
    public static boolean compareFileContents(Path expected, Path actual) throws Exception {
        return compareFileContents(expected, actual, 1);
    }

    /**
     * Returns true if the contents of the files at {@code expected} and {@code actual} are the same.
     * Also prints out error message if the lines count are different,
     * else prints out maximum {@code maxTraceCounts} lines of content difference (if any).
     */
    public static boolean compareFileContents(Path expected, Path actual, int maxTraceCounts) throws Exception {
        int traceCounts = 0;

        System.out.println(String.format(MESSAGE_COMPARING_FILES, expected, actual));

        String[] expectedContent = StringsUtil.NEWLINE.split(new String(Files.readAllBytes(expected))
                .replace("\r", ""));
        String[] actualContent = StringsUtil.NEWLINE.split(new String(Files.readAllBytes(actual))
                .replace("\r", ""));

        for (int i = 0; i < Math.min(expectedContent.length, actualContent.length); i++) {
            if (!expectedContent[i].equals(actualContent[i])) {
                System.out.println(
                        String.format(MESSAGE_LINE_CONTENT_DIFFERENT, i + 1, expectedContent[i], actualContent[i]));
                if (++traceCounts >= maxTraceCounts) {
                    break;
                }
            }
        }
        if (expectedContent.length != actualContent.length) {
            System.out.println(MESSAGE_LINES_LENGTH_DIFFERENT);
            return false;
        } else if (traceCounts >= maxTraceCounts) {
            return false;
        }
        return true;
    }

    /**
     * Returns true if {@code expected} directory has all files with same content as {@code actual} directory.
     */
    public static boolean compareDirectories(Path expected, Path actual) throws Exception {
        List<Path> expectedPaths = Files.walk(expected)
                .sorted()
                .collect(Collectors.toList());
        List<Path> actualPaths = Files.walk(actual)
                .sorted()
                .collect(Collectors.toList());

        if (expectedPaths.size() != actualPaths.size()) {
            return false;
        }

        for (int i = 0; i < expectedPaths.size(); i++) {
            if (!(Files.isDirectory(expectedPaths.get(i)) || Files.isDirectory(actualPaths.get(i)))
                    && !TestUtil.compareFileContents(expectedPaths.get(i), actualPaths.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates and returns a {@link LocalDateTime} object with the specified {@code year}, {@code month}, {@code day}
     * and {@code time}.
     */
    public static LocalDateTime getDate(int year, int month, int date, int[] time) {
        return LocalDateTime.of(year, month, date, time[0], time[1], time[2], 0);
    }

    /**
     * Wrapper for {@code getDate} method to get since date with time 00:00:00
     * from the parameters {@code year}, {@code month}, {@code date}.
     */
    public static LocalDateTime getSinceDate(int year, int month, int date) {
        return getDate(year, month, date, START_OF_DAY_TIME);
    }

    /**
     * Wrapper for {@code getDate} method to get until date with time 23:59:59
     * from the parameters {@code year}, {@code month}, {@code date}.
     */
    public static LocalDateTime getUntilDate(int year, int month, int date) {
        return getDate(year, month, date, END_OF_DAY_TIME);
    }

    /**
     * Returns a {@link ZoneId} object for the specified {@code timezone}.
     */
    public static ZoneId getZoneId(String timezone) {
        return ZoneId.of(timezone);
    }

    /**
     * Compares attributes of {@code expectedRepoConfig} and {@code actualRepoConfig}.
     *
     * @throws AssertionError if any attributes fail equality check.
     */
    public static void compareRepoConfig(RepoConfiguration expectedRepoConfig, RepoConfiguration actualRepoConfig) {
        Assertions.assertEquals(expectedRepoConfig, actualRepoConfig);

        for (int i = 0; i < expectedRepoConfig.getAuthorList().size(); i++) {
            compareAuthor(expectedRepoConfig.getAuthorList().get(i), actualRepoConfig.getAuthorList().get(i));
        }
    }

    /**
     * Compares attributes of {@code expectedAuthor} and {@code actualAuthor}, with exception of it's display name.
     *
     * The display name is not compared as it varies with object construction.
     * It is a transient value and it is not needed for object matching.
     *
     * @throws AssertionError if any attributes fail equality check.
     */
    public static void compareAuthor(Author expectedAuthor, Author actualAuthor) {
        Assertions.assertEquals(expectedAuthor.getGitId(), actualAuthor.getGitId());
        Assertions.assertEquals(expectedAuthor.getIgnoreGlobList(), actualAuthor.getIgnoreGlobList());
        Assertions.assertEquals(expectedAuthor.getAuthorAliases(), actualAuthor.getAuthorAliases());
    }

    /**
     * Returns true if the {@code expectedNumberCommits} is equal to the expected number of commits captured in
     * {@code gitLogResult}.
     */
    public static boolean compareNumberExpectedCommitsToGitLogLines(int expectedNumberCommits, String gitLogResult) {
        // if git log result is empty, then there are no commits
        if (gitLogResult.isEmpty()) {
            return expectedNumberCommits == 0;
        }

        // (actualSplitGitLogResilt - 1) as the 1st token is always empty.
        return expectedNumberCommits == (gitLogResult.split(GitLog.COMMIT_INFO_DELIMITER).length - 1);
    }

    /**
     * Returns true if the {@code expectedNumberFilesChanged} is equal to the actual number of files changed in
     * {@code gitLogResult}.
     */
    public static boolean compareNumberFilesChanged(int expectedNumberFilesChanged, String gitLogResult) {
        // if git log result is empty, then there are no files changed
        if (gitLogResult.isEmpty()) {
            return expectedNumberFilesChanged == 0;
        }
        String[] changesLogged = gitLogResult.split(GitLog.COMMIT_INFO_DELIMITER);
        HashSet<String> filesChanged = new HashSet<>();

        // start from index 1 as index 0 is always empty.
        for (int i = 1; i < changesLogged.length; i++) {
            filesChanged.addAll(getFilesChangedInCommit(changesLogged[i]));
        }
        return filesChanged.size() == expectedNumberFilesChanged;
    }

    /**
     * Returns the {@link Set} of files changed in the commit {@code rawCommitInfo}.
     */
    private static Set<String> getFilesChangedInCommit(String rawCommitInfo) {
        Set<String> filesChanged = new HashSet<>();
        String[] commitInfo = StringsUtil.NEWLINE.split(rawCommitInfo.replaceAll("\n+$", ""));
        int fileChangedNum = Integer.parseInt(commitInfo[commitInfo.length - 1].trim().split(" ")[0]);
        for (int fileNum = 0; fileNum < fileChangedNum; fileNum++) {
            filesChanged.add(getFileChanged(commitInfo[commitInfo.length - 2 - fileNum]));
        }
        return filesChanged;
    }

    /**
     * Returns the file changed given a {@code rawFileChangedString}.
     */
    private static String getFileChanged(String rawFileChangedString) {
        String fileChanged = StringsUtil.TAB.split(rawFileChangedString)[STAT_FILE_PATH_INDEX].trim();
        if (fileChanged.contains(MOVED_FILE_INDICATION)) {
            fileChanged = fileChanged.substring(fileChanged.indexOf(MOVED_FILE_INDICATION)
                    + MOVED_FILE_INDICATION.length());
        }
        return fileChanged;
    }

    /**
     * Returns true if the test environment is on Windows OS.
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * Returns the {@link Path} to a resource given by {@code pathToResource} string, using {@code classForLoading}.
     */
    public static Path loadResource(Class<?> classForLoading, String pathToResource) {
        ClassLoader classLoader = classForLoading.getClassLoader();
        URL url = classLoader.getResource(pathToResource);
        Path path = null;
        try {
            path = Paths.get(url.toURI());
        } catch (URISyntaxException e) {
            System.out.println("URL format does not follow required standard");
        }
        return path;
    }
}
