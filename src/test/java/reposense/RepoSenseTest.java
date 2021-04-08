package reposense;

import static org.apache.tools.ant.types.Commandline.translateCommandline;

import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.argparse4j.helper.HelpScreenException;
import reposense.model.CliArguments;
import reposense.model.ConfigCliArguments;
import reposense.model.LocationsCliArguments;
import reposense.model.RepoConfiguration;
import reposense.parser.ArgsParser;
import reposense.parser.ArgsParserTest;
import reposense.parser.ParseException;
import reposense.util.InputBuilder;

public class RepoSenseTest {

    private static final Path CONFIG_FOLDER_ABSOLUTE = loadResource(ArgsParserTest.class, "cli_location_test");
    private static final String TEST_REPO_REPOSENSE = "https://github.com/reposense/RepoSense.git";
    private static final String TEST_REPO_BETA = "https://github.com/reposense/testrepo-Beta.git";
    private static final String TEST_REPO_CHARLIE = "https://github.com/reposense/testrepo-Charlie.git";
    private static final String TEST_REPO_DELTA = "https://github.com/reposense/testrepo-Delta.git";

    @Test
    public void parse_validGitRepoLocations_repoConfigurationListCorrectSize() throws Exception {
        String input = new InputBuilder().addRepos(TEST_REPO_REPOSENSE, TEST_REPO_DELTA).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof LocationsCliArguments);
        List<RepoConfiguration> repoConfigs = RepoSense.getRepoConfigurations((LocationsCliArguments) cliArguments);
        Assert.assertEquals(2, repoConfigs.size());
    }

    @Test
    public void parse_configOrLocationsSimilar_success() throws Exception {
        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE).build();
        CliArguments configCliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(configCliArguments instanceof ConfigCliArguments);
        List<RepoConfiguration> actualRepoConfigs =
                RepoSense.getRepoConfigurations((ConfigCliArguments) configCliArguments);

        input = new InputBuilder().addRepos(TEST_REPO_BETA, TEST_REPO_CHARLIE, TEST_REPO_DELTA).build();
        CliArguments locationCliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(locationCliArguments instanceof LocationsCliArguments);
        List<RepoConfiguration> expectedRepoConfigs =
                RepoSense.getRepoConfigurations((LocationsCliArguments) locationCliArguments);

        Assert.assertEquals(actualRepoConfigs, expectedRepoConfigs);
    }

    @Test (expected = ParseException.class)
    public void parse_noValidRepoLocation_throwsParseException()
            throws ParseException, HelpScreenException {
        String input = new InputBuilder().addRepos("https://githubaaaa.com/asdasdasdasd/RepoSense").build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof LocationsCliArguments);
        RepoSense.getRepoConfigurations((LocationsCliArguments) cliArguments);
    }
}
