package reposense.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonSyntaxException;

import reposense.model.Author;
import reposense.model.RepoConfiguration;
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

    private static RepoConfiguration EXPECTED_GITHUBID_ONLY_REPOCONFIG;
    private static RepoConfiguration EXPECTED_FULL_REPOCONFIG;

    @BeforeClass
    public static void setUp() throws InvalidLocationException {
        Author author = new Author("yong24s");
        author.setAuthorAliases(Arrays.asList("Yong Hao TENG"));
        author.setIgnoreGlobList(Arrays.asList("**.css", "**.html", "**.jade", "**.js"));

        EXPECTED_GITHUBID_ONLY_REPOCONFIG = new RepoConfiguration(TEST_DUMMY_LOCATION);
        EXPECTED_GITHUBID_ONLY_REPOCONFIG.setFormats(ArgsParser.DEFAULT_FORMATS);
        EXPECTED_GITHUBID_ONLY_REPOCONFIG.setAuthorList(Arrays.asList(new Author("yong24s")));

        EXPECTED_FULL_REPOCONFIG = new RepoConfiguration(TEST_DUMMY_LOCATION);
        EXPECTED_FULL_REPOCONFIG.setFormats(Arrays.asList("gradle", "jade", "java", "js", "md", "scss", "yml"));
        EXPECTED_FULL_REPOCONFIG.setIgnoreCommitList(Arrays.asList("7b96c563eb2d3612aa5275364333664a18f01491"));
        EXPECTED_FULL_REPOCONFIG.setIgnoreGlobList(Arrays.asList("**.adoc", "collate**"));
        EXPECTED_FULL_REPOCONFIG.setAuthorList(Arrays.asList(author));
        EXPECTED_FULL_REPOCONFIG.setAuthorDisplayName(author, "Yong Hao");
        EXPECTED_FULL_REPOCONFIG.addAuthorAliases(author, Arrays.asList(author.getGitId()));
        EXPECTED_FULL_REPOCONFIG.addAuthorAliases(author, author.getAuthorAliases());
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
        isExact(EXPECTED_FULL_REPOCONFIG, config);
    }

    @Test
    public void standaloneConfig_githubIdOnlyConfig_success() throws IOException, InvalidLocationException {
        StandaloneConfig config = new StandaloneConfigJsonParser().parse(STANDALONE_CONFIG_GITHUBID_ONLY);
        isExact(EXPECTED_GITHUBID_ONLY_REPOCONFIG, config);
    }

    @Test(expected = JsonSyntaxException.class)
    public void standaloneConfig_malformedJsonFile_throwsIoException() throws IOException {
        new StandaloneConfigJsonParser().parse(STANDALONE_MALFORMED_CONFIG);
    }

    private void isExact(RepoConfiguration expectedRepoConfig, StandaloneConfig actualStandaloneConfig)
            throws InvalidLocationException {
        RepoConfiguration actualRepoConfig = new RepoConfiguration(TEST_DUMMY_LOCATION);
        actualRepoConfig.update(actualStandaloneConfig);
        TestUtil.compareRepoConfig(expectedRepoConfig, actualRepoConfig);
    }
}
