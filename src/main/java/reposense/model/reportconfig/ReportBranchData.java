package reposense.model.reportconfig;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single entry of a branch in the YAML config file.
 */
public class ReportBranchData {
    public static final String DEFAULT_BRANCH = "main";
    public static final String DEFAULT_BLURB = "very long blurb";
    @JsonProperty("branch")
    private String branch;

    @JsonProperty("blurb")
    private String blurb;

    public String getBranch() {
        return branch == null ? DEFAULT_BRANCH : branch;
    }

    public String getBlurb() {
        return blurb == null ? DEFAULT_BLURB : blurb;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof ReportBranchData) {
            ReportBranchData reportBranchData = (ReportBranchData) obj;
            return reportBranchData.getBranch().equals(this.getBranch())
                    && reportBranchData.getBlurb().equals(this.getBlurb());
        }

        return false;
    }
}
