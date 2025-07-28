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
import reposense.parser.exceptions.InvalidDatesException;

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
        List<String> globPatterns = new ArrayList<>();
        globPatterns.add("**.java");
        groups.add(new ReportGroupNameAndGlobs("code", globPatterns));

        List<String> emailList = new ArrayList<>();
        emailList.add("fh@gmail.com");
        ReportAuthorDetails author = new ReportAuthorDetails(emailList, "FH-30",
                "Francis Hodianto", "Francis Hodianto");

        List<ReportAuthorDetails> authorList = new ArrayList<>();
        authorList.add(author);
        List<String> ignoreGlobList = new ArrayList<>();
        ignoreGlobList.add("**.md");
        List<String> ignoreAuthorList = new ArrayList<>();
        ignoreAuthorList.add("bot");

        ReportBranchData branch = new ReportBranchData("master", "My project", authorList, ignoreGlobList,
                ignoreAuthorList, 2000000L, "1/1/2024", "11/11/2024");

        List<ReportBranchData> branches = new ArrayList<>();
        branches.add(branch);

        ReportRepoConfiguration repo = new ReportRepoConfiguration("https://github.com/reposense/testrepo-Delta.git",
                groups, branches);
        List<ReportRepoConfiguration> repos = new ArrayList<>();
        repos.add(repo);

        expectedReportConfig = new ReportConfiguration("Test RepoSense Report", repos);
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

    @Test
    public void getType_validType_success() {
        Assertions.assertEquals(ReportConfiguration.class, new ReportConfigYamlParser().getType());
    }
}
