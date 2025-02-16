package reposense.model.reportconfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportGroupNameAndGlobsTest {
    @Test
    public void getRepo_equalsDefaultReturnValue_success() {
        Assertions.assertSame(null, new ReportGroupNameAndGlobs().getGroupName());
    }

    @Test
    public void getReportGroupNameAndGlobsList_equalsDefaultReturnValue_success() {
        Assertions.assertSame(null,
                new ReportGroupNameAndGlobs().getGlobs());
    }

    @Test
    public void equals_defaultInstancesAreEqual_success() {
        Assertions.assertEquals(new ReportGroupNameAndGlobs(), new ReportGroupNameAndGlobs());
    }
}
