package reposense.model.reportconfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportAuthorDetailsTest {
    @Test
    public void getAuthorEmails_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportAuthorDetails().getAuthorEmails(),
                ReportAuthorDetails.DEFAULT_AUTHOR_EMAIL);
    }

    @Test
    public void getAuthorGitHostId_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportAuthorDetails().getAuthorGitHostId(),
                ReportAuthorDetails.DEFAULT_GIT_HOST_ID);
    }

    @Test
    public void getAuthorDisplayName_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportAuthorDetails().getAuthorDisplayName(),
                ReportAuthorDetails.DEFAULT_DISPLAY_NAME);
    }

    @Test
    public void getAuthorGitAuthorName_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportAuthorDetails().getAuthorGitAuthorName(),
                ReportAuthorDetails.DEFAULT_GIT_AUTHOR_NAME);
    }
}
