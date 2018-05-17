package reposense.dataobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RepoInfo {

    private String organization;
    private String repoName;
    private String branch;
    private Map<Author, String> authorDisplayNameMap = new HashMap<>();
    private HashMap<Author, Integer> authorIssueMap;
    private HashMap<Author, Integer> authorContributionMap;

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

    public Map<Author, String> getAuthorDisplayNameMap() {
        return authorDisplayNameMap;
    }

    public void setAuthorDisplayNameMap(Map<Author, String> authorDisplayNameMap) {
        this.authorDisplayNameMap = authorDisplayNameMap;
    }

    public String getDirectoryName() {
        return organization + "_" + repoName;
    }

    public HashMap<Author, Integer> getAuthorIssueMap() {
        return authorIssueMap;
    }

    public void setAuthorIssueMap(HashMap<Author, Integer> authorIssueMap) {
        this.authorIssueMap = authorIssueMap;
    }

    public HashMap<Author, Integer> getAuthorContributionMap() {
        return authorContributionMap;
    }

    public void setAuthorContributionMap(HashMap<Author, Integer> authorContributionMap) {
        this.authorContributionMap = authorContributionMap;
    }
}
