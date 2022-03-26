package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import reposense.git.exception.CommitNotFoundException;
import reposense.model.CommitHash;
import reposense.system.LogsManager;

/**
 * Contains git show related functionalities.
 * Git show shows various types of objects.
 */
public class GitShow {

    private static final Logger logger = LogsManager.getLogger(GitShow.class);

    /**
     * Returns expanded form of the {@link CommitHash} associated with the {@code shortCommitHash}, with
     * the {@link Path} given by {@code root} as the working directory.
     *
     * @throws CommitNotFoundException if there is no commit associated with {@code shortCommitHash}.
     */
    public static CommitHash getExpandedCommitHash(String root, String shortCommitHash) throws CommitNotFoundException {
        Path rootPath = Paths.get(root);
        String showCommand = "git show -s --format=%H " + shortCommitHash;

        try {
            String output = runCommand(rootPath, showCommand);
            List<CommitHash> commitHashes = Arrays.stream(output.split("\n"))
                    .map(CommitHash::new).collect(Collectors.toList());
            if (commitHashes.size() > 1) {
                logger.warning(String.format("%s can be expanded to %d different commits, "
                        + "assuming %s refers to commit hash %s",
                        shortCommitHash, commitHashes.size(), shortCommitHash, commitHashes.get(0)));
            }

            return commitHashes.get(0);
        } catch (RuntimeException re) {
            throw new CommitNotFoundException("Commit not found: " + shortCommitHash);
        }
    }

    /**
     * Returns {@link LocalDateTime} of the commit associated with commit hash, with {@link Path} given by {@code root}
     * as the working directory.
     *
     * @throws CommitNotFoundException if no commit exists for the given {@code commitHash}.
     * @throws ParseException if the date string for the given {@code commitHash} could not be parsed into
     * a {@link LocalDateTime} object.
     */
    public static LocalDateTime getCommitDate(String root, String commitHash) throws CommitNotFoundException,
            ParseException {
        Path rootPath = Paths.get(root);
        String showCommand = "git show -s --format=%ci " + commitHash;
        try {
            String output = runCommand(rootPath, showCommand);
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss Z'\n'");
            return LocalDateTime.parse(output, format);
        } catch (RuntimeException re) {
            throw new CommitNotFoundException("Commit not found: " + commitHash);
        }
    }

    /**
     * Returns {@link LocalDateTime} of the earliest commit out of the input list of commits in {@code commitHashes},
     * with the {@code root} string denoting the working directory.
     *
     * @throws CommitNotFoundException if no commit exists for a given hash in {@code commitHashes}
     * or if no date string was successfully parsed to a {@link LocalDateTime} for earliest date.
     */
    public static LocalDateTime getEarliestCommitDate(String root, List<String> commitHashes)
            throws CommitNotFoundException {
        LocalDateTime earliest = null;
        for (String hash : commitHashes) {
            try {
                LocalDateTime date = getCommitDate(root, hash);
                if (earliest == null || date.compareTo(earliest) < 0) {
                    earliest = date;
                }
            } catch (CommitNotFoundException e) {
                logger.warning("Commit not found: " + hash);
            } catch (ParseException pe) {
                logger.warning("Could not parse date for commit: " + hash);
            }
        }
        if (earliest == null) {
            throw new CommitNotFoundException("Date could not be retrieved for all input commits");
        } else {
            return earliest;
        }
    }
}
