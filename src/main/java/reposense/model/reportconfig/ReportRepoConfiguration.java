package reposense.model.reportconfig;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a single repository configuration in the overall report
 * configuration.
 */
public class ReportRepoConfiguration {
    public static final String DEFAULT_REPO = "https://github.com/user/repo";
    public static final List<String> DEFAULT_AUTHOR_NAMES = List.of(
            "johnDoe", "John Doe", "my home PC"
    );
    public static final List<ReportBranchData> DEFAULT_BRANCHES = List.of(
            new ReportBranchData()
    );

    public static final ReportRepoConfiguration DEFAULT_INSTANCE = new ReportRepoConfiguration();

    static {
        DEFAULT_INSTANCE.repo = DEFAULT_REPO;
        DEFAULT_INSTANCE.authorNames = DEFAULT_AUTHOR_NAMES;
        DEFAULT_INSTANCE.branches = DEFAULT_BRANCHES;
    }

    @JsonProperty("repo")
    private String repo;

    @JsonProperty("branches")
    private List<ReportBranchData> branches;

    @JsonProperty("authorNames")
    private List<String> authorNames;

    public String getRepo() {
        return repo == null ? DEFAULT_REPO : repo;
    }

    public List<ReportBranchData> getBranches() {
        return branches == null ? DEFAULT_BRANCHES : branches;
    }

    public List<String> getAuthorNames() {
        return authorNames == null ? DEFAULT_AUTHOR_NAMES : authorNames;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof ReportRepoConfiguration) {
            ReportRepoConfiguration rrc = (ReportRepoConfiguration) obj;
            return rrc.getRepo().equals(this.getRepo())
                    && rrc.getBranches().equals(this.getBranches())
                    && rrc.getAuthorNames().equals(this.getAuthorNames());
        }

        return false;
    }
}
