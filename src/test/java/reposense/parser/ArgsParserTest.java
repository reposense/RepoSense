package reposense.parser;

import static org.apache.tools.ant.types.Commandline.translateCommandline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import reposense.model.CliArguments;
import reposense.util.FileUtil;
import reposense.util.TestUtil;

public class ArgsParserTest {
    private static final Path PROJECT_DIRECTORY = Paths.get(System.getProperty("user.dir"));
    private static final Path CONFIG_FILE_ABSOLUTE = new File(ArgsParserTest.class.getClassLoader()
            .getResource("output/parser_test.csv").getFile()).toPath();
    private static final Path OUTPUT_DIRECTORY_ABSOLUTE = new File(ArgsParserTest.class.getClassLoader()
            .getResource("output").getFile()).toPath();
    private static final Path CONFIG_FILE_RELATIVE = PROJECT_DIRECTORY.relativize(CONFIG_FILE_ABSOLUTE);
    private static final Path OUTPUT_DIRECTORY_RELATIVE = PROJECT_DIRECTORY.relativize(OUTPUT_DIRECTORY_ABSOLUTE);
    private static final String DEFAULT_MANDATORY_ARGS =  CONFIG_FILE_ABSOLUTE + " ";

    static {
        try {
            List<Path> files = Files.list(PROJECT_DIRECTORY).collect(Collectors.toList());

            if (files.size() != 0) {
                System.out.println(PROJECT_DIRECTORY + " is not empty.");
                System.out.println(files);
                throw new AssertionError(
                        "\nWARNING!"
                        + "\nTesting environment is not in a sandbox!"
                        + "\nYou may lose your working files."
                        + "\nPlease refer to the developer guide for instructions."
                        + "\nQuitting for safety.");
            }
        } catch (IOException e) {
            // Ignore exception
        }
    }

    @Before
    public void cleanupSandboxFolder() throws IOException {
        FileUtil.deleteDirectory(ArgsParser.REPOSENSE_CONFIG_FOLDER);
        FileUtil.deleteDirectory(ArgsParser.REPOSENSE_REPORT_FOLDER);
    }

    private void createTestFiles(Path srcFolderPath, List<String> filenames) throws IOException {
        for (String filename : filenames) {
            srcFolderPath.toFile().mkdirs();
            Path toCreate = Paths.get(srcFolderPath.toString(), filename);
            Files.createFile(toCreate);
        }
    }

    @Test
    public void genericInput_allCorrectInputs_success() throws ParseException, IOException {
        // Run with no parameters; only _reposense folder exists, get the correct folder
        Path pathToCreateTestFiles = Paths.get(PROJECT_DIRECTORY.toString(), ArgsParser.REPOSENSE_CONFIG_FOLDER);
        createTestFiles(pathToCreateTestFiles, GenericInputArgumentType.REPOSENSE_CONFIG_FOLDER_FILES);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(""));
        Assert.assertEquals(GenericInputType.REPOSENSE_CONFIG_FOLDER, cliArguments.getGenericInputType());
        Assert.assertTrue(cliArguments.getGenericInputValue() instanceof Path);
        cleanupSandboxFolder();

        // Run with no parameters; only docs folder exists, get the correct folder
        pathToCreateTestFiles = Paths.get(PROJECT_DIRECTORY.toString(), ArgsParser.REPOSENSE_REPORT_FOLDER);
        createTestFiles(pathToCreateTestFiles, GenericInputArgumentType.REPOSENSE_REPORT_FOLDER_FILES);
        cliArguments = ArgsParser.parse(translateCommandline(""));
        Assert.assertEquals(GenericInputType.REPOSENSE_REPORT_FOLDER, cliArguments.getGenericInputType());
        Assert.assertTrue(cliArguments.getGenericInputValue() instanceof Path);
        cleanupSandboxFolder();

