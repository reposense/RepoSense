package reposense.model.reportconfig;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains information about the group contained in a particular repo.
 */
public class ReportGroupDetails {
    public static final String DEFAULT_REPO = "https://github.com/user/repo/tree/fake-branch";
    public static final List<ReportGroupNameAndGlobs> DEFAULT_NAMES_AND_GLOBS =
            ReportGroupNameAndGlobs.DEFAULT_INSTANCES;
    public static final ReportGroupDetails DEFAULT_INSTANCE = new ReportGroupDetails();

    static {
        DEFAULT_INSTANCE.repo = DEFAULT_REPO;
        DEFAULT_INSTANCE.reportGroupNameAndGlobsList = ReportGroupNameAndGlobs.DEFAULT_INSTANCES;
    }

    @JsonProperty("repo")
    private String repo;

    @JsonProperty("groups")
    private List<ReportGroupNameAndGlobs> reportGroupNameAndGlobsList;

    public String getRepo() {
        return repo == null ? DEFAULT_REPO : repo;
    }

    public List<ReportGroupNameAndGlobs> getReportGroupNameAndGlobsList() {
        return reportGroupNameAndGlobsList == null ? DEFAULT_NAMES_AND_GLOBS : reportGroupNameAndGlobsList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof ReportGroupDetails) {
            ReportGroupDetails rgd = (ReportGroupDetails) obj;
            return this.getRepo().equals(rgd.getRepo())
                    && this.getReportGroupNameAndGlobsList().equals(rgd.getReportGroupNameAndGlobsList());
        }

        return false;
    }
}
