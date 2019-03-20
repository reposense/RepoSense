package reposense.commits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

    public static List<CommitInfo> extractCommitInfos(RepoConfiguration config) {
        return extractCommitInfos(config, null, null);
    }

    /**
     * Extracts out and returns the raw information of each commit for the repo in {@code config}.
     */
    public static List<CommitInfo> extractCommitInfos(RepoConfiguration config, Date sinceDate, Date untilDate) {
        logger.info("Extracting commits info for " + config.getLocation() + "...");

        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());

        List<CommitInfo> repoCommitInfos = new ArrayList<>();

        for (Author author : config.getAuthorList()) {
            String gitLogResult = GitLog.get(config, author, sinceDate, untilDate);
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
