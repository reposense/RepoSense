package reposense.model.reportconfig;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportAuthorDetailsTest {
    private static final ReportAuthorDetails details1 = new ReportAuthorDetails(
            List.of("test@example.com"),
            "gitHostId",
            "Display Name",
            "Git Author"
    );

    @Test
    public void constructor_withValidInputs_success() {
        List<String> emails = List.of("test@example.com", "test2@example.com");
        ReportAuthorDetails details = new ReportAuthorDetails(
                emails,
                "Git Host Id",
                "Display Name",
                "Git Author"
        );

        Assertions.assertEquals(emails, details.getAuthorEmails());
        Assertions.assertEquals("Git Host Id", details.getAuthorGitHostId());
        Assertions.assertEquals("Display Name", details.getAuthorDisplayName());
        Assertions.assertEquals("Git Author", details.getAuthorGitAuthorName());
    }

    @Test
    public void constructor_nullGitHostId_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new ReportAuthorDetails(
                    List.of("test@example.com"),
                    null,
                    "Display Name",
                    "Git Author"
            );
        });
    }

    @Test
    public void equals_sameObject_success() {
        Assertions.assertEquals(details1, details1);
    }

    @Test
    public void equals_equivalentObject_success() {
        ReportAuthorDetails details2 = new ReportAuthorDetails(
                List.of("test@example.com"),
                "gitHostId",
                "Display Name",
                "Git Author"
        );

        Assertions.assertEquals(details1, details2);
    }

    @Test
    public void equals_differentObject_failure() {
        ReportAuthorDetails details2 = new ReportAuthorDetails(
                List.of("test1@example.com"),
                "gitHostId",
                "Display Name",
                "Git Author"
        );

        Assertions.assertFalse(details1.equals(details2));
    }
}
