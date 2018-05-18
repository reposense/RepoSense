package reposense.gitmanagement;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import reposense.dataobject.GithubIssue;
import reposense.dataobject.GithubIssueIndex;
import reposense.dataobject.GithubIssueLabels;
import reposense.dataobject.GithubIssueState;
import reposense.dataobject.GithubIssueTitle;

/**
 * Manages github issues for a given repository
 */
public class GitIssueManager {

    private static List<GithubIssue> issueList;
    private static List<GHIssue> GHIssueList;
    private static GHRepository repository;
    private static String repoAddress;

    /**
     * Creates a list of github issues for a given repository
     * @param organization
     * @param repoName
     * @return a list of github issues for a given repository
     * @throws IOException
     */
    public static List<GithubIssue> fetchGithubIssues(GitHub github, String organization, String repoName)
            throws IOException  {

        repoAddress = organization + "/" + repoName;
        repository = github.getRepository(repoAddress);
        GHIssueList = repository.getIssues(GHIssueState.OPEN);
        issueList = new ArrayList<>();

        for (GHIssue issue: GHIssueList) {
            if (issue != null) {
                issueList.add(convertIssue(issue));
            } else { break; };
        }

        return issueList;
    }

    /**
     * Converts a GHIssue object to GithubIssue object
     * @param issue
     * @return a converted GithubIssue object
     * @throws IOException
     */
    private static GithubIssue convertIssue(GHIssue issue) throws IOException {

        String title;
        String state;
        Collection<GHLabel> labels;
        List<GithubIssueLabels> githubLabels = new ArrayList<>();
        int index;

        title = issue.getTitle();
        labels = issue.getLabels();
        state = issue.getState().toString();
        index = issue.getNumber();

        for (GHLabel label: labels) {
            githubLabels.add(new GithubIssueLabels(label.toString()));
        }

        return new GithubIssue(new GithubIssueTitle(title), githubLabels,
                new GithubIssueIndex(index), new GithubIssueState(state));
    }
}
