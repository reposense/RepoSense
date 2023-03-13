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

        ErrorSummary errorSummary = new ErrorSummary();

        try {
            new RepoLocation(invalidLocation1);
        } catch (InvalidLocationException ile) {
            errorSummary.addErrorMessage(invalidLocation1, ile.getMessage());
        }
        Assertions.assertEquals(1, errorSummary.getErrorSet().size());

        try {
            new RepoLocation(invalidLocation1);
        } catch (InvalidLocationException ile) {
            errorSummary.addErrorMessage(invalidLocation1, ile.getMessage());
        }
        Assertions.assertEquals(1, errorSummary.getErrorSet().size());

        try {
            new RepoLocation(invalidLocation2);
        } catch (InvalidLocationException ile) {
            errorSummary.addErrorMessage(invalidLocation2, ile.getMessage());
        }
        Assertions.assertEquals(2, errorSummary.getErrorSet().size());

        try {
            new RepoLocation(invalidLocation1);
        } catch (InvalidLocationException ile) {
            errorSummary.addErrorMessage(invalidLocation1, ile.getMessage());
        }
        Assertions.assertEquals(2, errorSummary.getErrorSet().size());

        try {
            new RepoLocation(invalidLocation3);
        } catch (InvalidLocationException ile) {
            errorSummary.addErrorMessage(invalidLocation3, ile.getMessage());
        }
        Assertions.assertEquals(3, errorSummary.getErrorSet().size());
    }
}
