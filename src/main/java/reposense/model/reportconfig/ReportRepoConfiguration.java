package reposense.model.reportconfig;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single repository configuration in the overall report
 * configuration.
 */
public class ReportRepoConfiguration {
    public static final String DEFAULT_REPO = "https://github.com/user/repo";
    public static final List<ReportGroupNameAndGlobs> DEFAULT_GROUP_DETAILS = ReportGroupNameAndGlobs.DEFAULT_INSTANCES;
    public static final List<ReportBranchData> DEFAULT_BRANCHES = List.of(
            ReportBranchData.DEFAULT_INSTANCE
    );
    public static final ReportRepoConfiguration DEFAULT_INSTANCE = new ReportRepoConfiguration();

    static {
        DEFAULT_INSTANCE.repo = DEFAULT_REPO;
        DEFAULT_INSTANCE.groups = DEFAULT_GROUP_DETAILS;
        DEFAULT_INSTANCE.branches = DEFAULT_BRANCHES;
    }

    @JsonProperty("repo")
    private String repo;

    @JsonProperty("groups")
    private List<ReportGroupNameAndGlobs> groups;

    @JsonProperty("branches")
    private List<ReportBranchData> branches;

    public String getRepo() {
        return repo == null ? DEFAULT_REPO : repo;
    }

    public List<ReportGroupNameAndGlobs> getGroupDetails() {
        return groups == null ? DEFAULT_GROUP_DETAILS : groups;
    }

    public List<ReportBranchData> getBranches() {
        return branches == null ? DEFAULT_BRANCHES : branches;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof ReportRepoConfiguration) {
            ReportRepoConfiguration rrc = (ReportRepoConfiguration) obj;
            return rrc.getRepo().equals(this.getRepo())
                    && rrc.getGroupDetails().equals(this.getGroupDetails())
                    && rrc.getBranches().equals(this.getBranches());
        }

        return false;
    }

    @Override
    public String toString() {
        return repo + "\n" + groups + "\n" + branches;
    }
}
