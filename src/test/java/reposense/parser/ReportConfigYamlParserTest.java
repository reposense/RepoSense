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
        Assertions.assertEquals(reportConfig.getRepoUrl(), ReportConfiguration.DEFAULT_REPO_URL);
        Assertions.assertEquals(reportConfig.getReportTitle(), ReportConfiguration.DEFAULT_TITLE);
        Assertions.assertEquals(reportConfig.getAuthorDisplayName(), ReportConfiguration.DEFAULT_AUTHOR_DISPLAY_NAME);
        Assertions.assertEquals(reportConfig.getAuthorGithubId(), ReportConfiguration.DEFAULT_AUTHOR_GITHUB_ID);
        Assertions.assertEquals(reportConfig.getStartDate(), ReportConfiguration.DEFAULT_START_DATE);
        Assertions.assertEquals(reportConfig.getEndDate(), ReportConfiguration.DEFAULT_END_DATE);
        Assertions.assertEquals(reportConfig.getBranchesWithBlurbs(), ReportConfiguration.DEFAULT_BRANCHES_WITH_BLURBS);
    }

    @Test
    public void reportConfig_parseInvalidYamlFile_getDefaultTitle() throws Exception {
        ReportConfiguration reportConfig = new ReportConfigYamlParser().parse(INVALID_REPORT_CONFIG);
        Assertions.assertEquals(reportConfig.getRepoUrl(), ReportConfiguration.DEFAULT_REPO_URL);
        Assertions.assertEquals(reportConfig.getReportTitle(), ReportConfiguration.DEFAULT_TITLE);
        Assertions.assertEquals(reportConfig.getAuthorDisplayName(), ReportConfiguration.DEFAULT_AUTHOR_DISPLAY_NAME);
        Assertions.assertEquals(reportConfig.getAuthorGithubId(), ReportConfiguration.DEFAULT_AUTHOR_GITHUB_ID);
        Assertions.assertEquals(reportConfig.getStartDate(), ReportConfiguration.DEFAULT_START_DATE);
        Assertions.assertEquals(reportConfig.getEndDate(), ReportConfiguration.DEFAULT_END_DATE);
        Assertions.assertEquals(reportConfig.getBranchesWithBlurbs(), ReportConfiguration.DEFAULT_BRANCHES_WITH_BLURBS);
    }

    @Test
    public void reportConfig_parseValidYamlFile_getCustomTitle() throws Exception {
        ReportConfiguration reportConfig = new ReportConfigYamlParser().parse(VALID_REPORT_CONFIG);
        Assertions.assertNotEquals(reportConfig.getRepoUrl(), ReportConfiguration.DEFAULT_REPO_URL);
        Assertions.assertNotEquals(reportConfig.getReportTitle(), ReportConfiguration.DEFAULT_TITLE);
        Assertions.assertNotEquals(reportConfig.getAuthorDisplayName(),
                ReportConfiguration.DEFAULT_AUTHOR_DISPLAY_NAME);
        Assertions.assertNotEquals(reportConfig.getAuthorGithubId(), ReportConfiguration.DEFAULT_AUTHOR_GITHUB_ID);
        Assertions.assertNotEquals(reportConfig.getStartDate(), ReportConfiguration.DEFAULT_START_DATE);
        Assertions.assertNotEquals(reportConfig.getEndDate(), ReportConfiguration.DEFAULT_END_DATE);
        Assertions.assertNotEquals(reportConfig.getBranchesWithBlurbs(),
                ReportConfiguration.DEFAULT_BRANCHES_WITH_BLURBS);
    }
}
