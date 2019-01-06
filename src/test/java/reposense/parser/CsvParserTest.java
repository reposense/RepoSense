package reposense.parser;

import static org.apache.tools.ant.types.Commandline.translateCommandline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.model.Author;
import reposense.model.CliArguments;
import reposense.model.CommitHash;
import reposense.model.ConfigCliArguments;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;

public class CsvParserTest {
    private static final Path TEST_CONFIG_FOLDER = new File(CsvParserTest.class.getClassLoader()
            .getResource("repoconfig_merge_test").getFile()).toPath();
    private static final Path TEST_EMPTY_BRANCH_CONFIG_FOLDER = new File(CsvParserTest.class.getClassLoader()
            .getResource("repoconfig_empty_branch_test").getFile()).toPath();
    private static final Path REPO_CONFIG_NO_SPECIAL_CHARACTER_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/repoconfig_noSpecialCharacter_test.csv").getFile()).toPath();
    private static final Path AUTHOR_CONFIG_EMPTY_LOCATION_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/authorconfig_emptyLocation_test.csv").getFile()).toPath();
    private static final Path AUTHOR_CONFIG_NO_SPECIAL_CHARACTER_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/authorconfig_noSpecialCharacter_test.csv").getFile()).toPath();
    private static final Path AUTHOR_CONFIG_SPECIAL_CHARACTER_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/authorconfig_specialCharacter_test.csv").getFile()).toPath();
    private static final Path MERGE_EMPTY_LOCATION_FOLDER = new File(CsvParserTest.class.getClassLoader()
            .getResource("repoconfig_merge_empty_location_test").getFile()).toPath();

    private static final String TEST_REPO_BETA_LOCATION = "https://github.com/reposense/testrepo-Beta.git";
    private static final String TEST_REPO_BETA_BRANCH = "master";

    private static final String TEST_REPO_DELTA_LOCATION = "https://github.com/reposense/testrepo-Delta.git";
    private static final String TEST_REPO_DELTA_BRANCH = "HEAD";

    private static final List<String> TEST_REPO_BETA_CONFIG_FORMATS = Arrays.asList("java", "adoc", "md");
    private static final List<String> TEST_REPO_BETA_CONFIG_IGNORED_COMMITS =
            Arrays.asList("abcde12345", "67890fdecba");

    private static final Author FIRST_AUTHOR = new Author("nbriannl");
    private static final Author SECOND_AUTHOR = new Author("zacharytang");
    private static final List<Author> AUTHOR_CONFIG_NO_SPECIAL_CHARACTER_AUTHORS =
            Arrays.asList(FIRST_AUTHOR, SECOND_AUTHOR);

    private static final Author FIRST_SPECIAL_CHARACTER_AUTHOR = new Author("‘Processed�‘Cooked�");
    private static final Author SECOND_SPECIAL_CHARACTER_AUTHOR = new Author("(codeeong)");
    private static final Author THIRD_SPECIAL_CHARACTER_AUTHOR = new Author("^:jordancjq;$");
    private static final List<Author> AUTHOR_CONFIG_SPECIAL_CHARACTER_AUTHORS = Arrays.asList(
            FIRST_SPECIAL_CHARACTER_AUTHOR, SECOND_SPECIAL_CHARACTER_AUTHOR, THIRD_SPECIAL_CHARACTER_AUTHOR);

    private static final List<String> REPO_LEVEL_GLOB_LIST = Arrays.asList("collated**");
    private static final List<String> FIRST_AUTHOR_GLOB_LIST = Arrays.asList("collated**", "**.java");

    @Test
    public void repoConfig_noSpecialCharacter_success() throws IOException, InvalidLocationException {
        RepoConfigCsvParser repoConfigCsvParser = new RepoConfigCsvParser(REPO_CONFIG_NO_SPECIAL_CHARACTER_FILE);
        List<RepoConfiguration> configs = repoConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        RepoConfiguration config = configs.get(0);

        Assert.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assert.assertEquals(TEST_REPO_BETA_BRANCH, config.getBranch());

        Assert.assertEquals(TEST_REPO_BETA_CONFIG_FORMATS, TEST_REPO_BETA_CONFIG_FORMATS);

        Assert.assertTrue(config.isStandaloneConfigIgnored());

        Assert.assertEquals(config.getIgnoreCommitList(),
                CommitHash.convertStringsToCommits(TEST_REPO_BETA_CONFIG_IGNORED_COMMITS));
    }

