package reposense.model.reportconfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportBranchDataTest {
    @Test
    public void getBranch_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportBranchData().getBranch(), ReportBranchData.DEFAULT_BRANCH);
    }

    @Test
    public void getIgnoreGlobList_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportBranchData().getIgnoreGlobList(), ReportBranchData.DEFAULT_IGNORE_GLOB_LIST);
    }

    @Test
    public void getIgnoreAuthorList_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportBranchData().getIgnoreAuthorList(),
                ReportBranchData.DEFAULT_IGNORE_AUTHORS_LIST);
    }

    @Test
    public void equals_defaultInstancesAreEqual_success() {
        Assertions.assertEquals(new ReportBranchData(), new ReportBranchData());
    }
}
