package reposense.parser;

import static org.apache.tools.ant.types.Commandline.translateCommandline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import reposense.RepoSense;
import reposense.model.CliArguments;
import reposense.model.ConfigCliArguments;
import reposense.model.LocationsCliArguments;
import reposense.model.RepoConfiguration;
import reposense.model.ViewCliArguments;
import reposense.util.TestUtil;

public class ArgsParserTest {

    private static final Path PROJECT_DIRECTORY = Paths.get(System.getProperty("user.dir"));
    private static final Path CONFIG_FOLDER_ABSOLUTE = new File(ArgsParserTest.class.getClassLoader()
            .getResource("cli_location_test").getFile()).toPath();
    private static final Path OUTPUT_DIRECTORY_ABSOLUTE = new File(ArgsParserTest.class.getClassLoader()
            .getResource("output").getFile()).toPath();
    private static final Path CONFIG_FOLDER_RELATIVE = PROJECT_DIRECTORY.relativize(CONFIG_FOLDER_ABSOLUTE);
    private static final Path OUTPUT_DIRECTORY_RELATIVE = PROJECT_DIRECTORY.relativize(OUTPUT_DIRECTORY_ABSOLUTE);
    private static final String DEFAULT_MANDATORY_ARGS = "-config " + CONFIG_FOLDER_ABSOLUTE + " ";
    private static final Path REPO_CONFIG_CSV_FILE =
            CONFIG_FOLDER_ABSOLUTE.resolve(RepoConfigCsvParser.REPO_CONFIG_FILENAME);
    private static final Path AUTHOR_CONFIG_CSV_FILE =
            CONFIG_FOLDER_ABSOLUTE.resolve(AuthorConfigCsvParser.AUTHOR_CONFIG_FILENAME);

    private static final String TEST_REPO_REPOSENSE = "https://github.com/reposense/RepoSense.git";
    private static final String TEST_REPO_BETA = "https://github.com/reposense/testrepo-Beta.git";
    private static final String TEST_REPO_CHARLIE = "https://github.com/reposense/testrepo-Charlie.git";
    private static final String TEST_REPO_DELTA = "https://github.com/reposense/testrepo-Delta.git";

    @Test
    public void parse_allCorrectInputs_success() throws ParseException, IOException {
        String input = String.format("-config %s -output %s -since 01/07/2017 -until 30/11/2017 "
                + "-formats java adoc html css js",
                CONFIG_FOLDER_ABSOLUTE, OUTPUT_DIRECTORY_ABSOLUTE);
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

        List<String> expectedFormats = Arrays.asList("java", "adoc", "html", "css", "js");
        Assert.assertEquals(expectedFormats, cliArguments.getFormats());
    }

    @Test
    public void parse_withExtraWhitespaces_success() throws ParseException, IOException {
        String input = String.format("-config %s      -output   %s   -since 01/07/2017   -until    30/11/2017   "
                + "-formats     java   adoc     html css js ", CONFIG_FOLDER_ABSOLUTE, OUTPUT_DIRECTORY_ABSOLUTE);
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

        List<String> expectedFormats = Arrays.asList("java", "adoc", "html", "css", "js");
        Assert.assertEquals(expectedFormats, cliArguments.getFormats());
    }

    @Test
    public void parse_configFolderOnly_success() throws ParseException, IOException {
        String input = String.format("-config %s", CONFIG_FOLDER_ABSOLUTE);
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
        Assert.assertEquals(ArgsParser.DEFAULT_FORMATS, cliArguments.getFormats());

        input = String.format("-config %s", CONFIG_FOLDER_RELATIVE);
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
        Assert.assertEquals(ArgsParser.DEFAULT_FORMATS, cliArguments.getFormats());
    }

