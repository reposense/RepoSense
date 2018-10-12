package reposense.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonSyntaxException;

import reposense.model.StandaloneAuthor;
import reposense.model.StandaloneConfig;

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
    public void standaloneConfig_correctConfig_success() throws IOException {
        StandaloneConfig config = new StandaloneConfigJsonParser().parse(STANDALONE_CONFIG_FULL);

        Assert.assertEquals(Arrays.asList("**.adoc", "collate**"), config.getIgnoreGlobList());
        Assert.assertEquals(Arrays.asList("gradle", "jade", "java", "js", "md", "scss", "yml"), config.getFormats());
        Assert.assertEquals(Arrays.asList("7b96c563eb2d3612aa5275364333664a18f01491"), config.getIgnoreCommitList());
        Assert.assertEquals(1, config.getAuthors().size());

        StandaloneAuthor author = config.getAuthors().get(0);
        Assert.assertEquals("yong24s", author.getGithubId());
        Assert.assertEquals("Yong Hao", author.getDisplayName());
        Assert.assertEquals(Arrays.asList("Yong Hao TENG"), author.getAuthorNames());
        Assert.assertEquals(Arrays.asList("**.css", "**.html", "**.jade", "**.js"), author.getIgnoreGlobList());
    }

    @Test
    public void standaloneConfig_githubIdOnlyConfig_success() throws IOException {
        StandaloneConfig config = new StandaloneConfigJsonParser().parse(STANDALONE_CONFIG_GITHUBID_ONLY);

        Assert.assertEquals(Collections.emptyList(), config.getIgnoreGlobList());
        Assert.assertEquals(Collections.emptyList(), config.getIgnoreCommitList());
        Assert.assertEquals(ArgsParser.DEFAULT_FORMATS, config.getFormats());
        Assert.assertEquals(1, config.getAuthors().size());

        StandaloneAuthor author = config.getAuthors().get(0);
        Assert.assertEquals("yong24s", author.getGithubId());
        Assert.assertEquals("", author.getDisplayName());
        Assert.assertEquals(Collections.emptyList(), author.getAuthorNames());
        Assert.assertEquals(Collections.emptyList(), author.getIgnoreGlobList());
    }

    @Test(expected = JsonSyntaxException.class)
    public void standaloneConfig_malformedJsonFile_throwsIoException() throws IOException {
        new StandaloneConfigJsonParser().parse(STANDALONE_MALFORMED_CONFIG);
    }
}
