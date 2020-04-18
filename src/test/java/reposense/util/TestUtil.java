package reposense.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;

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
    private static final String TAB_SPLITTER = "\t";
    private static final String MOVED_FILE_INDICATION = "=> ";
    private static final int STAT_FILE_PATH_INDEX = 2;

    /**
     * Returns true if the files' contents are the same.
     * Also prints out error message if the lines count are different,
     * else prints out the first line of content difference (if any).
     */
    public static boolean compareFileContents(Path expected, Path actual) throws IOException {
        return compareFileContents(expected, actual, 1);
    }

    /**
     * Returns true if the files' contents are the same.
     * Also prints out error message if the lines count are different,
     * else prints out maximum {@code maxTraceCounts} lines of content difference (if any).
     */
    public static boolean compareFileContents(Path expected, Path actual, int maxTraceCounts) throws IOException {
        int traceCounts = 0;

        System.out.println(String.format(MESSAGE_COMPARING_FILES, expected, actual));

        String[] expectedContent = new String(Files.readAllBytes(expected))
                .replace("\r", "").split("\n");
        String[] actualContent = new String(Files.readAllBytes(actual))
                .replace("\r", "").split("\n");

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
    public static boolean compareDirectories(Path expected, Path actual) throws IOException {
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
     * Creates and returns a {@code Date} object with the specified {@code year}, {@code month}, {@code day}.
     */
    private static Date getDate(int year, int month, int date, int[] time) {
        return new Calendar
                .Builder()
                .setDate(year, month, date)
                .setTimeOfDay(time[0], time[1], time[2])
                .build()
                .getTime();
    }

    /**
     * Wrapper for {@code getDate} method to get since date with time 00:00:00
     */
    public static Date getSinceDate(int year, int month, int date) {
        return getDate(year, month, date, START_OF_DAY_TIME);
    }

    /**
     * Wrapper for {@code getDate} method to get until date with time 23:59:59
     */
    public static Date getUntilDate(int year, int month, int date) {
        return getDate(year, month, date, END_OF_DAY_TIME);
    }

    /**
     * Compares attributes of {@code expectedRepoConfig} and {@code actualRepoConfig}.
     *
     * @throws AssertionError if any attributes fail equality check.
     */
    public static void compareRepoConfig(RepoConfiguration expectedRepoConfig, RepoConfiguration actualRepoConfig) {
        Assert.assertEquals(expectedRepoConfig, actualRepoConfig);

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
        Assert.assertEquals(expectedAuthor.getGitId(), actualAuthor.getGitId());
        Assert.assertEquals(expectedAuthor.getIgnoreGlobList(), actualAuthor.getIgnoreGlobList());
        Assert.assertEquals(expectedAuthor.getAuthorAliases(), actualAuthor.getAuthorAliases());
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
     * Returns the {@code set} of files changed in the commit {@code rawCommitInfo}.
     */
    private static Set<String> getFilesChangedInCommit(String rawCommitInfo) {
        Set<String> filesChanged = new HashSet<>();
        String[] commitInfo = rawCommitInfo.replaceAll("\n+$", "").split("\n");
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
        String fileChanged = rawFileChangedString.split(TAB_SPLITTER)[STAT_FILE_PATH_INDEX].trim();
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

}
