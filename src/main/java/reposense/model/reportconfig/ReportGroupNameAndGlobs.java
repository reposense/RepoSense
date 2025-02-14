package reposense.model.reportconfig;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import reposense.model.FileType;

/**
 * Contains details about each report group and the corresponding globs.
 */
public class ReportGroupNameAndGlobs {
    public static final String DEFAULT_GROUP_NAME = "code";
    public static final List<String> DEFAULT_GLOBS = List.of();
    public static final List<ReportGroupNameAndGlobs> DEFAULT_INSTANCES = new ArrayList<>();

    static {
        ReportGroupNameAndGlobs rg1 = new ReportGroupNameAndGlobs();
        rg1.groupName = "code";
        rg1.globs = List.of("**.java");

        ReportGroupNameAndGlobs rg2 = new ReportGroupNameAndGlobs();
        rg2.groupName = "tests";
        rg2.globs = List.of("src/test**");

        ReportGroupNameAndGlobs rg3 = new ReportGroupNameAndGlobs();
        rg3.groupName = "docs";
        rg3.globs = List.of("docs**", "**.adoc", "**.md");
    }

    @JsonProperty("group-name")
    private String groupName;

    @JsonProperty("globs")
    private List<String> globs;

    public String getGroupName() {
        return groupName == null ? DEFAULT_GROUP_NAME : groupName;
    }

    public List<String> getGlobs() {
        return globs == null ? DEFAULT_GLOBS : globs;
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
