package reposense.model.reportconfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportRepoConfigurationTest {
    @Test
    public void getRepo_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportRepoConfiguration().getRepo(), ReportRepoConfiguration.DEFAULT_REPO);
    }

    @Test
    public void getAuthorEmails_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportRepoConfiguration().getAuthorEmails(),
                ReportRepoConfiguration.DEFAULT_AUTHOR_EMAIL);
    }

    @Test
    public void getAuthorGitHostId_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportRepoConfiguration().getAuthorGitHostId(),
                ReportRepoConfiguration.DEFAULT_GIT_HOST_ID);
    }

    @Test
    public void getAuthorDisplayName_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportRepoConfiguration().getAuthorDisplayName(),
                ReportRepoConfiguration.DEFAULT_DISPLAY_NAME);
    }

    @Test
    public void getAuthorGitAuthorName_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportRepoConfiguration().getAuthorGitAuthorName(),
                ReportRepoConfiguration.DEFAULT_GIT_AUTHOR_NAME);
    }

    @Test
    public void getBranches_equalsDefaultReturnValue_success() {
        Assertions.assertSame(new ReportRepoConfiguration().getBranches(), ReportRepoConfiguration.DEFAULT_BRANCHES);
    }

    @Test
    public void equals_defaultInstancesAreEqual_success() {
        Assertions.assertEquals(new ReportRepoConfiguration(), new ReportRepoConfiguration());
        Assertions.assertEquals(new ReportRepoConfiguration(), ReportRepoConfiguration.DEFAULT_INSTANCE);
    }
}
