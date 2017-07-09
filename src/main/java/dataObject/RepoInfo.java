package dataObject;

import java.util.ArrayList;

/**
 * Created by matanghao1 on 8/7/17.
 */
public class RepoInfo {

    private String organization;
    private String repoName;
    private ArrayList<CommitInfo> commits;

    public RepoInfo(String organization, String repoName) {
        this.organization = organization;
        this.repoName = repoName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }



    public ArrayList<CommitInfo> getCommits() {

        return commits;
    }

    public void setCommits(ArrayList<CommitInfo> commits) {
        this.commits = commits;
    }


}
