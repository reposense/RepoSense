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

import reposense.model.AuthorBlurbMap;
import reposense.model.ChartBlurbMap;
import reposense.model.RepoBlurbMap;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.model.SupportedDomainUrlMap;
import reposense.model.reportconfig.ReportConfiguration;
import reposense.parser.SummaryJsonParserTest;

public class SummaryJsonTest {
    private static final Path VALID_SUMMARY_JSON = loadResource(
            SummaryJsonParserTest.class, "SummaryJsonParserTest/valid-summary.json");
    private static SummaryJson expectedUpdatedSummaryJson;
    private static final String REPORT_GENERATED_TIME = "Wed, 1 Jan 2025 00:00:00 SGT";
    private static final String REPORT_GENERATION_TIME = " 1 minute(s) 0.01 second(s)";
    private static final RepoBlurbMap REPO_BLURBS = new RepoBlurbMap();
    private static final AuthorBlurbMap AUTHOR_BLURBS = new AuthorBlurbMap();
    private static final ChartBlurbMap CHART_BLURBS = new ChartBlurbMap();

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
                .untilDate(LocalDateTime.parse("2025-03-16T23:59:59"))
                .build();
        List<RepoConfiguration> repos = List.of(repoConfig);

        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("repoName", "reposense/RepoSense[master]");
        errorMap.put("errorMessage", "Failed to clone from https://github.com/reposense/RepoSense.git");
        Set<Map<String, String>> errorSet = Set.of(errorMap);

        REPO_BLURBS.withRecord("https://github.com/reposense/testrepo-Alpha/tree/master", "This is a test blurb");
        AUTHOR_BLURBS.withRecord("nbriannl", "Test for author-blurbs.md");
        CHART_BLURBS.withRecord("https://github.com/reposense/testrepo-Alpha/tree/master|nbriannl",
                "Test for chart-blurbs.md");


        expectedUpdatedSummaryJson = new SummaryJson(repos, reportConfig, REPORT_GENERATED_TIME, sinceDate, untilDate,
                isSinceDateProvided, isUntilDateProvided, repoSenseVersion, errorSet, REPORT_GENERATION_TIME, zoneId,
                isAuthorshipAnalyzed, REPO_BLURBS, AUTHOR_BLURBS, CHART_BLURBS, isPortfolio);
    }

    @Test
    public void updateSummaryJson_success() throws IOException {
        SummaryJson updatedSummaryJson = SummaryJson.updateSummaryJson(VALID_SUMMARY_JSON, REPO_BLURBS, AUTHOR_BLURBS,
                CHART_BLURBS, REPORT_GENERATED_TIME, REPORT_GENERATION_TIME);

        Assertions.assertNotNull(updatedSummaryJson);
        Assertions.assertEquals(expectedUpdatedSummaryJson.getRepoBlurbs(), updatedSummaryJson.getRepoBlurbs());
        Assertions.assertEquals(expectedUpdatedSummaryJson.getAuthorBlurbs(), updatedSummaryJson.getAuthorBlurbs());
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
