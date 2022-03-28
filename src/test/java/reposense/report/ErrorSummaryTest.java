package reposense.report;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.model.RepoLocation;
import reposense.parser.InvalidLocationException;

public class ErrorSummaryTest {
    @Test
    public void errorSummary_addRepeatedErrorMessage_containsNoDuplicates() {
        String invalidLocation1 = "ttp://github.com/reposense.RepoSense.git";
        String invalidLocation2 = "https://github.com/contains-illegal-chars/^\\/";
        String invalidLocation3 = "not-valid-protocol://abc.com/reposense/RepoSense.git";

        ErrorSummary errorSummaryInstance = ErrorSummary.getInstance();
        errorSummaryInstance.clearErrorSet();

        try {
            new RepoLocation(invalidLocation1);
        } catch (InvalidLocationException e) {
            // not relevant to the test
        }
        Assertions.assertEquals(1, errorSummaryInstance.getErrorSet().size());

        try {
            new RepoLocation(invalidLocation1);
        } catch (InvalidLocationException e) {
            // not relevant to the test
        }
        Assertions.assertEquals(1, errorSummaryInstance.getErrorSet().size());

        try {
            new RepoLocation(invalidLocation2);
        } catch (InvalidLocationException e) {
            // not relevant to the test
        }
        Assertions.assertEquals(2, errorSummaryInstance.getErrorSet().size());

        try {
            new RepoLocation(invalidLocation1);
        } catch (InvalidLocationException e) {
            // not relevant to the test
        }
        Assertions.assertEquals(2, errorSummaryInstance.getErrorSet().size());

        try {
            new RepoLocation(invalidLocation3);
        } catch (InvalidLocationException e) {
            // not relevant to the test
        }
        Assertions.assertEquals(3, errorSummaryInstance.getErrorSet().size());
    }
}
