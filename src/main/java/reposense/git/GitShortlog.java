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
 * Executes and extracts information from git-shortlog related commands.
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

    private static String getShortlogSummary(String root, Date sinceDate, Date untilDate) {
        Path rootPath = Paths.get(root);
        String command = "git log --pretty=short";
        command += Util.convertToGitDateRangeArgs(sinceDate, untilDate);
        command += " | git shortlog --summary";

        return runCommand(rootPath, command);
    }
}
