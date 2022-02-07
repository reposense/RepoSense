package reposense.report;

import org.junit.Assert;
import org.junit.Test;

import reposense.model.RepoLocation;
import reposense.parser.InvalidLocationException;

public class ErrorSummaryTest {
    @Test
    public void errorSummary_addRepeatedErrorMessage_containsNoDuplicates() {
        ErrorSummary.getInstance().clearErrorSet();
        String invalidLocation1 = "ftp://github.com/reposense/testrepo-Beta.git";
        String invalidLocation2 = "tp://github.com/reposense/testrepo-Beta.git";
        String validLocation = "https://github.com/reposense/testrepo-Beta.git";

        try {
            RepoLocation repoLocation1 = new RepoLocation(invalidLocation1);
        } catch (InvalidLocationException e) {
            // Ignore as it is not the purpose of this test
        }
        Assert.assertEquals(1, ErrorSummary.getInstance().getErrorSet().size());

        try {
            RepoLocation repoLocation2 = new RepoLocation(validLocation);
        } catch (InvalidLocationException e) {
            // Ignore as it is not the purpose of this test
        }
        Assert.assertEquals(1, ErrorSummary.getInstance().getErrorSet().size());

        try {
            RepoLocation repoLocation3 = new RepoLocation(invalidLocation1);
        } catch (InvalidLocationException e) {
            // Ignore as it is not the purpose of this test
        }
        Assert.assertEquals(1, ErrorSummary.getInstance().getErrorSet().size());

        try {
            RepoLocation repoLocation4 = new RepoLocation(invalidLocation2);
        } catch (InvalidLocationException e) {
            // Ignore as it is not the purpose of this test
        }
        Assert.assertEquals(2, ErrorSummary.getInstance().getErrorSet().size());

        try {
            RepoLocation repoLocation5 = new RepoLocation(invalidLocation2);
        } catch (InvalidLocationException e) {
            // Ignore as it is not the purpose of this test
        }
        Assert.assertEquals(2, ErrorSummary.getInstance().getErrorSet().size());

        try {
            RepoLocation repoLocation6 = new RepoLocation(invalidLocation1);
        } catch (InvalidLocationException e) {
            // Ignore as it is not the purpose of this test
        }
        Assert.assertEquals(2, ErrorSummary.getInstance().getErrorSet().size());
    }
}
