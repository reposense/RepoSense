package reposense.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.model.reportconfig.ReportAuthorDetails;
import reposense.model.reportconfig.ReportBranchData;
import reposense.model.reportconfig.ReportConfiguration;
import reposense.model.reportconfig.ReportGroupNameAndGlobs;
import reposense.model.reportconfig.ReportRepoConfiguration;
import reposense.parser.exceptions.InvalidLocationException;

class OneStopConfigRunConfigurationTest {

    private static CliArguments testSetUp;
    private static List<RepoConfiguration> expectedRepoConfigurations;

    public static void setUpActualRepoConfigurations() {
        CliArguments.Builder testCliBuilder = new CliArguments.Builder();

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
        ReportConfiguration expectedReportConfig = new ReportConfiguration("Test RepoSense Report", repos);
        testCliBuilder.reportConfiguration(expectedReportConfig);
        testSetUp = testCliBuilder.build();
    }

    public static void setUpExpectedRepoConfigurations() throws InvalidLocationException {
        expectedRepoConfigurations = new ArrayList<>();
        RepoLocation expectedRepoLocation = new RepoLocation("https://github.com/reposense/testrepo-Delta.git");
        List<AuthorConfiguration> authorConfigs = new ArrayList<>();
        List<GroupConfiguration> groupConfigs = new ArrayList<>();

        RepoConfiguration.Builder builder = new RepoConfiguration.Builder()
                .location(expectedRepoLocation)
                .branch("master")
                .ignoreGlobList(List.of("**.md"))
                .ignoredAuthorsList(List.of("bot"))
                .fileSizeLimit(2000000L)
                // Needs to be removed this when we deprecate the standalone config
                .isStandaloneConfigIgnored(true);

        expectedRepoConfigurations.add(builder.build());

        GroupConfiguration groupConfiguration = new GroupConfiguration(expectedRepoLocation);
        groupConfiguration.addGroup(new FileType("code", List.of("**.java")));

        groupConfigs.add(groupConfiguration);

        Author author = new Author("FH-30");
        author.setEmails(List.of("fh@gmail.com"));
        author.setDisplayName("Francis Hodianto");
        author.setAuthorAliases(List.of("Francis Hodianto"));

        AuthorConfiguration authorConfiguration = new AuthorConfiguration(expectedRepoLocation, "master");
        authorConfiguration.addAuthor(author);
        authorConfigs.add(authorConfiguration);

        RepoConfiguration.merge(expectedRepoConfigurations, authorConfigs);
        RepoConfiguration.setGroupConfigsToRepos(expectedRepoConfigurations, groupConfigs);
    }

    @Test
    public void getRepoConfigurations_withValidInputs_returnsRepoConfigurations() throws InvalidLocationException {
        setUpActualRepoConfigurations();
        setUpExpectedRepoConfigurations();

        OneStopConfigRunConfiguration config = new OneStopConfigRunConfiguration(testSetUp);
        List<RepoConfiguration> actualRepoConfigurations = config.getRepoConfigurations();
        Assertions.assertEquals(expectedRepoConfigurations, actualRepoConfigurations);
    }
}
