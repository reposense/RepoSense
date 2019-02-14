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
import reposense.model.AuthorConfiguration;
import reposense.model.CliArguments;
import reposense.model.CommitHash;
import reposense.model.ConfigCliArguments;
import reposense.model.Format;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.util.InputBuilder;
import reposense.util.TestUtil;

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
    private static final Path AUTHOR_CONFIG_MULTIPLE_EMAILS_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/authorconfig_multipleEmails_test.csv").getFile()).toPath();
    private static final Path MERGE_EMPTY_LOCATION_FOLDER = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/repoconfig_merge_empty_location_test").getFile()).toPath();

    private static final String TEST_REPO_BETA_LOCATION = "https://github.com/reposense/testrepo-Beta.git";
    private static final String TEST_REPO_BETA_BRANCH = "master";

    private static final String TEST_REPO_DELTA_LOCATION = "https://github.com/reposense/testrepo-Delta.git";
    private static final String TEST_REPO_DELTA_BRANCH = "HEAD";
    private static final List<Format> TEST_REPO_DELTA_FORMATS =
            Format.convertStringsToFormats(Arrays.asList("java", "fxml"));

    private static final List<String> TEST_REPO_BETA_CONFIG_FORMATS = Arrays.asList("java", "adoc", "md");
    private static final List<String> TEST_REPO_BETA_CONFIG_IGNORED_COMMITS =
            Arrays.asList("abcde12345", "67890fdecba");

    private static final Author FIRST_AUTHOR = new Author("nbriannl");
    private static final Author SECOND_AUTHOR = new Author("zacharytang");
    private static final List<String> SECOND_AUTHOR_ALIASES = Arrays.asList("Zachary Tang");
    private static final List<Author> AUTHOR_CONFIG_NO_SPECIAL_CHARACTER_AUTHORS =
            Arrays.asList(FIRST_AUTHOR, SECOND_AUTHOR);

    private static final Author FIRST_SPECIAL_CHARACTER_AUTHOR = new Author("‘Processed�‘Cooked�");
    private static final Author SECOND_SPECIAL_CHARACTER_AUTHOR = new Author("(codeeong)");
    private static final Author THIRD_SPECIAL_CHARACTER_AUTHOR = new Author("^:jordancjq;$");
    private static final List<Author> AUTHOR_CONFIG_SPECIAL_CHARACTER_AUTHORS = Arrays.asList(
            FIRST_SPECIAL_CHARACTER_AUTHOR, SECOND_SPECIAL_CHARACTER_AUTHOR, THIRD_SPECIAL_CHARACTER_AUTHOR);

    private static final List<String> REPO_LEVEL_GLOB_LIST = Arrays.asList("collated**");
    private static final List<String> FIRST_AUTHOR_GLOB_LIST = Arrays.asList("**.java", "collated**");
    private static final List<String> FIRST_AUTHOR_EMAIL_LIST =
            Arrays.asList("nbr@example.com", "nbriannl@test.net", "nbriannl@users.noreply.github.com");

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
        List<AuthorConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        AuthorConfiguration config = configs.get(0);

        Assert.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assert.assertEquals(TEST_REPO_BETA_BRANCH, config.getBranch());

        Assert.assertEquals(AUTHOR_CONFIG_NO_SPECIAL_CHARACTER_AUTHORS, config.getAuthorList());
    }

    @Test
    public void authorConfig_emptyLocation_success() throws ParseException, IOException {
        AuthorConfiguration expectedConfig = new AuthorConfiguration(new RepoLocation(""));

        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_EMPTY_LOCATION_FILE);
        List<AuthorConfiguration> authorConfigs = authorConfigCsvParser.parse();
        AuthorConfiguration authorConfig = authorConfigs.get(0);

        Assert.assertEquals(1, authorConfigs.size());
        Assert.assertEquals(expectedConfig.getLocation(), authorConfig.getLocation());
        Assert.assertEquals(expectedConfig.getBranch(), authorConfig.getBranch());
        Assert.assertEquals(AUTHOR_CONFIG_NO_SPECIAL_CHARACTER_AUTHORS, authorConfig.getAuthorList());
    }

    @Test
    public void authorConfig_specialCharacter_success() throws IOException, InvalidLocationException {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_SPECIAL_CHARACTER_FILE);
        List<AuthorConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        AuthorConfiguration config = configs.get(0);

        Assert.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assert.assertEquals(TEST_REPO_BETA_BRANCH, config.getBranch());

        Assert.assertEquals(AUTHOR_CONFIG_SPECIAL_CHARACTER_AUTHORS, config.getAuthorList());
    }

    @Test
    public void authorConfig_multipleEmails_success() throws IOException, InvalidLocationException {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_MULTIPLE_EMAILS_FILE);
        List<AuthorConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        AuthorConfiguration config = configs.get(0);

        Author actualAuthor = config.getAuthorList().get(0);
        Assert.assertEquals(FIRST_AUTHOR_EMAIL_LIST.size(), actualAuthor.getEmails().size());
        Assert.assertTrue(actualAuthor.getEmails().containsAll(FIRST_AUTHOR_EMAIL_LIST));
    }

    @Test
    public void merge_twoRepoConfigs_success() throws ParseException, IOException {
        FIRST_AUTHOR.setIgnoreGlobList(FIRST_AUTHOR_GLOB_LIST);
        SECOND_AUTHOR.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);
        SECOND_AUTHOR.setAuthorAliases(SECOND_AUTHOR_ALIASES);

        List<Author> expectedAuthors = new ArrayList<>();
        expectedAuthors.add(FIRST_AUTHOR);
        expectedAuthors.add(SECOND_AUTHOR);

        RepoConfiguration expectedConfig = new RepoConfiguration(new RepoLocation(TEST_REPO_BETA_LOCATION),
                TEST_REPO_BETA_BRANCH);
        expectedConfig.setAuthorList(expectedAuthors);
        expectedConfig.setAuthorDisplayName(FIRST_AUTHOR, "Nbr");
        expectedConfig.setAuthorDisplayName(SECOND_AUTHOR, "Zac");
        expectedConfig.addAuthorEmailsAndAliasesMapEntry(SECOND_AUTHOR,  Arrays.asList("Zachary Tang"));
        expectedConfig.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);

        String input = new InputBuilder().addConfig(TEST_CONFIG_FOLDER).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        List<AuthorConfiguration> authorConfigs =
                new AuthorConfigCsvParser(((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()).parse();
        RepoConfiguration.merge(actualConfigs, authorConfigs);

        Assert.assertEquals(1, actualConfigs.size());
        TestUtil.compareRepoConfig(expectedConfig, actualConfigs.get(0));
    }

    @Test
    public void merge_emptyLocation_success() throws ParseException, IOException {
        FIRST_AUTHOR.setIgnoreGlobList(FIRST_AUTHOR_GLOB_LIST);
        SECOND_AUTHOR.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);
        SECOND_AUTHOR.setAuthorAliases(SECOND_AUTHOR_ALIASES);

        List<Author> expectedBetaAuthors = new ArrayList<>();
        expectedBetaAuthors.add(SECOND_AUTHOR);
        expectedBetaAuthors.add(FIRST_AUTHOR);

        List<Author> expectedDeltaAuthors = new ArrayList<>();
        expectedDeltaAuthors.add(FIRST_AUTHOR);

        RepoConfiguration expectedBetaConfig =
                new RepoConfiguration(new RepoLocation(TEST_REPO_BETA_LOCATION), TEST_REPO_BETA_BRANCH);
        expectedBetaConfig.setAuthorList(expectedBetaAuthors);
        expectedBetaConfig.setAuthorDisplayName(FIRST_AUTHOR, "Nbr");
        expectedBetaConfig.setAuthorDisplayName(SECOND_AUTHOR, "Zac");
        expectedBetaConfig.addAuthorEmailsAndAliasesMapEntry(SECOND_AUTHOR,  Arrays.asList("Zachary Tang"));
        expectedBetaConfig.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);

        RepoConfiguration expectedDeltaConfig =
                new RepoConfiguration(new RepoLocation(TEST_REPO_DELTA_LOCATION), TEST_REPO_DELTA_BRANCH);
        expectedDeltaConfig.setAuthorList(expectedDeltaAuthors);
        expectedDeltaConfig.setAuthorDisplayName(FIRST_AUTHOR, "Nbr");
        expectedDeltaConfig.setStandaloneConfigIgnored(true);
        expectedDeltaConfig.setFormats(TEST_REPO_DELTA_FORMATS);

        List<RepoConfiguration> expectedConfigs = new ArrayList<>();
        expectedConfigs.add(expectedBetaConfig);
        expectedConfigs.add(expectedDeltaConfig);

        String input = new InputBuilder().addConfig(MERGE_EMPTY_LOCATION_FOLDER).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        List<AuthorConfiguration> authorConfigs =
                new AuthorConfigCsvParser(((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()).parse();
        RepoConfiguration.merge(actualConfigs, authorConfigs);

        Assert.assertEquals(2, actualConfigs.size());
        Assert.assertEquals(expectedConfigs, actualConfigs);

        TestUtil.compareRepoConfig(expectedConfigs.get(0), actualConfigs.get(0));
        TestUtil.compareRepoConfig(expectedConfigs.get(1), actualConfigs.get(1));
    }

    @Test
    public void repoConfig_defaultBranch_success() throws ParseException, IOException {
        RepoConfiguration expectedConfig = new RepoConfiguration(new RepoLocation(TEST_REPO_BETA_LOCATION),
                RepoConfiguration.DEFAULT_BRANCH);

        String input = new InputBuilder().addConfig(TEST_EMPTY_BRANCH_CONFIG_FOLDER).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        List<AuthorConfiguration> authorConfigs =
                new AuthorConfigCsvParser(((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()).parse();
        RepoConfiguration.merge(actualConfigs, authorConfigs);

        Assert.assertEquals(1, actualConfigs.size());
        Assert.assertEquals(expectedConfig.getBranch(), actualConfigs.get(0).getBranch());
        Assert.assertEquals(expectedConfig.getBranch(), authorConfigs.get(0).getBranch());
    }
}
