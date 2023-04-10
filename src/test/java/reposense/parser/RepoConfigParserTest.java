package reposense.parser;

import static org.apache.tools.ant.types.Commandline.translateCommandline;
import static reposense.model.RepoConfiguration.DEFAULT_FILE_SIZE_LIMIT;
import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.model.Author;
import reposense.model.AuthorConfiguration;
import reposense.model.CliArguments;
import reposense.model.CommitHash;
import reposense.model.FileType;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.util.InputBuilder;
import reposense.util.TestUtil;

public class RepoConfigParserTest {
    private static final Path TEST_CONFIG_FOLDER = loadResource(RepoConfigParserTest.class, "repoconfig_merge_test");
    private static final Path TEST_EMPTY_BRANCH_CONFIG_FOLDER = loadResource(RepoConfigParserTest.class,
            "repoconfig_empty_branch_test");
    private static final Path REPO_CONFIG_NO_SPECIAL_CHARACTER_FILE = loadResource(RepoConfigParserTest.class,
            "RepoConfigParserTest/repoconfig_noSpecialCharacter_test.csv");
    private static final Path REPO_CONFIG_OVERRIDE_KEYWORD_FILE = loadResource(RepoConfigParserTest.class,
            "RepoConfigParserTest/repoconfig_overrideKeyword_test.csv");
    private static final Path REPO_CONFIG_REDUNDANT_LINES_FILE = loadResource(RepoConfigParserTest.class,
            "RepoConfigParserTest/require_trailing_whitespaces/repoconfig_redundantLines_test.csv");
    private static final Path REPO_CONFIG_UNRECOGNIZED_VALUES_FOR_YES_KEYWORD_HEADERS_FILE =
            loadResource(RepoConfigParserTest.class,
            "RepoConfigParserTest/repoconfig_unrecognizedValuesForYesKeywordHeaders_test.csv");
    private static final Path REPO_CONFIG_DUPLICATE_HEADERS_CASE_SENSITIVE_FILE =
            loadResource(RepoConfigParserTest.class,
            "RepoConfigParserTest/repoconfig_duplicateHeadersCaseSensitive_test.csv");
    private static final Path REPO_CONFIG_DUPLICATE_HEADERS_CASE_INSENSITIVE_FILE =
            loadResource(RepoConfigParserTest.class,
            "RepoConfigParserTest/repoconfig_duplicateHeadersCaseInsensitive_test.csv");
    private static final Path REPO_CONFIG_DIFFERENT_COLUMN_ORDER_FILE = loadResource(RepoConfigParserTest.class,
            "RepoConfigParserTest/repoconfig_differentColumnOrder_test.csv");
    private static final Path REPO_CONFIG_OPTIONAL_HEADER_MISSING_FILE = loadResource(RepoConfigParserTest.class,
            "RepoConfigParserTest/repoconfig_missingOptionalHeader_test.csv");
    private static final Path REPO_CONFIG_MANDATORY_HEADER_MISSING_FILE = loadResource(RepoConfigParserTest.class,
            "RepoConfigParserTest/repoconfig_missingMandatoryHeader_test.csv");
    private static final Path MERGE_EMPTY_LOCATION_FOLDER = loadResource(RepoConfigParserTest.class,
            "RepoConfigParserTest/repoconfig_merge_empty_location_test");
    private static final Path REPO_CONFIG_UNKNOWN_HEADER_FILE = loadResource(RepoConfigParserTest.class,
            "RepoConfigParserTest/repoconfig_unknownHeaders_test.csv");
    private static final Path REPO_CONFIG_INVALID_FILE_SIZE_LIMIT = loadResource(RepoConfigParserTest.class,
            "RepoConfigParserTest/repoconfig_invalidFileSizeLimit_test.csv");
    private static final Path REPO_CONFIG_IGNORE_FILE_SIZE_LIMIT = loadResource(RepoConfigParserTest.class,
            "RepoConfigParserTest/repoconfig_ignoreFileSizeLimit_test.csv");
    private static final Path REPO_CONFIG_ZERO_VALID_RECORDS = loadResource(RepoConfigParserTest.class,
            "CsvParserTest/repoconfig_zeroValidRecords_test.csv");

