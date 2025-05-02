package reposense.parser;

import static reposense.util.TestUtil.loadResource;

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
import reposense.report.SummaryJson;

public class SummaryJsonParserTest {
    private static final Path VALID_SUMMARY_JSON = loadResource(
            SummaryJsonParserTest.class, "SummaryJsonParserTest/valid-summary.json");
    private static final Path INVALID_SUMMARY_JSON = loadResource(
            SummaryJsonParserTest.class, "SummaryJsonParserTest/invalid-summary.json");
    private static final Path EMPTY_SUMMARY_JSON = loadResource(
            SummaryJsonParserTest.class, "SummaryJsonParserTest/empty-summary.json");
    private static SummaryJson expectedValidSummaryJson;

    @BeforeAll
    public static void setUp() throws Exception {
        String reportGeneratedTime = "Sun, 16 Mar 2025 14:55:54 SGT";
        String reportGenerationTime = " 1 minute(s) 19.83 second(s)";
        String repoSenseVersion = "48a60d6c0d";
        ZoneId zoneId = ZoneId.of("Asia/Singapore");
        String reportTitle = "RepoSense Report";
        LocalDateTime sinceDate = LocalDate.parse("2025-02-16").atStartOfDay();
        LocalDateTime untilDate = LocalDateTime.parse("2025-03-16T23:59:59");
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

        RepoBlurbMap repoBlurbs = new RepoBlurbMap();
        repoBlurbs.withRecord("https://github.com/reposense/testrepo-Alpha/tree/master",
                "Master branch of testrepo-Alpha");

        AuthorBlurbMap authorBlurbs = new AuthorBlurbMap();
        authorBlurbs.withRecord("nbriannl", "Test for author-blurbs.md");

        ChartBlurbMap chartBlurbs = new ChartBlurbMap();
        chartBlurbs.withRecord("https://github.com/reposense/testrepo-Alpha/tree/master|nbriannl",
                "Test for chart-blurbs.md");

        expectedValidSummaryJson = new SummaryJson(repos, reportTitle, reportGeneratedTime, sinceDate, untilDate,
                isSinceDateProvided, isUntilDateProvided, repoSenseVersion, errorSet, reportGenerationTime, zoneId,
                isAuthorshipAnalyzed, repoBlurbs, authorBlurbs, chartBlurbs, isPortfolio);
    }

    @Test
    public void summaryJson_parseEmptyJsonFile_success() throws Exception {
        SummaryJson summaryJson = new SummaryJsonParser().parse(EMPTY_SUMMARY_JSON);
        Assertions.assertNull(summaryJson);
    }

    @Test
    public void summaryJson_parseInvalidJsonFile_success() throws Exception {
        SummaryJson parsedSummaryJson = new SummaryJsonParser().parse(INVALID_SUMMARY_JSON);
        Assertions.assertNotNull(parsedSummaryJson);
        Assertions.assertNull(parsedSummaryJson.getRepoSenseVersion());
    }

    @Test
    public void summaryJson_parseValidJsonFile_success() throws Exception {
        SummaryJson parsedSummaryJson = new SummaryJsonParser().parse(VALID_SUMMARY_JSON);

        RepoConfiguration parsedRepo1 = parsedSummaryJson.getRepos().get(0);
        RepoConfiguration repoConfig = new RepoConfiguration.Builder()
                .location(new RepoLocation(parsedRepo1.getLocation().getLocation()))
                .branch(parsedRepo1.getBranch())
                .displayName(parsedRepo1.getDisplayName())
                .outputFolderName(parsedRepo1.getOutputFolderName())
                .sinceDate(parsedRepo1.getSinceDate())
                .untilDate(parsedRepo1.getUntilDate())
                .build();

        SummaryJson actualSummaryJson = new SummaryJson(List.of(repoConfig),
                parsedSummaryJson.getReportTitle(), parsedSummaryJson.getReportGeneratedTime(),
                parsedSummaryJson.getSinceDate(), parsedSummaryJson.getUntilDate(),
                parsedSummaryJson.isSinceDateProvided(), parsedSummaryJson.isUntilDateProvided(),
                parsedSummaryJson.getRepoSenseVersion(), parsedSummaryJson.getErrorSet(),
                parsedSummaryJson.getReportGenerationTime(), parsedSummaryJson.getZoneId(),
                parsedSummaryJson.isAuthorshipAnalyzed(), parsedSummaryJson.getRepoBlurbs(),
                parsedSummaryJson.getAuthorBlurbs(), parsedSummaryJson.getChartBlurbs(),
                parsedSummaryJson.isPortfolio());

        Assertions.assertEquals(expectedValidSummaryJson, actualSummaryJson);
    }
}
