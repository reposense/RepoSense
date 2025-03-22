package reposense.report;

import static reposense.util.TestUtil.loadResource;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import reposense.model.BlurbMap;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.model.ReportConfiguration;
import reposense.model.SupportedDomainUrlMap;
import reposense.parser.SummaryJsonParserTest;

public class SummaryJsonTest {
    private static final Path VALID_SUMMARY_JSON = loadResource(
            SummaryJsonParserTest.class, "SummaryJsonParserTest/valid-summary.json");
    private static SummaryJson expectedUpdatedSummaryJson;
    private static final String REPORT_GENERATED_TIME = "Wed, 1 Jan 2025 00:00:00 SGT";
    private static final String REPORT_GENERATION_TIME = " 1 minute(s) 0.01 second(s)";
    private static final BlurbMap BLURBS = new BlurbMap();

    @BeforeAll
    public static void setUp() throws Exception {
        String repoSenseVersion = "48a60d6c0d";
        ZoneId zoneId = ZoneId.of("Asia/Singapore");
        ReportConfiguration reportConfig = new ReportConfiguration();
        LocalDateTime sinceDate = LocalDate.parse("2025-02-16").atStartOfDay();
        LocalDateTime untilDate = LocalDate.parse("2025-03-16").atStartOfDay();
        boolean isSinceDateProvided = false;
        boolean isUntilDateProvided = false;
        boolean isAuthorshipAnalyzed = false;
        boolean isPortfolio = false;

        RepoLocation repoLocation = new RepoLocation("https://github.com/reposense/testrepo-Alpha.git");
        RepoConfiguration repoConfig = new RepoConfiguration.Builder()
                .location(repoLocation)
                .branch("master")
                .displayName("reposense/testrepo-Alpha[master]")
                .outputFolderName("reposense_testrepo-Alpha_master")
                .sinceDate(LocalDate.parse("2025-02-16").atStartOfDay())
                .untilDate(LocalDate.parse("2025-03-16").atStartOfDay())
                .build();
        List<RepoConfiguration> repos = List.of(repoConfig);

        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("repoName", "reposense/RepoSense[master]");
        errorMap.put("errorMessage", "Failed to clone from https://github.com/reposense/RepoSense.git");
        Set<Map<String, String>> errorSet = Set.of(errorMap);

        BLURBS.withRecord("https://github.com/reposense/testrepo-Alpha/tree/master", "This is a test blurb");

        expectedUpdatedSummaryJson = new SummaryJson(repos, reportConfig, REPORT_GENERATED_TIME, sinceDate, untilDate,
                isSinceDateProvided, isUntilDateProvided, repoSenseVersion, errorSet, REPORT_GENERATION_TIME, zoneId,
                isAuthorshipAnalyzed, BLURBS, isPortfolio);
    }

    @Test
    public void updateSummaryJson_success() throws IOException {
        SummaryJson updatedSummaryJson = SummaryJson.updateSummaryJson(VALID_SUMMARY_JSON, BLURBS,
                REPORT_GENERATED_TIME, REPORT_GENERATION_TIME);

        Assertions.assertNotNull(updatedSummaryJson);
        Assertions.assertEquals(expectedUpdatedSummaryJson.getBlurbs(), updatedSummaryJson.getBlurbs());
        Assertions.assertEquals(expectedUpdatedSummaryJson.getReportGeneratedTime(),
                updatedSummaryJson.getReportGeneratedTime());
        Assertions.assertEquals(expectedUpdatedSummaryJson.getReportGenerationTime(),
                updatedSummaryJson.getReportGenerationTime());

    }

    @Test
    public void equals_differentClass_returnFalse() {
        Assertions.assertNotEquals(expectedUpdatedSummaryJson, new Object());
    }

    @Test
    public void getSupportedDomainUrlMap_success() {
        Map<String, Map<String, String>> supportedDomainUrlMap = expectedUpdatedSummaryJson.getSupportedDomainUrlMap();
        Assertions.assertEquals(supportedDomainUrlMap, SupportedDomainUrlMap.getDefaultDomainUrlMap());
    }
}