    private static final String TEST_REPO_BETA_LOCATION = "https://github.com/reposense/testrepo-Beta.git";
    private static final String TEST_REPO_BETA_MASTER_BRANCH = "master";
    private static final String TEST_REPO_BETA_ADD_CONFIG_JSON_BRANCH = "add-config-json";

    private static final String TEST_REPO_DELTA_LOCATION = "https://github.com/reposense/testrepo-Delta.git";
    private static final String TEST_REPO_DELTA_BRANCH = "HEAD";
    private static final List<FileType> TEST_REPO_DELTA_FORMATS =
            FileType.convertFormatStringsToFileTypes(Arrays.asList("java", "fxml"));

    private static final List<FileType> TEST_REPO_BETA_CONFIG_FORMATS =
            FileType.convertFormatStringsToFileTypes(Arrays.asList("java", "adoc", "md"));
    private static final List<String> TEST_REPO_BETA_CONFIG_IGNORED_COMMITS =
            Arrays.asList("abcde12345", "67890fdecba");

    private static final int FILE_SIZE_LIMIT_VALUE = 100000;

    private static final String TEST_REPO_CHARLIE_LOCATION = "https://github.com/reposense/testrepo-Charlie.git";
    private static final String TEST_REPO_CHARLIE_BRANCH = "HEAD";

    private static final Author FIRST_AUTHOR = new Author("nbriannl");
    private static final Author SECOND_AUTHOR = new Author("zacharytang");
    private static final List<String> SECOND_AUTHOR_ALIASES = Arrays.asList("Zachary Tang");

    private static final List<String> REPO_LEVEL_GLOB_LIST = Arrays.asList("collated**");
    private static final List<String> FIRST_AUTHOR_GLOB_LIST = Arrays.asList("**.java", "collated**");
    private static final List<String> SECOND_AUTHOR_GLOB_LIST = Arrays.asList("**.doc", "collated**");

    @Test
    public void repoConfig_noSpecialCharacter_success() throws Exception {
        RepoConfigCsvParser repoConfigCsvParser = new RepoConfigCsvParser(REPO_CONFIG_NO_SPECIAL_CHARACTER_FILE);
        List<RepoConfiguration> configs = repoConfigCsvParser.parse();

        Assertions.assertEquals(1, configs.size());

        RepoConfiguration config = configs.get(0);

        Assertions.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assertions.assertEquals(TEST_REPO_BETA_MASTER_BRANCH, config.getBranch());

        Assertions.assertEquals(TEST_REPO_BETA_CONFIG_FORMATS, config.getFileTypeManager().getFormats());

        Assertions.assertTrue(config.isStandaloneConfigIgnored());

        Assertions.assertEquals(config.getIgnoreCommitList(),
                CommitHash.convertStringsToCommits(TEST_REPO_BETA_CONFIG_IGNORED_COMMITS));
        Assertions.assertEquals(config.getFileSizeLimit(), FILE_SIZE_LIMIT_VALUE);

        Assertions.assertTrue(config.isShallowCloningPerformed());

        Assertions.assertFalse(config.isFormatsOverriding());
        Assertions.assertFalse(config.isIgnoreGlobListOverriding());
        Assertions.assertFalse(config.isIgnoreCommitListOverriding());
        Assertions.assertFalse(config.isFileSizeLimitOverriding());
        Assertions.assertFalse(config.isFileSizeLimitIgnored());
        Assertions.assertTrue(config.isIgnoredFileAnalysisSkipped());
    }

    @Test
    public void merge_twoRepoConfigs_success() throws Exception {
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
        firstRepo.addAuthorNamesToAuthorMapEntry(SECOND_AUTHOR, Arrays.asList("Zachary Tang"));
        firstRepo.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);

