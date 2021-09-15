package reposense.parser;

import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonSyntaxException;

import reposense.model.Author;
import reposense.model.CommitHash;
import reposense.model.FileType;
import reposense.model.FileTypeTest;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.model.StandaloneConfig;
import reposense.util.TestUtil;

public class StandaloneConfigJsonParserTest {

    private static final Path STANDALONE_MALFORMED_CONFIG = loadResource(
            StandaloneConfigJsonParserTest.class, "StandaloneConfigJsonParserTest/standaloneConfig_malformedJson.json");

    private static final Path STANDALONE_UNKNOWN_PROPERTY_CONFIG = loadResource(
            StandaloneConfigJsonParserTest.class,
            "StandaloneConfigJsonParserTest/standaloneConfig_unknownPropertyInJson.json");

    private static final Path STANDALONE_CONFIG_FULL = loadResource(
            StandaloneConfigJsonParserTest.class,
            "StandaloneConfigJsonParserTest/standaloneConfig_full.json");

    private static final Path STANDALONE_CONFIG_EMPTY_TEXT_FILE = loadResource(
            StandaloneConfigJsonParserTest.class,
            "StandaloneConfigJsonParserTest/standaloneConfig_emptyText.json");

    private static final Path STANDALONE_CONFIG_EMPTY_JSON_FILE = loadResource(
            StandaloneConfigJsonParserTest.class,
            "StandaloneConfigJsonParserTest/standaloneConfig_emptyJson.json");

    private static final Path STANDALONE_CONFIG_GITHUBID_ONLY = loadResource(
            StandaloneConfigJsonParserTest.class,
            "StandaloneConfigJsonParserTest/standaloneConfig_githubId_only.json");

    private static final String TEST_DUMMY_LOCATION = "https://github.com/reposense/RepoSense.git";

    private static RepoConfiguration expectedGithubIdOnlyRepoconfig;
    private static RepoConfiguration expectedFullRepoConfig;

    @BeforeClass
    public static void setUp() throws Exception {
        Author author = new Author("yong24s");
        author.setAuthorAliases(Arrays.asList("Yong Hao TENG"));
        author.setIgnoreGlobList(Arrays.asList("**.css", "**.html", "**.jade", "**.js"));

        expectedGithubIdOnlyRepoconfig = new RepoConfiguration(new RepoLocation(TEST_DUMMY_LOCATION));
        expectedGithubIdOnlyRepoconfig.setFormats(FileTypeTest.NO_SPECIFIED_FORMATS);
        expectedGithubIdOnlyRepoconfig.setAuthorList(Arrays.asList(new Author("yong24s")));
        expectedGithubIdOnlyRepoconfig.addAuthorDetailsToAuthorMapEntry(author, author.getEmails());

        expectedFullRepoConfig = new RepoConfiguration(new RepoLocation(TEST_DUMMY_LOCATION));
        expectedFullRepoConfig.setFormats(FileType.convertFormatStringsToFileTypes(
                Arrays.asList("gradle", "jade", "java", "js", "md", "scss", "yml")));
        expectedFullRepoConfig.setIgnoreCommitList(Arrays.asList(new CommitHash(
                "7b96c563eb2d3612aa5275364333664a18f01491")));
        expectedFullRepoConfig.setIgnoreGlobList(Arrays.asList("**.adoc", "collate**"));
        expectedFullRepoConfig.setIgnoredAuthorsList(Arrays.asList("yong24s"));
        expectedFullRepoConfig.setAuthorList(Arrays.asList(author));
        expectedFullRepoConfig.setAuthorDisplayName(author, "Yong Hao");
        expectedFullRepoConfig.addAuthorDetailsToAuthorMapEntry(author, Arrays.asList(author.getGitId()));
        expectedFullRepoConfig.addAuthorDetailsToAuthorMapEntry(author, author.getAuthorAliases());
        expectedFullRepoConfig.addAuthorDetailsToAuthorMapEntry(author, author.getEmails());
    }

    @Test
    public void standaloneConfig_parseEmptyTextFile_success() throws Exception {
        new StandaloneConfigJsonParser().parse(STANDALONE_CONFIG_EMPTY_TEXT_FILE);
    }

    @Test
    public void standaloneConfig_parseEmptyJsonFile_success() throws Exception {
        new StandaloneConfigJsonParser().parse(STANDALONE_CONFIG_EMPTY_JSON_FILE);
    }

    @Test
    public void standaloneConfig_ignoresUnknownProperty_success() throws Exception {
        new StandaloneConfigJsonParser().parse(STANDALONE_UNKNOWN_PROPERTY_CONFIG);
    }

    @Test
    public void standaloneConfig_correctConfig_success() throws Exception {
        StandaloneConfig config = new StandaloneConfigJsonParser().parse(STANDALONE_CONFIG_FULL);
        assertSameConfig(expectedFullRepoConfig, config);
    }

    @Test
    public void standaloneConfig_githubIdOnlyConfig_success() throws Exception {
        StandaloneConfig config = new StandaloneConfigJsonParser().parse(STANDALONE_CONFIG_GITHUBID_ONLY);
        assertSameConfig(expectedGithubIdOnlyRepoconfig, config);
    }

    @Test(expected = JsonSyntaxException.class)
    public void standaloneConfig_malformedJsonFile_throwsJsonSyntaxException() throws Exception {
        new StandaloneConfigJsonParser().parse(STANDALONE_MALFORMED_CONFIG);
    }

    private void assertSameConfig(RepoConfiguration expectedRepoConfig, StandaloneConfig actualStandaloneConfig)
            throws Exception {
        RepoConfiguration actualRepoConfig = new RepoConfiguration(new RepoLocation(TEST_DUMMY_LOCATION));
        actualRepoConfig.update(actualStandaloneConfig);
        TestUtil.compareRepoConfig(expectedRepoConfig, actualRepoConfig);
    }
}
