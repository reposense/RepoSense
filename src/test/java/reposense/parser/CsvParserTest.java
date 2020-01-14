package reposense.parser;

import static org.apache.tools.ant.types.Commandline.translateCommandline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.argparse4j.helper.HelpScreenException;
import reposense.model.Author;
import reposense.model.AuthorConfiguration;
import reposense.model.CliArguments;
import reposense.model.CommitHash;
import reposense.model.ConfigCliArguments;
import reposense.model.FileType;
import reposense.model.GroupConfiguration;
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
    private static final Path REPO_CONFIG_OVERRIDE_KEYWORD_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/repoconfig_overrideKeyword_test.csv").getFile()).toPath();
    private static final Path REPO_CONFIG_REDUNDANT_LINES_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/require_trailing_whitespaces/repoconfig_redundantLines_test.csv")
            .getFile()).toPath();
    private static final Path REPO_CONFIG_INVALID_HEADER_SIZE_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/repoconfig_invalidHeaderSize_test.csv").getFile()).toPath();
    private static final Path AUTHOR_CONFIG_EMPTY_LOCATION_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/authorconfig_emptyLocation_test.csv").getFile()).toPath();
    private static final Path AUTHOR_CONFIG_EMPTY_CONFIG_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/require_trailing_whitespaces/authorconfig_emptyConfig_test.csv")
            .getFile()).toPath();
    private static final Path AUTHOR_CONFIG_NO_SPECIAL_CHARACTER_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/authorconfig_noSpecialCharacter_test.csv").getFile()).toPath();
    private static final Path AUTHOR_CONFIG_SPECIAL_CHARACTER_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/authorconfig_specialCharacter_test.csv").getFile()).toPath();
    private static final Path AUTHOR_CONFIG_COMMAS_AND_DOUBLEQUOTES_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/authorconfig_commasAndDoubleQuotes_test.csv").getFile()).toPath();
    private static final Path AUTHOR_CONFIG_MULTIPLE_EMAILS_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/authorconfig_multipleEmails_test.csv").getFile()).toPath();
    private static final Path AUTHOR_CONFIG_INVALID_LOCATION = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/authorconfig_invalidLocation_test.csv").getFile()).toPath();
    private static final Path AUTHOR_CONFIG_INVALID_HEADER_SIZE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/authorconfig_invalidHeaderSize_test.csv").getFile()).toPath();
    private static final Path MERGE_EMPTY_LOCATION_FOLDER = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/repoconfig_merge_empty_location_test").getFile()).toPath();
    private static final Path GROUP_CONFIG_MULTI_LOCATION_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/groupconfig_multipleLocation_test.csv").getFile()).toPath();
    private static final Path GROUP_CONFIG_EMPTY_LOCATION_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/groupconfig_emptyLocation_test.csv").getFile()).toPath();
    private static final Path GROUP_CONFIG_INVALID_LOCATION_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/groupconfig_invalidLocation_test.csv").getFile()).toPath();
    private static final Path GROUP_CONFIG_INVALID_HEADER_SIZE_FILE = new File(CsvParserTest.class.getClassLoader()
            .getResource("CsvParserTest/groupconfig_invalidHeaderSize_test.csv").getFile()).toPath();

    private static final String TEST_REPO_BETA_LOCATION = "https://github.com/reposense/testrepo-Beta.git";
    private static final String TEST_REPO_BETA_MASTER_BRANCH = "master";
    private static final String TEST_REPO_BETA_ADD_CONFIG_JSON_BRANCH = "add-config-json";
    private static final List<FileType> TEST_REPO_BETA_GROUPS = Arrays.asList(
            new FileType("Code", Arrays.asList("**/*.java", "**/*.py")),
            new FileType("Docs", Collections.singletonList("docs/**")));

    private static final String TEST_REPO_DELTA_LOCATION = "https://github.com/reposense/testrepo-Delta.git";
    private static final String TEST_REPO_DELTA_BRANCH = "HEAD";
    private static final List<FileType> TEST_REPO_DELTA_FORMATS =
            FileType.convertFormatStringsToFileTypes(Arrays.asList("java", "fxml"));
    private static final List<FileType> TEST_REPO_DELTA_GROUPS = Arrays.asList(
            new FileType("Main", Collections.singletonList("src/main/**")),
            new FileType("Test", Arrays.asList("src/test/**", "src/systest/**")));

    private static final List<FileType> TEST_REPO_BETA_CONFIG_FORMATS =
            FileType.convertFormatStringsToFileTypes(Arrays.asList("java", "adoc", "md"));
    private static final List<String> TEST_REPO_BETA_CONFIG_IGNORED_COMMITS =
            Arrays.asList("abcde12345", "67890fdecba");
    private static final List<String> TEST_REPO_BETA_CONFIG_IGNORED_AUTHORS = Arrays.asList("zacharytang");

    private static final String TEST_REPO_CHARLIE_LOCATION = "https://github.com/reposense/testrepo-Charlie.git";
    private static final String TEST_REPO_CHARLIE_BRANCH = "HEAD";

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

    private static final Author FIRST_COMMAS_AND_DOUBLEQUOTES_AUTHOR = new Author("ProcessedCooked");
    private static final Author SECOND_COMMAS_AND_DOUBLEQUOTES_AUTHOR = new Author("codeeong");
    private static final Author THIRD_COMMAS_AND_DOUBLEQUOTES_AUTHOR = new Author("jordancjq");
    private static final String FIRST_COMMAS_AND_DOUBLEQUOTES_DISPLAY_NAME = "Tay Fan Gao, Douya";
    private static final String SECOND_COMMAS_AND_DOUBLEQUOTES_DISPLAY_NAME = "\"\"Tora, S/O,\" Doyua, T.\"";
    private static final String THIRD_COMMAS_AND_DOUBLEQUOTES_DISPLAY_NAME = "jordancjq";
    private static final List<String> FIRST_COMMAS_AND_DOUBLEQUOTES_ALIAS =
            Collections.singletonList("Tay Fan Gao, Douya \"SOC, Y2S1\"");
    private static final List<String> SECOND_COMMAS_AND_DOUBLEQUOTES_ALIAS = Collections.emptyList();
    private static final List<String> THIRD_COMMAS_AND_DOUBLEQUOTES_ALIAS =
            Arrays.asList("Borex T\"ony Tong");
    private static final Map<Author, List<String>> AUTHOR_ALIAS_COMMAS_AND_DOUBLE_QUOTES_MAP =
            Stream.of(new Object[][]{
                    {FIRST_COMMAS_AND_DOUBLEQUOTES_AUTHOR, FIRST_COMMAS_AND_DOUBLEQUOTES_ALIAS},
                    {SECOND_COMMAS_AND_DOUBLEQUOTES_AUTHOR, SECOND_COMMAS_AND_DOUBLEQUOTES_ALIAS},
                    {THIRD_COMMAS_AND_DOUBLEQUOTES_AUTHOR, THIRD_COMMAS_AND_DOUBLEQUOTES_ALIAS}
            }).collect(Collectors.toMap(data -> (Author) data[0], data -> (List<String>) data[1]));
    private static final Map<Author, String> AUTHOR_DISPLAY_NAME_COMMAS_AND_DOUBLE_QUOTES_MAP =
            Stream.of(new Object[][]{
                    {FIRST_COMMAS_AND_DOUBLEQUOTES_AUTHOR, FIRST_COMMAS_AND_DOUBLEQUOTES_DISPLAY_NAME},
                    {SECOND_COMMAS_AND_DOUBLEQUOTES_AUTHOR, SECOND_COMMAS_AND_DOUBLEQUOTES_DISPLAY_NAME},
                    {THIRD_COMMAS_AND_DOUBLEQUOTES_AUTHOR, THIRD_COMMAS_AND_DOUBLEQUOTES_DISPLAY_NAME}
            }).collect(Collectors.toMap(data -> (Author) data[0], data -> (String) data[1]));

    private static final List<String> REPO_LEVEL_GLOB_LIST = Arrays.asList("collated**");
    private static final List<String> FIRST_AUTHOR_GLOB_LIST = Arrays.asList("**.java", "collated**");
    private static final List<String> SECOND_AUTHOR_GLOB_LIST = Arrays.asList("**.doc", "collated**");
    private static final List<String> FIRST_AUTHOR_EMAIL_LIST =
            Arrays.asList("nbr@example.com", "nbriannl@test.net", "nbriannl@users.noreply.github.com");

    @Test
    public void repoConfig_noSpecialCharacter_success() throws IOException, InvalidLocationException {
        RepoConfigCsvParser repoConfigCsvParser = new RepoConfigCsvParser(REPO_CONFIG_NO_SPECIAL_CHARACTER_FILE);
        List<RepoConfiguration> configs = repoConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        RepoConfiguration config = configs.get(0);

        Assert.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assert.assertEquals(TEST_REPO_BETA_MASTER_BRANCH, config.getBranch());

        Assert.assertEquals(TEST_REPO_BETA_CONFIG_FORMATS, config.getFileTypeManager().getFormats());

        Assert.assertTrue(config.isStandaloneConfigIgnored());

        Assert.assertEquals(config.getIgnoreCommitList(),
                CommitHash.convertStringsToCommits(TEST_REPO_BETA_CONFIG_IGNORED_COMMITS));
        Assert.assertEquals(config.getIgnoredAuthorsList(), TEST_REPO_BETA_CONFIG_IGNORED_AUTHORS);

        Assert.assertFalse(config.isFormatsOverriding());
        Assert.assertFalse(config.isIgnoreGlobListOverriding());
        Assert.assertFalse(config.isIgnoreCommitListOverriding());
        Assert.assertFalse(config.isIgnoredAuthorsListOverriding());
    }

    @Test
    public void authorConfig_noSpecialCharacter_success() throws IOException, InvalidLocationException {
        AuthorConfigCsvParser authorConfigCsvParser =
                new AuthorConfigCsvParser(AUTHOR_CONFIG_NO_SPECIAL_CHARACTER_FILE);
        List<AuthorConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        AuthorConfiguration config = configs.get(0);

        Assert.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assert.assertEquals(TEST_REPO_BETA_MASTER_BRANCH, config.getBranch());

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

    @Test (expected = IOException.class)
    public void authorConfig_emptyConfig_throwsIoException() throws IOException {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_EMPTY_CONFIG_FILE);
        authorConfigCsvParser.parse();
    }

    @Test
    public void authorConfig_specialCharacter_success() throws IOException, InvalidLocationException {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_SPECIAL_CHARACTER_FILE);
        List<AuthorConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        AuthorConfiguration config = configs.get(0);

        Assert.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assert.assertEquals(TEST_REPO_BETA_MASTER_BRANCH, config.getBranch());

        Assert.assertEquals(AUTHOR_CONFIG_SPECIAL_CHARACTER_AUTHORS, config.getAuthorList());
    }

    @Test
    public void authorConfig_multipleEmails_success() throws IOException {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_MULTIPLE_EMAILS_FILE);
        List<AuthorConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        AuthorConfiguration config = configs.get(0);

        Author actualAuthor = config.getAuthorList().get(0);
        Assert.assertEquals(FIRST_AUTHOR_EMAIL_LIST.size(), actualAuthor.getEmails().size());
        Assert.assertTrue(actualAuthor.getEmails().containsAll(FIRST_AUTHOR_EMAIL_LIST));
    }

    @Test
    public void authorConfig_invalidLocation_success() throws IOException {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_INVALID_LOCATION);
        List<AuthorConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        AuthorConfiguration config = configs.get(0);

        Assert.assertEquals(3, config.getAuthorList().size());
    }

    @Test (expected = IOException.class)
    public void authorConfig_invalidHeaderSize_throwsIoException() throws IOException {
        AuthorConfigCsvParser authorConfigCsvParser = new AuthorConfigCsvParser(AUTHOR_CONFIG_INVALID_HEADER_SIZE);
        authorConfigCsvParser.parse();
    }

    @Test
    public void groupConfig_invalidLocation_success() throws IOException {
        GroupConfigCsvParser groupConfigCsvParser = new GroupConfigCsvParser(GROUP_CONFIG_INVALID_LOCATION_FILE);
        List<GroupConfiguration> groupConfigs = groupConfigCsvParser.parse();

        Assert.assertEquals(1, groupConfigs.size());

        GroupConfiguration actualConfig = groupConfigs.get(0);
        Assert.assertEquals(2, actualConfig.getGroupsList().size());
    }

    @Test
    public void groupConfig_emptyLocation_success() throws IOException {
        GroupConfigCsvParser groupConfigCsvParser = new GroupConfigCsvParser(GROUP_CONFIG_EMPTY_LOCATION_FILE);
        List<GroupConfiguration> groupConfigs = groupConfigCsvParser.parse();

        Assert.assertEquals(2, groupConfigs.size());

        GroupConfiguration actualReposenseConfig = groupConfigs.get(0);
        Assert.assertEquals(2, actualReposenseConfig.getGroupsList().size());

        GroupConfiguration actualEmptyLocationConfig = groupConfigs.get(1);
        Assert.assertEquals(1, actualEmptyLocationConfig.getGroupsList().size());
    }

    @Test
    public void groupConfig_multipleLocations_success() throws IOException {
        GroupConfigCsvParser groupConfigCsvParser = new GroupConfigCsvParser(GROUP_CONFIG_MULTI_LOCATION_FILE);
        List<GroupConfiguration> groupConfigs = groupConfigCsvParser.parse();

        Assert.assertEquals(2, groupConfigs.size());

        GroupConfiguration actualBetaConfig = groupConfigs.get(0);
        Assert.assertEquals(TEST_REPO_BETA_LOCATION, actualBetaConfig.getLocation().toString());
        Assert.assertEquals(TEST_REPO_BETA_GROUPS, actualBetaConfig.getGroupsList());

        GroupConfiguration actualDeltaConfig = groupConfigs.get(1);
        Assert.assertEquals(TEST_REPO_DELTA_LOCATION, actualDeltaConfig.getLocation().toString());
        Assert.assertEquals(TEST_REPO_DELTA_GROUPS, actualDeltaConfig.getGroupsList());
    }

    @Test (expected = IOException.class)
    public void groupConfig_invalidHeaderSize_throwsIoException() throws IOException {
        GroupConfigCsvParser groupConfigCsvParser = new GroupConfigCsvParser(GROUP_CONFIG_INVALID_HEADER_SIZE_FILE);
        groupConfigCsvParser.parse();
    }

    @Test
    public void parse_multipleColumnsWithCommasAndDoubleQuotes_success() throws IOException, InvalidLocationException {
        AuthorConfigCsvParser authorConfigCsvParser =
                new AuthorConfigCsvParser(AUTHOR_CONFIG_COMMAS_AND_DOUBLEQUOTES_FILE);
        List<AuthorConfiguration> configs = authorConfigCsvParser.parse();

        Assert.assertEquals(1, configs.size());

        AuthorConfiguration config = configs.get(0);

        Assert.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assert.assertEquals(TEST_REPO_BETA_MASTER_BRANCH, config.getBranch());
        Assert.assertEquals(AUTHOR_DISPLAY_NAME_COMMAS_AND_DOUBLE_QUOTES_MAP, config.getAuthorDisplayNameMap());

        Assert.assertEquals(AUTHOR_ALIAS_COMMAS_AND_DOUBLE_QUOTES_MAP.size(), config.getAuthorList().size());
        config.getAuthorList().forEach(author -> {
            Assert.assertEquals(AUTHOR_ALIAS_COMMAS_AND_DOUBLE_QUOTES_MAP.get(author), author.getAuthorAliases());
        });
    }

    @Test
    public void merge_twoRepoConfigs_success() throws ParseException, IOException, HelpScreenException {
        FIRST_AUTHOR.setIgnoreGlobList(FIRST_AUTHOR_GLOB_LIST);
        SECOND_AUTHOR.setIgnoreGlobList(SECOND_AUTHOR_GLOB_LIST);
        SECOND_AUTHOR.setAuthorAliases(SECOND_AUTHOR_ALIASES);

        List<Author> expectedAuthors = new ArrayList<>();
        expectedAuthors.add(FIRST_AUTHOR);
        expectedAuthors.add(SECOND_AUTHOR);

        RepoConfiguration firstRepo = new RepoConfiguration(new RepoLocation(TEST_REPO_BETA_LOCATION),
                TEST_REPO_BETA_MASTER_BRANCH);
        firstRepo.setAuthorList(expectedAuthors);
        firstRepo.setAuthorDisplayName(FIRST_AUTHOR, "Nbr");
        firstRepo.setAuthorDisplayName(SECOND_AUTHOR, "Zac");
        firstRepo.addAuthorEmailsAndAliasesMapEntry(SECOND_AUTHOR,  Arrays.asList("Zachary Tang"));
        firstRepo.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);

        RepoConfiguration secondRepo = new RepoConfiguration(new RepoLocation(TEST_REPO_BETA_LOCATION),
                TEST_REPO_BETA_ADD_CONFIG_JSON_BRANCH);
        secondRepo.setAuthorList(Arrays.asList(SECOND_AUTHOR));
        secondRepo.setAuthorDisplayName(SECOND_AUTHOR, "Zac");
        secondRepo.addAuthorEmailsAndAliasesMapEntry(SECOND_AUTHOR,  Arrays.asList("Zachary Tang"));
        secondRepo.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);

        String input = new InputBuilder().addConfig(TEST_CONFIG_FOLDER).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        List<AuthorConfiguration> authorConfigs =
                new AuthorConfigCsvParser(((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()).parse();
        RepoConfiguration.merge(actualConfigs, authorConfigs);

        Assert.assertEquals(2, actualConfigs.size());
        TestUtil.compareRepoConfig(firstRepo, actualConfigs.get(0));
        TestUtil.compareRepoConfig(secondRepo, actualConfigs.get(1));
    }

    @Test
    public void merge_emptyLocation_success() throws ParseException, IOException, HelpScreenException {
        FIRST_AUTHOR.setIgnoreGlobList(FIRST_AUTHOR_GLOB_LIST);
        SECOND_AUTHOR.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);
        SECOND_AUTHOR.setAuthorAliases(SECOND_AUTHOR_ALIASES);

        List<Author> expectedBetaAuthors = new ArrayList<>();
        expectedBetaAuthors.add(FIRST_AUTHOR);
        expectedBetaAuthors.add(SECOND_AUTHOR);

        List<Author> expectedDeltaAuthors = new ArrayList<>();
        expectedDeltaAuthors.add(FIRST_AUTHOR);

        RepoConfiguration expectedBetaConfig =
                new RepoConfiguration(new RepoLocation(TEST_REPO_BETA_LOCATION), TEST_REPO_BETA_MASTER_BRANCH);
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
    public void repoConfig_defaultBranch_success() throws ParseException, IOException, HelpScreenException {
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

    @Test
    public void repoConfig_overrideKeyword_success() throws ParseException, IOException, HelpScreenException {
        RepoConfigCsvParser repoConfigCsvParser = new RepoConfigCsvParser(REPO_CONFIG_OVERRIDE_KEYWORD_FILE);
        List<RepoConfiguration> configs = repoConfigCsvParser.parse();
        RepoConfiguration config = configs.get(0);

        Assert.assertEquals(1, configs.size());
        Assert.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assert.assertEquals(TEST_REPO_BETA_MASTER_BRANCH, config.getBranch());
        Assert.assertEquals(TEST_REPO_BETA_CONFIG_FORMATS, config.getFileTypeManager().getFormats());
        Assert.assertFalse(config.isStandaloneConfigIgnored());
        Assert.assertEquals(CommitHash.convertStringsToCommits(TEST_REPO_BETA_CONFIG_IGNORED_COMMITS),
                config.getIgnoreCommitList());
        Assert.assertEquals(TEST_REPO_BETA_CONFIG_IGNORED_AUTHORS, config.getIgnoredAuthorsList());

        Assert.assertTrue(config.isFormatsOverriding());
        Assert.assertTrue(config.isIgnoreGlobListOverriding());
        Assert.assertTrue(config.isIgnoreCommitListOverriding());
        Assert.assertTrue(config.isIgnoredAuthorsListOverriding());
    }

    @Test
    public void repoConfig_redundantLines_success() throws ParseException, IOException {
        RepoConfigCsvParser repoConfigCsvParser = new RepoConfigCsvParser(REPO_CONFIG_REDUNDANT_LINES_FILE);
        List<RepoConfiguration> configs = repoConfigCsvParser.parse();

        Assert.assertEquals(3, configs.size());
        RepoConfiguration betaConfig = configs.get(0);
        RepoConfiguration charlieConfig = configs.get(1);
        RepoConfiguration deltaConfig = configs.get(2);

        Assert.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), betaConfig.getLocation());
        Assert.assertEquals(TEST_REPO_BETA_MASTER_BRANCH, betaConfig.getBranch());
        Assert.assertEquals(new RepoLocation(TEST_REPO_CHARLIE_LOCATION), charlieConfig.getLocation());
        Assert.assertEquals(TEST_REPO_CHARLIE_BRANCH, charlieConfig.getBranch());
        Assert.assertEquals(new RepoLocation(TEST_REPO_DELTA_LOCATION), deltaConfig.getLocation());
        Assert.assertEquals(TEST_REPO_DELTA_BRANCH, deltaConfig.getBranch());
        Assert.assertTrue(deltaConfig.isStandaloneConfigIgnored());
    }

    @Test (expected = IOException.class)
    public void repoConfig_invalidHeaderSize_throwsIoException() throws IOException {
        RepoConfigCsvParser repoConfigCsvParser = new RepoConfigCsvParser(REPO_CONFIG_INVALID_HEADER_SIZE_FILE);
        repoConfigCsvParser.parse();
    }
}
