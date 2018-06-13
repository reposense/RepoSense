package reposense.analyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import reposense.dataobject.CommitInfo;
import reposense.dataobject.RepoConfiguration;
import reposense.git.GitChecker;
import reposense.system.CommandRunner;
import reposense.system.LogsManager;

/**
 * Extracts out the raw information generated from git log for each commit.
 */
public class CommitInfoExtractor {
    private static final Logger logger = LogsManager.getLogger(CommitInfoExtractor.class);

    public static List<CommitInfo> extractCommitInfos(RepoConfiguration config) {
        logger.info("Extracting commits for " + config.getOrganization() + "/" + config.getRepoName() + "...");

        GitChecker.checkoutBranch(config.getRepoRoot(), config.getBranch());
        String gitLogResults = CommandRunner.gitLog(config.getRepoRoot(), config.getSinceDate(), config.getUntilDate());
        return parseGitLogResults(gitLogResults);
    }

    private static ArrayList<CommitInfo> parseGitLogResults(String rawResult) {
        ArrayList<CommitInfo> commitInfos = new ArrayList<>();
        String[] rawLines = rawResult.split("\n");

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
