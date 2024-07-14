package reposense.model.reportconfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportRepoConfigurationTest {
    @Test
    public void getRepo_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportRepoConfiguration().getRepo(), ReportRepoConfiguration.DEFAULT_REPO);
    }

    @Test
    public void getBlurb_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportRepoConfiguration().getBlurb(), ReportRepoConfiguration.DEFAULT_BLURB);
    }

    @Test
    public void getBranches_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportRepoConfiguration().getBranches(), ReportRepoConfiguration.DEFAULT_BRANCHES);
    }

    @Test
    public void equals_defaultInstancesAreEqual_success() {
        Assertions.assertEquals(new ReportRepoConfiguration(), new ReportRepoConfiguration());
        Assertions.assertEquals(new ReportRepoConfiguration(), ReportRepoConfiguration.DEFAULT_INSTANCE);
    }
}
