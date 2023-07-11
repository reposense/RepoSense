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
        try {
            RepoLocation repoLocation = new RepoLocation(invalidLocation1);
            Assertions.assertEquals(1, repoLocation.getErrorSummary().getErrorSet().size());
        } catch (InvalidLocationException e) {
            // not relevant to the test
        }

        try {
            RepoLocation repoLocation = new RepoLocation(invalidLocation1);
            Assertions.assertEquals(1, repoLocation.getErrorSummary().getErrorSet().size());
        } catch (InvalidLocationException e) {
            // not relevant to the test
        }

        try {
            RepoLocation repoLocation = new RepoLocation(invalidLocation2);
            Assertions.assertEquals(2, repoLocation.getErrorSummary().getErrorSet().size());
        } catch (InvalidLocationException e) {
            // not relevant to the test
        }

        try {
            RepoLocation repoLocation = new RepoLocation(invalidLocation1);
            Assertions.assertEquals(2, repoLocation.getErrorSummary().getErrorSet().size());
        } catch (InvalidLocationException e) {
            // not relevant to the test
        }

        try {
            RepoLocation repoLocation = new RepoLocation(invalidLocation3);
            Assertions.assertEquals(3, repoLocation.getErrorSummary().getErrorSet().size());
        } catch (InvalidLocationException e) {
            // not relevant to the test
        }
    }
}
