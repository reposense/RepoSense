package reposense.model.reportconfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportGroupNameAndGlobsTest {
    @Test
    public void getRepo_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportGroupNameAndGlobs().getGroupName(), ReportGroupNameAndGlobs.DEFAULT_GROUP_NAME);
    }

    @Test
    public void getReportGroupNameAndGlobsList_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportGroupNameAndGlobs().getGlobs(),
                ReportGroupNameAndGlobs.DEFAULT_GLOBS);
    }

    @Test
    public void equals_defaultInstancesAreEqual_success() {
        Assertions.assertEquals(new ReportGroupNameAndGlobs(), new ReportGroupNameAndGlobs());
    }
}
