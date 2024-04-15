package reposense.model.reportconfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportConfigurationTest {
    @Test
    public void getTitle_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportConfiguration().getTitle(), ReportConfiguration.DEFAULT_TITLE);
    }

    @Test
    public void getReportRepoConfigurations_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportConfiguration().getReportRepoConfigurations(),
                ReportConfiguration.DEFAULT_REPORT_REPO_CONFIGS);
    }

    @Test
    public void getGroupDetails_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportConfiguration().getGroupDetails(),
                ReportConfiguration.DEFAULT_REPORT_GROUP_DETAILS);
    }

    @Test
    public void equals_defaultInstancesAreEqual_success() {
        Assertions.assertEquals(new ReportConfiguration(), new ReportConfiguration());
    }
}
