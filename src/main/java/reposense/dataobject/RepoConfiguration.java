package reposense.dataobject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import reposense.util.FileUtil;


public class RepoConfiguration {
    private String organization;
    private String repoName;
    private String branch;
    private String displayName;
    private Date sinceDate;
    private Date untilDate;

    private transient boolean needCheckStyle = false;
    private transient int commitNum = 1;
    private transient List<String> ignoreDirectoryList = new ArrayList<>();
    private transient List<Author> authorList = new ArrayList<>();
    private transient TreeMap<String, Author> authorAliasMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private transient Map<Author, String> authorDisplayNameMap = new HashMap<>();
    private transient boolean annotationOverwrite = true;

    public RepoConfiguration(String organization, String repoName, String branch) {
        this.organization = organization;
        this.repoName = repoName;
        this.branch = branch;
        this.displayName = organization + "_" + repoName;
    }

    @Override
    public boolean equals(Object other) {

        if (other == this) {
            return true;
        }

        if (!(other instanceof RepoConfiguration)) {
            return false;
        }

        return hashCode() == ((RepoConfiguration) other).hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.organization, this.repoName, this.branch);
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

    public Date getSinceDate() {
        return sinceDate;
    }

    public void setSinceDate(Date sinceDate) {
        this.sinceDate = sinceDate;
    }

    public Date getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(Date untilDate) {
        this.untilDate = untilDate;
    }

    public String getDisplayName() {
        return displayName;
    }
}
