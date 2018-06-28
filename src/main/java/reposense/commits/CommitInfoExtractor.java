package reposense.commits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
    public static HashMap<String, List<CommitInfo>> extractCommitInfos(RepoConfiguration config) {
        logger.info("Extracting commits info for " + config.getOrganization() + "/" + config.getRepoName() + "...");

        GitChecker.checkoutBranch(config.getRepoRoot(), config.getBranch());
        HashMap<String, String> gitLogResult = getGitLogResult(config);
        return parseGitLogResultsForAllDocTypes(gitLogResult);
    }

    /**
     * Returns the git log information for the repo for the date range in {@code config}.
     */
    private static HashMap<String, String> getGitLogResult(RepoConfiguration config) {
        String[] docTypes = {"*.java", "*.adoc"};
        HashMap<String, String> results = new HashMap<>();
        for (String doctype: docTypes){
            String result = CommandRunner.gitLog(config.getRepoRoot(), config.getSinceDate(), config.getUntilDate(), doctype);
            results.put(doctype, result);
        }
        return results;
    }

    private static HashMap<String, List<CommitInfo>> parseGitLogResultsForAllDocTypes(
            HashMap<String, String> logresults) {
        HashMap<String, List<CommitInfo>> gitLogResultForEachDocType = new HashMap<>();
        for(String doctype: logresults.keySet()) {
            gitLogResultForEachDocType.put(doctype, parseGitLogResults(logresults.get(doctype)));
        }
        return gitLogResultForEachDocType;
    }

    /**
     * Parses the {@code gitLogResult} into a list of {@code CommitInfo} and returns it.
     */
    private static List<CommitInfo> parseGitLogResults(String gitLogResult) {
        List<CommitInfo> commitInfos = new ArrayList<>();
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
