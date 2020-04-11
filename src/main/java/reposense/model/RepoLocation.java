package reposense.model;

import java.util.Optional;

import reposense.parser.InvalidLocationException;
import reposense.parser.RepoLocationParser;

/**
 * Represents a repository location.
 */
public class RepoLocation {
    private final String location;
    private final transient Optional<String> parsedBranch;
    private final String repoName;
    private String organization;

    /**
     * @throws InvalidLocationException if {@code location} cannot be represented by a {@code URL} or {@code Path}.
     */
    public RepoLocation(String location) throws InvalidLocationException {
        this(RepoLocationParser.parse(location));
    }

    private RepoLocation(String[] repoLocationDetails) {
        assert repoLocationDetails.length == 4;
        location = repoLocationDetails[RepoLocationParser.LOCATION_INDEX];
        repoName = repoLocationDetails[RepoLocationParser.REPO_NAME_INDEX];
        organization = repoLocationDetails[RepoLocationParser.ORG_INDEX];
        parsedBranch = Optional.ofNullable(repoLocationDetails[RepoLocationParser.BRANCH_NAME_INDEX]);
    }

    public boolean isEmpty() {
        return location.isEmpty();
    }

    public String getRepoName() {
        return repoName;
    }

    public String getOrganization() {
        return organization;
    }

    public Optional<String> getParsedBranch() {
        return parsedBranch;
    }

    @Override
    public String toString() {
        return location;
    }

    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (this == other) {
            return true;
        }

        // instanceof handles null
        if (!(other instanceof RepoLocation)) {
            return false;
        }

        RepoLocation otherLocation = (RepoLocation) other;
        return this.location.equals(otherLocation.location);
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }
}
