package reposense.model.reportconfig;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import reposense.model.FileType;

/**
 * Contains details about each report group and the corresponding globs.
 */
public class ReportGroupNameAndGlobs {
    private final String groupName;

    private final List<String> globs;

    @JsonCreator
    public ReportGroupNameAndGlobs(
            @JsonProperty("group-name") String groupName,
            @JsonProperty("globs") List<String> globs) {
        validate(groupName, globs);
        this.groupName = groupName;
        this.globs = globs;
    }

    /**
     * Validates the group name and globs.
     *
     * @param groupName the name of the group.
     * @param globs the list of globs.
     * @throws IllegalArgumentException if the group name or globs is invalid.
     */
    private static void validate(String groupName, List<String> globs) throws IllegalArgumentException {
        if (groupName == null) {
            throw new IllegalArgumentException("Group name cannot be empty.");
        }
        if (globs == null) {
            throw new IllegalArgumentException("Globs cannot be empty.");
        }
    }

    public String getGroupName() {
        return groupName;
    }

    public List<String> getGlobs() {
        return globs;
    }

    /**
     * Converts this {@code ReportGroupNameAndGlobs} into a {@code FileType}.
     *
     * @return Adapted {@code FileType} object.
     */
    public FileType toFileType() {
        return new FileType(
                this.getGroupName(),
                this.getGlobs()
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof ReportGroupNameAndGlobs) {
            ReportGroupNameAndGlobs rgnag = (ReportGroupNameAndGlobs) obj;
            return rgnag.getGroupName().equals(this.getGroupName())
                    && rgnag.getGlobs().equals(this.getGlobs());
        }

        return false;
    }
}
