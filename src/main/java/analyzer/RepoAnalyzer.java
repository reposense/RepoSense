package analyzer;

import dataObject.Author;
import dataObject.CommitInfo;
import dataObject.Configuration;
import dataObject.RepoInfo;
import git.GitChecker;
import git.GitLogger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by matanghao1 on 21/6/17.
 */
public class RepoAnalyzer {


    public static void analyzeCommits(Configuration config, RepoInfo repo){
        List<CommitInfo> commits = GitLogger.getCommits(config.getRepoRoot(), config);
        processCommits(config, commits);
        formatAuthorContributionMaps(commits);
        repo.setCommits(commits);
    }

    private static void formatAuthorContributionMaps(List<CommitInfo> commits) {
        HashSet<Author> authors = new HashSet<>();
        for (CommitInfo commit : commits) {
            for (Author author : commit.getAuthorIssueMap().keySet()) {
                if (!authors.contains(author)) {
                    authors.add(author);
                }
            }
            for (Author author : commit.getAuthorContributionMap().keySet()) {
                if (!authors.contains(author)) {
                    authors.add(author);
                }
            }
        }
        for (CommitInfo commit : commits) {
            for (Author author: authors) {
                if (!commit.getAuthorContributionMap().containsKey(author)){
                    commit.getAuthorContributionMap().put(author,0);
                }
                if (!commit.getAuthorIssueMap().containsKey(author)){
                    commit.getAuthorIssueMap().put(author,0);
                }
            }
        }
    }

    private static void processCommits(Configuration config, List<CommitInfo> commits){
        for (int i=0;i<commits.size();i++){
            CommitInfo commit = commits.get(i);
            GitChecker.checkOutToCommit(config.getRepoRoot(),commit);
            CommitAnalyzer.aggregateFileInfos(config,commit);
            if (i != (commits.size()-1)) {
                commit.minify();
            }

        }
        GitChecker.checkOutToRecentBranch(config.getRepoRoot());
    }
}
