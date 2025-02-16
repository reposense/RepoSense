package reposense.model.reportconfig;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportRepoConfigurationTest {
    @Test
    public void getRepo_equalsDefaultReturnValue_success() {
        Assertions.assertSame("", new ReportRepoConfiguration().getRepo());
    }

    @Test
    public void getBranches_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ArrayList<>(), new ReportRepoConfiguration().getBranches());
    }

    @Test
    public void equals_defaultInstancesAreEqual_success() {
        Assertions.assertEquals(new ReportRepoConfiguration(), new ReportRepoConfiguration());
    }
}
