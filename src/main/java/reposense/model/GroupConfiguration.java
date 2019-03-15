package reposense.model;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import reposense.system.LogsManager;

public class GroupConfiguration {
    private static final Logger logger = LogsManager.getLogger(AuthorConfiguration.class);

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

        if (!(other instanceof AuthorConfiguration)) {
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

    /**
     * Adds new groups from {@code groupList}. Skips groups that have already been added previously.
     */
    public void addGroups(List<Group> groupList) {
        for (Group group : groupList) {
            if (containsGroup(group)) {
                logger.warning(String.format(
                        "Skipping group as %s already specified in repository %s", group.toString(), location));
                continue;
            }

            addGroup(group);
        }
    }

    public boolean containsGroup(Group group) {
        return groupList.contains(group);
    }

    public void setGroupList(List<Group> groupList) {
        this.groupList = groupList;
    }

    public RepoLocation getLocation() {
        return location;
    }
}
