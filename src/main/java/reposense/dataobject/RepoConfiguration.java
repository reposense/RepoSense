package reposense.dataobject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import reposense.util.FileUtil;


public class RepoConfiguration {
    private boolean needCheckStyle = false;
    private int commitNum = 1;
    private String organization;
    private String repoName;
    private String branch;
    private List<String> ignoreDirectoryList = new ArrayList<>();
    private List<Author> authorList = new ArrayList<>();
    private TreeMap<String, Author> authorAliasMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private Map<Author, String> authorDisplayNameMap = new HashMap<>();
    private boolean annotationOverwrite = true;
    private Date fromDate;
    private Date toDate;
    private List<GithubIssue> githubIssues;


    public RepoConfiguration(String organization, String repoName, String branch) {
        this.organization = organization;
        this.repoName = repoName;
        this.branch = branch;
    }

    public Map<Author, String> getAuthorDisplayNameMap() {
        return authorDisplayNameMap;
    }

    public void setAuthorDisplayNameMap(Map<Author, String> authorDisplayNameMap) {
        this.authorDisplayNameMap = authorDisplayNameMap;
    }

    public String getRepoRoot() {
        return FileUtil.getRepoDirectory(organization, repoName);
    }

    public int getCommitNum() {
        return commitNum;
    }

    public void setCommitNum(int commitNum) {
        this.commitNum = commitNum;
    }

    public boolean isNeedCheckStyle() {
        return needCheckStyle;
    }

    public void setNeedCheckStyle(boolean needCheckStyle) {
        this.needCheckStyle = needCheckStyle;
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

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public boolean isAnnotationOverwrite() {
        return annotationOverwrite;
    }

    public void setAnnotationOverwrite(boolean annotationOverwrite) {
        this.annotationOverwrite = annotationOverwrite;
    }

    public List<String> getIgnoreDirectoryList() {
        return ignoreDirectoryList;
    }

    public void setIgnoreDirectoryList(List<String> ignoreDirectoryList) {
        this.ignoreDirectoryList = ignoreDirectoryList;
    }

    public List<Author> getAuthorList() {
        return authorList;
    }

    public void setAuthorList(List<Author> authorList) {
        this.authorList = authorList;
    }

    public TreeMap<String, Author> getAuthorAliasMap() {
        return authorAliasMap;
    }

    public void setAuthorAliasMap(TreeMap<String, Author> authorAliasMap) {
        this.authorAliasMap = authorAliasMap;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public List<GithubIssue> getGithubIssues() {
        return githubIssues;
    }

    public void setGithubIssues(List<GithubIssue> githubIssues) {
        this.githubIssues = githubIssues;
    }
}
