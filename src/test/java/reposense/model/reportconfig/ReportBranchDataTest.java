package reposense.model.reportconfig;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportBranchDataTest {
    @Test
    public void constructor_withValidInputs_success() {
        String branch = "main";
        String blurb = "Test blurb";
        List<ReportAuthorDetails> authors = null;
        List<String> ignoreGlobs = List.of("*.log");
        List<String> ignoreAuthors = List.of("bot");
        Long fileSize = 2000000L;

        ReportBranchData data = new ReportBranchData(branch, blurb, authors, ignoreGlobs, ignoreAuthors, fileSize);

        Assertions.assertEquals(branch, data.getBranch());
        Assertions.assertEquals(blurb, data.getBlurb());
        Assertions.assertEquals(new ArrayList<>(), data.getReportAuthorDetails());
        Assertions.assertEquals(ignoreGlobs, data.getIgnoreGlobList());
        Assertions.assertEquals(ignoreAuthors, data.getIgnoreAuthorList());
        Assertions.assertEquals(fileSize, data.getFileSizeLimit());
    }

    @Test
    public void constructor_withNullInputs_shouldUseDefaultValues() {
        ReportBranchData data = new ReportBranchData(null, null, null, null, null, null);

        Assertions.assertEquals(ReportBranchData.DEFAULT_BRANCH, data.getBranch());
        Assertions.assertEquals("", data.getBlurb());
        Assertions.assertEquals(new ArrayList<>(), data.getReportAuthorDetails());
        Assertions.assertEquals(new ArrayList<>(), data.getIgnoreGlobList());
        Assertions.assertEquals(new ArrayList<>(), data.getIgnoreAuthorList());
        Assertions.assertEquals(ReportBranchData.DEFAULT_FILE_SIZE_LIMIT, data.getFileSizeLimit());
    }

    @Test
    public void equals_sameObject_success() {
        ReportBranchData data1 = new ReportBranchData(
                "main",
                "Test blurb",
                null,
                List.of("*.log"),
                List.of("bot"),
                2000000L
        );

        ReportBranchData data2 = new ReportBranchData(
                "main",
                "Test blurb",
                null,
                List.of("*.log"),
                List.of("bot"),
                2000000L
        );

        Assertions.assertEquals(data1, data2);
    }

    @Test
    public void equals_differentObject_failure() {
        ReportBranchData data1 = new ReportBranchData(
                "main",
                "Test blurb",
                null,
                List.of("*.log"),
                List.of("bot"),
                2000000L
        );

        ReportBranchData data2 = new ReportBranchData(
                "master",
                "Test blurb",
                null,
                List.of("*.log"),
                List.of("bot"),
                2000000L
        );

        Assertions.assertNotEquals(data1, data2);
    }
}
