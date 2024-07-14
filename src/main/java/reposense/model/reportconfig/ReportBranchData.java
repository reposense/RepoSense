package reposense.model.reportconfig;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single entry of a branch in the YAML config file.
 */
public class ReportBranchData {
    public static final String DEFAULT_BRANCH = "main";
    public static final List<ReportAuthorDetails> DEFAULT_REPORT_AUTHOR_DETAILS = new ArrayList<>(
            ReportAuthorDetails.DEFAULT_INSTANCES
    );
    public static final List<String> DEFAULT_IGNORE_GLOB_LIST = List.of(
            "docs**"
    );
    public static final List<String> DEFAULT_IGNORE_AUTHORS_LIST = List.of(
            "author1",
            "author2"
    );

    public static final ReportBranchData DEFAULT_INSTANCE = new ReportBranchData();

    static {
        DEFAULT_INSTANCE.branch = ReportBranchData.DEFAULT_BRANCH;
        DEFAULT_INSTANCE.ignoreGlobList = ReportBranchData.DEFAULT_IGNORE_GLOB_LIST;
        DEFAULT_INSTANCE.ignoreAuthorList = ReportBranchData.DEFAULT_IGNORE_AUTHORS_LIST;
        DEFAULT_INSTANCE.reportAuthorDetails = ReportAuthorDetails.DEFAULT_INSTANCES;
    }

    @JsonProperty("branch")
    private String branch;

    @JsonProperty("authors")
    private List<ReportAuthorDetails> reportAuthorDetails;

    @JsonProperty("ignore-glob-list")
    private List<String> ignoreGlobList;

    @JsonProperty("ignore-authors-list")
    private List<String> ignoreAuthorList;

    public String getBranch() {
        return branch == null ? DEFAULT_BRANCH : branch;
    }

    public List<ReportAuthorDetails> getReportAuthorDetails() {
        return reportAuthorDetails == null ? DEFAULT_REPORT_AUTHOR_DETAILS : reportAuthorDetails;
    }

    public List<String> getIgnoreGlobList() {
        return ignoreGlobList == null ? DEFAULT_IGNORE_GLOB_LIST : ignoreGlobList;
    }

    public List<String> getIgnoreAuthorList() {
        return ignoreAuthorList == null ? DEFAULT_IGNORE_AUTHORS_LIST : ignoreAuthorList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof ReportBranchData) {
            ReportBranchData rbd = (ReportBranchData) obj;
            return this.getBranch().equals(rbd.getBranch())
                    && this.getReportAuthorDetails().equals(rbd.getReportAuthorDetails())
                    && this.getIgnoreGlobList().equals(rbd.getIgnoreGlobList())
                    && this.getIgnoreAuthorList().equals(rbd.getIgnoreAuthorList());
        }

        return false;
    }
}
