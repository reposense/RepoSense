package reposense.model.reportconfig;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single entry of a branch in the YAML config file.
 */
public class ReportBranchData {
    public static final String DEFAULT_BRANCH = "main";
    public static final List<String> DEFAULT_FILE_FORMATS = List.of(
            "override:java", "md", "fxml"
    );
    public static final List<String> DEFAULT_IGNORE_GLOB_LIST = List.of(
            "docs**"
    );
    public static final List<String> DEFAULT_IGNORE_COMMITS_LIST = List.of(
            "2fb6b9b2dd9fa40bf0f9815da2cb0ae8731436c7",
            "c5a6dc774e22099cd9ddeb0faff1e75f9cf4f151",
            "cd7f610e0becbdf331d5231887d8010a689f87c7",
            "768015345e70f06add2a8b7d1f901dc07bf70582"
    );
    public static final List<String> DEFAULT_IGNORE_AUTHORS_LIST = List.of(
            "author1",
            "author2"
    );
    public static final boolean DEFAULT_IS_FIND_PREVIOUS_AUTHOR = false;
    public static final boolean DEFAULT_IS_SHALLOW_CLONING = true;
    public static final boolean DEFAULT_IS_IGNORE_STANDALONE_CONFIG = true;

    public static final ReportBranchData DEFAULT_INSTANCE = new ReportBranchData();

    static {
        DEFAULT_INSTANCE.branch = ReportBranchData.DEFAULT_BRANCH;
        DEFAULT_INSTANCE.fileFormats = ReportBranchData.DEFAULT_FILE_FORMATS;
        DEFAULT_INSTANCE.ignoreGlobList = ReportBranchData.DEFAULT_IGNORE_GLOB_LIST;
        DEFAULT_INSTANCE.ignoreCommitList = ReportBranchData.DEFAULT_IGNORE_COMMITS_LIST;
        DEFAULT_INSTANCE.ignoreAuthorList = ReportBranchData.DEFAULT_IGNORE_AUTHORS_LIST;
        DEFAULT_INSTANCE.isFindPreviousAuthor = ReportBranchData.DEFAULT_IS_FIND_PREVIOUS_AUTHOR;
        DEFAULT_INSTANCE.isShallowCloning = ReportBranchData.DEFAULT_IS_SHALLOW_CLONING;
        DEFAULT_INSTANCE.isIgnoreStandaloneConfig = ReportBranchData.DEFAULT_IS_IGNORE_STANDALONE_CONFIG;
    }

    @JsonProperty("branch")
    private String branch;

    @JsonProperty("file-formats")
    private List<String> fileFormats;

    @JsonProperty("ignore-glob-list")
    private List<String> ignoreGlobList;

    @JsonProperty("ignore-standalone-config")
    private Boolean isIgnoreStandaloneConfig;

    @JsonProperty("ignore-commits-list")
    private List<String> ignoreCommitList;

    @JsonProperty("ignore-authors-list")
    private List<String> ignoreAuthorList;

    @JsonProperty("is-shallow-cloning")
    private Boolean isShallowCloning;

    @JsonProperty("is-find-previous-authors")
    private Boolean isFindPreviousAuthor;

    public String getBranch() {
        return branch == null ? DEFAULT_BRANCH : branch;
    }

    public List<String> getFileFormats() {
        return fileFormats == null ? DEFAULT_FILE_FORMATS : fileFormats;
    }

    public List<String> getIgnoreGlobList() {
        return ignoreGlobList == null ? DEFAULT_IGNORE_GLOB_LIST : fileFormats;
    }

    public boolean getIsIgnoreStandaloneConfig() {
        return isIgnoreStandaloneConfig == null ? DEFAULT_IS_IGNORE_STANDALONE_CONFIG : isIgnoreStandaloneConfig;
    }

    public List<String> getIgnoreCommitList() {
        return ignoreCommitList == null ? DEFAULT_IGNORE_COMMITS_LIST : ignoreCommitList;
    }

    public List<String> getIgnoreAuthorList() {
        return ignoreAuthorList == null ? DEFAULT_IGNORE_AUTHORS_LIST : ignoreAuthorList;
    }

    public boolean getIsShallowCloning() {
        return isShallowCloning == null ? DEFAULT_IS_SHALLOW_CLONING : isShallowCloning;
    }

    public boolean getIsFindPreviousAuthor() {
        return isFindPreviousAuthor == null ? DEFAULT_IS_FIND_PREVIOUS_AUTHOR : isFindPreviousAuthor;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof ReportBranchData) {
            ReportBranchData rbd = (ReportBranchData) obj;
            return this.getBranch().equals(rbd.getBranch())
                    && this.getFileFormats().equals(rbd.getFileFormats())
                    && this.getIgnoreGlobList().equals(rbd.getIgnoreGlobList())
                    && this.getIsIgnoreStandaloneConfig() == rbd.getIsIgnoreStandaloneConfig()
                    && this.getIgnoreCommitList().equals(rbd.getIgnoreCommitList())
                    && this.getIgnoreAuthorList().equals(rbd.getIgnoreAuthorList())
                    && this.getIsShallowCloning() == rbd.getIsShallowCloning()
                    && this.getIsFindPreviousAuthor() == rbd.getIsFindPreviousAuthor();
        }

        return false;
    }
}
