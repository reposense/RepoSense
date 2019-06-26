package reposense.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;

import reposense.model.Author;
import reposense.model.Group;
import reposense.model.RepoConfiguration;

/**
 * Contains testing related functionalities.
 */
public class TestUtil {
    private static final String MESSAGE_COMPARING_FILES = "Comparing files %s & %s\n";

    private static final String MESSAGE_LINE_CONTENT_DIFFERENT = "Content different at line number %d:\n"
            + "<< %s\n"
            + ">> %s\n";

    private static final String MESSAGE_LINES_LENGTH_DIFFERENT = "The files' lines count do not match.";

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
    public static Date getDate(int year, int month, int date) {
        return new Calendar
                .Builder()
                .setDate(year, month, date)
                .setTimeOfDay(0, 0, 0)
                .build()
                .getTime();
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
     * Returns true if the {@code expectedNumberCommits} is equal to the expected number of lines in
     * {@code gitLogResult}.
     */
    public static boolean compareNumberExpectedCommitsToGitLogLines(int expectedNumberCommits, String gitLogResult) {
        // if git log result is empty, then there are no commits
        if (gitLogResult.isEmpty()) {
            return expectedNumberCommits == 0;
        }

        // each commit has 2 lines of info, and a blank line in between each
        return expectedNumberCommits * 3 - 1 == gitLogResult.split("\n").length;
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
        String[] changesLogged = gitLogResult.split("\n");
        HashSet<String> filesChanged = new HashSet<>();
        // Checks the 2nd line of each commit which contains info of the changed file
        for (int i = 0; i < changesLogged.length; i += 4) {
            String log = changesLogged[i + 1];
            String fileChanged = log.split("\\| ")[0].trim();
            if (fileChanged.contains("=>")) {
                fileChanged = fileChanged.substring(fileChanged.indexOf("=> ") + 3);
            }
            filesChanged.add(fileChanged);
        }
        return filesChanged.size() == expectedNumberFilesChanged;
    }

    /**
     * Returns true if the test environment is on Windows OS.
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * Converts all the strings in {@code groups} into {@code Group} objects. Returns null if {@code groups} is null.
     * @throws IllegalArgumentException if any of the strings are in invalid formats.
     */
    public static List<Group> convertStringsToGroups(List<String> groups) throws IllegalArgumentException {
        if (groups == null) {
            return null;
        }

        return groups.stream()
                .map(temp -> {
                    String[] elements = temp.split(":");
                    Group obj = new Group(elements[0], Arrays.asList(elements[1].split(";")));
                    return obj;
                }).collect(Collectors.toList());
    }
}
