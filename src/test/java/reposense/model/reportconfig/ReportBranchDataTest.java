package reposense.model.reportconfig;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportBranchDataTest {
    private static final ReportBranchData data1 = new ReportBranchData(
            "main",
            "Test blurb",
            null,
            List.of("*.log"),
            List.of("bot"),
            2000000L,
            "2025/04/10",
            "2025/05/10"
    );

    @Test
    public void constructor_withValidInputs_success() {
        String branch = "main";
        String blurb = "Test blurb";
        List<ReportAuthorDetails> authors = null;
        List<String> ignoreGlobs = List.of("*.log");
        List<String> ignoreAuthors = List.of("bot");
        Long fileSize = 2000000L;
        String sinceDateStr = "10/5/2025 12:10:30";
        String untilDateStr = "30/5/2025";

        ReportBranchData data = new ReportBranchData(branch, blurb, authors, ignoreGlobs, ignoreAuthors, fileSize, sinceDateStr, untilDateStr);

        Assertions.assertEquals(branch, data.getBranch());
        Assertions.assertEquals(blurb, data.getBlurb());
        Assertions.assertEquals(new ArrayList<>(), data.getReportAuthorDetails());
        Assertions.assertEquals(ignoreGlobs, data.getIgnoreGlobList());
        Assertions.assertEquals(ignoreAuthors, data.getIgnoreAuthorList());
        Assertions.assertEquals(fileSize, data.getFileSizeLimit());
    }

    @Test
    public void constructor_withNullInputs_shouldUseDefaultValues() {
        ReportBranchData data = new ReportBranchData(null, null, null, null, null, null, null, null);

        Assertions.assertEquals(ReportBranchData.DEFAULT_BRANCH, data.getBranch());
        Assertions.assertEquals("", data.getBlurb());
        Assertions.assertEquals(new ArrayList<>(), data.getReportAuthorDetails());
        Assertions.assertEquals(new ArrayList<>(), data.getIgnoreGlobList());
        Assertions.assertEquals(new ArrayList<>(), data.getIgnoreAuthorList());
        Assertions.assertEquals(ReportBranchData.DEFAULT_FILE_SIZE_LIMIT, data.getFileSizeLimit());
        // since and until date remain null when not specified
        Assertions.assertNull(data.getSinceDate());
        Assertions.assertNull(data.getUntilDate());
    }

    @Test
    public void equals_sameObject_success() {
        Assertions.assertEquals(data1, data1);
    }

    @Test
    public void equals_equivalentObject_success() {
        ReportBranchData data2 = new ReportBranchData(
                "main",
                "Test blurb",
                null,
                List.of("*.log"),
                List.of("bot"),
                2000000L,
                "2025/04/10",
                "2025/05/10"
        );

        Assertions.assertEquals(data1, data2);
    }

    @Test
    public void equals_differentObject_failure() {
        // differs in branch
        ReportBranchData data2 = new ReportBranchData(
                "master",
                "Test blurb",
                null,
                List.of("*.log"),
                List.of("bot"),
                2000000L,
                "2025/04/10",
                "2025/05/10"
        );

        Assertions.assertNotEquals(data1, data2);
    }

    @Test
    public void equals_differentClass_failure() {
        Assertions.assertNotEquals(data1, new Object());
    }

    @Test
    public void dateFormat_dateTimeValid_success() {
        ReportBranchData data = new ReportBranchData(
                "master",
                "Test blurb",
                null,
                List.of("*.log"),
                List.of("bot"),
                2000000L,
                "10/04/2025 12:10:10", // dd/MM/yyyy HH:mm:ss
                "1/5/2025 13:10" // dd/MM/yyyy HH:mm, single digit day and month are allowed
        );
        Assertions.assertEquals(data.getSinceDate(), LocalDateTime.of(2025, 4, 10, 12, 10 ,10));
        Assertions.assertEquals(data.getUntilDate(), LocalDateTime.of(2025, 5, 1, 13, 10 ,0));
    }

    @Test
    public void dateFormat_invalid_failure() {
        ReportBranchData data1 = new ReportBranchData(
                "master",
                "Test blurb",
                null,
                List.of("*.log"),
                List.of("bot"),
                2000000L,
                "10-04-2025", // using - instead of /
                "2025/04/10" // using yyyy/MM/dd instead of dd/MM/yyyy
        );
        // Invalid date format will be converted into null
        Assertions.assertNull(data1.getSinceDate());
        Assertions.assertNull(data1.getUntilDate());
    }


}
