package reposense.report;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static reposense.util.TestUtil.compareFileContents;
import static reposense.util.TestUtil.loadResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.model.AuthorBlurbMap;
import reposense.model.ChartBlurbMap;
import reposense.model.RepoBlurbMap;
import reposense.model.reportconfig.ReportConfiguration;
import reposense.parser.SummaryJsonParser;
import reposense.util.TimeUtil;

class ReportGeneratorTest {
    private static final Path OUTPUT_PATH = loadResource(ReportGeneratorTest.class, "ReportGeneratorTest");
    private static final Path ASSETS_PATH = loadResource(ReportGeneratorTest.class, "ReportGeneratorTest/test_assets");
    private static final Path ORIGINAL_SUMMARY_JSON_PATH = loadResource(ReportGeneratorTest.class,
            "ReportGeneratorTest/original_files/summary.json");
    private static final Path SUMMARY_JSON_PATH = loadResource(ReportGeneratorTest.class,
            "ReportGeneratorTest/summary.json");
    private static final Path ORIGINAL_INTRO_MD_PATH = loadResource(ReportGeneratorTest.class,
            "ReportGeneratorTest/original_files/intro.md");
    private static final Path INTRO_MD_PATH = loadResource(ReportGeneratorTest.class,
            "ReportGeneratorTest/intro.md");
    private static final Path TEST_INTRO_MD_PATH = loadResource(ReportGeneratorTest.class,
            "ReportGeneratorTest/test_assets/intro.md");

    private static final String REPORT_GENERATED_TIME = "Wed, 1 Jan 2025 00:00:00 SGT";
    private static final String GITHUB_API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    @Test
    void generateReposReport_isOnlyTextRefreshedTrue_success() throws Exception {
        RepoBlurbMap repoBlurbMap = new RepoBlurbMap();
        repoBlurbMap.withRecord("https://github.com/reposense/testrepo-Delta/tree/master", "This is a test blurb");
        AuthorBlurbMap authorBlurbMap = new AuthorBlurbMap();
        authorBlurbMap.withRecord("nbriannl", "Test for author-blurbs.md");
        ChartBlurbMap chartBlurbMap = new ChartBlurbMap();
        chartBlurbMap.withRecord("https://github.com/reposense/testrepo-Delta/tree/master|nbriannl",
                 "This is a test blurb for chart-blurbs.md");
        TimeUtil.startTimer();

        List<Path> reportFoldersAndFiles = new ReportGenerator().generateReposReport(List.of(), OUTPUT_PATH.toString(),
                ASSETS_PATH.toString(), new ReportConfiguration(), REPORT_GENERATED_TIME,
                LocalDateTime.parse("2025-02-16T00:00:00", DateTimeFormatter.ofPattern(GITHUB_API_DATE_FORMAT)),
                LocalDateTime.parse("2025-03-16T23:59:59", DateTimeFormatter.ofPattern(GITHUB_API_DATE_FORMAT)),
                false, false, 4, 12, TimeUtil::getElapsedTime,
                ZoneId.of("Asia/Singapore"), false, false, 0.51,
                repoBlurbMap, authorBlurbMap, chartBlurbMap, false, true);

        SummaryJson actualSummaryJson = new SummaryJsonParser().parse(SUMMARY_JSON_PATH);

        assertNull(reportFoldersAndFiles);
        assertEquals(repoBlurbMap, actualSummaryJson.getRepoBlurbs());
        assertEquals(authorBlurbMap, actualSummaryJson.getAuthorBlurbs());
        assertTrue(compareFileContents(INTRO_MD_PATH, TEST_INTRO_MD_PATH));
    }

    @Test
    void generateReposReport_isOnlyTextRefreshedTrueButInvalidPath_throwsIoException() throws Exception {
        ReportGenerator reportGenerator = new ReportGenerator();
        TimeUtil.startTimer();
        RepoBlurbMap repoBlurbMap = new RepoBlurbMap();
        repoBlurbMap.withRecord("https://github.com/reposense/testrepo-Delta/tree/master", "This is a test blurb");
        AuthorBlurbMap authorBlurbMap = new AuthorBlurbMap();
        ChartBlurbMap chartBlurbMap = new ChartBlurbMap();
        Assertions.assertThrows(
                IOException.class, () -> reportGenerator.generateReposReport(List.of(), ASSETS_PATH.toString(),
                ASSETS_PATH.toString(), new ReportConfiguration(), REPORT_GENERATED_TIME,
                LocalDateTime.parse("2025-02-16T00:00:00", DateTimeFormatter.ofPattern(GITHUB_API_DATE_FORMAT)),
                LocalDateTime.parse("2025-03-16T23:59:59", DateTimeFormatter.ofPattern(GITHUB_API_DATE_FORMAT)),
                false, false, 4, 12, TimeUtil::getElapsedTime,
                ZoneId.of("Asia/Singapore"), false, false, 0.51,
                repoBlurbMap, authorBlurbMap, chartBlurbMap, false, true)
        );
    }

    @AfterEach
    void reset() throws IOException {
        Files.copy(ORIGINAL_SUMMARY_JSON_PATH, SUMMARY_JSON_PATH, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(ORIGINAL_INTRO_MD_PATH, INTRO_MD_PATH, StandardCopyOption.REPLACE_EXISTING);
    }
}
