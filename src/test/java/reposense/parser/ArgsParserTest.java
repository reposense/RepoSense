package reposense.parser;

import static org.apache.tools.ant.types.Commandline.translateCommandline;
import static reposense.util.TestUtil.loadResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.argparse4j.helper.HelpScreenException;
import reposense.model.CliArguments;
import reposense.model.FileType;
import reposense.model.FileTypeTest;
import reposense.model.RepoConfiguration;
import reposense.model.RunConfigurationDecider;
import reposense.util.FileUtil;
import reposense.util.InputBuilder;
import reposense.util.TestUtil;
import reposense.util.TimeUtil;

public class ArgsParserTest {

    private static final Path PROJECT_DIRECTORY = Paths.get(System.getProperty("user.dir"));
    private static final Path CONFIG_DIRECTORY = Paths.get(System.getProperty("user.dir")
            + File.separator + "config" + File.separator);
    private static final Path CONFIG_FOLDER_ABSOLUTE = loadResource(ArgsParserTest.class, "cli_location_test");
    private static final Path OUTPUT_DIRECTORY_ABSOLUTE = loadResource(ArgsParserTest.class, "output");
    private static final Path CONFIG_FOLDER_RELATIVE = PROJECT_DIRECTORY.relativize(CONFIG_FOLDER_ABSOLUTE);
    private static final Path OUTPUT_DIRECTORY_RELATIVE = PROJECT_DIRECTORY.relativize(OUTPUT_DIRECTORY_ABSOLUTE);
    private static final Path REPO_CONFIG_CSV_FILE =
            CONFIG_FOLDER_ABSOLUTE.resolve(RepoConfigCsvParser.REPO_CONFIG_FILENAME);
    private static final Path AUTHOR_CONFIG_CSV_FILE =
            CONFIG_FOLDER_ABSOLUTE.resolve(AuthorConfigCsvParser.AUTHOR_CONFIG_FILENAME);
    private static final String NONEXISTENT_DIRECTORY = "some_non_existent_dir/";

    private static final InputBuilder DEFAULT_INPUT_BUILDER = new InputBuilder();

    private static final String TEST_REPO_REPOSENSE = "https://github.com/reposense/RepoSense.git";
    private static final String TEST_REPO_BETA = "https://github.com/reposense/testrepo-Beta.git";
    private static final String TEST_REPO_CHARLIE = "https://github.com/reposense/testrepo-Charlie.git";
    private static final String TEST_REPO_DELTA = "https://github.com/reposense/testrepo-Delta.git";

