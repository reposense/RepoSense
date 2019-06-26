package reposense.model;

import static org.apache.tools.ant.types.Commandline.translateCommandline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sourceforge.argparse4j.helper.HelpScreenException;
import reposense.RepoSense;
import reposense.git.GitClone;
import reposense.git.exception.GitCloneException;
import reposense.parser.ArgsParser;
import reposense.parser.AuthorConfigCsvParser;
import reposense.parser.CsvParserTest;
import reposense.parser.GroupConfigCsvParser;
import reposense.parser.InvalidLocationException;
import reposense.parser.ParseException;
import reposense.parser.RepoConfigCsvParser;
import reposense.report.ReportGenerator;
import reposense.util.FileUtil;
import reposense.util.InputBuilder;
import reposense.util.TestUtil;

public class RepoConfigurationTest {
    private static final Path IGNORE_STANDALONE_TEST_CONFIG_FILES = new File(CsvParserTest.class.getClassLoader()
            .getResource("RepoConfigurationTest/repoconfig_ignoreStandAlone_test").getFile()).toPath();
    private static final Path IGNORE_STANDALONE_KEYWORD_TEST_CONFIG_FILES =
            new File(CsvParserTest.class.getClassLoader()
                    .getResource("RepoConfigurationTest/repoconfig_ignoreStandAloneKeyword_test").getFile()).toPath();
    private static final Path FORMATS_TEST_CONFIG_FILES = new File(CsvParserTest.class.getClassLoader()
            .getResource("RepoConfigurationTest/repoconfig_formats_test").getFile()).toPath();
    private static final Path WITHOUT_FORMATS_TEST_CONFIG_FILES = new File(CsvParserTest.class.getClassLoader()
            .getResource("RepoConfigurationTest/repoconfig_withoutformats_test").getFile()).toPath();
    private static final Path GROUPS_TEST_CONFIG_FILES = new File(CsvParserTest.class.getClassLoader()
        .getResource("RepoConfigurationTest/repoconfig_groups_test").getFile()).toPath();
    private static final Path OVERRIDE_STANDALONE_TEST_CONFIG_FILE = new File(CsvParserTest.class.getClassLoader()
                    .getResource("RepoConfigurationTest/repoconfig_overrideStandAlone_test").getFile()).toPath();

    private static final String TEST_REPO_DELTA = "https://github.com/reposense/testrepo-Delta.git";

    private static final Author FIRST_AUTHOR = new Author("lithiumlkid");
    private static final Author SECOND_AUTHOR = new Author("codeeong");
    private static final Author THIRD_AUTHOR = new Author("jordancjq");
    private static final Author FOURTH_AUTHOR = new Author("lohtianwei");

    private static final List<String> FIRST_AUTHOR_ALIASES = Collections.singletonList("Ahmad Syafiq");
    private static final List<String> SECOND_AUTHOR_ALIASES = Collections.emptyList();
    private static final List<String> THIRD_AUTHOR_ALIASES = Collections.singletonList("Jordan Chong");
    private static final List<String> FOURTH_AUTHOR_ALIASES = Collections.singletonList("Tianwei");

    private static final List<String> REPO_LEVEL_GLOB_LIST = Collections.singletonList("collated**");
    private static final List<String> FIRST_AUTHOR_GLOB_LIST =
            Arrays.asList("*.aa1", "**.aa2", "**.java", "collated**");
    private static final List<String> SECOND_AUTHOR_GLOB_LIST = Arrays.asList("", "collated**");
    private static final List<String> THIRD_AUTHOR_GLOB_LIST = Arrays.asList("**[!(.md)]", "collated**");
    private static final List<String> FOURTH_AUTHOR_GLOB_LIST = Collections.singletonList("collated**");

    private static final List<Format> CONFIG_FORMATS = Format.convertStringsToFormats(Arrays.asList(
            "java", "adoc", "md"));
    private static final List<Group> CONFIG_GROUPS = TestUtil.convertStringsToGroups(Arrays.asList("test: **/test/*",
        "code: **/*.java", "docs: **/docs/*"));
    private static final List<String> CLI_FORMATS = Arrays.asList("css", "html");

    private static RepoConfiguration repoDeltaStandaloneConfig;

