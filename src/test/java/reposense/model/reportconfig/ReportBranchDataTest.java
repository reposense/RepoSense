package reposense.model.reportconfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportBranchDataTest {
    @Test
    public void getBranch_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportBranchData().getBranch(), ReportBranchData.DEFAULT_BRANCH);
    }

    @Test
    public void getFileFormats_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportBranchData().getFileFormats(), ReportBranchData.DEFAULT_FILE_FORMATS);
    }

    @Test
    public void getIgnoreGlobList_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportBranchData().getIgnoreGlobList(), ReportBranchData.DEFAULT_IGNORE_GLOB_LIST);
    }

    @Test
    public void getIsIgnoreStandaloneConfig_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportBranchData().getIsIgnoreStandaloneConfig(),
                ReportBranchData.DEFAULT_IS_IGNORE_STANDALONE_CONFIG);
    }

    @Test
    public void getIgnoreCommitList_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportBranchData().getIgnoreCommitList(),
                ReportBranchData.DEFAULT_IGNORE_COMMITS_LIST);
    }

    @Test
    public void getIgnoreAuthorList_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportBranchData().getIgnoreAuthorList(),
                ReportBranchData.DEFAULT_IGNORE_AUTHORS_LIST);
    }

    @Test
    public void getIsShallowCloning_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportBranchData().getIsShallowCloning(),
                ReportBranchData.DEFAULT_IS_SHALLOW_CLONING);
    }

    @Test
    public void getIsFindPreviousAuthor_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportBranchData().getIsFindPreviousAuthor(),
                ReportBranchData.DEFAULT_IS_FIND_PREVIOUS_AUTHOR);
    }

    @Test
    public void equals_defaultInstancesAreEqual_success() {
        Assertions.assertEquals(new ReportBranchData(), new ReportBranchData());
    }
}
