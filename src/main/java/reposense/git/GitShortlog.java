package reposense.git;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import reposense.model.Author;
import reposense.model.RepoConfiguration;
import reposense.system.CommandRunner;

/**
 * Executes and extracts information from git-shortlog related commands.
 */
public class GitShortlog {

    /**
     * Extracts all the author identities from the repository given in {@code config}.
     */
    public static List<Author> extractAuthorsFromLog(RepoConfiguration config) {
        String summary = CommandRunner.getShortlogSummary(config);
        String[] lines = summary.split("\n");
        return Arrays.stream(lines)
                .map(line -> new Author(line.split("\t")[1]))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
