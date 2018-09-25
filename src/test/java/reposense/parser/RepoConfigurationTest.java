package reposense.parser;

import static org.apache.tools.ant.types.Commandline.translateCommandline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import reposense.git.GitDownloader;
import reposense.git.GitDownloaderException;
import reposense.model.Author;
import reposense.model.CliArguments;
import reposense.model.ConfigCliArguments;
import reposense.model.RepoConfiguration;
import reposense.report.ReportGenerator;
import reposense.util.FileUtil;
import reposense.util.TestUtil;

public class RepoConfigurationTest {
    private static final Path IGNORE_STANDALONE_TEST_CONFIG_FILES = new File(CsvParserTest.class.getClassLoader()
            .getResource("RepoConfigurationTest/repoconfig_authorSpecialCharacter_test").getFile()).toPath();

    private static final Path IGNORE_STANDALONE_KEYWORD_TEST_CONFIG_FILES =
            new File(CsvParserTest.class.getClassLoader()
                    .getResource("RepoConfigurationTest/repoconfig_ignoreStandAloneKeyword_test").getFile()).toPath();
    private static final Path FORMATS_TEST_CONFIG_FILES = new File(CsvParserTest.class.getClassLoader()
            .getResource("RepoConfigurationTest/repoconfig_formats_test").getFile()).toPath();
    private static final Path WITHOUT_FORMATS_TEST_CONFIG_FILES = new File(CsvParserTest.class.getClassLoader()
            .getResource("RepoConfigurationTest/repoconfig_withoutformats_test").getFile()).toPath();

    private static final String TEST_REPO_DELTA = "https://github.com/reposense/testrepo-Delta.git";

    private static final Author FIRST_AUTHOR = new Author("lithiumlkid");
    private static final Author SECOND_AUTHOR = new Author("codeeong");
    private static final Author THIRD_AUTHOR = new Author("jordancjq");
    private static final Author FOURTH_AUTHOR = new Author("lohtianwei");

    private static final List<String> FIRST_AUTHOR_ALIASES = Arrays.asList("Ahmad Syafiq");
    private static final List<String> SECOND_AUTHOR_ALIASES = Arrays.asList("Codee");
    private static final List<String> THIRD_AUTHOR_ALIASES = Arrays.asList("Jordan Chong");
    private static final List<String> FOURTH_AUTHOR_ALIASES = Arrays.asList("Tianwei");

    private static final List<String> REPO_LEVEL_GLOB_LIST = Arrays.asList("collated**");
    private static final List<String> FIRST_AUTHOR_GLOB_LIST =
            Arrays.asList("collated**", "*.aa1", "**.aa2", "**.java");
    private static final List<String> SECOND_AUTHOR_GLOB_LIST = Arrays.asList("collated**", "**[!(.md)]");

    private static final List<String> CONFIG_FORMATS = Arrays.asList("java", "adoc", "md");
    private static final List<String> CLI_FORMATS = Arrays.asList("css", "html");

    private static RepoConfiguration REPO_DELTA_STANDALONE_CONFIG;

