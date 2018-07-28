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

import reposense.model.CliArguments;
import reposense.util.TestUtil;

public class ArgsParserTest {

    private static final Path PROJECT_DIRECTORY = Paths.get(System.getProperty("user.dir"));
    private static final Path CONFIG_FOLDER_ABSOLUTE = new File(ArgsParserTest.class.getClassLoader()
            .getResource("output").getFile()).toPath();
    private static final Path OUTPUT_DIRECTORY_ABSOLUTE = new File(ArgsParserTest.class.getClassLoader()
            .getResource("output").getFile()).toPath();
    private static final Path CONFIG_FOLDER_RELATIVE = PROJECT_DIRECTORY.relativize(CONFIG_FOLDER_ABSOLUTE);
    private static final Path OUTPUT_DIRECTORY_RELATIVE = PROJECT_DIRECTORY.relativize(OUTPUT_DIRECTORY_ABSOLUTE);
    private static final String DEFAULT_MANDATORY_ARGS = "-config " + CONFIG_FOLDER_ABSOLUTE + " ";
    private static final Path REPO_COFIG_CSV_FILE =
            Paths.get(CONFIG_FOLDER_ABSOLUTE.toString(), CsvParser.REPO_CONFIG_FILENAME);

    @Test
    public void parse_allCorrectInputs_success() throws ParseException, IOException {
        String input = String.format("-config %s -output %s -since 01/07/2017 -until 30/11/2017 "
                + "-formats java adoc html css js",
                CONFIG_FOLDER_ABSOLUTE, OUTPUT_DIRECTORY_ABSOLUTE);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(Files.isSameFile(CONFIG_FOLDER_ABSOLUTE, cliArguments.getConfigFolderPath()));
        Assert.assertTrue(Files.isSameFile(Paths.get(OUTPUT_DIRECTORY_ABSOLUTE.toString(),
                ArgsParser.DEFAULT_REPORT_NAME), cliArguments.getOutputFilePath()));

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
        Assert.assertTrue(Files.isSameFile(CONFIG_FOLDER_ABSOLUTE, cliArguments.getConfigFolderPath()));
        Assert.assertTrue(Files.isSameFile(Paths.get(OUTPUT_DIRECTORY_ABSOLUTE.toString(),
                ArgsParser.DEFAULT_REPORT_NAME), cliArguments.getOutputFilePath()));

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
        Assert.assertTrue(Files.isSameFile(CONFIG_FOLDER_ABSOLUTE, cliArguments.getConfigFolderPath()));
        // Optional arguments have default values
        Assert.assertEquals(Optional.empty(), cliArguments.getSinceDate());
        Assert.assertEquals(Optional.empty(), cliArguments.getUntilDate());
        Assert.assertEquals(ArgsParser.DEFAULT_REPORT_NAME, cliArguments.getOutputFilePath().getFileName().toString());
        Assert.assertEquals(ArgsParser.DEFAULT_FORMATS, cliArguments.getFormats());
        Assert.assertNull(cliArguments.getReportDirectoryPath());

        input = String.format("-config %s", CONFIG_FOLDER_RELATIVE);
        cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(Files.isSameFile(CONFIG_FOLDER_RELATIVE, cliArguments.getConfigFolderPath()));
        // Optional arguments have default values
        Assert.assertEquals(Optional.empty(), cliArguments.getSinceDate());
        Assert.assertEquals(Optional.empty(), cliArguments.getUntilDate());
        Assert.assertEquals(ArgsParser.DEFAULT_REPORT_NAME, cliArguments.getOutputFilePath().getFileName().toString());
        Assert.assertEquals(ArgsParser.DEFAULT_FORMATS, cliArguments.getFormats());
        Assert.assertNull(cliArguments.getReportDirectoryPath());
    }

    @Test
    public void parse_viewOnly_success() throws ParseException, IOException {
        String input = String.format("-view %s", OUTPUT_DIRECTORY_ABSOLUTE);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(Files.isSameFile(OUTPUT_DIRECTORY_ABSOLUTE, cliArguments.getReportDirectoryPath()));
        // Optional arguments have default values
        Assert.assertEquals(Optional.empty(), cliArguments.getSinceDate());
        Assert.assertEquals(Optional.empty(), cliArguments.getUntilDate());
        Assert.assertEquals(ArgsParser.DEFAULT_REPORT_NAME, cliArguments.getOutputFilePath().getFileName().toString());
        Assert.assertEquals(ArgsParser.DEFAULT_FORMATS, cliArguments.getFormats());
        Assert.assertNull(cliArguments.getConfigFolderPath());
    }

