package reposense.commits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private static final String INFO_STAT_SEPARATOR = "|";

    private static final Pattern TRAILING_NEWLINES_PATTERN = Pattern.compile("\n+$");

    /**
     * Extracts out and returns the raw information of each commit for the repo in {@code config}.
     */
    public List<CommitInfo> extractCommitInfos(RepoConfiguration config) {
        logger.info(String.format(MESSAGE_START_EXTRACTING_COMMIT_INFO, config.getLocation(), config.getBranch()));

        GitCheckout.checkoutBranch(config.getRepoRoot(), config.getBranch());

        List<CommitInfo> repoCommitInfos = new ArrayList<>();

        for (Author author : config.getAuthorList()) {
            String gitLogResult = GitLog.getWithFiles(config, author);
            List<CommitInfo> authorCommitInfos = parseGitLogResults(gitLogResult);
            repoCommitInfos.addAll(authorCommitInfos);
        }

        return repoCommitInfos;
    }

    /**
     * Parses the {@code gitLogResult} into a list of {@link CommitInfo} and returns it.
     */
    private ArrayList<CommitInfo> parseGitLogResults(String gitLogResult) {
        ArrayList<CommitInfo> commitInfos = new ArrayList<>();
        String[] rawCommitInfos = gitLogResult.split(GitLog.COMMIT_INFO_DELIMITER);

        if (rawCommitInfos.length < 2) {
            //no log (maybe because no contribution for that file type)
            return commitInfos;
        }

        // Starts from 1 as index 0 is always empty.
        for (int i = 1; i < rawCommitInfos.length; i++) {
            Matcher matcher = TRAILING_NEWLINES_PATTERN.matcher(rawCommitInfos[i]);
            String rawCommitInfo = matcher.replaceAll("");

            int statLineSeparatorIndex = rawCommitInfo.lastIndexOf(INFO_STAT_SEPARATOR);
            String infoLine = rawCommitInfo.substring(0, statLineSeparatorIndex);
            String statLine = rawCommitInfo.substring(statLineSeparatorIndex + 1).trim();
            commitInfos.add(new CommitInfo(infoLine, statLine));
        }

        Collections.reverse(commitInfos);
        return commitInfos;
    }
}
