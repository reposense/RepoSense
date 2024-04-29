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
import reposense.util.StringsUtil;

/**
 * Contains git shortlog related functionalities.
 * Git shortlog provides a summary of git log output.
 */
public class GitShortlog {

    /**
     * Extracts all the author identities from the repository and date range given in {@code config},
     * with the timezone taken into account.
     */
    public static List<Author> getAuthors(RepoConfiguration config) {
        String summary = getShortlogSummary(
                config.getRepoRoot(), config.getSinceDate(), config.getUntilDate(), config.getZoneId());

        if (summary.isEmpty()) {
            return Collections.emptyList();
        }

        String[] lines = StringsUtil.NEWLINE.split(summary);
        return Arrays.stream(lines)
                .map(line -> new Author(StringsUtil.TAB.split(line)[1]))
                .collect(Collectors.toList());
    }

    /**
     * Obtains summarized version of git log from the repository at {@code root} for the date range
     * given by {@code sinceDate} and {@code untilDate}, with {@code zoneId} taken into account for both dates.
     */
    private static String getShortlogSummary(String root, LocalDateTime sinceDate, LocalDateTime untilDate,
            ZoneId zoneId) {
        Path rootPath = Paths.get(root);
        String command = "git log --pretty=short";
        command += GitUtil.convertToGitDateRangeArgs(sinceDate, untilDate, zoneId);
        command += " | git shortlog --summary";

        return runCommand(rootPath, command);
    }
}
