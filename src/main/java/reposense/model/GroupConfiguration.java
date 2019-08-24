package reposense.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents groups configuration information from CSV config file for a single repository.
 * This class is only used to add the contents in {@code groups} into the respective repo configurations.
 */
public class GroupConfiguration {

    private RepoLocation location;

    private transient List<FileType> groups = new ArrayList<>();

    public GroupConfiguration(RepoLocation location) {
        this.location = location;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof GroupConfiguration)) {
            return false;
        }

        GroupConfiguration otherGroupConfig = (GroupConfiguration) other;

        return location.equals(otherGroupConfig.location)
                && groups.equals(otherGroupConfig.groups);
    }

    public List<FileType> getGroupsList() {
        return groups;
    }

    public void addGroup(FileType group) {
        groups.add(group);
    }

    public boolean containsGroup(FileType group) {
        return groups.contains(group);
    }

    public RepoLocation getLocation() {
        return location;
    }
}
