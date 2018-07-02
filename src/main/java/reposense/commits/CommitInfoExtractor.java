package reposense.commits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import reposense.commits.model.CommitInfo;
import reposense.git.GitChecker;
import reposense.model.RepoConfiguration;
import reposense.system.CommandRunner;
import reposense.system.LogsManager;

/**
 * Extracts commit information of a repository.
 */
public class CommitInfoExtractor {
    private static final Logger logger = LogsManager.getLogger(CommitInfoExtractor.class);

    /**
     * Extracts out and returns the raw information of each commit for the repo in {@code config}.
     */
    public static List<CommitInfo> extractCommitInfos(RepoConfiguration config) {
        logger.info("Extracting commits info for " + config.getOrganization() + "/" + config.getRepoName() + "...");

        GitChecker.checkoutBranch(config.getRepoRoot(), config.getBranch());
        String gitLogResult = getGitLogResult(config);
        return parseGitLogResults(gitLogResult);
    }

    /**
     * Returns the git log information for the repo for the date range in {@code config}.
     */
    private static String getGitLogResult(RepoConfiguration config) {
        return CommandRunner.gitLog(config.getRepoRoot(), config.getSinceDate(), config.getUntilDate());
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