    @Test
    public void parse_viewOnly_success() throws ParseException, IOException {
        String input = String.format("-view %s", OUTPUT_DIRECTORY_ABSOLUTE);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ViewCliArguments);
        Assert.assertTrue(Files.isSameFile(
                OUTPUT_DIRECTORY_ABSOLUTE, ((ViewCliArguments) cliArguments).getReportDirectoryPath()));
    }

    @Test
    public void parse_configFolderAndOutputDirectory_success() throws ParseException, IOException {
        Path expectedRelativeOutputDirectoryPath = OUTPUT_DIRECTORY_RELATIVE.resolve(ArgsParser.DEFAULT_REPORT_NAME);
        Path expectedAbsoluteOutputDirectoryPath = OUTPUT_DIRECTORY_ABSOLUTE.resolve(ArgsParser.DEFAULT_REPORT_NAME);

        String input = String.format("-config %s -output %s", CONFIG_FOLDER_ABSOLUTE, OUTPUT_DIRECTORY_RELATIVE);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getRepoConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(expectedRelativeOutputDirectoryPath, cliArguments.getOutputFilePath()));

        input = String.format("-config %s -output %s", CONFIG_FOLDER_RELATIVE, OUTPUT_DIRECTORY_ABSOLUTE);
        cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Assert.assertTrue(Files.isSameFile(
                REPO_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getRepoConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(
                AUTHOR_CONFIG_CSV_FILE, ((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(expectedAbsoluteOutputDirectoryPath, cliArguments.getOutputFilePath()));
    }

    @Test
    public void sinceDate_correctFormat_success() throws ParseException {
        String sinceDate = "01/07/2017";
        String input = DEFAULT_MANDATORY_ARGS + String.format("-since %s", sinceDate);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Date expectedSinceDate = TestUtil.getDate(2017, Calendar.JULY, 1);
        Assert.assertEquals(expectedSinceDate, cliArguments.getSinceDate().get());
    }

    @Test
    public void untilDate_correctFormat_success() throws ParseException {
        String untilDate = "30/11/2017";
        String input = DEFAULT_MANDATORY_ARGS + String.format("-until %s", untilDate);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        Date expectedUntilDate = TestUtil.getDate(2017, Calendar.NOVEMBER, 30);
        Assert.assertEquals(expectedUntilDate, cliArguments.getUntilDate().get());
    }

    @Test
    public void formats_inAlphanumeric_success() throws ParseException {
        String formats = "java js css 7z";
        String input = DEFAULT_MANDATORY_ARGS + String.format("-formats %s", formats);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof ConfigCliArguments);
        List<String> expectedFormats = Arrays.asList("java", "js", "css", "7z");
        Assert.assertEquals(expectedFormats, cliArguments.getFormats());
    }

    @Test
    public void parse_validGitRepoLocations_repoConfigurationListCorrectSize() throws ParseException, IOException {
        String input = String.format("-repos \"%s\" %s", TEST_REPO_REPOSENSE, TEST_REPO_DELTA);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof LocationsCliArguments);
        List<RepoConfiguration> repoConfigs = RepoSense.getRepoConfigurations((LocationsCliArguments) cliArguments);
        Assert.assertEquals(2, repoConfigs.size());
    }

    @Test
    public void parse_configOrLocationsSimilar_success() throws ParseException, IOException {
        String input = String.format("-config %s", CONFIG_FOLDER_ABSOLUTE);
        CliArguments configCliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(configCliArguments instanceof ConfigCliArguments);
        List<RepoConfiguration> actualRepoConfigs =
                RepoSense.getRepoConfigurations((ConfigCliArguments) configCliArguments);

        input = String.format("-repos \"%s\" %s %s", TEST_REPO_BETA, TEST_REPO_CHARLIE, TEST_REPO_DELTA);
        CliArguments locationCliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(locationCliArguments instanceof LocationsCliArguments);
        List<RepoConfiguration> expectedRepoConfigs =
                RepoSense.getRepoConfigurations((LocationsCliArguments) locationCliArguments);

        Assert.assertEquals(actualRepoConfigs, expectedRepoConfigs);
    }

    @Test
    public void parse_repoAliases_sameResult() throws ParseException, IOException {
        String input = String.format("-repos %s", TEST_REPO_BETA);
        CliArguments repoAliasCliArguments = ArgsParser.parse(translateCommandline(input));

        input = String.format("-repo %s", TEST_REPO_BETA);
        CliArguments reposAliasCliArguments = ArgsParser.parse(translateCommandline(input));

        Assert.assertEquals(repoAliasCliArguments, reposAliasCliArguments);
    }

    @Test(expected = ParseException.class)
    public void emptyArgs_throwsParseException() throws ParseException {
        ArgsParser.parse(new String[]{});
    }

    @Test
    public void parse_invalidRepoLocation_emptyRepoConfigurationList() throws ParseException, IOException {
        String input = String.format("-repos %s", "https://githubaaaa.com/asdasdasdasd/RepoSense");
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(cliArguments instanceof LocationsCliArguments);
        List<RepoConfiguration> repoConfigs = RepoSense.getRepoConfigurations((LocationsCliArguments) cliArguments);
        Assert.assertTrue(repoConfigs.isEmpty());
    }

    @Test(expected = ParseException.class)
    public void missingMandatoryConfigArg_throwsParseException() throws ParseException {
        String input = String.format("-output %s -since 01/07/2017 -until 30/11/2017", OUTPUT_DIRECTORY_ABSOLUTE);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void absoluteConfigFolder_withoutRequiredConfigFiles_throwsParseException() throws ParseException {
        String absDirectory = PROJECT_DIRECTORY.getParent().toString();
        String input = String.format("-config %s", absDirectory);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void relativeConfigFolder_withoutRequiredConfigFiles_throwsParseException() throws ParseException {
        String relDirectory = PROJECT_DIRECTORY.getParent().toString();
        String input = String.format("-config %s", relDirectory);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_notExistsConfigFolder_throwsParseException() throws ParseException {
        String absConfigFolder = PROJECT_DIRECTORY.resolve("non_existing_random_folder").toString();
        String input = String.format("-config %s", absConfigFolder);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_configCsvFileAsConfigFolder_throwsParseException() throws ParseException {
        String input = String.format("-config %s", REPO_CONFIG_CSV_FILE);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_missingConfigValue_throwsParseException() throws ParseException {
        String input = "-config";
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void file_forOutputDirectory_throwsParseException() throws ParseException {
        String file = PROJECT_DIRECTORY.resolve("parser_test.csv").toString();
        String input = DEFAULT_MANDATORY_ARGS + String.format("-output %s", file);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void nonExistentDirectory_forOutputDirectory_throwsParseException() throws ParseException {
        String nonExistentDirectory = PROJECT_DIRECTORY.resolve("some_non_existent_dir/").toString();
        String input = DEFAULT_MANDATORY_ARGS + String.format("-output %s", nonExistentDirectory);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void sinceDate_unsupportedFormats_throwsParseException() throws ParseException {
        String sinceDate = "01 July 17";
        String input = DEFAULT_MANDATORY_ARGS + String.format("-since %s", sinceDate);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void untilDate_unsupportedFormats_throwsParseException() throws ParseException {
        String untilDate = "11/31/2017";
        String input = DEFAULT_MANDATORY_ARGS + String.format("-until %s", untilDate);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void sinceDate_laterThanUntilDate_throwsParseException() throws ParseException {
        String sinceDate = "01/12/2017";
        String untilDate = "30/11/2017";
        String input = DEFAULT_MANDATORY_ARGS + String.format("-since %s -until %s", sinceDate, untilDate);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void formats_notInAlphanumeric_throwsParseException() throws ParseException {
        String formats = ".java";
        String input = DEFAULT_MANDATORY_ARGS + String.format("-formats %s", formats);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_missingViewValue_throwsParseException() throws ParseException {
        String input = "-view";
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_mutuallyExclusiveArgumentsConfigAndViewTogether_throwsParseException() throws ParseException {
        String input = String.format("-config %s -view %s", CONFIG_FOLDER_ABSOLUTE, OUTPUT_DIRECTORY_ABSOLUTE);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_mutuallyExclusiveArgumentsConfigAndReposTogether_throwsParseException() throws ParseException {
        String input = String.format("-config %s -repos %s", CONFIG_FOLDER_ABSOLUTE, TEST_REPO_REPOSENSE);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_mutuallyExclusiveArgumentsViewAndReposTogether_throwsParseException() throws ParseException {
        String input = String.format("-view %s -repos %s", OUTPUT_DIRECTORY_ABSOLUTE, TEST_REPO_REPOSENSE);
        ArgsParser.parse(translateCommandline(input));
    }
}
