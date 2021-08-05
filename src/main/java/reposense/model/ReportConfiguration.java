package reposense.model;

/**
 * Represents configuration information from JSON config file for generated report.
 */
public class ReportConfiguration {
    private static final String DEFAULT_TITLE = "RepoSense Report";
    private String title;

    public String getTitle() {
        return title == null ? DEFAULT_TITLE : title;
    }
}
