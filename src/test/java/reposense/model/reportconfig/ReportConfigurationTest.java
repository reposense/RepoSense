package reposense.model.reportconfig;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import reposense.model.BlurbMap;

public class ReportConfigurationTest {

    private static ReportConfiguration expectedReportConfig;

    @BeforeAll
    public static void setUp() {
        List<String> globPatterns = List.of("**.java");
        List<ReportGroupNameAndGlobs> groups = List.of(new ReportGroupNameAndGlobs("code", globPatterns));

        List<String> emailList = List.of("fh@gmail.com");
        ReportAuthorDetails author = new ReportAuthorDetails(emailList, "FH-30",
                "Francis Hodianto", "Francis Hodianto");

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
        BlurbMap expectedBlurbMap = new BlurbMap();
        expectedBlurbMap.withRecord("https://github.com/reposense/testrepo-Delta/tree/master", "My project");

        Assertions.assertEquals(expectedBlurbMap, expectedReportConfig.getBlurbMap());
    }

    @Test
    public void equals_withSameObject_success() {
        Assertions.assertEquals(expectedReportConfig, expectedReportConfig);
    }

    @Test
    public void equals_withDifferentObject_failure() {
        Assertions.assertFalse(expectedReportConfig.equals(new ReportConfiguration(null, null)));
    }
}
