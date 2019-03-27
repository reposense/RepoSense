package reposense.commits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import reposense.commits.model.CommitInfo;
import reposense.git.GitCheckout;
import reposense.git.GitLog;
import reposense.model.Author;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;

/**
 * Extracts commit information of a repository.
 */
public class CommitInfoExtractor {
    private static final Logger logger = LogsManager.getLogger(CommitInfoExtractor.class);
    private static final String MESSAGE_START_EXTRACTING_COMMIT_INFO = "Extracting commits info for %s (%s)...";

    /**
     * Extracts out and returns the raw information of each commit for the repo in {@code config}.
     */
    public static List<CommitInfo> extractCommitInfos(RepoConfiguration config) {
        logger.info(String.format(MESSAGE_START_EXTRACTING_COMMIT_INFO, config.getLocation(), config.getBranch()));

        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());

        List<CommitInfo> repoCommitInfos = new ArrayList<>();

        for (Author author : config.getAuthorList()) {
            String gitLogResult = GitLog.get(config, author);
            List<CommitInfo> authorCommitInfos = parseGitLogResults(gitLogResult);
            repoCommitInfos.addAll(authorCommitInfos);
        }

        return repoCommitInfos;
    }

    /**
     * Parses the {@code gitLogResult} into a list of {@code CommitInfo} and returns it.
     */
    private static ArrayList<CommitInfo> parseGitLogResults(String gitLogResult) {
        ArrayList<CommitInfo> commitInfos = new ArrayList<>();
        String[] rawLines = gitLogResult.split("\n");

        if (rawLines.length < 2) {
            //no log (maybe because no contribution for that file type)
            return commitInfos;
        }

        for (int i = 0; i < rawLines.length; i++) {
            commitInfos.add(new CommitInfo(rawLines[i], rawLines[++i]));
            i++; //to skip the empty line
        }

        Collections.reverse(commitInfos);
        return commitInfos;
    }
}
