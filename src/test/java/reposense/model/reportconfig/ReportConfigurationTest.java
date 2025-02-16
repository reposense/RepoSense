package reposense.model.reportconfig;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportConfigurationTest {
    @Test
    public void getTitle_equalsDefaultReturnValue_success() {
        Assertions.assertSame(ReportConfiguration.DEFAULT_TITLE, new ReportConfiguration().getTitle());
    }

    @Test
    public void getReportRepoConfigurations_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ArrayList<>(),
                new ReportConfiguration().getReportRepoConfigurations());
    }

    @Test
    public void equals_defaultInstancesAreEqual_success() {
        Assertions.assertEquals(new ReportConfiguration(), new ReportConfiguration());
    }
}
