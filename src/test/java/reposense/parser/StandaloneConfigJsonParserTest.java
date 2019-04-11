package reposense.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonSyntaxException;

import reposense.model.Author;
import reposense.model.CommitHash;
import reposense.model.Format;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.model.StandaloneConfig;
import reposense.util.TestUtil;

public class StandaloneConfigJsonParserTest {

    private static final Path STANDALONE_MALFORMED_CONFIG = new File(
            StandaloneConfigJsonParserTest.class.getClassLoader()
            .getResource("StandaloneConfigJsonParserTest/standaloneConfig_malformedJson.json").getFile()).toPath();

    private static final Path STANDALONE_UNKNOWN_PROPERTY_CONFIG = new File(
            StandaloneConfigJsonParserTest.class.getClassLoader().getResource(
                    "StandaloneConfigJsonParserTest/standaloneConfig_unknownPropertyInJson.json").getFile()).toPath();

    private static final Path STANDALONE_CONFIG_FULL = new File(
            StandaloneConfigJsonParserTest.class.getClassLoader().getResource(
                    "StandaloneConfigJsonParserTest/standaloneConfig_full.json").getFile()).toPath();

    private static final Path STANDALONE_CONFIG_EMPTY_TEXT_FILE = new File(
            StandaloneConfigJsonParserTest.class.getClassLoader().getResource(
                    "StandaloneConfigJsonParserTest/standaloneConfig_emptyText.json").getFile()).toPath();

    private static final Path STANDALONE_CONFIG_EMPTY_JSON_FILE = new File(
            StandaloneConfigJsonParserTest.class.getClassLoader().getResource(
                    "StandaloneConfigJsonParserTest/standaloneConfig_emptyJson.json").getFile()).toPath();

    private static final Path STANDALONE_CONFIG_GITHUBID_ONLY = new File(
            StandaloneConfigJsonParserTest.class.getClassLoader().getResource(
                    "StandaloneConfigJsonParserTest/standaloneConfig_githubId_only.json").getFile()).toPath();

    private static final String TEST_DUMMY_LOCATION = "https://github.com/reposense/RepoSense.git";

    private static RepoConfiguration expectedGithubIdOnlyRepoconfig;
    private static RepoConfiguration expectedFullRepoConfig;

    @BeforeClass
    public static void setUp() throws InvalidLocationException {
        Author author = new Author("yong24s");
        author.setAuthorAliases(Arrays.asList("Yong Hao TENG"));
        author.setIgnoreGlobList(Arrays.asList("**.css", "**.html", "**.jade", "**.js"));

        expectedGithubIdOnlyRepoconfig = new RepoConfiguration(new RepoLocation(TEST_DUMMY_LOCATION));
        expectedGithubIdOnlyRepoconfig.setFormats(Format.DEFAULT_FORMATS);
        expectedGithubIdOnlyRepoconfig.setAuthorList(Arrays.asList(new Author("yong24s")));
        expectedGithubIdOnlyRepoconfig.addAuthorEmailsAndAliasesMapEntry(author, author.getEmails());

        expectedFullRepoConfig = new RepoConfiguration(new RepoLocation(TEST_DUMMY_LOCATION));
        expectedFullRepoConfig.setFormats(Format.convertStringsToFormats(
                Arrays.asList("gradle", "jade", "java", "js", "md", "scss", "yml")));
        expectedFullRepoConfig.setIgnoreCommitList(Arrays.asList(new CommitHash(
                "7b96c563eb2d3612aa5275364333664a18f01491")));
        expectedFullRepoConfig.setIgnoreGlobList(Arrays.asList("**.adoc", "collate**"));
        expectedFullRepoConfig.setAuthorList(Arrays.asList(author));
        expectedFullRepoConfig.setAuthorDisplayName(author, "Yong Hao");
        expectedFullRepoConfig.addAuthorEmailsAndAliasesMapEntry(author, Arrays.asList(author.getGitId()));
        expectedFullRepoConfig.addAuthorEmailsAndAliasesMapEntry(author, author.getAuthorAliases());
        expectedFullRepoConfig.addAuthorEmailsAndAliasesMapEntry(author, author.getEmails());
    }

    @Test
    public void standaloneConfig_parseEmptyTextFile_success() throws IOException {
        new StandaloneConfigJsonParser().parse(STANDALONE_CONFIG_EMPTY_TEXT_FILE);
    }

    @Test
    public void standaloneConfig_parseEmptyJsonFile_success() throws IOException {
        new StandaloneConfigJsonParser().parse(STANDALONE_CONFIG_EMPTY_JSON_FILE);
    }

    @Test
    public void standaloneConfig_ignoresUnknownProperty_success() throws IOException {
        new StandaloneConfigJsonParser().parse(STANDALONE_UNKNOWN_PROPERTY_CONFIG);
    }

    @Test
    public void standaloneConfig_correctConfig_success() throws IOException, InvalidLocationException {
        StandaloneConfig config = new StandaloneConfigJsonParser().parse(STANDALONE_CONFIG_FULL);
        assertSameConfig(expectedFullRepoConfig, config);
    }

    @Test
    public void standaloneConfig_githubIdOnlyConfig_success() throws IOException, InvalidLocationException {
        StandaloneConfig config = new StandaloneConfigJsonParser().parse(STANDALONE_CONFIG_GITHUBID_ONLY);
        assertSameConfig(expectedGithubIdOnlyRepoconfig, config);
    }

    @Test(expected = JsonSyntaxException.class)
    public void standaloneConfig_malformedJsonFile_throwsJsonSyntaxException() throws IOException {
        new StandaloneConfigJsonParser().parse(STANDALONE_MALFORMED_CONFIG);
    }

    private void assertSameConfig(RepoConfiguration expectedRepoConfig, StandaloneConfig actualStandaloneConfig)
            throws InvalidLocationException {
        RepoConfiguration actualRepoConfig = new RepoConfiguration(new RepoLocation(TEST_DUMMY_LOCATION));
        actualRepoConfig.update(actualStandaloneConfig);
        TestUtil.compareRepoConfig(expectedRepoConfig, actualRepoConfig);
    }
}