    @Test
    public void authorConfig_noSpecialCharacter_success() throws IOException, InvalidLocationException {
        AuthorConfigCsvParser authorConfigCsvParser =
                new AuthorConfigCsvParser(AUTHOR_CONFIG_NO_SPECIAL_CHARACTER_FILE);
        List<RepoConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        RepoConfiguration config = configs.get(0);

        Assert.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assert.assertEquals(TEST_REPO_BETA_BRANCH, config.getBranch());

        Assert.assertEquals(AUTHOR_CONFIG_NO_SPECIAL_CHARACTER_AUTHORS, config.getAuthorList());
    }

    @Test
    public void authorConfig_emptyLocation_success() throws ParseException, IOException {
        RepoConfiguration expectedConfig = new RepoConfiguration(new RepoLocation(""));

        AuthorConfigCsvParser authorConfigCsvParser =
                new AuthorConfigCsvParser(AUTHOR_CONFIG_EMPTY_LOCATION_FILE);
        List<RepoConfiguration> authorConfigs = authorConfigCsvParser.parse();
        RepoConfiguration authorConfig = authorConfigs.get(0);

        Assert.assertEquals(1, authorConfigs.size());
        Assert.assertEquals(expectedConfig, authorConfig);
        Assert.assertEquals(expectedConfig.getLocation(), authorConfig.getLocation());
        Assert.assertEquals(expectedConfig.getBranch(), authorConfig.getBranch());
        Assert.assertEquals(AUTHOR_CONFIG_NO_SPECIAL_CHARACTER_AUTHORS, authorConfig.getAuthorList());
    }

    @Test
    public void authorConfig_specialCharacter_success() throws IOException, InvalidLocationException {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_SPECIAL_CHARACTER_FILE);
        List<RepoConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        RepoConfiguration config = configs.get(0);

        Assert.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assert.assertEquals(TEST_REPO_BETA_BRANCH, config.getBranch());

