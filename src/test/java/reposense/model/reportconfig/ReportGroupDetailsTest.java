package reposense.model.reportconfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportGroupDetailsTest {
    @Test
    public void getRepo_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportGroupDetails().getRepo(), ReportGroupDetails.DEFAULT_REPO);
    }

    @Test
    public void getReportGroupNameAndGlobsList_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportGroupDetails().getReportGroupNameAndGlobsList(),
                ReportGroupDetails.DEFAULT_NAMES_AND_GLOBS);
    }

    @Test
    public void equals_defaultInstancesAreEqual_success() {
        Assertions.assertEquals(new ReportGroupDetails(), new ReportGroupDetails());
        Assertions.assertEquals(new ReportGroupDetails(), ReportGroupDetails.DEFAULT_INSTANCE);
    }
}
