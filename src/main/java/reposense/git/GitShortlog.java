package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import reposense.model.Author;
import reposense.model.RepoConfiguration;

/**
 * Contains git shortlog related functionalities.
 * Git shortlog provides a summary of git log output.
 */
public class GitShortlog {

    /**
     * Extracts all the author identities from the repository and date range given in {@code config}.
     */
    public static List<Author> getAuthors(RepoConfiguration config) {
        String summary = getShortlogSummary(
                config.getRepoRoot(), config.getSinceDate(), config.getUntilDate());

        if (summary.isEmpty()) {
            return Collections.emptyList();
        }

        String[] lines = summary.split("\n");
        return Arrays.stream(lines)
                .map(line -> new Author(line.split("\t")[1]))
                .collect(Collectors.toList());
    }

    /**
     * Obtains summarised version of git log command for the date range given by {@code sinceDate}
     * and {@code untiLDate}.
     */
    private static String getShortlogSummary(String root, Date sinceDate, Date untilDate) {
        Path rootPath = Paths.get(root);
        String command = "git log --pretty=short";
        command += GitUtil.convertToGitDateRangeArgs(sinceDate, untilDate);
        command += " | git shortlog --summary";

        return runCommand(rootPath, command);
    }
}
