package reposense.parser;

import static org.apache.tools.ant.types.Commandline.translateCommandline;

import java.io.File;
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
import reposense.model.FileType;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.util.InputBuilder;
import reposense.util.TestUtil;

public class RepoConfigParserTest {
    private static final Path TEST_CONFIG_FOLDER = new File(RepoConfigParserTest.class.getClassLoader()
            .getResource("repoconfig_merge_test").getFile()).toPath();
    private static final Path TEST_EMPTY_BRANCH_CONFIG_FOLDER = new File(RepoConfigParserTest.class.getClassLoader()
            .getResource("repoconfig_empty_branch_test").getFile()).toPath();
    private static final Path REPO_CONFIG_NO_SPECIAL_CHARACTER_FILE = new File(RepoConfigParserTest.class
            .getClassLoader().getResource("RepoConfigParserTest/repoconfig_noSpecialCharacter_test.csv")
            .getFile()).toPath();
    private static final Path REPO_CONFIG_OVERRIDE_KEYWORD_FILE = new File(RepoConfigParserTest.class.getClassLoader()
            .getResource("RepoConfigParserTest/repoconfig_overrideKeyword_test.csv").getFile()).toPath();
    private static final Path REPO_CONFIG_REDUNDANT_LINES_FILE = new File(RepoConfigParserTest.class.getClassLoader()
            .getResource("RepoConfigParserTest/require_trailing_whitespaces/repoconfig_redundantLines_test.csv")
            .getFile()).toPath();
    private static final Path REPO_CONFIG_INVALID_HEADER_SIZE_FILE = new File(RepoConfigParserTest.class
            .getClassLoader().getResource("RepoConfigParserTest/repoconfig_invalidHeaderSize_test.csv")
            .getFile()).toPath();
    private static final Path MERGE_EMPTY_LOCATION_FOLDER = new File(RepoConfigParserTest.class.getClassLoader()
            .getResource("RepoConfigParserTest/repoconfig_merge_empty_location_test").getFile()).toPath();
    private static final Path REPO_CONFIG_ZERO_VALID_RECORDS = new File(RepoConfigParserTest.class.getClassLoader()
            .getResource("CsvParserTest/repoconfig_zeroValidRecords_test.csv").getFile()).toPath();

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

        Assert.assertEquals(1, configs.size());

        RepoConfiguration config = configs.get(0);

        Assert.assertEquals(new RepoLocation(TEST_REPO_BETA_LOCATION), config.getLocation());
        Assert.assertEquals(TEST_REPO_BETA_MASTER_BRANCH, config.getBranch());

        Assert.assertEquals(TEST_REPO_BETA_CONFIG_FORMATS, config.getFileTypeManager().getFormats());

        Assert.assertTrue(config.isStandaloneConfigIgnored());

        Assert.assertEquals(config.getIgnoreCommitList(),
                CommitHash.convertStringsToCommits(TEST_REPO_BETA_CONFIG_IGNORED_COMMITS));

        Assert.assertFalse(config.isFormatsOverriding());
        Assert.assertFalse(config.isIgnoreGlobListOverriding());
        Assert.assertFalse(config.isIgnoreCommitListOverriding());
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
    public void repoConfig_defaultBranch_success() throws Exception {
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
    public void repoConfig_overrideKeyword_success() throws Exception {
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

        Assert.assertTrue(config.isFormatsOverriding());
        Assert.assertTrue(config.isIgnoreGlobListOverriding());
        Assert.assertTrue(config.isIgnoreCommitListOverriding());
    }

    @Test
    public void repoConfig_redundantLines_success() throws Exception {
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

    @Test (expected = InvalidCsvException.class)
    public void repoConfig_invalidHeaderSize_throwsInvalidCsvException() throws Exception {
        RepoConfigCsvParser repoConfigCsvParser = new RepoConfigCsvParser(REPO_CONFIG_INVALID_HEADER_SIZE_FILE);
        repoConfigCsvParser.parse();
    }

    @Test (expected = InvalidCsvException.class)
    public void repoConfig_zeroValidRecords_throwsInvalidCsvException() throws Exception {
        RepoConfigCsvParser repoConfigCsvParser = new RepoConfigCsvParser(REPO_CONFIG_ZERO_VALID_RECORDS);
        repoConfigCsvParser.parse();
    }
}
