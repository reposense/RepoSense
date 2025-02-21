package reposense.model.reportconfig;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportConfigurationTest {
    @Test
    public void constructor_withValidInputs_success() {
        ReportConfiguration reportConfiguration = new ReportConfiguration("My Report", new ArrayList<>());
        Assertions.assertNotNull(reportConfiguration);
    }

    @Test
    public void getTitle_equalsDefaultReturnValue_success() {
        Assertions.assertSame(ReportConfiguration.DEFAULT_TITLE,
                new ReportConfiguration(null, new ArrayList<>()).getTitle());
    }
}
