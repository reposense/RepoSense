package reposense.parser;

import static org.apache.tools.ant.types.Commandline.translateCommandline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import net.sourceforge.argparse4j.helper.HelpScreenException;
import reposense.RepoSense;
import reposense.model.CliArguments;
import reposense.model.ConfigCliArguments;
import reposense.model.Format;
import reposense.model.LocationsCliArguments;
import reposense.model.RepoConfiguration;
import reposense.model.ViewCliArguments;
import reposense.util.InputBuilder;
import reposense.util.TestUtil;

public class ArgsParserTest {

    private static final Path PROJECT_DIRECTORY = Paths.get(System.getProperty("user.dir"));
    private static final Path CONFIG_FOLDER_ABSOLUTE = new File(ArgsParserTest.class.getClassLoader()
            .getResource("cli_location_test").getFile()).toPath();
    private static final Path OUTPUT_DIRECTORY_ABSOLUTE = new File(ArgsParserTest.class.getClassLoader()
            .getResource("output").getFile()).toPath();
    private static final Path CONFIG_FOLDER_RELATIVE = PROJECT_DIRECTORY.relativize(CONFIG_FOLDER_ABSOLUTE);
    private static final Path OUTPUT_DIRECTORY_RELATIVE = PROJECT_DIRECTORY.relativize(OUTPUT_DIRECTORY_ABSOLUTE);
    private static final Path REPO_CONFIG_CSV_FILE =
            CONFIG_FOLDER_ABSOLUTE.resolve(RepoConfigCsvParser.REPO_CONFIG_FILENAME);
    private static final Path AUTHOR_CONFIG_CSV_FILE =
            CONFIG_FOLDER_ABSOLUTE.resolve(AuthorConfigCsvParser.AUTHOR_CONFIG_FILENAME);

    private static final InputBuilder DEFAULT_INPUT_BUILDER = new InputBuilder();

    private static final String TEST_REPO_REPOSENSE = "https://github.com/reposense/RepoSense.git";
    private static final String TEST_REPO_BETA = "https://github.com/reposense/testrepo-Beta.git";
    private static final String TEST_REPO_CHARLIE = "https://github.com/reposense/testrepo-Charlie.git";
    private static final String TEST_REPO_DELTA = "https://github.com/reposense/testrepo-Delta.git";

    private static final String DEFAULT_TIMEZONE = "UTC+08";

    @Before
    public void before() {
        DEFAULT_INPUT_BUILDER.reset().addConfig(CONFIG_FOLDER_ABSOLUTE);
    }

