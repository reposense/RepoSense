package reposense.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents repo configuration information from CSV config file for a single repository.
 */
public class RepoCsvConfiguration {
    public static final String DEFAULT_BRANCH = "HEAD";

    private RepoLocation location;
    private String branch;
    private transient List<Format> formats;
    private transient List<CommitHash> ignoreCommitList;
    private transient List<String> ignoreGlobList = new ArrayList<>();
    private transient boolean isStandaloneConfigIgnored;

    public RepoCsvConfiguration(RepoLocation location, String branch) {
        this(location, branch, Collections.emptyList(), Collections.emptyList(), false, Collections.emptyList());
    }

    public RepoCsvConfiguration(RepoLocation location, String branch, List<Format> formats,
            List<String> ignoreGlobList, boolean isStandaloneConfigIgnored, List<CommitHash> ignoreCommitList) {
        this.location = location;
        this.branch = location.isEmpty() ? DEFAULT_BRANCH : branch;
        this.formats = formats;
        this.ignoreCommitList = ignoreCommitList;
        this.ignoreGlobList = ignoreGlobList;
        this.isStandaloneConfigIgnored = isStandaloneConfigIgnored;
    }

    public void update(StandaloneConfig standaloneConfig) {
        ignoreGlobList = standaloneConfig.getIgnoreGlobList();
        formats = Format.convertStringsToFormats(standaloneConfig.getFormats());
        ignoreCommitList = CommitHash.convertStringsToCommits(standaloneConfig.getIgnoreCommitList());
    }

    @Override
    public boolean equals(Object other) {

        if (other == this) {
            return true;
        }

        if (!(other instanceof RepoCsvConfiguration)) {
            return false;
        }

        RepoCsvConfiguration otherRepoConfig = (RepoCsvConfiguration) other;

        return location.equals(otherRepoConfig.location)
                && branch.equals(otherRepoConfig.branch)
                && ignoreGlobList.equals(otherRepoConfig.ignoreGlobList)
                && isStandaloneConfigIgnored == otherRepoConfig.isStandaloneConfigIgnored
                && formats.equals(otherRepoConfig.formats);
    }

    public RepoLocation getLocation() {
        return location;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public List<String> getIgnoreGlobList() {
        return ignoreGlobList;
    }

    public void setIgnoreGlobList(List<String> ignoreGlobList) {
        this.ignoreGlobList = ignoreGlobList;
    }

    public List<CommitHash> getIgnoreCommitList() {
        return ignoreCommitList;
    }

    public void setIgnoreCommitList(List<CommitHash> ignoreCommitList) {
        this.ignoreCommitList = ignoreCommitList;
    }

    public List<Format> getFormats() {
        return formats;
    }

    public void setFormats(List<Format> formats) {
        this.formats = formats;
    }

    public boolean isStandaloneConfigIgnored() {
        return isStandaloneConfigIgnored;
    }

    public void setStandaloneConfigIgnored(boolean isStandaloneConfigIgnored) {
        this.isStandaloneConfigIgnored = isStandaloneConfigIgnored;
    }
}
