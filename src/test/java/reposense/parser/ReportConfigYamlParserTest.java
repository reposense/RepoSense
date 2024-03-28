package reposense.parser;

import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.model.ReportConfiguration;

public class ReportConfigYamlParserTest {

    private static final Path VALID_REPORT_CONFIG = loadResource(
            ReportConfigYamlParserTest.class, "ReportConfigYamlParserTest/report-config-valid.yaml");
    private static final Path INVALID_REPORT_CONFIG = loadResource(
            ReportConfigYamlParserTest.class, "ReportConfigYamlParserTest/report-config-invalid.yaml");
    private static final Path EMPTY_REPORT_CONFIG = loadResource(
            ReportConfigYamlParserTest.class, "ReportConfigYamlParserTest/report-config-empty.yaml");

    @Test
    public void reportConfig_parseEmptyYamlFile_getDefaultTitle() throws Exception {
        ReportConfiguration reportConfig = new ReportConfigYamlParser().parse(EMPTY_REPORT_CONFIG);
        Assertions.assertEquals(reportConfig.getTitle(), ReportConfiguration.DEFAULT_TITLE);
        Assertions.assertEquals(reportConfig.getReportRepoConfigurations(),
                ReportConfiguration.DEFAULT_REPORT_REPO_CONFIGS);
    }

    @Test
    public void reportConfig_parseInvalidYamlFile_getDefaultTitle() throws Exception {
        ReportConfiguration reportConfig = new ReportConfigYamlParser().parse(INVALID_REPORT_CONFIG);
        Assertions.assertEquals(reportConfig.getTitle(), ReportConfiguration.DEFAULT_TITLE);
        Assertions.assertEquals(reportConfig.getReportRepoConfigurations(),
                ReportConfiguration.DEFAULT_REPORT_REPO_CONFIGS);
    }

    @Test
    public void reportConfig_parseValidYamlFile_getCustomTitle() throws Exception {
        ReportConfiguration reportConfig = new ReportConfigYamlParser().parse(VALID_REPORT_CONFIG);
        Assertions.assertNotEquals(reportConfig.getTitle(), ReportConfiguration.DEFAULT_TITLE);
        Assertions.assertNotEquals(reportConfig.getReportRepoConfigurations(),
                ReportConfiguration.DEFAULT_REPORT_REPO_CONFIGS);
    }
}
