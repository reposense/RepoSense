package dataObject;

import util.FileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matanghao1 on 10/7/17.
 */
public class Configuration {
    private boolean needCheckStyle = false;
    private int commitNum = 1;
    private String organization;
    private String repoName;
    private String branch;
    private List<String> ignoreDirectoryList = new ArrayList<>();
    private List<Author> ignoreAuthorList = new ArrayList<>();
    private boolean annotationOverwrite = false;


    public Configuration(String organization, String repoName, String branch) {
        this.organization = organization;
        this.repoName = repoName;
        this.branch = branch;
    }

    public String getRepoRoot(){
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

    public List<Author> getIgnoreAuthorList() {
        return ignoreAuthorList;
    }

    public void setIgnoreAuthorList(List<Author> ignoreAuthorList) {
        this.ignoreAuthorList = ignoreAuthorList;
    }

    public List<String> getIgnoreDirectoryList() {
        return ignoreDirectoryList;
    }

    public void setIgnoreDirectoryList(List<String> ignoreDirectoryList) {
        this.ignoreDirectoryList = ignoreDirectoryList;
    }
}
