package reposense.report;

import org.junit.Assert;
import org.junit.Test;

public class ErrorSummaryTest {
    @Test
    public void errorSummary_addRepeatedErrorMessage_containsNoDuplicates() {
        ErrorSummary errorSummaryInstance = ErrorSummary.getInstance();
        errorSummaryInstance.clearErrorSet();

        String repoLocation1 = "reposense/RepoSense[master]";
        String repoLocation2 = "reposense/testrepo-Alpha[master]";
        String errorMessage1 = "error message 1";
        String errorMessage2 = "error message 2";

        errorSummaryInstance.addErrorMessage(repoLocation1, errorMessage1);
        Assert.assertEquals(1, errorSummaryInstance.getErrorSet().size());

        errorSummaryInstance.addErrorMessage(repoLocation1, errorMessage1);
        Assert.assertEquals(1, errorSummaryInstance.getErrorSet().size());

        errorSummaryInstance.addErrorMessage(repoLocation2, errorMessage2);
        Assert.assertEquals(2, errorSummaryInstance.getErrorSet().size());

        errorSummaryInstance.addErrorMessage(repoLocation1, errorMessage1);
        Assert.assertEquals(2, errorSummaryInstance.getErrorSet().size());

        errorSummaryInstance.addErrorMessage(repoLocation1, errorMessage2);
        Assert.assertEquals(3, errorSummaryInstance.getErrorSet().size());
    }
}
