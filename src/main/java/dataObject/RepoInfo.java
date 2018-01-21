package dataObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matanghao1 on 8/7/17.
 */
public class RepoInfo {

    private String organization;
    private String repoName;
    private List<CommitInfo> commits;
    private String branch;
    private Map<Author,String> authorDisplayNameMap = new HashMap<>();

    public RepoInfo(String organization, String repoName, String branch, Map<Author, String> authorDisplayNameMap) {
        this.organization = organization;
        this.repoName = repoName;
        this.branch = branch;
        this.authorDisplayNameMap = authorDisplayNameMap;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
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

    public List<CommitInfo> getCommits() {

        return commits;
    }

    public Map<Author, String> getAuthorDisplayNameMap() {
        return authorDisplayNameMap;
    }

    public void setAuthorDisplayNameMap(Map<Author, String> authorDisplayNameMap) {
        this.authorDisplayNameMap = authorDisplayNameMap;
    }

    public void setCommits(List<CommitInfo> commits) {
        this.commits = commits;
    }

    public String getDirectoryName(){
        return organization + "_" + repoName;
    }
}