    private static final String DEFAULT_TIME_ZONE_STRING = "Asia/Singapore";
    private static final ZoneId DEFAULT_TIME_ZONE_ID = TestUtil.getZoneId(DEFAULT_TIME_ZONE_STRING);

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
    public void parse_d1CorrectTimeZone_success() throws Exception {
        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE)
                .addSinceDate(SinceDateArgumentType.FIRST_COMMIT_DATE_SHORTHAND)
                .addUntilDate("30/11/2017")
                .addTimezone(DEFAULT_TIME_ZONE_STRING)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assertions.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, cliArguments.getRepoConfigFilePath()));
        Assertions.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, cliArguments.getAuthorConfigFilePath()));

        LocalDateTime expectedSinceDate = TimeUtil.getArbitraryFirstCommitDateConverted(DEFAULT_TIME_ZONE_ID);
        LocalDateTime expectedUntilDate = TestUtil.getUntilDate(2017, Month.NOVEMBER.getValue(), 30);
        Assertions.assertEquals(expectedSinceDate, cliArguments.getSinceDate());
        Assertions.assertEquals(expectedUntilDate, cliArguments.getUntilDate());

        Assertions.assertEquals(DEFAULT_TIME_ZONE_ID, cliArguments.getZoneId());
    }

    @Test
    public void parse_allCorrectInputs_success() throws Exception {
        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE)
                .addOutput(OUTPUT_DIRECTORY_ABSOLUTE)
                .addSinceDate("01/07/2017")
                .addUntilDate("30/11/2017")
                .addFormats("java adoc html css js")
                .addIgnoreStandaloneConfig()
                .addIgnoreFilesizeLimit()
                .addView()
                .addTimezone(DEFAULT_TIME_ZONE_STRING)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assertions.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, cliArguments.getRepoConfigFilePath()));
        Assertions.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, cliArguments.getAuthorConfigFilePath()));
        Assertions.assertTrue(Files.isSameFile(
                OUTPUT_DIRECTORY_ABSOLUTE.resolve(ArgsParser.DEFAULT_REPORT_NAME), cliArguments.getOutputFilePath()));

        LocalDateTime expectedSinceDate = TestUtil.getSinceDate(2017, Month.JULY.getValue(), 1);
        LocalDateTime expectedUntilDate = TestUtil.getUntilDate(2017, Month.NOVEMBER.getValue(), 30);
        Assertions.assertEquals(expectedSinceDate, cliArguments.getSinceDate());
        Assertions.assertEquals(expectedUntilDate, cliArguments.getUntilDate());

        List<FileType> expectedFormats = FileType.convertFormatStringsToFileTypes(
                Arrays.asList("java", "adoc", "html", "css", "js"));
        Assertions.assertEquals(expectedFormats, cliArguments.getFormats());

        Assertions.assertTrue(cliArguments.isAutomaticallyLaunching());
        Assertions.assertTrue(cliArguments.isFileSizeLimitIgnored());

        Assertions.assertEquals(DEFAULT_TIME_ZONE_ID, cliArguments.getZoneId());
    }

    @Test
    public void parse_help_throwsHelpScreenException() {
        String input = "--help";
        Assertions.assertThrows(HelpScreenException.class, () -> ArgsParser.parse(translateCommandline(input)));
    }

    @Test
    public void parse_allCorrectInputsAlias_success() throws Exception {
        String input = String.format(
                "-c \"%s\" -o \"%s\" -s 01/07/2017 -u 30/11/2017 -f java adoc html css js -i -I -v -t %s",
                CONFIG_FOLDER_ABSOLUTE, OUTPUT_DIRECTORY_ABSOLUTE, DEFAULT_TIME_ZONE_STRING);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assertions.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, cliArguments.getRepoConfigFilePath()));
        Assertions.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, cliArguments.getAuthorConfigFilePath()));
        Assertions.assertTrue(Files.isSameFile(
                OUTPUT_DIRECTORY_ABSOLUTE.resolve(ArgsParser.DEFAULT_REPORT_NAME), cliArguments.getOutputFilePath()));

        LocalDateTime expectedSinceDate = TestUtil.getSinceDate(2017, Month.JULY.getValue(), 1);
        LocalDateTime expectedUntilDate = TestUtil.getUntilDate(2017, Month.NOVEMBER.getValue(), 30);
        Assertions.assertEquals(expectedSinceDate, cliArguments.getSinceDate());
        Assertions.assertEquals(expectedUntilDate, cliArguments.getUntilDate());

        List<FileType> expectedFormats = FileType.convertFormatStringsToFileTypes(
                Arrays.asList("java", "adoc", "html", "css", "js"));
        Assertions.assertEquals(expectedFormats, cliArguments.getFormats());

        Assertions.assertTrue(cliArguments.isAutomaticallyLaunching());
        Assertions.assertTrue(cliArguments.isFileSizeLimitIgnored());

        Assertions.assertEquals(DEFAULT_TIME_ZONE_ID, cliArguments.getZoneId());
    }

    @Test
    public void parse_withExtraWhitespaces_success() throws Exception {
        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE).addWhiteSpace(5)
                .addOutput(OUTPUT_DIRECTORY_ABSOLUTE).addWhiteSpace(4)
                .addSinceDate("01/07/2017").addWhiteSpace(3)
                .addUntilDate("30/11/2017").addWhiteSpace(6)
                .addFormats("java   adoc  html      css js   ")
                .addIgnoreStandaloneConfig().addWhiteSpace(1)
                .addIgnoreFilesizeLimit().addWhiteSpace(2)
                .addView().addWhiteSpace(4)
                .addTimezone(DEFAULT_TIME_ZONE_STRING).addWhiteSpace(5)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assertions.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, cliArguments.getRepoConfigFilePath()));
        Assertions.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, cliArguments.getAuthorConfigFilePath()));
        Assertions.assertTrue(Files.isSameFile(
                OUTPUT_DIRECTORY_ABSOLUTE.resolve(ArgsParser.DEFAULT_REPORT_NAME), cliArguments.getOutputFilePath()));

        LocalDateTime expectedSinceDate = TestUtil.getSinceDate(2017, Month.JULY.getValue(), 1);
        LocalDateTime expectedUntilDate = TestUtil.getUntilDate(2017, Month.NOVEMBER.getValue(), 30);
        Assertions.assertEquals(expectedSinceDate, cliArguments.getSinceDate());
        Assertions.assertEquals(expectedUntilDate, cliArguments.getUntilDate());

        List<FileType> expectedFormats = FileType.convertFormatStringsToFileTypes(Arrays.asList(
                "java", "adoc", "html", "css", "js"));
        Assertions.assertEquals(expectedFormats, cliArguments.getFormats());

        Assertions.assertTrue(cliArguments.isAutomaticallyLaunching());
        Assertions.assertTrue(cliArguments.isFileSizeLimitIgnored());

        Assertions.assertEquals(DEFAULT_TIME_ZONE_ID, cliArguments.getZoneId());
    }

    @Test
    public void parse_configFolderOnly_success() throws Exception {
        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assertions.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, cliArguments.getRepoConfigFilePath()));
        Assertions.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, cliArguments.getAuthorConfigFilePath()));
        // Optional arguments have default values
        assertDateDiffOneMonth(cliArguments.getSinceDate(), cliArguments.getUntilDate());
        assertDateDiffEndOfDay(cliArguments.getUntilDate());
        Assertions.assertEquals(ArgsParser.DEFAULT_REPORT_NAME,
                cliArguments.getOutputFilePath().getFileName().toString());
        Assertions.assertEquals(FileTypeTest.NO_SPECIFIED_FORMATS, cliArguments.getFormats());
        Assertions.assertFalse(cliArguments.isAutomaticallyLaunching());

        input = new InputBuilder().addConfig(CONFIG_FOLDER_RELATIVE).build();
        cliArguments = ArgsParser.parse(translateCommandline(input));

        Assertions.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, cliArguments.getRepoConfigFilePath()));
        Assertions.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, cliArguments.getAuthorConfigFilePath()));
        // Optional arguments have default values
        assertDateDiffOneMonth(cliArguments.getSinceDate(), cliArguments.getUntilDate());
        assertDateDiffEndOfDay(cliArguments.getUntilDate());
        Assertions.assertEquals(ArgsParser.DEFAULT_REPORT_NAME,
                cliArguments.getOutputFilePath().getFileName().toString());
        Assertions.assertEquals(FileTypeTest.NO_SPECIFIED_FORMATS, cliArguments.getFormats());
        Assertions.assertFalse(cliArguments.isAutomaticallyLaunching());
        Assertions.assertEquals(ZoneId.systemDefault(), cliArguments.getZoneId());
    }

    @Test
    public void parse_viewOnly_success() throws Exception {
        String input = new InputBuilder().addView(OUTPUT_DIRECTORY_ABSOLUTE).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assertions.assertTrue(Files.isSameFile(
                OUTPUT_DIRECTORY_ABSOLUTE, cliArguments.getReportDirectoryPath()));
    }

    @Test
    public void parse_withIgnore_success() throws Exception {
        String input = new InputBuilder().addRepos(TEST_REPO_REPOSENSE, TEST_REPO_DELTA)
                .addIgnoreStandaloneConfig()
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        String inputWithAlias = new InputBuilder().addRepos(TEST_REPO_REPOSENSE, TEST_REPO_DELTA)
                .add("-i")
                .build();
        CliArguments cliArgumentsWithAlias = ArgsParser.parse(translateCommandline(inputWithAlias));

        Assertions.assertTrue(cliArguments.isStandaloneConfigIgnored());
        Assertions.assertTrue(cliArgumentsWithAlias.isStandaloneConfigIgnored());

        Assertions.assertTrue(cliArguments.equals(cliArgumentsWithAlias));

        Assertions.assertEquals(cliArguments, cliArgumentsWithAlias);
    }

    @Test
    public void parse_withoutIgnore_success() throws Exception {
        String input = new InputBuilder().addRepos(TEST_REPO_REPOSENSE, TEST_REPO_DELTA).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assertions.assertFalse(cliArguments.isStandaloneConfigIgnored());
    }

    @Test
    public void parse_viewOnlyWithoutArgs_returnsConfigCliArguments() throws Exception {
        String input = new InputBuilder().addView().build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assertions.assertEquals(CONFIG_DIRECTORY.toString(), (
                cliArguments).getConfigFolderPath().toString());
        Assertions.assertTrue(cliArguments.isAutomaticallyLaunching());
    }

    @Test
    public void parse_configFolderAndOutputDirectory_success() throws Exception {
        Path expectedRelativeOutputDirectoryPath = OUTPUT_DIRECTORY_RELATIVE.resolve(ArgsParser.DEFAULT_REPORT_NAME);
        Path expectedAbsoluteOutputDirectoryPath = OUTPUT_DIRECTORY_ABSOLUTE.resolve(ArgsParser.DEFAULT_REPORT_NAME);

        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE)
                .addOutput(OUTPUT_DIRECTORY_RELATIVE)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assertions.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, cliArguments.getRepoConfigFilePath()));
        Assertions.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, cliArguments.getAuthorConfigFilePath()));
        Assertions.assertTrue(Files.isSameFile(expectedRelativeOutputDirectoryPath, cliArguments.getOutputFilePath()));

        input = new InputBuilder().addConfig(CONFIG_FOLDER_RELATIVE)
                .addOutput(OUTPUT_DIRECTORY_ABSOLUTE)
                .build();
        cliArguments = ArgsParser.parse(translateCommandline(input));
        Assertions.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, cliArguments.getRepoConfigFilePath()));
        Assertions.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, cliArguments.getAuthorConfigFilePath()));
        Assertions.assertTrue(Files.isSameFile(expectedAbsoluteOutputDirectoryPath, cliArguments.getOutputFilePath()));
    }

    @Test
    public void parse_configFolderAndViewWithouthArgs_returnsConfigCliArguments() throws Exception {
        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE)
                .addView()
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assertions.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, cliArguments.getRepoConfigFilePath()));
        Assertions.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, cliArguments.getAuthorConfigFilePath()));
        Assertions.assertTrue(cliArguments.isAutomaticallyLaunching());

        input = new InputBuilder().addConfig(CONFIG_FOLDER_RELATIVE)
                .addView()
                .build();
        cliArguments = ArgsParser.parse(translateCommandline(input));

        Assertions.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, cliArguments.getRepoConfigFilePath()));
        Assertions.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, cliArguments.getAuthorConfigFilePath()));
        Assertions.assertTrue(cliArguments.isAutomaticallyLaunching());
    }

    @Test
    public void parse_configFolderAndViewWithArgs_returnsConfigCliArguments() throws Exception {
        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE)
                .addView(OUTPUT_DIRECTORY_ABSOLUTE)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assertions.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, cliArguments.getRepoConfigFilePath()));
        Assertions.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, cliArguments.getAuthorConfigFilePath()));
        Assertions.assertTrue(cliArguments.isAutomaticallyLaunching());
    }

    @Test
    public void sinceDate_correctFormat_success() throws Exception {
        String input = DEFAULT_INPUT_BUILDER
                .addSinceDate("01/07/2017")
                .addTimezone(DEFAULT_TIME_ZONE_STRING)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        LocalDateTime expectedSinceDate = TestUtil.getSinceDate(2017, Month.JULY.getValue(), 1);
        Assertions.assertEquals(expectedSinceDate, cliArguments.getSinceDate());
    }

    @Test
    public void untilDate_correctFormat_success() throws Exception {
        String input = DEFAULT_INPUT_BUILDER
                .addUntilDate("30/11/2017")
                .addTimezone(DEFAULT_TIME_ZONE_STRING)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        LocalDateTime expectedUntilDate = TestUtil.getUntilDate(2017, Month.NOVEMBER.getValue(), 30);
        Assertions.assertEquals(expectedUntilDate, cliArguments.getUntilDate());
    }

    @Test
    public void sinceDate_withExtraDate_success() throws Exception {
        String input = DEFAULT_INPUT_BUILDER
                .addSinceDate("\"01/07/2017 01/07/2018\"")
                .addTimezone(DEFAULT_TIME_ZONE_STRING)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        LocalDateTime expectedSinceDate = TestUtil.getSinceDate(2017, Month.JULY.getValue(), 1);
        Assertions.assertEquals(expectedSinceDate, cliArguments.getSinceDate());
    }

    @Test
    public void untilDate_withExtraTime_success() throws Exception {
        String input = DEFAULT_INPUT_BUILDER
                .addUntilDate("\"30/11/2017 10:10:10\"")
                .addTimezone(DEFAULT_TIME_ZONE_STRING)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        LocalDateTime expectedUntilDate = TestUtil.getUntilDate(2017, Month.NOVEMBER.getValue(), 30);
        Assertions.assertEquals(expectedUntilDate, cliArguments.getUntilDate());
    }

    @Test
    public void period_inDaysWithSinceDate_success() throws Exception {
        String input = DEFAULT_INPUT_BUILDER
                .addSinceDate("01/07/2017")
                .addPeriod("2d")
                .addTimezone(DEFAULT_TIME_ZONE_STRING)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        LocalDateTime expectedUntilDate = TestUtil.getUntilDate(2017, Month.JULY.getValue(), 3);
        Assertions.assertEquals(expectedUntilDate, cliArguments.getUntilDate());
    }

    @Test
    public void period_inWeeksWithUntilDate_success() throws Exception {
        String input = DEFAULT_INPUT_BUILDER
                .addUntilDate("14/07/2017")
                .addTimezone(DEFAULT_TIME_ZONE_STRING)
                .addPeriod("2w")
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        LocalDateTime expectedSinceDate = TestUtil.getSinceDate(2017, Month.JUNE.getValue(), 30);
        Assertions.assertEquals(expectedSinceDate, cliArguments.getSinceDate());
    }

    @Test
    public void formats_inAlphanumeric_success() throws Exception {
        String input = DEFAULT_INPUT_BUILDER.addFormats("java js css 7z").build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<FileType> expectedFormats = FileType.convertFormatStringsToFileTypes(
                Arrays.asList("java", "js", "css", "7z"));
        Assertions.assertEquals(expectedFormats, cliArguments.getFormats());
    }

    @Test
    public void numCloningThreads_default_success() throws Exception {
        String input = DEFAULT_INPUT_BUILDER.build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        int expectedNumThreads = ArgsParser.DEFAULT_NUM_CLONING_THREADS;
        Assertions.assertEquals(expectedNumThreads, cliArguments.getNumCloningThreads());
    }

    @Test
    public void numCloningThreads_isNumeric_success() throws Exception {
        String input = DEFAULT_INPUT_BUILDER
                .addNumCloningThreads(2)
                .build();
        System.out.println(input);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        int expectedNumThreads = 2;
        Assertions.assertEquals(expectedNumThreads, cliArguments.getNumCloningThreads());
    }

    @Test
    public void numAnalysisThreads_default_success() throws Exception {
        String input = DEFAULT_INPUT_BUILDER.build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        int expectedNumThreads = ArgsParser.DEFAULT_NUM_ANALYSIS_THREADS;
        Assertions.assertEquals(expectedNumThreads, cliArguments.getNumAnalysisThreads());
    }

    @Test
    public void numAnalysisThreads_isNumeric_success() throws Exception {
        String input = DEFAULT_INPUT_BUILDER
                .addNumAnalysisThreads(2)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        int expectedNumThreads = 2;
        Assertions.assertEquals(expectedNumThreads, cliArguments.getNumAnalysisThreads());
    }

    @Test
    public void parse_validGitRepoLocations_repoConfigurationListCorrectSize() throws Exception {
        String input = new InputBuilder().addRepos(TEST_REPO_REPOSENSE, TEST_REPO_DELTA).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        List<RepoConfiguration> repoConfigs = RunConfigurationDecider
                .getRunConfiguration(cliArguments)
                .getRepoConfigurations();
        Assertions.assertEquals(2, repoConfigs.size());
    }

    @Test
    public void parse_repoLocationsAndView_returnsLocationCliArguments() throws Exception {
        String input = new InputBuilder().addRepos(TEST_REPO_REPOSENSE, TEST_REPO_DELTA)
                .addView()
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assertions.assertTrue(cliArguments.isAutomaticallyLaunching());
        List<String> expectedLocations = Arrays.asList(TEST_REPO_REPOSENSE, TEST_REPO_DELTA);
        Assertions.assertEquals(expectedLocations, cliArguments.getLocations());
    }

    @Test
    public void parse_repoLocationsOnly_success() throws Exception {
        String input = new InputBuilder().addRepos(TEST_REPO_REPOSENSE, TEST_REPO_DELTA).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assertions.assertFalse(cliArguments.isAutomaticallyLaunching());
        List<String> expectedLocations = Arrays.asList(TEST_REPO_REPOSENSE, TEST_REPO_DELTA);
        Assertions.assertEquals(expectedLocations, cliArguments.getLocations());
    }

    @Test
    public void parse_repoLocationsAndViewWithArgs_returnsLocationsCliArguments() throws Exception {
        String input = new InputBuilder().addRepos(TEST_REPO_REPOSENSE, TEST_REPO_DELTA)
                .addView(OUTPUT_DIRECTORY_ABSOLUTE)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assertions.assertTrue(cliArguments.isAutomaticallyLaunching());
        List<String> expectedLocations = Arrays.asList(TEST_REPO_REPOSENSE, TEST_REPO_DELTA);
        Assertions.assertEquals(expectedLocations, cliArguments.getLocations());
    }

    @Test
    public void parse_configOrLocationsSimilar_success() throws Exception {
        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE).build();
        CliArguments configCliArguments = ArgsParser.parse(translateCommandline(input));
        List<RepoConfiguration> actualRepoConfigs = RunConfigurationDecider
                .getRunConfiguration(configCliArguments)
                .getRepoConfigurations();

        input = new InputBuilder().addRepos(TEST_REPO_BETA, TEST_REPO_CHARLIE, TEST_REPO_DELTA).build();
        CliArguments locationCliArguments = ArgsParser.parse(translateCommandline(input));
        List<RepoConfiguration> expectedRepoConfigs = RunConfigurationDecider
                .getRunConfiguration(locationCliArguments)
                .getRepoConfigurations();

        Assertions.assertEquals(actualRepoConfigs, expectedRepoConfigs);
    }

    @Test
    public void emptyArgs_defaultConfigFolderPath() throws Exception {
        CliArguments cliArguments = ArgsParser.parse(new String[]{});

        Assertions.assertEquals(CONFIG_DIRECTORY.toString(), cliArguments.getConfigFolderPath().toString());
    }

    @Test
    public void parse_repoAliases_sameResult() throws Exception {
        String input = new InputBuilder().addRepos(TEST_REPO_BETA).build();
        CliArguments repoAliasCliArguments = ArgsParser.parse(translateCommandline(input));

        input = new InputBuilder().add(String.format("--repos %s", TEST_REPO_BETA)).build();
        CliArguments reposAliasCliArguments = ArgsParser.parse(translateCommandline(input));

        Assertions.assertEquals(repoAliasCliArguments, reposAliasCliArguments);
    }

    @Test
    public void absoluteConfigFolder_withoutRequiredConfigFiles_throwsParseException() {
        Path absDirectory = PROJECT_DIRECTORY.getParent().toAbsolutePath();
        String input = new InputBuilder().addConfig(absDirectory).build();
        Assertions.assertThrows(ParseException.class, () -> ArgsParser.parse(translateCommandline(input)));
    }

    @Test
    public void relativeConfigFolder_withoutRequiredConfigFiles_throwsParseException() {
        Path relDirectory = PROJECT_DIRECTORY.getParent();
        String input = new InputBuilder().addConfig(relDirectory).build();
        Assertions.assertThrows(ParseException.class, () -> ArgsParser.parse(translateCommandline(input)));
    }

    @Test
    public void parse_notExistsConfigFolder_throwsParseException() {
        Path absConfigFolder = PROJECT_DIRECTORY.resolve("non_existing_random_folder");
        String input = new InputBuilder().addConfig(absConfigFolder).build();
        Assertions.assertThrows(ParseException.class, () -> ArgsParser.parse(translateCommandline(input)));
    }

    @Test
    public void parse_configCsvFileAsConfigFolder_throwsParseException() {
        String input = new InputBuilder().addConfig(REPO_CONFIG_CSV_FILE).build();
        Assertions.assertThrows(ParseException.class, () -> ArgsParser.parse(translateCommandline(input)));
    }

    @Test
    public void parse_missingConfigValue_throwsParseException() {
        String input = new InputBuilder().addConfig(new File("").toPath()).build();
        Assertions.assertThrows(ParseException.class, () -> ArgsParser.parse(translateCommandline(input)));
    }

    @Test
    public void outputPath_nonExistentDirectory_success() throws Exception {
        Path nonExistentDirectory = PROJECT_DIRECTORY.resolve(NONEXISTENT_DIRECTORY);
        Path expectedRelativeOutputDirectoryPath = nonExistentDirectory.resolve(ArgsParser.DEFAULT_REPORT_NAME);
        String input = new InputBuilder().addOutput(nonExistentDirectory).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assertions.assertTrue(Files.isSameFile(
                expectedRelativeOutputDirectoryPath, cliArguments.getOutputFilePath()));
    }

    @Test
    public void sinceDate_unsupportedFormats_throwsParseException() {
        String input = DEFAULT_INPUT_BUILDER.addSinceDate("01 July 17").build();
        Assertions.assertThrows(ParseException.class, () -> ArgsParser.parse(translateCommandline(input)));
    }

    @Test
    public void untilDate_unsupportedFormats_throwsParseException() {
        String input = DEFAULT_INPUT_BUILDER.addUntilDate("11/31/2017").build();
        Assertions.assertThrows(ParseException.class, () -> ArgsParser.parse(translateCommandline(input)));
    }

    @Test
    public void sinceDate_laterThanUntilDate_throwsParseException() {
        String input = DEFAULT_INPUT_BUILDER.addSinceDate("01/12/2017")
                .addUntilDate("30/11/2017")
                .build();
        Assertions.assertThrows(ParseException.class, () -> ArgsParser.parse(translateCommandline(input)));
    }

    @Test
    public void sinceDate_laterThanCurrentDate_throwsParseException() {
        LocalDateTime tomorrowDateTime = LocalDateTime.now()
                .plusDays(1L);
        LocalDateTime dayAfterDateTime = LocalDateTime.now()
                .plusDays(2L);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String tomorrow = tomorrowDateTime.format(formatter);
        String dayAfter = dayAfterDateTime.format(formatter);

        String input = DEFAULT_INPUT_BUILDER.addSinceDate(tomorrow)
                .addUntilDate(dayAfter)
                .build();
        Assertions.assertThrows(ParseException.class, () -> ArgsParser.parse(translateCommandline(input)));
    }

    @Test
    public void period_withBothSinceDateAndUntilDate_throwsParseException() {
        String input = DEFAULT_INPUT_BUILDER.addPeriod("18d")
                .addSinceDate("30/11/2017")
                .addUntilDate("01/12/2017")
                .build();
        Assertions.assertThrows(ParseException.class, () -> ArgsParser.parse(translateCommandline(input)));
    }

    @Test
    public void period_notNumeric_throwsParseExcpetion() {
        String input = DEFAULT_INPUT_BUILDER.addPeriod("abcd").build();
        Assertions.assertThrows(ParseException.class, () -> ArgsParser.parse(translateCommandline(input)));
    }

    @Test
    public void period_isZero_throwsParseExcpetion() {
        String input = DEFAULT_INPUT_BUILDER.addPeriod("0w").build();
        Assertions.assertThrows(ParseException.class, () -> ArgsParser.parse(translateCommandline(input)));
    }

    @Test
    public void formats_notInAlphanumeric_throwsParseException() {
        String input = DEFAULT_INPUT_BUILDER.addFormats(".java").build();
        Assertions.assertThrows(ParseException.class, () -> ArgsParser.parse(translateCommandline(input)));
    }

    @Test
    public void parse_mutuallyExclusiveArgumentsConfigAndReposTogether_throwsParseException() {
        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE)
                .addRepos(TEST_REPO_REPOSENSE)
                .build();
        Assertions.assertThrows(ParseException.class, () -> ArgsParser.parse(translateCommandline(input)));
    }

    @Test
    public void parse_extraArgumentForIgnore_throwsParseException() {
        String input = DEFAULT_INPUT_BUILDER.addIgnoreStandaloneConfig().add("true").build();
        Assertions.assertThrows(ParseException.class, () -> ArgsParser.parse(translateCommandline(input)));
    }

    @Test
    public void parse_withTimezone_success() throws Exception {
        String zoneId = "UTC+11";
        String input = DEFAULT_INPUT_BUILDER.addTimezone(zoneId).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Assertions.assertEquals(ZoneId.of(zoneId), cliArguments.getZoneId());

        zoneId = "UTC-1030";
        input = DEFAULT_INPUT_BUILDER.addTimezone(zoneId).build();
        cliArguments = ArgsParser.parse(translateCommandline(input));

        Assertions.assertEquals(ZoneId.of(zoneId), cliArguments.getZoneId());

        zoneId = "UTC";
        input = DEFAULT_INPUT_BUILDER.addTimezone(zoneId).build();
        cliArguments = ArgsParser.parse(translateCommandline(input));

        Assertions.assertEquals(ZoneId.of(zoneId), cliArguments.getZoneId());
    }

    @Test
    public void parse_incorrectTimezone_throwsParseException() {
        String input = DEFAULT_INPUT_BUILDER.addTimezone("UTC+").build();
        Assertions.assertThrows(ParseException.class, () -> ArgsParser.parse(translateCommandline(input)));;
    }

    @Test
    public void parse_timezoneWithoutArgument_throwsParseException() {
        String input = DEFAULT_INPUT_BUILDER.addTimezone("").build();
        Assertions.assertThrows(ParseException.class, () -> ArgsParser.parse(translateCommandline(input)));
    }

    @Test
    public void parse_shallowCloning_success() throws Exception {
        String input = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE)
                .addOutput(OUTPUT_DIRECTORY_ABSOLUTE)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assertions.assertEquals(false, cliArguments.isShallowCloningPerformed());

        String inputShallow = new InputBuilder().addConfig(CONFIG_FOLDER_ABSOLUTE)
                .addOutput(OUTPUT_DIRECTORY_ABSOLUTE)
                .addShallowCloning()
                .build();
        CliArguments cliArgumentsShallow = ArgsParser.parse(translateCommandline(inputShallow));
        Assertions.assertEquals(true, cliArgumentsShallow.isShallowCloningPerformed());
    }

    /**
     * Ensures that {@code actualSinceDate} is exactly one month before {@code untilDate}.
     *
     * @throws AssertionError if {@code actualSinceDate} is not one month before {@code untilDate}.
     */
    private void assertDateDiffOneMonth(LocalDateTime actualSinceDate, LocalDateTime untilDate) {
        LocalDateTime oneMonthBeforeUntilDate = untilDate.withHour(0).withMinute(0).withSecond(0).minusMonths(1);
        Assertions.assertTrue(actualSinceDate.equals(oneMonthBeforeUntilDate));
    }

    /**
     * Ensures that {@code actualUntilDate} falls on the date of report generation with time at 23:59:59.
     *
     * @throws AssertionError if {@code actualUntilDate} does not fall on the date of report generation
     * with time at 23:59:59.
     */
    private void assertDateDiffEndOfDay(LocalDateTime actualUntilDate) {

        LocalDateTime currentDate = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(0);
        Assertions.assertTrue(actualUntilDate.equals(currentDate));
    }
}
