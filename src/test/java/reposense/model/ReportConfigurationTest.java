package reposense.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportConfigurationTest {
    @Test
    public void clone_cloneReportConfiguration_success() throws Exception {
        ReportConfiguration reportConfiguration = new ReportConfiguration();
        Assertions.assertNotSame(reportConfiguration, reportConfiguration.clone());
    }
}
