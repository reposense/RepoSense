package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
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
                config.getRepoRoot(), config.getSinceDate(), config.getUntilDate(), ZoneId.of(config.getZoneId()));

        if (summary.isEmpty()) {
            return Collections.emptyList();
        }

        String[] lines = summary.split("\n");
        return Arrays.stream(lines)
                .map(line -> new Author(line.split("\t")[1]))
                .collect(Collectors.toList());
    }

    private static String getShortlogSummary(String root, LocalDateTime sinceDate,
                                             LocalDateTime untilDate, ZoneId zoneId) {
        Path rootPath = Paths.get(root);
        String command = "git log --pretty=short";
        command += GitUtil.convertToGitDateRangeArgs(sinceDate, untilDate, zoneId);
        command += " | git shortlog --summary";

        return runCommand(rootPath, command);
    }
}