        Assert.assertEquals(AUTHOR_CONFIG_SPECIAL_CHARACTER_AUTHORS, config.getAuthorList());
    }

    @Test
    public void merge_twoRepoConfigs_success() throws ParseException, IOException {
        FIRST_AUTHOR.setIgnoreGlobList(FIRST_AUTHOR_GLOB_LIST);
        SECOND_AUTHOR.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);

        List<Author> expectedAuthors = new ArrayList<>();
        expectedAuthors.add(FIRST_AUTHOR);
        expectedAuthors.add(SECOND_AUTHOR);

        RepoConfiguration expectedConfig = new RepoConfiguration(new RepoLocation(TEST_REPO_BETA_LOCATION),
                TEST_REPO_BETA_BRANCH);
        expectedConfig.setAuthorList(expectedAuthors);
        expectedConfig.setAuthorDisplayName(FIRST_AUTHOR, "Nbr");
        expectedConfig.setAuthorDisplayName(SECOND_AUTHOR, "Zac");
        expectedConfig.addAuthorAliases(SECOND_AUTHOR,  Arrays.asList("Zachary Tang"));
        expectedConfig.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);

        String input = String.format("-config %s", TEST_CONFIG_FOLDER);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        List<RepoConfiguration> authorConfigs =
                new AuthorConfigCsvParser(((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()).parse();
        RepoConfiguration.merge(actualConfigs, authorConfigs);

        Assert.assertEquals(1, actualConfigs.size());
        Assert.assertEquals(expectedConfig.getLocation(), actualConfigs.get(0).getLocation());
        Assert.assertEquals(expectedConfig.getAuthorList().hashCode(), actualConfigs.get(0).getAuthorList().hashCode());
        Assert.assertEquals(expectedConfig.getAuthorDisplayNameMap().hashCode(),
                actualConfigs.get(0).getAuthorDisplayNameMap().hashCode());
        Assert.assertEquals(expectedConfig.getAuthorAliasMap().hashCode(),
                actualConfigs.get(0).getAuthorAliasMap().hashCode());
        Assert.assertEquals(REPO_LEVEL_GLOB_LIST, actualConfigs.get(0).getIgnoreGlobList());
    }

    @Test
    public void merge_emptyLocation_success() throws ParseException, IOException {
        FIRST_AUTHOR.setIgnoreGlobList(FIRST_AUTHOR_GLOB_LIST);
        SECOND_AUTHOR.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);

        List<Author> expectedBetaAuthors = new ArrayList<>();
        expectedBetaAuthors.add(FIRST_AUTHOR);
        expectedBetaAuthors.add(SECOND_AUTHOR);

        List<Author> expectedDeltaAuthors = new ArrayList<>();
        expectedDeltaAuthors.add(FIRST_AUTHOR);

        RepoConfiguration expectedBetaConfig =
                new RepoConfiguration(new RepoLocation(TEST_REPO_BETA_LOCATION), TEST_REPO_BETA_BRANCH);
        expectedBetaConfig.setAuthorList(expectedBetaAuthors);
        expectedBetaConfig.setAuthorDisplayName(FIRST_AUTHOR, "Nbr");
        expectedBetaConfig.setAuthorDisplayName(SECOND_AUTHOR, "Zac");
        expectedBetaConfig.addAuthorAliases(SECOND_AUTHOR,  Arrays.asList("Zachary Tang"));
        expectedBetaConfig.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);

        RepoConfiguration expectedDeltaConfig =
                new RepoConfiguration(new RepoLocation(TEST_REPO_DELTA_LOCATION), TEST_REPO_DELTA_BRANCH);
        expectedDeltaConfig.setAuthorList(expectedDeltaAuthors);
        expectedDeltaConfig.setAuthorDisplayName(FIRST_AUTHOR, "Nbr");

        List<RepoConfiguration> expectedConfigs = new ArrayList<>();
        expectedConfigs.add(expectedBetaConfig);
        expectedConfigs.add(expectedDeltaConfig);

        String input = String.format("-config %s", MERGE_EMPTY_LOCATION_FOLDER);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        List<RepoConfiguration> authorConfigs =
                new AuthorConfigCsvParser(((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()).parse();
        RepoConfiguration.merge(actualConfigs, authorConfigs);

        Assert.assertEquals(2, actualConfigs.size());
        Assert.assertEquals(expectedConfigs, actualConfigs);

        RepoConfiguration firstExpectedConfig = expectedConfigs.get(0);
        RepoConfiguration secondExpectedConfig = expectedConfigs.get(1);
        RepoConfiguration firstActualConfig = actualConfigs.get(0);
        RepoConfiguration secondActualConfig = actualConfigs.get(1);

        Assert.assertEquals(firstExpectedConfig.getLocation(), firstActualConfig.getLocation());
        Assert.assertEquals(firstExpectedConfig.getAuthorList().hashCode(),
                firstActualConfig.getAuthorList().hashCode());
        Assert.assertEquals(firstExpectedConfig.getAuthorDisplayNameMap().hashCode(),
                firstActualConfig.getAuthorDisplayNameMap().hashCode());
        Assert.assertEquals(firstExpectedConfig.getAuthorAliasMap().hashCode(),
                firstActualConfig.getAuthorAliasMap().hashCode());
        Assert.assertEquals(REPO_LEVEL_GLOB_LIST, firstActualConfig.getIgnoreGlobList());

        Assert.assertEquals(secondExpectedConfig.getLocation(), secondActualConfig.getLocation());
        Assert.assertEquals(secondExpectedConfig.getAuthorList().hashCode(),
                secondActualConfig.getAuthorList().hashCode());
        Assert.assertEquals(secondExpectedConfig.getAuthorDisplayNameMap().hashCode(),
                secondActualConfig.getAuthorDisplayNameMap().hashCode());
        Assert.assertEquals(secondExpectedConfig.getAuthorAliasMap().hashCode(),
                secondActualConfig.getAuthorAliasMap().hashCode());
        Assert.assertEquals(new ArrayList<>(), secondActualConfig.getIgnoreGlobList());
    }

    @Test
    public void repoConfig_defaultBranch_success() throws ParseException, IOException {
        RepoConfiguration expectedConfig = new RepoConfiguration(new RepoLocation(TEST_REPO_BETA_LOCATION),
                RepoConfiguration.DEFAULT_BRANCH);

        String input = String.format("-config %s", TEST_EMPTY_BRANCH_CONFIG_FOLDER);
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        List<RepoConfiguration> authorConfigs =
                new AuthorConfigCsvParser(((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()).parse();
        RepoConfiguration.merge(actualConfigs, authorConfigs);

        Assert.assertEquals(1, actualConfigs.size());
        Assert.assertEquals(expectedConfig.getBranch(), actualConfigs.get(0).getBranch());
        Assert.assertEquals(expectedConfig.getBranch(), authorConfigs.get(0).getBranch());
    }
}
