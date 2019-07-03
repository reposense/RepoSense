package reposense.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import reposense.system.LogsManager;

/**
 * Represents groups configuration information from CSV config file for a single repository.
 * This class is only used to add the contents in {@link GroupConfiguration#groupList} into the respective
 * {@link RepoConfiguration}.
 */
public class GroupConfiguration {

    private RepoLocation location;

    private transient List<Group> groupList = new ArrayList<>();

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
                && groupList.equals(otherGroupConfig.groupList);
    }

    public List<Group> getGroupList() {
        return groupList;
    }

    public void addGroup(Group group) {
        groupList.add(group);
    }

    public boolean containsGroup(Group group) {
        return groupList.contains(group);
    }

    public RepoLocation getLocation() {
        return location;
    }
}
