package reposense.model.reportconfig;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single entry of a branch in the YAML config file.
 */
public class ReportBranchData {
    public static final String DEFAULT_BRANCH = "main";
    public static final String DEFAULT_BLURB = "";
    public static final List<ReportAuthorDetails> DEFAULT_REPORT_AUTHOR_DETAILS = new ArrayList<>();
    public static final List<String> DEFAULT_IGNORE_GLOB_LIST = List.of();
    public static final List<String> DEFAULT_IGNORE_AUTHORS_LIST = List.of();
    public static final Long DEFAULT_FILE_SIZE_LIMIT = 1000000L;

    public static final ReportBranchData DEFAULT_INSTANCE = new ReportBranchData();

    static {
        DEFAULT_INSTANCE.branch = ReportBranchData.DEFAULT_BRANCH;
        DEFAULT_INSTANCE.blurb = DEFAULT_BLURB;
        DEFAULT_INSTANCE.ignoreGlobList = ReportBranchData.DEFAULT_IGNORE_GLOB_LIST;
        DEFAULT_INSTANCE.ignoreAuthorList = ReportBranchData.DEFAULT_IGNORE_AUTHORS_LIST;
        DEFAULT_INSTANCE.reportAuthorDetails = ReportAuthorDetails.DEFAULT_INSTANCES;
        DEFAULT_INSTANCE.fileSizeLimit = DEFAULT_FILE_SIZE_LIMIT;
    }

    @JsonProperty("branch")
    private String branch;

    @JsonProperty("blurb")
    private String blurb;

    @JsonProperty("authors")
    private List<ReportAuthorDetails> reportAuthorDetails;

    @JsonProperty("ignore-glob-list")
    private List<String> ignoreGlobList;

    @JsonProperty("ignore-authors-list")
    private List<String> ignoreAuthorList;

    @JsonProperty("file-size-limit")
    private Long fileSizeLimit;

    public String getBranch() {
        return branch == null ? DEFAULT_BRANCH : branch;
    }

    public String getBlurb() {
        return blurb == null ? DEFAULT_BLURB : blurb;
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

    public Long getFileSizeLimit() {
        return fileSizeLimit == null ? DEFAULT_FILE_SIZE_LIMIT : fileSizeLimit;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof ReportBranchData) {
            ReportBranchData rbd = (ReportBranchData) obj;
            return this.getBranch().equals(rbd.getBranch())
                    && this.getBlurb().equals(rbd.getBlurb())
                    && this.getReportAuthorDetails().equals(rbd.getReportAuthorDetails())
                    && this.getIgnoreGlobList().equals(rbd.getIgnoreGlobList())
                    && this.getIgnoreAuthorList().equals(rbd.getIgnoreAuthorList())
                    && this.getFileSizeLimit().equals(rbd.getFileSizeLimit());
        }

        return false;
    }
}