        // Run with no parameters; _reposense and docs folder exists, get the docs folder for incremental build
        pathToCreateTestFiles = Paths.get(PROJECT_DIRECTORY.toString(), ArgsParser.REPOSENSE_CONFIG_FOLDER);
        createTestFiles(pathToCreateTestFiles, GenericInputArgumentType.REPOSENSE_CONFIG_FOLDER_FILES);
        pathToCreateTestFiles = Paths.get(PROJECT_DIRECTORY.toString(), ArgsParser.REPOSENSE_REPORT_FOLDER);
        createTestFiles(pathToCreateTestFiles, GenericInputArgumentType.REPOSENSE_REPORT_FOLDER_FILES);
        cliArguments = ArgsParser.parse(translateCommandline(""));
        Assert.assertEquals(GenericInputType.REPOSENSE_REPORT_FOLDER, cliArguments.getGenericInputType());
        Assert.assertTrue(cliArguments.getGenericInputValue() instanceof Path);
        cleanupSandboxFolder();

        // Run with path to csv file
        String input = CONFIG_FILE_RELATIVE.toString();
        cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertEquals(GenericInputType.CSV_FILE, cliArguments.getGenericInputType());
        Assert.assertTrue(cliArguments.getGenericInputValue() instanceof Path);

        input = CONFIG_FILE_ABSOLUTE.toString();
        cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertEquals(GenericInputType.CSV_FILE, cliArguments.getGenericInputType());
        Assert.assertTrue(cliArguments.getGenericInputValue() instanceof Path);
    }

    @Test
    public void parse_allCorrectInputs_success() throws ParseException, IOException {
        String input = String.format("%s -output %s -since 01/07/2017 -until 30/11/2017",
                CONFIG_FILE_ABSOLUTE, OUTPUT_DIRECTORY_ABSOLUTE);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(Files.isSameFile(CONFIG_FILE_ABSOLUTE, cliArguments.getGenericInputValue()));
        Assert.assertTrue(Files.isSameFile(Paths.get(OUTPUT_DIRECTORY_ABSOLUTE.toString(),
                ArgsParser.REPOSENSE_REPORT_FOLDER), cliArguments.getOutputPath()));

        Date expectedSinceDate = TestUtil.getDate(2017, Calendar.JULY, 1);
        Date expectedUntilDate = TestUtil.getDate(2017, Calendar.NOVEMBER, 30);
        Assert.assertEquals(expectedSinceDate, cliArguments.getSinceDate().get());
        Assert.assertEquals(expectedUntilDate, cliArguments.getUntilDate().get());
    }

    @Test
    public void parse_withExtraWhitespaces_success() throws ParseException, IOException {
        String input = String.format(" %s      -output   %s   -since 01/07/2017   -until    30/11/2017   ",
                CONFIG_FILE_ABSOLUTE, OUTPUT_DIRECTORY_ABSOLUTE);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(Files.isSameFile(CONFIG_FILE_ABSOLUTE, cliArguments.getGenericInputValue()));
        Assert.assertTrue(Files.isSameFile(Paths.get(OUTPUT_DIRECTORY_ABSOLUTE.toString(),
                ArgsParser.REPOSENSE_REPORT_FOLDER), cliArguments.getOutputPath()));

        Date expectedSinceDate = TestUtil.getDate(2017, Calendar.JULY, 1);
        Date expectedUntilDate = TestUtil.getDate(2017, Calendar.NOVEMBER, 30);
        Assert.assertEquals(expectedSinceDate, cliArguments.getSinceDate().get());
        Assert.assertEquals(expectedUntilDate, cliArguments.getUntilDate().get());
    }

    @Test
    public void parse_configFileOnly_success() throws ParseException, IOException {
        String input = String.format("%s", CONFIG_FILE_ABSOLUTE);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(Files.isSameFile(CONFIG_FILE_ABSOLUTE, cliArguments.getGenericInputValue()));
        // Optional arguments have default values
        Assert.assertEquals(Optional.empty(), cliArguments.getSinceDate());
        Assert.assertEquals(Optional.empty(), cliArguments.getUntilDate());
        Assert.assertEquals(
                ArgsParser.REPOSENSE_REPORT_FOLDER, cliArguments.getOutputPath().getFileName().toString());

        input = String.format(" %s", CONFIG_FILE_RELATIVE);
        cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(Files.isSameFile(CONFIG_FILE_RELATIVE, cliArguments.getGenericInputValue()));
        // Optional arguments have default values
        Assert.assertEquals(Optional.empty(), cliArguments.getSinceDate());
        Assert.assertEquals(Optional.empty(), cliArguments.getUntilDate());
        Assert.assertEquals(
                ArgsParser.REPOSENSE_REPORT_FOLDER, cliArguments.getOutputPath().getFileName().toString());
    }

    @Test
    public void parse_configFileAndOutputDirectory_success() throws ParseException, IOException {
        Path expectedRelativeOutputDirectoryPath =
                Paths.get(OUTPUT_DIRECTORY_RELATIVE.toString(), ArgsParser.REPOSENSE_REPORT_FOLDER);
        Path expectedAbsoluteOutputDirectoryPath =
                Paths.get(OUTPUT_DIRECTORY_ABSOLUTE.toString(), ArgsParser.REPOSENSE_REPORT_FOLDER);

        String input = String.format("%s -output %s", CONFIG_FILE_ABSOLUTE, OUTPUT_DIRECTORY_RELATIVE);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(Files.isSameFile(CONFIG_FILE_ABSOLUTE, cliArguments.getGenericInputValue()));
        Assert.assertTrue(Files.isSameFile(expectedRelativeOutputDirectoryPath, cliArguments.getOutputPath()));

        input = String.format("%s -output %s", CONFIG_FILE_RELATIVE, OUTPUT_DIRECTORY_ABSOLUTE);
        cliArguments = ArgsParser.parse(translateCommandline(input));
        Assert.assertTrue(Files.isSameFile(CONFIG_FILE_RELATIVE, cliArguments.getGenericInputValue()));
        Assert.assertTrue(Files.isSameFile(expectedAbsoluteOutputDirectoryPath, cliArguments.getOutputPath()));
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
    public void parse_nonExistentReposenseSourceFolder_throwsParseException() throws ParseException {
        String input = "";
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_nonExistentReposenseReportFolder_throwsParseException() throws ParseException {
        String input = "some/non_existent/reposense-report";
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_nonExistentCsvConfigFile_throwsParseException() throws ParseException {
        String input = "non_existent.csv";
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void missingMandatoryConfigArg_throwsParseException() throws ParseException {
        String input = String.format("-output %s -since 01/07/2017 -until 30/11/2017", OUTPUT_DIRECTORY_ABSOLUTE);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void absoluteDirectory_forConfigFile_throwsParseException() throws ParseException {
        String absDirectory = PROJECT_DIRECTORY.getParent().toString();
        String input = String.format("%s", absDirectory);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void relativeDirectory_forConfigFile_throwsParseException() throws ParseException {
        String relDirectory = PROJECT_DIRECTORY.getParent().toString();
        String input = String.format("%s", relDirectory);
        ArgsParser.parse(translateCommandline(input));
    }

    @Test(expected = ParseException.class)
    public void parse_absoluteConfigFileNotExists_throwsParseException() throws ParseException {
        String absConfigFile = Paths.get(PROJECT_DIRECTORY.toString(), "somefile_not_existent.csv").toString();
        String input = String.format("%s", absConfigFile);
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
    public void parse_missingFilesInReposenseConfigFolder_throwsParseException() throws ParseException, IOException {
        final int numOfFiles = GenericInputArgumentType.REPOSENSE_CONFIG_FOLDER_FILES.size();
        List<String> someFiles = GenericInputArgumentType.REPOSENSE_CONFIG_FOLDER_FILES.subList(0, numOfFiles - 1);
        Path pathToCreateTestFiles = Paths.get(PROJECT_DIRECTORY.toString(), ArgsParser.REPOSENSE_CONFIG_FOLDER);
        createTestFiles(pathToCreateTestFiles, someFiles);
        ArgsParser.parse(translateCommandline(""));
    }

    @Test(expected = ParseException.class)
    public void parse_missingFilesInReposenseReportFolder_throwsParseException() throws ParseException, IOException {
        final int numOfFiles = GenericInputArgumentType.REPOSENSE_REPORT_FOLDER_FILES.size();
        List<String> someFiles = GenericInputArgumentType.REPOSENSE_REPORT_FOLDER_FILES.subList(0, numOfFiles - 1);
        Path sourcePath = Paths.get(PROJECT_DIRECTORY.toString(), ArgsParser.REPOSENSE_REPORT_FOLDER);
        createTestFiles(sourcePath, someFiles);
        String input = ArgsParser.REPOSENSE_REPORT_FOLDER;
        ArgsParser.parse(translateCommandline(input));
    }
}