    @BeforeClass
    public static void setUp() throws InvalidLocationException {
        FIRST_AUTHOR.setAuthorAliases(FIRST_AUTHOR_ALIASES);
        SECOND_AUTHOR.setAuthorAliases(SECOND_AUTHOR_ALIASES);
        THIRD_AUTHOR.setAuthorAliases(THIRD_AUTHOR_ALIASES);
        FOURTH_AUTHOR.setAuthorAliases(FOURTH_AUTHOR_ALIASES);

        FIRST_AUTHOR.setIgnoreGlobList(FIRST_AUTHOR_GLOB_LIST);
        SECOND_AUTHOR.setIgnoreGlobList(SECOND_AUTHOR_GLOB_LIST);
        THIRD_AUTHOR.setIgnoreGlobList(THIRD_AUTHOR_GLOB_LIST);
        FOURTH_AUTHOR.setIgnoreGlobList(FOURTH_AUTHOR_GLOB_LIST);

        List<Author> expectedAuthors = new ArrayList<>();
        expectedAuthors.add(FIRST_AUTHOR);
        expectedAuthors.add(SECOND_AUTHOR);
        expectedAuthors.add(THIRD_AUTHOR);
        expectedAuthors.add(FOURTH_AUTHOR);

        repoDeltaStandaloneConfig = new RepoConfiguration(new RepoLocation(TEST_REPO_DELTA), "master");
        repoDeltaStandaloneConfig.setAuthorList(expectedAuthors);
        repoDeltaStandaloneConfig.addAuthorEmailsAndAliasesMapEntry(FIRST_AUTHOR, FIRST_AUTHOR_ALIASES);
        repoDeltaStandaloneConfig.addAuthorEmailsAndAliasesMapEntry(FOURTH_AUTHOR, FOURTH_AUTHOR_ALIASES);
        repoDeltaStandaloneConfig.setAuthorDisplayName(FIRST_AUTHOR, "Ahm");
        repoDeltaStandaloneConfig.setAuthorDisplayName(SECOND_AUTHOR, "Cod");
        repoDeltaStandaloneConfig.setAuthorDisplayName(THIRD_AUTHOR, "Jor");
        repoDeltaStandaloneConfig.setAuthorDisplayName(FOURTH_AUTHOR, "Loh");

        SECOND_AUTHOR.setEmails(Arrays.asList("codeeong@gmail.com", "33129797+codeeong@users.noreply.github.com"));
        for (Author author : expectedAuthors) {
            repoDeltaStandaloneConfig.addAuthorEmailsAndAliasesMapEntry(author, author.getEmails());
        }

        repoDeltaStandaloneConfig.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);
        repoDeltaStandaloneConfig.setFormats(CONFIG_FORMATS);
    }

    @Before
    public void cleanRepoDirectory() throws IOException {
        FileUtil.deleteDirectory(FileUtil.REPOS_ADDRESS);
    }

    @Test
    public void repoConfig_usesStandaloneConfig_success() throws GitCloneException, InvalidLocationException {
        RepoConfiguration actualConfig = new RepoConfiguration(new RepoLocation(TEST_REPO_DELTA), "master");
        GitClone.clone(actualConfig);
        ReportGenerator.updateRepoConfig(actualConfig);

        TestUtil.compareRepoConfig(repoDeltaStandaloneConfig, actualConfig);
    }

    @Test
    public void repoConfig_ignoresStandaloneConfig_success()
            throws ParseException, GitCloneException, IOException, HelpScreenException {
        List<Author> expectedAuthors = new ArrayList<>();
        Author author = new Author(FIRST_AUTHOR);
        author.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);
        expectedAuthors.add(author);

        RepoConfiguration expectedConfig = new RepoConfiguration(new RepoLocation(TEST_REPO_DELTA), "master");
        expectedConfig.setAuthorList(expectedAuthors);
        expectedConfig.addAuthorEmailsAndAliasesMapEntry(author, FIRST_AUTHOR_ALIASES);
        expectedConfig.setAuthorDisplayName(author, "Ahm");

        expectedConfig.setIgnoreGlobList(REPO_LEVEL_GLOB_LIST);
        expectedConfig.setFormats(CONFIG_FORMATS);
        expectedConfig.setStandaloneConfigIgnored(true);

        String formats = String.join(" ", CLI_FORMATS);
        String input = new InputBuilder().addConfig(IGNORE_STANDALONE_TEST_CONFIG_FILES)
                .addFormats(formats)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        List<AuthorConfiguration> authorConfigs =
                new AuthorConfigCsvParser(((ConfigCliArguments) cliArguments).getAuthorConfigFilePath()).parse();
        RepoConfiguration.merge(actualConfigs, authorConfigs);

        RepoConfiguration actualConfig = actualConfigs.get(0);
        GitClone.clone(actualConfig);
        ReportGenerator.updateRepoConfig(actualConfig);

        TestUtil.compareRepoConfig(expectedConfig, actualConfig);
    }

    @Test
    public void repoConfig_ignoresStandaloneConfigInCli_success()
            throws ParseException, GitCloneException, HelpScreenException {
        RepoConfiguration expectedConfig = new RepoConfiguration(new RepoLocation(TEST_REPO_DELTA), "master");
        expectedConfig.setFormats(Format.convertStringsToFormats(CLI_FORMATS));
        expectedConfig.setStandaloneConfigIgnored(true);

        String formats = String.join(" ", CLI_FORMATS);
        String input = new InputBuilder().addRepos(TEST_REPO_DELTA)
                .addFormats(formats)
                .addIgnoreStandaloneConfig()
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));
        List<RepoConfiguration> actualConfigs = RepoSense.getRepoConfigurations((LocationsCliArguments) cliArguments);
        RepoConfiguration.setFormatsToRepoConfigs(actualConfigs, cliArguments.getFormats());
        RepoConfiguration actualConfig = actualConfigs.get(0);
        GitClone.clone(actualConfig);
        ReportGenerator.updateRepoConfig(actualConfig);

        TestUtil.compareRepoConfig(expectedConfig, actualConfig);
    }

    @Test
    public void repoConfig_wrongKeywordUseStandaloneConfig_success()
            throws ParseException, GitCloneException, IOException, HelpScreenException {
        String formats = String.join(" ", CLI_FORMATS);
        String input = new InputBuilder().addConfig(IGNORE_STANDALONE_KEYWORD_TEST_CONFIG_FILES)
                .addFormats(formats)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();

        RepoConfiguration actualConfig = actualConfigs.get(0);
        GitClone.clone(actualConfig);
        ReportGenerator.updateRepoConfig(actualConfig);

        TestUtil.compareRepoConfig(repoDeltaStandaloneConfig, actualConfig);
    }

    @Test
    public void repoConfig_withFormats_ignoreCliFormats() throws ParseException, IOException, HelpScreenException {
        String formats = String.join(" ", CLI_FORMATS);
        String input = new InputBuilder().addConfig(FORMATS_TEST_CONFIG_FILES)
                .addFormats(formats)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        RepoConfiguration.setFormatsToRepoConfigs(actualConfigs, cliArguments.getFormats());

        Assert.assertEquals(1, actualConfigs.size());
        Assert.assertEquals(CONFIG_FORMATS, actualConfigs.get(0).getFormats());
    }

    @Test
    public void repoConfig_withoutFormats_useCliFormats() throws ParseException, IOException, HelpScreenException {
        String formats = String.join(" ", CLI_FORMATS);
        String input = new InputBuilder().addConfig(WITHOUT_FORMATS_TEST_CONFIG_FILES)
                .addFormats(formats)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        RepoConfiguration.setFormatsToRepoConfigs(actualConfigs, cliArguments.getFormats());

        Assert.assertEquals(1, actualConfigs.size());
        Assert.assertEquals(Format.convertStringsToFormats(CLI_FORMATS), actualConfigs.get(0).getFormats());
    }

    @Test
    public void repoConfig_withGroups() throws ParseException, IOException, HelpScreenException {
        String input = new InputBuilder().addConfig(GROUPS_TEST_CONFIG_FILES).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
            new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        List<GroupConfiguration> groupConfigs =
                new GroupConfigCsvParser(((ConfigCliArguments) cliArguments).getGroupConfigFilePath()).parse();

        RepoConfiguration.mergeGroups(actualConfigs, groupConfigs);

        Assert.assertEquals(1, actualConfigs.size());
        Assert.assertEquals(CONFIG_GROUPS, actualConfigs.get(0).getGroupList());
    }

    @Test
    public void repoConfig_withoutFormatsAndCliFormats_useDefaultFormats()
            throws ParseException, IOException, HelpScreenException {
        String input = new InputBuilder().addConfig(WITHOUT_FORMATS_TEST_CONFIG_FILES).build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();
        RepoConfiguration.setFormatsToRepoConfigs(actualConfigs, cliArguments.getFormats());

        Assert.assertEquals(1, actualConfigs.size());
        Assert.assertEquals(Format.DEFAULT_FORMATS, actualConfigs.get(0).getFormats());
    }

    @Test
    public void repoConfig_emptyLocationDifferentBranch_equal() throws InvalidLocationException {
        RepoConfiguration emptyLocationEmptyBranchRepoConfig = new RepoConfiguration(new RepoLocation(""), "");
        RepoConfiguration emptyLocationDefaultBranchRepoConfig = new RepoConfiguration(new RepoLocation(""));
        RepoConfiguration emptyLocationWithBranchRepoConfig = new RepoConfiguration(new RepoLocation(""), "master");

        Assert.assertEquals(emptyLocationDefaultBranchRepoConfig, emptyLocationEmptyBranchRepoConfig);
        Assert.assertEquals(emptyLocationWithBranchRepoConfig, emptyLocationEmptyBranchRepoConfig);
    }

    @Test
    public void repoConfig_sameLocationDifferentBranch_notEqual() throws InvalidLocationException {
        RepoConfiguration validLocationValidBranchRepoConfig =
                new RepoConfiguration(new RepoLocation(TEST_REPO_DELTA), "master");
        RepoConfiguration validLocationDefaultBranchRepoConfig =
                new RepoConfiguration(new RepoLocation(TEST_REPO_DELTA));

        Assert.assertNotEquals(validLocationDefaultBranchRepoConfig, validLocationValidBranchRepoConfig);
    }

    @Test
    public void repoConfig_overrideStandaloneConfig_success()
            throws ParseException, GitCloneException, IOException, HelpScreenException {
        RepoConfiguration expectedConfig = new RepoConfiguration(new RepoLocation(TEST_REPO_DELTA), "master",
                Collections.emptyList(), Collections.emptyList(), false, Collections.emptyList(),
                true, true, true);

        List<Author> expectedAuthorList = new ArrayList<>();
        Author[] authors = new Author[]{FIRST_AUTHOR, SECOND_AUTHOR, THIRD_AUTHOR, FOURTH_AUTHOR};
        for (Author author : authors) {
            Author expectedAuthor = new Author(author);
            List<String> expectedAuthorIgnoreGlobList = new ArrayList<>();
            expectedAuthorIgnoreGlobList.addAll(author.getIgnoreGlobList());

            // Authors' original ignoreGlobList contains values from StandaloneConfig repo level, thus need to remove
            expectedAuthorIgnoreGlobList.removeAll(REPO_LEVEL_GLOB_LIST);
            expectedAuthor.setIgnoreGlobList(expectedAuthorIgnoreGlobList);
            expectedAuthorList.add(expectedAuthor);
        }
        expectedConfig.setAuthorList(expectedAuthorList);
        expectedConfig.setAuthorDisplayNameMap(repoDeltaStandaloneConfig.getAuthorDisplayNameMap());
        expectedConfig.setAuthorEmailsAndAliasesMap(repoDeltaStandaloneConfig.getAuthorEmailsAndAliasesMap());

        String formats = String.join(" ", CLI_FORMATS);
        String input = new InputBuilder().addConfig(OVERRIDE_STANDALONE_TEST_CONFIG_FILE)
                .addFormats(formats)
                .build();
        CliArguments cliArguments = ArgsParser.parse(translateCommandline(input));

        List<RepoConfiguration> actualConfigs =
                new RepoConfigCsvParser(((ConfigCliArguments) cliArguments).getRepoConfigFilePath()).parse();

        RepoConfiguration actualConfig = actualConfigs.get(0);
        GitClone.clone(actualConfig);
        ReportGenerator.updateRepoConfig(actualConfig);

        TestUtil.compareRepoConfig(expectedConfig, actualConfig);
    }
}
