package reposense.model.reportconfig;

import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportBranchDataTest {
    @Test
    public void getBranch_equalsDefaultReturnValue_success() {
        Assertions.assertSame(ReportBranchData.DEFAULT_BRANCH, new ReportBranchData().getBranch());
    }

    @Test
    public void getIgnoreGlobList_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ArrayList<>(), new ReportBranchData().getIgnoreGlobList());
    }


    @Test
    public void getIgnoreAuthorList_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ArrayList<>(),
                new ReportBranchData().getIgnoreAuthorList());
    }

    @Test
    public void equals_defaultInstancesAreEqual_success() {
        Assertions.assertEquals(new ReportBranchData(), new ReportBranchData());
    }
}
