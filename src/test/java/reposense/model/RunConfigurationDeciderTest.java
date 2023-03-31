package reposense.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reposense.parser.ArgsParser;
import reposense.parser.ArgsParserTest;
import reposense.util.FileUtil;
import reposense.util.InputBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.tools.ant.types.Commandline.translateCommandline;
import static reposense.model.RunConfigurationDecider.getRunConfiguration;
import static reposense.util.TestUtil.loadResource;

public class RunConfigurationDeciderTest {
    private static final Path PROJECT_DIRECTORY = Paths.get(System.getProperty("user.dir"));
    private static final Path CONFIG_FOLDER_ABSOLUTE = loadResource(ArgsParserTest.class, "cli_location_test");

    private static final String NONEXISTENT_DIRECTORY = "some_non_existent_dir/";

    private static final InputBuilder DEFAULT_INPUT_BUILDER = new InputBuilder();

    private static final String TEST_REPO_REPOSENSE = "https://github.com/reposense/RepoSense.git";
    private static final String TEST_REPO_BETA = "https://github.com/reposense/testrepo-Beta.git";


    @BeforeEach
    public void before() {
        DEFAULT_INPUT_BUILDER.reset().addConfig(CONFIG_FOLDER_ABSOLUTE);
    }

    @AfterEach
    public void after() {
        try {
            FileUtil.deleteDirectory(PROJECT_DIRECTORY.resolve(NONEXISTENT_DIRECTORY).toString());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void parse_addConfig_returnsConfigRunConfiguration() throws Exception {
        String input = new InputBuilder()
                .addConfig(CONFIG_FOLDER_ABSOLUTE)
                .addSinceDate("01/07/2017")
                .addUntilDate("30/11/2017")
                .addView()
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        RunConfiguration runConfiguration = getRunConfiguration(cliArguments);
        Assertions.assertTrue(runConfiguration instanceof ConfigRunConfiguration);
    }

    @Test
    public void parse_addRepos_returnsCliRunConfiguration() throws Exception {
        String input = new InputBuilder().addRepos(TEST_REPO_REPOSENSE, TEST_REPO_BETA)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        RunConfiguration runConfiguration = getRunConfiguration(cliArguments);
        Assertions.assertTrue(runConfiguration instanceof CliRunConfiguration);
    }

    @Test
    public void parse_reposAndConfigNotSpecified_returnsConfigRunConfiguration() throws Exception {
        String input = new InputBuilder()
                .addSinceDate("01/07/2017")
                .addUntilDate("30/11/2017")
                .addView()
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        RunConfiguration runConfiguration = getRunConfiguration(cliArguments);
        Assertions.assertTrue(runConfiguration instanceof ConfigRunConfiguration);
    }
}