        RepoConfiguration secondRepo = new RepoConfiguration(new RepoLocation(TEST_REPO_BETA_LOCATION),
                TEST_REPO_BETA_ADD_CONFIG_JSON_BRANCH);
        secondRepo.setAuthorList(Arrays.asList(SECOND_AUTHOR));
        secondRepo.setAuthorDisplayName(SECOND_AUTHOR, "Zac");
        secondRepo.addAuthorNamesToAuthorMapEntry(SECOND_AUTHOR, Arrays.asList("Zachary Tang"));
        secondRepo.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);

        String input = new InputBuilder().addConfig(TEST_CONFIG_FOLDER).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(cliArguments.getRepoConfigFilePath()).parse();
        List<AuthorConfiguration> authorConfigs =
                new AuthorConfigCsvParser(cliArguments.getAuthorConfigFilePath()).parse();
        RepoConfiguration.merge(actualConfigs, authorConfigs);

        Assertions.assertEquals(2, actualConfigs.size());
        TestUtil.compareRepoConfig(firstRepo, actualConfigs.get(0));
        TestUtil.compareRepoConfig(secondRepo, actualConfigs.get(1));
    }

    @Test
    public void merge_emptyLocation_success() throws Exception {
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
        expectedBetaConfig.addAuthorNamesToAuthorMapEntry(SECOND_AUTHOR, Arrays.asList("Zachary Tang"));
        expectedBetaConfig.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);
        expectedBetaConfig.setIsShallowCloningPerformed(true);

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
                new RepoConfigCsvParser(cliArguments.getRepoConfigFilePath()).parse();
        List<AuthorConfiguration> authorConfigs =
                new AuthorConfigCsvParser(cliArguments.getAuthorConfigFilePath()).parse();
        RepoConfiguration.merge(actualConfigs, authorConfigs);

        Assertions.assertEquals(2, actualConfigs.size());
        Assertions.assertEquals(expectedConfigs, actualConfigs);

        TestUtil.compareRepoConfig(expectedConfigs.get(0), actualConfigs.get(0));
        TestUtil.compareRepoConfig(expectedConfigs.get(1), actualConfigs.get(1));
    }

    @Test
    public void repoConfig_defaultBranch_success() throws Exception {
        RepoConfiguration expectedConfig = new RepoConfiguration(new RepoLocation(TEST_REPO_BETA_LOCATION),
                RepoConfiguration.DEFAULT_BRANCH);

        String input = new InputBuilder().addConfig(TEST_EMPTY_BRANCH_CONFIG_FOLDER).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(cliArguments.getRepoConfigFilePath()).parse();
        List<AuthorConfiguration> authorConfigs =
                new AuthorConfigCsvParser(cliArguments.getAuthorConfigFilePath()).parse();
        RepoConfiguration.merge(actualConfigs, authorConfigs);

        Assertions.assertEquals(1, actualConfigs.size());
        Assertions.assertEquals(expectedConfig.getBranch(), actualConfigs.get(0).getBranch());
        Assertions.assertEquals(expectedConfig.getBranch(), authorConfigs.get(0).getBranch());
    }

    @Test
    public void repoConfig_overrideKeyword_success() throws Exception {
        RepoConfigCsvParser repoConfigCsvParser = new RepoConfigCsvParser(REPO_CONFIG_OVERRIDE_KEYWORD_FILE);
        List<RepoConfiguration> configs = repoConfigCsvParser.parse();
        RepoConfiguration config = configs.get(0);

        Assertions.assertEquals(1, configs.size());
        Assertions.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assertions.assertEquals(TEST_REPO_BETA_MASTER_BRANCH, config.getBranch());
        Assertions.assertEquals(TEST_REPO_BETA_CONFIG_FORMATS, config.getFileTypeManager().getFormats());
        Assertions.assertFalse(config.isStandaloneConfigIgnored());
        Assertions.assertEquals(CommitHash.convertStringsToCommits(TEST_REPO_BETA_CONFIG_IGNORED_COMMITS),
                config.getIgnoreCommitList());
        Assertions.assertEquals(FILE_SIZE_LIMIT_VALUE, config.getFileSizeLimit());

        Assertions.assertTrue(config.isFormatsOverriding());
        Assertions.assertTrue(config.isIgnoreGlobListOverriding());
        Assertions.assertTrue(config.isIgnoreCommitListOverriding());
        Assertions.assertTrue(config.isFileSizeLimitOverriding());
    }

    @Test
    public void repoConfig_redundantLines_success() throws Exception {
        RepoConfigCsvParser repoConfigCsvParser = new RepoConfigCsvParser(REPO_CONFIG_REDUNDANT_LINES_FILE);
        List<RepoConfiguration> configs = repoConfigCsvParser.parse();

        Assertions.assertEquals(3, configs.size());
        RepoConfiguration betaConfig = configs.get(0);
        RepoConfiguration charlieConfig = configs.get(1);
        RepoConfiguration deltaConfig = configs.get(2);

        Assertions.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), betaConfig.getLocation());
        Assertions.assertEquals(TEST_REPO_BETA_MASTER_BRANCH, betaConfig.getBranch());
        Assertions.assertEquals(new RepoLocation(TEST_REPO_CHARLIE_LOCATION), charlieConfig.getLocation());
        Assertions.assertEquals(TEST_REPO_CHARLIE_BRANCH, charlieConfig.getBranch());
        Assertions.assertEquals(new RepoLocation(TEST_REPO_DELTA_LOCATION), deltaConfig.getLocation());
        Assertions.assertEquals(TEST_REPO_DELTA_BRANCH, deltaConfig.getBranch());
        Assertions.assertTrue(deltaConfig.isStandaloneConfigIgnored());
    }

    @Test
    public void repoConfig_differentColumnOrder_success() throws Exception {
        RepoConfigCsvParser repoConfigCsvParser = new RepoConfigCsvParser(REPO_CONFIG_DIFFERENT_COLUMN_ORDER_FILE);
        List<RepoConfiguration> configs = repoConfigCsvParser.parse();

        Assertions.assertEquals(1, configs.size());

        RepoConfiguration config = configs.get(0);

        Assertions.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assertions.assertEquals(TEST_REPO_BETA_MASTER_BRANCH, config.getBranch());

        Assertions.assertEquals(TEST_REPO_BETA_CONFIG_FORMATS, config.getFileTypeManager().getFormats());

        Assertions.assertTrue(config.isStandaloneConfigIgnored());

        Assertions.assertEquals(config.getIgnoreCommitList(),
                CommitHash.convertStringsToCommits(TEST_REPO_BETA_CONFIG_IGNORED_COMMITS));

        Assertions.assertFalse(config.isFormatsOverriding());
        Assertions.assertFalse(config.isIgnoreGlobListOverriding());
        Assertions.assertFalse(config.isIgnoreCommitListOverriding());
    }

    @Test
    public void repoConfig_missingOptionalHeader_success() throws Exception {
        RepoConfigCsvParser repoConfigCsvParser = new RepoConfigCsvParser(REPO_CONFIG_OPTIONAL_HEADER_MISSING_FILE);
        List<RepoConfiguration> configs = repoConfigCsvParser.parse();

        Assertions.assertEquals(1, configs.size());

        RepoConfiguration config = configs.get(0);

        Assertions.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assertions.assertEquals(TEST_REPO_BETA_MASTER_BRANCH, config.getBranch());

        Assertions.assertEquals(TEST_REPO_BETA_CONFIG_FORMATS, config.getFileTypeManager().getFormats());
        Assertions.assertEquals(DEFAULT_FILE_SIZE_LIMIT, config.getFileSizeLimit());

        Assertions.assertTrue(config.isStandaloneConfigIgnored());

        Assertions.assertFalse(config.isFormatsOverriding());
        Assertions.assertFalse(config.isIgnoreGlobListOverriding());
        Assertions.assertFalse(config.isIgnoreCommitListOverriding());
        Assertions.assertFalse(config.isFileSizeLimitOverriding());
    }

    @Test
    public void repoConfig_withUnrecognizedValuesForYesKeywordHeaders_valuesIgnored() throws Exception {
        RepoConfigCsvParser repoConfigCsvParser =
                new RepoConfigCsvParser(REPO_CONFIG_UNRECOGNIZED_VALUES_FOR_YES_KEYWORD_HEADERS_FILE);
        List<RepoConfiguration> configs = repoConfigCsvParser.parse();

        Assertions.assertFalse(configs.get(0).isStandaloneConfigIgnored());
        Assertions.assertFalse(configs.get(0).isShallowCloningPerformed());
        Assertions.assertFalse(configs.get(0).isFindingPreviousAuthorsPerformed());
        Assertions.assertFalse(configs.get(0).isFileSizeLimitIgnored());
        Assertions.assertFalse(configs.get(0).isIgnoredFileAnalysisSkipped());
    }

    @Test
    public void repoConfig_invalidFileSizeLimit_valueIgnored() throws Exception {
        RepoConfigCsvParser repoConfigCsvParser =
                new RepoConfigCsvParser(REPO_CONFIG_INVALID_FILE_SIZE_LIMIT);
        List<RepoConfiguration> configs = repoConfigCsvParser.parse();

        Assertions.assertEquals(configs.get(0).getFileSizeLimit(), DEFAULT_FILE_SIZE_LIMIT);
        Assertions.assertFalse(configs.get(0).isFileSizeLimitOverriding());
    }

    @Test
    public void repoConfig_ignoreFileSizeLimit_ignoreFileSizeColumns() throws Exception {
        RepoConfigCsvParser repoConfigCsvParser =
                new RepoConfigCsvParser(REPO_CONFIG_IGNORE_FILE_SIZE_LIMIT);
        List<RepoConfiguration> configs = repoConfigCsvParser.parse();

        Assertions.assertTrue(configs.get(0).isFileSizeLimitIgnored());
        Assertions.assertFalse(configs.get(0).isFileSizeLimitOverriding());
        Assertions.assertFalse(configs.get(0).isIgnoredFileAnalysisSkipped());
    }

    @Test
    public void repoConfig_mandatoryHeaderMissing_throwsInvalidCsvException() throws Exception {
        RepoConfigCsvParser repoConfigCsvParser = new RepoConfigCsvParser(REPO_CONFIG_MANDATORY_HEADER_MISSING_FILE);
        Assertions.assertThrows(InvalidCsvException.class, () -> repoConfigCsvParser.parse());
    }

    @Test
    public void repoConfig_zeroValidRecords_throwsInvalidCsvException() throws Exception {
        RepoConfigCsvParser repoConfigCsvParser = new RepoConfigCsvParser(REPO_CONFIG_ZERO_VALID_RECORDS);
        Assertions.assertThrows(InvalidCsvException.class, () -> repoConfigCsvParser.parse());
    }

    @Test
    public void repoConfig_duplicateHeadersCaseSensitive_throwsInvalidCsvException() throws Exception {
        RepoConfigCsvParser repoConfigCsvParser =
                new RepoConfigCsvParser(REPO_CONFIG_DUPLICATE_HEADERS_CASE_SENSITIVE_FILE);
        Assertions.assertThrows(InvalidCsvException.class, () -> repoConfigCsvParser.parse());
    }

    @Test
    public void repoConfig_duplicateHeadersCaseInsensitive_throwsInvalidCsvException() throws Exception {
        RepoConfigCsvParser repoConfigCsvParser =
                new RepoConfigCsvParser(REPO_CONFIG_DUPLICATE_HEADERS_CASE_INSENSITIVE_FILE);
        Assertions.assertThrows(InvalidCsvException.class, () -> repoConfigCsvParser.parse());
    }

    @Test
    public void repoConfig_unknownHeaders_throwsInvalidHeaderException() throws Exception {
        RepoConfigCsvParser repoConfigCsvParser =
                new RepoConfigCsvParser(REPO_CONFIG_UNKNOWN_HEADER_FILE);
        Assertions.assertThrows(InvalidHeaderException.class, () -> repoConfigCsvParser.parse());
    }
}
