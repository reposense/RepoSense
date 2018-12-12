package reposense.report;

import java.util.Date;

import reposense.model.RepoConfiguration;

/**
 * Represents the structure of a repository's information in summary.json.
 */
public class RepoConfigurationJson {
    private String location;
    private String organization;
    private String repoName;
    private String branch;
    private String displayName;
    private Date sinceDate;
    private Date untilDate;

    public RepoConfigurationJson(RepoConfiguration config) {
        this.location = config.getLocation().toString();
        this.organization = config.getOrganization();
        this.repoName = config.getRepoName();
        this.branch = config.getBranch();
        this.displayName = config.getDisplayName();
        this.sinceDate = config.getSinceDate();
        this.untilDate = config.getUntilDate();
    }
}