    @Test
    public void parse_allCorrectInputs_success() throws ParseException, IOException, HelpScreenException {
        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE)
                .addOutput(OUTPUT_DIRECTORY_ABSOLUTE)
                .addSinceDate("01/07/2017")
                .addUntilDate("30/11/2017")
                .addFormats("java adoc html css js")
                .addIgnoreStandaloneConfig()
                .addView()
                .addTimezone(DEFAULT_TIMEZONE)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getRepoConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(
                OUTPUT_DIRECTORY_ABSOLUTE.resolve(ArgsParser.DEFAULT_REPORT_NAME), cliArguments.getOutputFilePath()));

        Date expectedSinceDate = TestUtil.getDate(2017, Calendar.JULY, 1);
        Date expectedUntilDate = TestUtil.getDate(2017, Calendar.NOVEMBER, 30);
        Assert.assertEquals(expectedSinceDate, cliArguments.getSinceDate().get());
        Assert.assertEquals(expectedUntilDate, cliArguments.getUntilDate().get());

        List<Format> expectedFormats = Format.convertStringsToFormats(
                Arrays.asList("java", "adoc", "html", "css", "js"));
        Assert.assertEquals(expectedFormats, cliArguments.getFormats());

        Assert.assertTrue(cliArguments.isAutomaticallyLaunching());

        Assert.assertEquals(ZoneId.of(DEFAULT_TIMEZONE), cliArguments.getZoneId());
    }

    @Test(expected = HelpScreenException.class)
    public void parse_help_throwsHelpScreenException() throws HelpScreenException, ParseException {
        String input = "--help";
        ArgsParser.parse(translateCommandline(input));
    }

    @Test
    public void parse_allCorrectInputsAlias_success() throws ParseException, IOException, HelpScreenException {
        String input = String.format("-c %s -o %s -s 01/07/2017 -u 30/11/2017 -f java adoc html css js -i -v -t %s",
                CONFIG_FOLDER_ABSOLUTE, OUTPUT_DIRECTORY_ABSOLUTE, DEFAULT_TIMEZONE);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getRepoConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(
                OUTPUT_DIRECTORY_ABSOLUTE.resolve(ArgsParser.DEFAULT_REPORT_NAME), cliArguments.getOutputFilePath()));

        Date expectedSinceDate = TestUtil.getDate(2017, Calendar.JULY, 1);
        Date expectedUntilDate = TestUtil.getDate(2017, Calendar.NOVEMBER, 30);
        Assert.assertEquals(expectedSinceDate, cliArguments.getSinceDate().get());
        Assert.assertEquals(expectedUntilDate, cliArguments.getUntilDate().get());

        List<Format> expectedFormats = Format.convertStringsToFormats(
                Arrays.asList("java", "adoc", "html", "css", "js"));
        Assert.assertEquals(expectedFormats, cliArguments.getFormats());

        Assert.assertTrue(cliArguments.isAutomaticallyLaunching());

        Assert.assertEquals(ZoneId.of(DEFAULT_TIMEZONE), cliArguments.getZoneId());
    }

    @Test
    public void parse_withExtraWhitespaces_success() throws ParseException, IOException, HelpScreenException {
        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE).addWhiteSpace(5)
                .addOutput(OUTPUT_DIRECTORY_ABSOLUTE).addWhiteSpace(4)
                .addSinceDate("01/07/2017").addWhiteSpace(3)
                .addUntilDate("30/11/2017").addWhiteSpace(6)
                .addFormats("java   adoc  html      css js   ")
                .addIgnoreStandaloneConfig().addWhiteSpace(1)
                .addView().addWhiteSpace(4)
                .addTimezone(DEFAULT_TIMEZONE).addWhiteSpace(5)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getRepoConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(
                OUTPUT_DIRECTORY_ABSOLUTE.resolve(ArgsParser.DEFAULT_REPORT_NAME), cliArguments.getOutputFilePath()));

        Date expectedSinceDate = TestUtil.getDate(2017, Calendar.JULY, 1);
        Date expectedUntilDate = TestUtil.getDate(2017, Calendar.NOVEMBER, 30);
        Assert.assertEquals(expectedSinceDate, cliArguments.getSinceDate().get());
        Assert.assertEquals(expectedUntilDate, cliArguments.getUntilDate().get());

        List<Format> expectedFormats = Format.convertStringsToFormats(Arrays.asList(
                "java", "adoc", "html", "css", "js"));
        Assert.assertEquals(expectedFormats, cliArguments.getFormats());

        Assert.assertTrue(cliArguments.isAutomaticallyLaunching());

        Assert.assertEquals(ZoneId.of(DEFAULT_TIMEZONE), cliArguments.getZoneId());
    }

    @Test
    public void parse_configFolderOnly_success() throws ParseException, IOException, HelpScreenException {
        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getRepoConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()));
        // Optional arguments have default values
        Assert.assertEquals(Optional.empty(), cliArguments.getSinceDate());
        Assert.assertEquals(Optional.empty(), cliArguments.getUntilDate());
        Assert.assertEquals(ArgsParser.DEFAULT_REPORT_NAME, cliArguments.getOutputFilePath().getFileName().toString());
        Assert.assertEquals(Format.DEFAULT_FORMATS, cliArguments.getFormats());
        Assert.assertFalse(cliArguments.isAutomaticallyLaunching());

        input = new InputBuilder().addConfig(CONFIG_FOLDER_RELATIVE).build();
        cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getRepoConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()));
        // Optional arguments have default values
        Assert.assertEquals(Optional.empty(), cliArguments.getSinceDate());
        Assert.assertEquals(Optional.empty(), cliArguments.getUntilDate());
        Assert.assertEquals(ArgsParser.DEFAULT_REPORT_NAME, cliArguments.getOutputFilePath().getFileName().toString());
        Assert.assertEquals(Format.DEFAULT_FORMATS, cliArguments.getFormats());
        Assert.assertFalse(cliArguments.isAutomaticallyLaunching());
        Assert.assertEquals(ZoneId.systemDefault(), cliArguments.getZoneId());
    }

    @Test
    public void parse_viewOnly_success() throws ParseException, IOException, HelpScreenException {
        String input = new InputBuilder().addView(OUTPUT_DIRECTORY_ABSOLUTE).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ViewCliArguments);
        Assert.assertTrue(Files.isSameFile(
                OUTPUT_DIRECTORY_ABSOLUTE, ((ViewCliArguments) cliArguments).getReportDirectoryPath()));
    }

    @Test
    public void parse_withIgnore_success() throws ParseException, HelpScreenException {
        String input = new InputBuilder().addRepos(TEST_REPO_REPOSENSE, TEST_REPO_DELTA)
                .addIgnoreStandaloneConfig()
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        String inputWithAlias = new InputBuilder().addRepos(TEST_REPO_REPOSENSE, TEST_REPO_DELTA)
                .add("-i")
                .build();
        CliArguments cliArgumentsWithAlias = ArgsParser.parse(translateCommandline(inputWithAlias));

        Assert.assertTrue(cliArguments instanceof LocationsCliArguments);
        Assert.assertTrue(cliArgumentsWithAlias instanceof LocationsCliArguments);

        Assert.assertTrue(((LocationsCliArguments) cliArguments).isStandaloneConfigIgnored());
        Assert.assertTrue(((LocationsCliArguments) cliArgumentsWithAlias).isStandaloneConfigIgnored());

        Assert.assertEquals(cliArguments, cliArgumentsWithAlias);
    }

    @Test
    public void parse_withoutIgnore_success() throws ParseException, HelpScreenException {
        String input = new InputBuilder().addRepos(TEST_REPO_REPOSENSE, TEST_REPO_DELTA).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assert.assertTrue(cliArguments instanceof LocationsCliArguments);
        Assert.assertFalse(((LocationsCliArguments) cliArguments).isStandaloneConfigIgnored());
    }

    @Test
    public void parse_viewOnlyWithoutArgs_returnsConfigCliArguments() throws ParseException, HelpScreenException {
        String input = new InputBuilder().addView().build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertEquals(PROJECT_DIRECTORY.toString(), (
                (ConfigCliArguments) cliArguments).getConfigFolderPath().toString());
        Assert.assertTrue(cliArguments.isAutomaticallyLaunching());
    }

    @Test
    public void parse_configFolderAndOutputDirectory_success() throws ParseException, IOException, HelpScreenException {
        Path expectedRelativeOutputDirectoryPath = OUTPUT_DIRECTORY_RELATIVE.resolve(ArgsParser.DEFAULT_REPORT_NAME);
        Path expectedAbsoluteOutputDirectoryPath = OUTPUT_DIRECTORY_ABSOLUTE.resolve(ArgsParser.DEFAULT_REPORT_NAME);

        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE)
                .addOutput(OUTPUT_DIRECTORY_RELATIVE)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getRepoConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(expectedRelativeOutputDirectoryPath, cliArguments.getOutputFilePath()));

        input = new InputBuilder().addConfig(CONFIG_FOLDER_RELATIVE)
                .addOutput(OUTPUT_DIRECTORY_ABSOLUTE)
                .build();
        cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getRepoConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(expectedAbsoluteOutputDirectoryPath, cliArguments.getOutputFilePath()));
    }

    @Test
    public void parse_configFolderAndViewWithouthArgs_returnsConfigCliArguments()
            throws ParseException, IOException, HelpScreenException {
        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE)
                .addView()
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getRepoConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()));
        Assert.assertTrue(cliArguments.isAutomaticallyLaunching());

        input = new InputBuilder().addConfig(CONFIG_FOLDER_RELATIVE)
                .addView()
                .build();
        cliArguments = ArgsParser.parse(translateCommandline(input));

        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getRepoConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()));
        Assert.assertTrue(cliArguments.isAutomaticallyLaunching());
    }

    @Test
    public void parse_configFolderAndViewWithArgs_returnsConfigCliArguments()
            throws ParseException, IOException, HelpScreenException {
        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE)
                .addView(OUTPUT_DIRECTORY_ABSOLUTE)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getRepoConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()));
        Assert.assertTrue(cliArguments.isAutomaticallyLaunching());
    }

    @Test
    public void sinceDate_correctFormat_success() throws ParseException, HelpScreenException {
        String input = DEFAULT_INPUT_BUILDER.addSinceDate("01/07/2017").build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Date expectedSinceDate = TestUtil.getDate(2017, Calendar.JULY, 1);
        Assert.assertEquals(expectedSinceDate, cliArguments.getSinceDate().get());
    }

    @Test
    public void untilDate_correctFormat_success() throws ParseException, HelpScreenException {
        String input = DEFAULT_INPUT_BUILDER.addUntilDate("30/11/2017").build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Date expectedUntilDate = TestUtil.getDate(2017, Calendar.NOVEMBER, 30);
        Assert.assertEquals(expectedUntilDate, cliArguments.getUntilDate().get());
    }

    @Test
    public void formats_inAlphanumeric_success() throws ParseException, HelpScreenException {
        String input = DEFAULT_INPUT_BUILDER.addFormats("java js css 7z").build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        List<Format> expectedFormats = Format.convertStringsToFormats(Arrays.asList("java", "js", "css", "7z"));
        Assert.assertEquals(expectedFormats, cliArguments.getFormats());
    }

    @Test
    public void parse_validGitRepoLocations_repoConfigurationListCorrectSize()
            throws ParseException, HelpScreenException {
        String input = new InputBuilder().addRepos(TEST_REPO_REPOSENSE, TEST_REPO_DELTA).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof LocationsCliArguments);
        List<RepoConfiguration> repoConfigs = RepoSense.getRepoConfigurations((LocationsCliArguments) cliArguments);
        Assert.assertEquals(2, repoConfigs.size());
    }

    @Test
    public void parse_repoLocationsAndView_returnsLocationCliArguments() throws ParseException, HelpScreenException {
        String input = new InputBuilder().addRepos(TEST_REPO_REPOSENSE, TEST_REPO_DELTA)
                .addView()
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assert.assertTrue(cliArguments instanceof LocationsCliArguments);
        Assert.assertTrue(cliArguments.isAutomaticallyLaunching());
        List<String> expectedLocations = Arrays.asList(TEST_REPO_REPOSENSE, TEST_REPO_DELTA);
        Assert.assertEquals(expectedLocations, ((LocationsCliArguments) cliArguments).getLocations());
    }

    @Test
    public void parse_repoLocationsOnly_success() throws ParseException, HelpScreenException {
        String input = new InputBuilder().addRepos(TEST_REPO_REPOSENSE, TEST_REPO_DELTA).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assert.assertTrue(cliArguments instanceof LocationsCliArguments);
        Assert.assertFalse(cliArguments.isAutomaticallyLaunching());
        List<String> expectedLocations = Arrays.asList(TEST_REPO_REPOSENSE, TEST_REPO_DELTA);
        Assert.assertEquals(expectedLocations, ((LocationsCliArguments) cliArguments).getLocations());
    }

    @Test
    public void parse_repoLocationsAndViewWithArgs_returnsLocationsCliArguments()
            throws ParseException, HelpScreenException {
        String input = new InputBuilder().addRepos(TEST_REPO_REPOSENSE, TEST_REPO_DELTA)
                .addView(OUTPUT_DIRECTORY_ABSOLUTE)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assert.assertTrue(cliArguments instanceof LocationsCliArguments);
        Assert.assertTrue(cliArguments.isAutomaticallyLaunching());
        List<String> expectedLocations = Arrays.asList(TEST_REPO_REPOSENSE, TEST_REPO_DELTA);
        Assert.assertEquals(expectedLocations, ((LocationsCliArguments) cliArguments).getLocations());
    }

    @Test
    public void parse_viewWithArgCwd_returnsViewCliArguments() throws ParseException, HelpScreenException {
        String input = new InputBuilder().addView(new File(".").toPath()).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assert.assertTrue(cliArguments instanceof ViewCliArguments);
    }

    @Test
    public void parse_configOrLocationsSimilar_success() throws ParseException, IOException, HelpScreenException {
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

    @Test
    public void emptyArgs_defaultConfigFolderPath() throws ParseException, HelpScreenException {
        CliArguments cliArguments = ArgsParser.parse(new String[]{});
        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertEquals(PROJECT_DIRECTORY.toString(), (
                (ConfigCliArguments) cliArguments).getConfigFolderPath().toString());
    }

    @Test
    public void parse_repoAliases_sameResult() throws ParseException, HelpScreenException {
        String input = new InputBuilder().addRepos(TEST_REPO_BETA).build();
        CliArguments repoAliasCliArguments = ArgsParser.parse(translateCommandline(input));

        input = new InputBuilder().add(String.format("--repos %s", TEST_REPO_BETA)).build();
        CliArguments reposAliasCliArguments = ArgsParser.parse(translateCommandline(input));

        Assert.assertEquals(repoAliasCliArguments, reposAliasCliArguments);
    }

    @Test
    public void parse_invalidRepoLocation_emptyRepoConfigurationList()
            throws ParseException, HelpScreenException {
        String input = new InputBuilder().addRepos("https://githubaaaa.com/asdasdasdasd/RepoSense").build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof LocationsCliArguments);
        List<RepoConfiguration> repoConfigs = RepoSense.getRepoConfigurations((LocationsCliArguments) cliArguments);
        Assert.assertTrue(repoConfigs.isEmpty());
    }

    @Test(expected = ParseException.class)
    public void absoluteConfigFolder_withoutRequiredConfigFiles_throwsParseException()
            throws ParseException, HelpScreenException {
        Path absDirectory = PROJECT_DIRECTORY.getParent().toAbsolutePath();
        String input = new InputBuilder().addConfig(absDirectory).build();
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void relativeConfigFolder_withoutRequiredConfigFiles_throwsParseException()
            throws ParseException, HelpScreenException {
        Path relDirectory = PROJECT_DIRECTORY.getParent();
        String input = new InputBuilder().addConfig(relDirectory).build();
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_notExistsConfigFolder_throwsParseException() throws ParseException, HelpScreenException {
        Path absConfigFolder = PROJECT_DIRECTORY.resolve("non_existing_random_folder");
        String input = new InputBuilder().addConfig(absConfigFolder).build();
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_configCsvFileAsConfigFolder_throwsParseException() throws ParseException, HelpScreenException {
        String input = new InputBuilder().addConfig(REPO_CONFIG_CSV_FILE).build();
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_missingConfigValue_throwsParseException() throws ParseException, HelpScreenException {
        String input = new InputBuilder().addConfig(new File("").toPath()).build();
        ArgsParser.parse(translateCommandline(input));
    }

    @Test
    public void outputPath_nonExistentDirectory_success() throws ParseException, IOException, HelpScreenException {
        Path nonExistentDirectory = PROJECT_DIRECTORY.resolve("some_non_existent_dir/");
        Path expectedRelativeOutputDirectoryPath = nonExistentDirectory.resolve(ArgsParser.DEFAULT_REPORT_NAME);
        String input = new InputBuilder().addOutput(nonExistentDirectory).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertTrue(Files.isSameFile(
                expectedRelativeOutputDirectoryPath, cliArguments.getOutputFilePath()));
    }

    @Test(expected = ParseException.class)
    public void sinceDate_unsupportedFormats_throwsParseException() throws ParseException, HelpScreenException {
        String input = DEFAULT_INPUT_BUILDER.addSinceDate("01 July 17").build();
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void untilDate_unsupportedFormats_throwsParseException() throws ParseException, HelpScreenException {
        String input = DEFAULT_INPUT_BUILDER.addUntilDate("11/31/2017").build();
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void sinceDate_laterThanUntilDate_throwsParseException() throws ParseException, HelpScreenException {
        String input = DEFAULT_INPUT_BUILDER.addSinceDate("01/12/2017")
                .addUntilDate("30/11/2017")
                .build();
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void formats_notInAlphanumeric_throwsParseException() throws ParseException, HelpScreenException {
        String input = DEFAULT_INPUT_BUILDER.addFormats(".java").build();
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_mutuallyExclusiveArgumentsConfigAndReposTogether_throwsParseException()
            throws ParseException, HelpScreenException {
        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE)
                .addRepos(TEST_REPO_REPOSENSE)
                .build();
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_extraArgumentForIgnore_throwsParseException() throws ParseException, HelpScreenException {
        String input = DEFAULT_INPUT_BUILDER.addIgnoreStandaloneConfig().add("true").build();
        ArgsParser.parse(translateCommandline(input));
    }

    @Test
    public void parse_withTimezone_success() throws ParseException, HelpScreenException {
        String zoneId = "UTC+11";
        String input = DEFAULT_INPUT_BUILDER.addTimezone(zoneId).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertEquals(ZoneId.of(zoneId), cliArguments.getZoneId());

        zoneId = "UTC-1030";
        input = DEFAULT_INPUT_BUILDER.addTimezone(zoneId).build();
        cliArguments = ArgsParser.parse(translateCommandline(input));

        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertEquals(ZoneId.of(zoneId), cliArguments.getZoneId());

        input = DEFAULT_INPUT_BUILDER.addTimezone("UTC+00").build();
        cliArguments = ArgsParser.parse(translateCommandline(input));

        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertEquals(ZoneId.of("UTC"), cliArguments.getZoneId());
    }

    @Test(expected = ParseException.class)
    public void parse_incorrectTimezone_throwsParseException() throws ParseException, HelpScreenException {
        String input = DEFAULT_INPUT_BUILDER.addTimezone("UTC+").build();
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_timezoneWithoutArgument_throwsParseException() throws ParseException, HelpScreenException {
        String input = DEFAULT_INPUT_BUILDER.addTimezone("").build();
        ArgsParser.parse(translateCommandline(input));
    }
}