    @Test
    public void parse_configFolderAndOutputDirectory_success() throws ParseException, IOException {
        Path expectedRelativeOutputDirectoryPath =
                Paths.get(OUTPUT_DIRECTORY_RELATIVE.toString(), ArgsParser.DEFAULT_REPORT_NAME);
        Path expectedAbsoluteOutputDirectoryPath =
                Paths.get(OUTPUT_DIRECTORY_ABSOLUTE.toString(), ArgsParser.DEFAULT_REPORT_NAME);

        String input = String.format("-config %s -output %s", CONFIG_FOLDER_ABSOLUTE, OUTPUT_DIRECTORY_RELATIVE);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(Files.isSameFile(CONFIG_FOLDER_ABSOLUTE, cliArguments.getConfigFolderPath()));
        Assert.assertTrue(Files.isSameFile(expectedRelativeOutputDirectoryPath, cliArguments.getOutputFilePath()));

        input = String.format("-config %s -output %s", CONFIG_FOLDER_RELATIVE, OUTPUT_DIRECTORY_ABSOLUTE);
        cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(Files.isSameFile(CONFIG_FOLDER_RELATIVE, cliArguments.getConfigFolderPath()));
        Assert.assertTrue(Files.isSameFile(expectedAbsoluteOutputDirectoryPath, cliArguments.getOutputFilePath()));
    }

    @Test
    public void sinceDate_correctFormat_success() throws ParseException {
        String sinceDate = "01/07/2017";
        String input = DEFAULT_MANDATORY_ARGS + String.format("-since %s", sinceDate);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Date expectedSinceDate = TestUtil.getDate(2017, Calendar.JULY, 1);
        Assert.assertEquals(expectedSinceDate, cliArguments.getSinceDate().get());
    }

    @Test
    public void untilDate_correctFormat_success() throws ParseException {
        String untilDate = "30/11/2017";
        String input = DEFAULT_MANDATORY_ARGS + String.format("-until %s", untilDate);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        Date expectedUntilDate = TestUtil.getDate(2017, Calendar.NOVEMBER, 30);
        Assert.assertEquals(expectedUntilDate, cliArguments.getUntilDate().get());
    }

    @Test
    public void formats_inAlphanumeric_success() throws ParseException {
        String formats = "java js css 7z";
        String input = DEFAULT_MANDATORY_ARGS + String.format("-formats %s", formats);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<String> expectedFormats = Arrays.asList("java", "js", "css", "7z");
        Assert.assertEquals(expectedFormats, cliArguments.getFormats());
    }

    @Test(expected = ParseException.class)
    public void emptyArgs_throwsParseException() throws ParseException {
        ArgsParser.parse(new String[]{});
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
        String absConfigFolder = Paths.get(PROJECT_DIRECTORY.toString(), "non_existing_random_folder").toString();
        String input = String.format("-config %s", absConfigFolder);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_configCsvFileAsConfigFolder_throwsParseException() throws ParseException {
        String input = String.format("-config %s", REPO_COFIG_CSV_FILE);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_missingConfigValue_throwsParseException() throws ParseException {
        String input = "-config";
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void file_forOutputDirectory_throwsParseException() throws ParseException {
        String file = Paths.get(PROJECT_DIRECTORY.toString(), "parser_test.csv").toString();
        String input = DEFAULT_MANDATORY_ARGS + String.format("-output %s", file);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void nonExistentDirectory_forOutputDirectory_throwsParseException() throws ParseException {
        String nonExistentDirectory = Paths.get(PROJECT_DIRECTORY.toString(), "some_non_existent_dir/").toString();
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
    public void parse_mutuallyExclusiveArgumentsTogether_throwsParseException() throws ParseException {
        String input = String.format("-config %s -view %s", CONFIG_FOLDER_ABSOLUTE, OUTPUT_DIRECTORY_ABSOLUTE);
        ArgsParser.parse(translateCommandline(input));
    }
}