    @BeforeClass
    public static void setUp() throws InvalidLocationException {
        FIRST_AUTHOR.setIgnoreGlobList(FIRST_AUTHOR_GLOB_LIST);
        SECOND_AUTHOR.setIgnoreGlobList(SECOND_AUTHOR_GLOB_LIST);
        THIRD_AUTHOR.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);
        FOURTH_AUTHOR.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);

        List<Author> expectedAuthors = new ArrayList<>();
        expectedAuthors.add(FIRST_AUTHOR);
        expectedAuthors.add(SECOND_AUTHOR);
        expectedAuthors.add(THIRD_AUTHOR);
        expectedAuthors.add(FOURTH_AUTHOR);

        REPO_DELTA_STANDALONE_CONFIG = new RepoConfiguration(TEST_REPO_DELTA, "master");
        REPO_DELTA_STANDALONE_CONFIG.setAuthorList(expectedAuthors);
        REPO_DELTA_STANDALONE_CONFIG.addAuthorAliases(FIRST_AUTHOR, FIRST_AUTHOR_ALIASES);
        REPO_DELTA_STANDALONE_CONFIG.addAuthorAliases(SECOND_AUTHOR, SECOND_AUTHOR_ALIASES);
        REPO_DELTA_STANDALONE_CONFIG.addAuthorAliases(THIRD_AUTHOR, THIRD_AUTHOR_ALIASES);
        REPO_DELTA_STANDALONE_CONFIG.addAuthorAliases(FOURTH_AUTHOR, FOURTH_AUTHOR_ALIASES);
        REPO_DELTA_STANDALONE_CONFIG.setAuthorDisplayName(FIRST_AUTHOR, "Ahm");
        REPO_DELTA_STANDALONE_CONFIG.setAuthorDisplayName(SECOND_AUTHOR, "Cod");
        REPO_DELTA_STANDALONE_CONFIG.setAuthorDisplayName(THIRD_AUTHOR, "Jor");
        REPO_DELTA_STANDALONE_CONFIG.setAuthorDisplayName(FOURTH_AUTHOR, "Loh");

        REPO_DELTA_STANDALONE_CONFIG.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);
        REPO_DELTA_STANDALONE_CONFIG.setFormats(CONFIG_FORMATS);
    }

    @Before
    public void cleanRepoDirectory() throws IOException {
        FileUtil.deleteDirectory(FileUtil.REPOS_ADDRESS);
    }

    @Test
    public void setFormats_alphaNumeric_success() throws InvalidLocationException {
        RepoConfiguration actualConfig = new RepoConfiguration(TEST_REPO_DELTA, "master");
        actualConfig.setFormats(Arrays.asList("java", "7z"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setFormats_nonAlphaNumeric_throwIllegalArgumentException() throws InvalidLocationException {
        RepoConfiguration actualConfig = new RepoConfiguration(TEST_REPO_DELTA, "master");
        actualConfig.setFormats(Arrays.asList(".java"));
    }

    @Test
    public void repoConfig_usesStandaloneConfig_success() throws InvalidLocationException, GitDownloaderException {
        RepoConfiguration actualConfig = new RepoConfiguration(TEST_REPO_DELTA, "master");
        GitDownloader.downloadRepo(actualConfig);
        ReportGenerator.updateRepoConfig(actualConfig);

        TestUtil.compareRepoConfig(REPO_DELTA_STANDALONE_CONFIG, actualConfig);
    }

    @Test
    public void repoConfig_ignoresStandaloneConfig_success()
            throws ParseException, GitDownloaderException, IOException {
        List<Author> expectedAuthors = new ArrayList<>();
        expectedAuthors.add(FIRST_AUTHOR);

        RepoConfiguration expectedConfig = new RepoConfiguration(TEST_REPO_DELTA, "master");
        expectedConfig.setAuthorList(expectedAuthors);
        expectedConfig.addAuthorAliases(FIRST_AUTHOR, FIRST_AUTHOR_ALIASES);
        expectedConfig.setAuthorDisplayName(FIRST_AUTHOR, "Ahm");

        expectedConfig.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);
        expectedConfig.setFormats(CONFIG_FORMATS);

        String formats = String.join(" ", CLI_FORMATS);
        String input = String.format("-config %s -formats %s", IGNORE_STANDALONE_TEST_CONFIG_FILES, formats);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        List<RepoConfiguration> authorConfigs =
                new AuthorConfigCsvParser(((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()).parse();
        RepoConfiguration.merge(actualConfigs, authorConfigs);

        RepoConfiguration actualConfig = actualConfigs.get(0);
        GitDownloader.downloadRepo(actualConfig);
        ReportGenerator.updateRepoConfig(actualConfig);

        TestUtil.compareRepoConfig(expectedConfig, actualConfig);
    }

    @Test
    public void repoConfig_wrongKeywordUseStandaloneConfig_success()
            throws ParseException, GitDownloaderException, IOException {
        String formats = String.join(" ", CLI_FORMATS);
        String input = String.format("-config %s -formats %s", IGNORE_STANDALONE_KEYWORD_TEST_CONFIG_FILES, formats);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();

        RepoConfiguration actualConfig = actualConfigs.get(0);
        GitDownloader.downloadRepo(actualConfig);
        ReportGenerator.updateRepoConfig(actualConfig);

        TestUtil.compareRepoConfig(REPO_DELTA_STANDALONE_CONFIG, actualConfig);
    }

    @Test
    public void repoConfig_withFormats_ignoreCliFormats() throws ParseException, IOException {
        String formats = String.join(" ", CLI_FORMATS);
        String input = String.format("-config %s -formats %s", FORMATS_TEST_CONFIG_FILES, formats);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        RepoConfiguration.setFormatsToRepoConfigs(actualConfigs, cliArguments.getFormats());

        Assert.assertEquals(1, actualConfigs.size());
        Assert.assertEquals(CONFIG_FORMATS, actualConfigs.get(0).getFormats());
    }

    @Test
    public void repoConfig_withoutFormats_useCliFormats() throws ParseException, IOException {
        String formats = String.join(" ", CLI_FORMATS);
        String input = String.format("-config %s -formats %s", WITHOUT_FORMATS_TEST_CONFIG_FILES, formats);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        RepoConfiguration.setFormatsToRepoConfigs(actualConfigs, cliArguments.getFormats());

        Assert.assertEquals(1, actualConfigs.size());
        Assert.assertEquals(CLI_FORMATS, actualConfigs.get(0).getFormats());
    }

    @Test
    public void repoConfig_withoutFormatsAndCliFormats_useDefaultFormats() throws ParseException, IOException {
        String input = String.format("-config %s", WITHOUT_FORMATS_TEST_CONFIG_FILES);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        RepoConfiguration.setFormatsToRepoConfigs(actualConfigs, cliArguments.getFormats());

        Assert.assertEquals(1, actualConfigs.size());
        Assert.assertEquals(ArgsParser.DEFAULT_FORMATS, actualConfigs.get(0).getFormats());
    }
}
