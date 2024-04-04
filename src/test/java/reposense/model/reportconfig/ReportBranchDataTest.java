package reposense.model.reportconfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportBranchDataTest {
    @Test
    public void getBranch_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportBranchData().getBranch(), ReportBranchData.DEFAULT_BRANCH);
    }

    @Test
    public void getBlurb_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportBranchData().getBlurb(), ReportBranchData.DEFAULT_BLURB);
    }

    @Test
    public void equals_defaultInstancesAreEqual_success() {
        Assertions.assertEquals(new ReportBranchData(), new ReportBranchData());
    }
}
