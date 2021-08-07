package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
     * Returns expanded form of the commit hash associated with the {@code shortCommitHash}
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
     * Returns date of commit associated with commit hash.
     */
    public static Date getCommitDate(String root, String commitHash) throws CommitNotFoundException, ParseException {
        Path rootPath = Paths.get(root);
        String showCommand = "git show -s --format=%ci " + commitHash;
        try {
            String output = runCommand(rootPath, showCommand);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
            return format.parse(output);
        } catch (RuntimeException re) {
            throw new CommitNotFoundException("Commit not found: " + commitHash);
        }
    }

    /**
     * Returns date of earliest commit out of the input list of commits.
     */
    public static Date getEarliestCommitDate(String root, List<String> commitHashes) throws CommitNotFoundException {
        Date earliest = null;
        for (String hash : commitHashes) {
            try {
                Date date = getCommitDate(root, hash);
                if (earliest == null || date.before(earliest)) {
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
