package reposense.parser;

import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import reposense.model.reportconfig.ReportAuthorDetails;
import reposense.model.reportconfig.ReportBranchData;
import reposense.model.reportconfig.ReportConfiguration;
import reposense.model.reportconfig.ReportGroupNameAndGlobs;
import reposense.model.reportconfig.ReportRepoConfiguration;

public class ReportConfigYamlParserTest {

    private static final Path VALID_REPORT_CONFIG = loadResource(
            ReportConfigYamlParserTest.class, "ReportConfigYamlParserTest/report-config-valid.yaml");
    private static final Path INVALID_REPORT_CONFIG = loadResource(
            ReportConfigYamlParserTest.class, "ReportConfigYamlParserTest/report-config-invalid.yaml");
    private static final Path EMPTY_REPORT_CONFIG = loadResource(
            ReportConfigYamlParserTest.class, "ReportConfigYamlParserTest/report-config-empty.yaml");

    private static ReportConfiguration expectedReportConfig;

    @BeforeAll
    public static void setUp() {
        List<ReportGroupNameAndGlobs> groups = new ArrayList<>();
        groups.add(new ReportGroupNameAndGlobs("code", List.of("**.java")));

        ReportAuthorDetails author = new ReportAuthorDetails(List.of("fh@gmail.com"), "FH-30",
                "Francis Hodianto", "Francis Hodianto");

        ReportBranchData branch = new ReportBranchData("master", "My project", List.of(author),
                List.of("**.java"), List.of("bot"), 2000000L);

        ReportRepoConfiguration repo = new ReportRepoConfiguration("https://github.com/reposense/testrepo-Delta.git",
                groups, List.of(branch));

        expectedReportConfig = new ReportConfiguration("Test RepoSense Report", List.of(repo));
    }

    @Test
    public void reportConfig_parseEmptyYamlFile_throwsIoException() {
        Assertions.assertThrows(Exception.class, () -> new ReportConfigYamlParser().parse(EMPTY_REPORT_CONFIG));
    }

    @Test
    public void reportConfig_parseInvalidYamlFile_throwsIoException() {
        Assertions.assertThrows(Exception.class, () -> new ReportConfigYamlParser().parse(INVALID_REPORT_CONFIG));
    }

    @Test
    public void reportConfig_parseValidYamlFile_success() throws Exception {
        ReportConfiguration reportConfig = new ReportConfigYamlParser().parse(VALID_REPORT_CONFIG);
        Assertions.assertEquals(expectedReportConfig, reportConfig);
    }
}
