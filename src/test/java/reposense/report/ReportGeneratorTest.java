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
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.model.BlurbMap;
import reposense.model.ReportConfiguration;
import reposense.parser.SummaryJsonParser;
import reposense.util.TimeUtil;

class ReportGeneratorTest {
    private static final Path OUTPUT_PATH = loadResource(ReportGeneratorTest.class, "ReportGeneratorTest");
    private static final Path ASSETS_PATH = loadResource(ReportGeneratorTest.class, "ReportGeneratorTest/test_assets");
    private static final Path ORIGINAL_SUMMARY_JSON_PATH = loadResource(ReportGeneratorTest.class,
            "ReportGeneratorTest/original_files/summary.json");
    private static final Path SUMMARY_JSON_PATH = loadResource(ReportGeneratorTest.class,
            "ReportGeneratorTest/summary.json");
    private static final Path ORIGINAL_TITLE_MD_PATH = loadResource(ReportGeneratorTest.class,
            "ReportGeneratorTest/original_files/title.md");
    private static final Path TITLE_MD_PATH = loadResource(ReportGeneratorTest.class, "ReportGeneratorTest/title.md");
    private static final Path TEST_TITLE_MD_PATH = loadResource(ReportGeneratorTest.class,
            "ReportGeneratorTest/test_assets/title.md");

    private static final String REPORT_GENERATED_TIME = "Wed, 1 Jan 2025 00:00:00 SGT";

    @Test
    void generateReposReport_isOnlyTextRefreshedTrue_success() throws Exception {
        ReportGenerator reportGenerator = new ReportGenerator();
        TimeUtil.startTimer();
        BlurbMap blurbMap = new BlurbMap();
        blurbMap.withRecord("https://github.com/reposense/testrepo-Delta/tree/master", "This is a test blurb");
        List<Path> reportFoldersAndFiles = reportGenerator.generateReposReport(List.of(), OUTPUT_PATH.toString(),
                ASSETS_PATH.toString(), new ReportConfiguration(), REPORT_GENERATED_TIME,
                LocalDate.parse("2025-02-16").atStartOfDay(), LocalDate.parse("2025-03-16").atStartOfDay(),
                false, false, 4, 12, TimeUtil::getElapsedTime,
                ZoneId.of("Asia/Singapore"), false, false, 0.51,
                blurbMap, false, true);

        SummaryJson actualSummaryJson = new SummaryJsonParser().parse(SUMMARY_JSON_PATH);

        assertNull(reportFoldersAndFiles);
        assertEquals(blurbMap, actualSummaryJson.getBlurbs());
        assertTrue(compareFileContents(TITLE_MD_PATH, TEST_TITLE_MD_PATH));
    }

    @Test
    void generateReposReport_isOnlyTextRefreshedTrueButInvalidPath_throwsIoException() throws Exception {
        ReportGenerator reportGenerator = new ReportGenerator();
        TimeUtil.startTimer();
        BlurbMap blurbMap = new BlurbMap();
        blurbMap.withRecord("https://github.com/reposense/testrepo-Delta/tree/master", "This is a test blurb");
        Assertions.assertThrows(
                IOException.class, () -> reportGenerator.generateReposReport(List.of(), ASSETS_PATH.toString(),
                        ASSETS_PATH.toString(), new ReportConfiguration(), REPORT_GENERATED_TIME,
                        LocalDate.parse("2025-02-16").atStartOfDay(), LocalDate.parse("2025-03-16").atStartOfDay(),
                        false, false, 4, 12, TimeUtil::getElapsedTime,
                        ZoneId.of("Asia/Singapore"), false, false, 0.51,
                        blurbMap, false, true)
        );

    }

    @AfterEach
    void reset() throws IOException {
        Files.copy(ORIGINAL_SUMMARY_JSON_PATH, SUMMARY_JSON_PATH, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(ORIGINAL_TITLE_MD_PATH, TITLE_MD_PATH, StandardCopyOption.REPLACE_EXISTING);
    }
}
