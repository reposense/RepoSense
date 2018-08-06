package reposense.parser;

import static org.apache.tools.ant.types.Commandline.translateCommandline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import reposense.git.GitDownloader;
import reposense.git.GitDownloaderException;
import reposense.model.Author;
import reposense.model.CliArguments;
import reposense.model.ConfigCliArguments;
import reposense.model.RepoConfiguration;
import reposense.report.ReportGenerator;

public class RepoConfigurationTest {

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

    @BeforeClass
    public static void setUp() {
        FIRST_AUTHOR.setIgnoreGlobList(FIRST_AUTHOR_GLOB_LIST);
        SECOND_AUTHOR.setIgnoreGlobList(SECOND_AUTHOR_GLOB_LIST);
        THIRD_AUTHOR.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);
        FOURTH_AUTHOR.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);
    }

    @Test
    public void repoConfig_usesStandaloneConfig_success() throws InvalidLocationException, GitDownloaderException {
        List<Author> expectedAuthors = new ArrayList<>();
        expectedAuthors.add(FIRST_AUTHOR);
        expectedAuthors.add(SECOND_AUTHOR);
        expectedAuthors.add(THIRD_AUTHOR);
        expectedAuthors.add(FOURTH_AUTHOR);

        RepoConfiguration expectedConfig = new RepoConfiguration(TEST_REPO_DELTA, "master");
        expectedConfig.setAuthorList(expectedAuthors);
        expectedConfig.addAuthorAliases(FIRST_AUTHOR, FIRST_AUTHOR_ALIASES);
        expectedConfig.addAuthorAliases(SECOND_AUTHOR, SECOND_AUTHOR_ALIASES);
        expectedConfig.addAuthorAliases(THIRD_AUTHOR, THIRD_AUTHOR_ALIASES);
        expectedConfig.addAuthorAliases(FOURTH_AUTHOR, FOURTH_AUTHOR_ALIASES);
        expectedConfig.setAuthorDisplayName(FIRST_AUTHOR, "Ahm");
        expectedConfig.setAuthorDisplayName(SECOND_AUTHOR, "Cod");
        expectedConfig.setAuthorDisplayName(THIRD_AUTHOR, "Jor");
        expectedConfig.setAuthorDisplayName(FOURTH_AUTHOR, "Loh");

        expectedConfig.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);

        RepoConfiguration actualConfig = new RepoConfiguration(TEST_REPO_DELTA, "master");
        GitDownloader.downloadRepo(actualConfig);
        ReportGenerator.updateRepoConfig(actualConfig);

        Assert.assertEquals(expectedConfig.getLocation(), actualConfig.getLocation());
        Assert.assertEquals(expectedConfig.getAuthorList().hashCode(), actualConfig.getAuthorList().hashCode());
        Assert.assertEquals(
                expectedConfig.getAuthorDisplayNameMap().hashCode(), actualConfig.getAuthorDisplayNameMap().hashCode());
        Assert.assertEquals(expectedConfig.getAuthorAliasMap().hashCode(), actualConfig.getAuthorAliasMap().hashCode());
        Assert.assertEquals(REPO_LEVEL_GLOB_LIST, actualConfig.getIgnoreGlobList());
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

        Path testFolderPath = new File(CsvParserTest.class.getClassLoader()
                .getResource("repoconfig_ignore_test").getFile()).toPath();

        String input = String.format("-config %s", testFolderPath);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        List<RepoConfiguration> authorConfigs =
                new AuthorConfigCsvParser(((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()).parse();
        RepoConfiguration.merge(actualConfigs, authorConfigs);

        RepoConfiguration actualConfig = actualConfigs.get(0);
        GitDownloader.downloadRepo(actualConfig);
        ReportGenerator.updateRepoConfig(actualConfig);

        Assert.assertEquals(expectedConfig.getLocation(), actualConfig.getLocation());
        Assert.assertEquals(expectedConfig.getAuthorList().hashCode(), actualConfig.getAuthorList().hashCode());
        Assert.assertEquals(
                expectedConfig.getAuthorDisplayNameMap().hashCode(), actualConfig.getAuthorDisplayNameMap().hashCode());
        Assert.assertEquals(expectedConfig.getAuthorAliasMap().hashCode(), actualConfig.getAuthorAliasMap().hashCode());
        Assert.assertEquals(REPO_LEVEL_GLOB_LIST, actualConfig.getIgnoreGlobList());
    }
}
