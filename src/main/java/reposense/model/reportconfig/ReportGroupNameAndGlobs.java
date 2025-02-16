package reposense.model.reportconfig;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import reposense.model.FileType;

/**
 * Contains details about each report group and the corresponding globs.
 */
public class ReportGroupNameAndGlobs {

    @JsonProperty("group-name")
    private String groupName;

    @JsonProperty("globs")
    private List<String> globs;

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
