package reposense.parser;

import static org.apache.tools.ant.types.Commandline.translateCommandline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Test;

import reposense.model.CliArguments;
import reposense.util.Constants;
import reposense.util.TestUtil;

public class ArgsParserTest {

    private static final Path PROJECT_DIRECTORY = Paths.get(System.getProperty("user.dir"));
    private static final Path CONFIG_FILE_ABSOLUTE = new File(ArgsParserTest.class.getClassLoader()
            .getResource("output/parser_test.csv").getFile()).toPath();
    private static final Path OUTPUT_DIRECTORY_ABSOLUTE = new File(ArgsParserTest.class.getClassLoader()
            .getResource("output").getFile()).toPath();
    private static final Path CONFIG_FILE_RELATIVE = PROJECT_DIRECTORY.relativize(CONFIG_FILE_ABSOLUTE);
    private static final Path OUTPUT_DIRECTORY_RELATIVE = PROJECT_DIRECTORY.relativize(OUTPUT_DIRECTORY_ABSOLUTE);
    private static final String DEFAULT_MANDATORY_ARGS = "-config " + CONFIG_FILE_ABSOLUTE + " ";

    @Test
    public void parse_allCorrectInputs_success() throws ParseException, IOException {
        String input = String.format("-config %s -output %s -since 01/07/2017 -until 30/11/2017",
                CONFIG_FILE_ABSOLUTE, OUTPUT_DIRECTORY_ABSOLUTE);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Files.isSameFile(CONFIG_FILE_ABSOLUTE, cliArguments.getConfigFilePath());
        Files.isSameFile(OUTPUT_DIRECTORY_ABSOLUTE, cliArguments.getOutputFilePath());

        Date expectedSinceDate = TestUtil.getDate(2017, Calendar.JULY, 1);
        Date expectedUntilDate = TestUtil.getDate(2017, Calendar.NOVEMBER, 30);
        Assert.assertEquals(expectedSinceDate, cliArguments.getSinceDate().get());
        Assert.assertEquals(expectedUntilDate, cliArguments.getUntilDate().get());
    }

    @Test
    public void parse_withExtraWhitespaces_success() throws ParseException, IOException {
        String input = String.format("-config %s      -output   %s   -since 01/07/2017   -until    30/11/2017   ",
                CONFIG_FILE_ABSOLUTE, OUTPUT_DIRECTORY_ABSOLUTE);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Files.isSameFile(CONFIG_FILE_ABSOLUTE, cliArguments.getConfigFilePath());
        Files.isSameFile(OUTPUT_DIRECTORY_ABSOLUTE, cliArguments.getOutputFilePath());

        Date expectedSinceDate = TestUtil.getDate(2017, Calendar.JULY, 1);
        Date expectedUntilDate = TestUtil.getDate(2017, Calendar.NOVEMBER, 30);
        Assert.assertEquals(expectedSinceDate, cliArguments.getSinceDate().get());
        Assert.assertEquals(expectedUntilDate, cliArguments.getUntilDate().get());
    }

    @Test
    public void parse_configFileOnly_success() throws ParseException, IOException, java.text.ParseException {
        Path expected = CONFIG_FILE_ABSOLUTE;
        String input = String.format("-config %s", CONFIG_FILE_ABSOLUTE);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Files.isSameFile(expected, cliArguments.getConfigFilePath());
        //Optional arguments have default values
        Assert.assertEquals(Optional.empty(), cliArguments.getSinceDate());
        Assert.assertEquals(Optional.empty(), cliArguments.getUntilDate());
        //Checks if generate output folder is in yyyy-MM-dd-HH-mm-ss format
        Constants.DEFAULT_REPORT_NAME_FORMAT.parse(cliArguments.getOutputFilePath().getFileName().toString());

        input = String.format("-config %s", CONFIG_FILE_RELATIVE);
        cliArguments = ArgsParser.parse(translateCommandline(input));
        Files.isSameFile(expected, cliArguments.getConfigFilePath());
        //Optional arguments have default values
        Assert.assertEquals(Optional.empty(), cliArguments.getSinceDate());
        Assert.assertEquals(Optional.empty(), cliArguments.getUntilDate());
        //Checks if generate output folder is in yyyy-MM-dd-HH-mm-ss format
        Constants.DEFAULT_REPORT_NAME_FORMAT.parse(cliArguments.getOutputFilePath().getFileName().toString());
    }

    @Test
    public void parse_configFileAndOutputDirectory_success() throws ParseException, IOException {
        Path expectedConfigFilePath = CONFIG_FILE_ABSOLUTE;
        Path expectedOutputDirectoryPath = OUTPUT_DIRECTORY_ABSOLUTE;

        String input = String.format("-config %s -output %s", CONFIG_FILE_ABSOLUTE, OUTPUT_DIRECTORY_RELATIVE);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(Files.isSameFile(expectedConfigFilePath, cliArguments.getConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(expectedOutputDirectoryPath, cliArguments.getOutputFilePath()));

        input = String.format("-config %s -output %s", CONFIG_FILE_RELATIVE, OUTPUT_DIRECTORY_ABSOLUTE);
        cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(Files.isSameFile(expectedConfigFilePath, cliArguments.getConfigFilePath()));
        Assert.assertTrue(Files.isSameFile(expectedOutputDirectoryPath, cliArguments.getOutputFilePath()));
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
    public void absoluteDirectory_forConfigFile_throwsParseException() throws ParseException {
        String absDirectory = PROJECT_DIRECTORY.getParent().toString();
        String input = String.format("-config %s", absDirectory);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void relativeDirectory_forConfigFile_throwsParseException() throws ParseException {
        String relDirectory = PROJECT_DIRECTORY.getParent().toString();
        String input = String.format("-config %s", relDirectory);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_absoluteConfigFileNotExists_throwsParseException() throws ParseException {
        String absConfigFile = Paths.get(PROJECT_DIRECTORY.toString(), "somefile_not_existent.csv").toString();
        String input = String.format("-config %s", absConfigFile);
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
}
