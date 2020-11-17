package reposense.parser;

import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;

import org.junit.Assert;
import org.junit.Test;

import reposense.model.ReportConfiguration;

public class ReportConfigJsonParserTest {

    private static final Path VALID_REPORT_CONFIG = loadResource(
            ReportConfigJsonParserTest.class, "ReportConfigJsonParserTest/report-config-valid.json");
    private static final Path INVALID_REPORT_CONFIG = loadResource(
            ReportConfigJsonParserTest.class, "ReportConfigJsonParserTest/report-config-invalid.json");
    private static final Path EMPTY_REPORT_CONFIG = loadResource(
            ReportConfigJsonParserTest.class, "ReportConfigJsonParserTest/report-config-empty.json");
    private static final String DEFAULT_TITLE = "RepoSense Report";

    @Test
    public void reportConfig_parseEmptyJsonFile_getDefaultTitle() throws Exception {
        ReportConfiguration reportConfig = new ReportConfigJsonParser().parse(EMPTY_REPORT_CONFIG);
        Assert.assertEquals(reportConfig.getTitle(), DEFAULT_TITLE);
    }
    @Test
    public void reportConfig_parseInvalidJsonFile_getDefaultTitle() throws Exception {
        ReportConfiguration reportConfig = new ReportConfigJsonParser().parse(INVALID_REPORT_CONFIG);
        Assert.assertEquals(reportConfig.getTitle(), DEFAULT_TITLE);
    }

    @Test
    public void reportConfig_parseValidJsonFile_getCustomTitle() throws Exception {
        ReportConfiguration reportConfig = new ReportConfigJsonParser().parse(VALID_REPORT_CONFIG);
        Assert.assertNotEquals(reportConfig.getTitle(), DEFAULT_TITLE);
    }
}
