package reposense.model.reportconfig;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import reposense.model.GroupConfiguration;
import reposense.model.RepoLocation;

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

    /**
     * Represents a single mapped entry between a fully qualified repository name and its associated blurb.
     */
    public static final class MapEntry {
        private final String key;
        private final String value;

        public MapEntry(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    public String getRepo() {
        return repo == null ? DEFAULT_REPO : repo;
    }

    /**
     * Returns the repository name from the {@code repo} URL. Each entry is represented by a list containing
     * the fully qualified repository name and the associated blurb.
     *
     * @return {@code List<MapEntry>} containing the fully qualified repository name and the associated blurb.
     * @throws IllegalArgumentException if the {@code repo} URL is not in the correct format.
     */
    public List<MapEntry> getFullyQualifiedRepoNamesWithBlurbs() {
        List<MapEntry> repoNames = new ArrayList<>();
        Pattern pattern = Pattern.compile(".git");
        Matcher matcher = pattern.matcher(getRepo());

        if (!matcher.find()) {
            // no point continuing if there are no blurbs
            throw new IllegalArgumentException("Repo URL: " + this.getRepo() + " is not in the correct format. "
                    + "Skipping...");
        }

        for (ReportBranchData rbd : this.getBranches()) {
            String qualifiedName = getRepo().substring(0, matcher.start()) + "/" + rbd.getBranch();
            repoNames.add(new MapEntry(qualifiedName, rbd.getBlurb()));
        }

        return repoNames;
    }

    public List<ReportGroupNameAndGlobs> getGroupDetails() {
        return groups == null ? DEFAULT_GROUP_DETAILS : groups;
    }

    /**
     * Merges all {@code ReportGroupNameAndGlobs} objects in {@code groups} into a {@code GroupConfiguration} object.
     *
     * @param location {@code RepoLocation} object.
     * @return Adapted {@code GroupConfiguration} object.
     */
    public GroupConfiguration getGroupConfiguration(RepoLocation location) {
        GroupConfiguration groupConfiguration = new GroupConfiguration(location);

        this.getGroupDetails()
                .forEach(x -> groupConfiguration.addGroup(x.toFileType()));

        return groupConfiguration;
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
}
