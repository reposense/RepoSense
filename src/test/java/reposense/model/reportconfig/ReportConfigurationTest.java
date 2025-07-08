package reposense.model.reportconfig;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import reposense.model.RepoBlurbMap;

public class ReportConfigurationTest {

    private static ReportConfiguration expectedReportConfig;
    private static ReportConfiguration invalidReportConfig;

    @BeforeAll
    public static void setUp() {
        List<String> globPatterns = List.of("**.java");
        List<ReportGroupNameAndGlobs> groups = List.of(new ReportGroupNameAndGlobs("code", globPatterns));

        List<String> emailList = List.of("fh@gmail.com");
        ReportAuthorDetails author = new ReportAuthorDetails(emailList, "FH-30",
                "Francis Hodianto", List.of("Francis Hodianto"));

        List<ReportAuthorDetails> authorList = List.of(author);
        List<String> ignoreGlobList = List.of("**.md");
        List<String> ignoreAuthorList = List.of("bot");
        ReportBranchData branch = new ReportBranchData("master", "My project", authorList,
                ignoreGlobList, ignoreAuthorList, 2000000L);

        List<ReportBranchData> branches = List.of(branch);
        ReportRepoConfiguration repo = new ReportRepoConfiguration("https://github.com/reposense/testrepo-Delta.git",
                groups, branches);

        List<ReportRepoConfiguration> repos = List.of(repo);
        expectedReportConfig = new ReportConfiguration("Test RepoSense Report", repos);

        // Invalid repo with missing .git
        ReportRepoConfiguration invalidRepo = new ReportRepoConfiguration("https://github.com/reposense/testrepo-Delta",
                groups, branches);

        List<ReportRepoConfiguration> invalidRepos = List.of(invalidRepo);
        invalidReportConfig = new ReportConfiguration("Test RepoSense Report", invalidRepos);
    }

    @Test
    public void constructor_withValidInputs_success() {
        ReportConfiguration reportConfiguration = new ReportConfiguration("My Report", new ArrayList<>());
        Assertions.assertNotNull(reportConfiguration);
    }

    @Test
    public void getTitle_equalsDefaultReturnValue_success() {
        Assertions.assertSame(ReportConfiguration.DEFAULT_TITLE,
                new ReportConfiguration(null, null).getTitle());
    }

    @Test
    public void getBlurbMap_withValidInputs_success() {
        RepoBlurbMap expectedBlurbMap = new RepoBlurbMap();
        expectedBlurbMap.withRecord("https://github.com/reposense/testrepo-Delta/tree/master", "My project");

        Assertions.assertEquals(expectedBlurbMap, expectedReportConfig.getRepoBlurbMap());
    }

    @Test
    public void getBlurbMap_withInvalidInputs_returnEmptyBlurbMap() {
        RepoBlurbMap expectedBlurbMap = new RepoBlurbMap();
        expectedBlurbMap.withRecord("https://github.com/reposense/testrepo-Delta/tree/master", "My project");

        Assertions.assertNotEquals(expectedBlurbMap, invalidReportConfig.getRepoBlurbMap());
        Assertions.assertEquals(new RepoBlurbMap(), invalidReportConfig.getRepoBlurbMap());
    }

    @Test
    public void equals_withSameObject_success() {
        Assertions.assertEquals(expectedReportConfig, expectedReportConfig);
    }

    @Test
    public void equals_withDifferentObject_failure() {
        Assertions.assertNotEquals(new ReportConfiguration(null, null), expectedReportConfig);
    }

    @Test
    public void equals_differentClass_failure() {
        Assertions.assertNotEquals(expectedReportConfig, new Object());
    }

}
