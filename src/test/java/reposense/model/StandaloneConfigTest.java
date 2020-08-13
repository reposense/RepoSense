package reposense.model;

import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.JsonSyntaxException;

import reposense.parser.StandaloneConfigJsonParser;
import reposense.template.GitTestTemplate;

public class StandaloneConfigTest extends GitTestTemplate {

    private static final Path VALID_CONFIG = loadResource(StandaloneConfigTest.class,
            "StandaloneConfigTest/valid_config.json");
    private static final Path INVALID_IGNOREGLOB_CONFIG = loadResource(StandaloneConfigTest.class,
            "StandaloneConfigTest/lithiumlkid_invalidIgnoreGlob_config.json");
    private static final Path INVALID_FORMATS_CONFIG = loadResource(StandaloneConfigTest.class,
            "StandaloneConfigTest/invalidFormats_config.json");
    private static final Path INVALID_IGNORECOMMIT_CONFIG = loadResource(StandaloneConfigTest.class,
            "StandaloneConfigTest/invalidIgnoreCommit_config.json");
    private static final Path SPECIAL_CHARACTER_AUTHOR_CONFIG = loadResource(StandaloneConfigTest.class,
            "StandaloneConfigTest/specialCharacterAuthor_config.json");
    private static final Path AUTHORS_TRAILING_COMMAS_CONFIG = loadResource(StandaloneConfigTest.class,
            "StandaloneConfigTest/authors_trailingCommas_config.json");
    private static final Path LITHIUMLKID_TRAILING_COMMAS_CONFIG = loadResource(StandaloneConfigTest.class,
            "StandaloneConfigTest/lithiumlkid_trailingCommas_config.json");

    private static final Author FIRST_SPECIAL_CHARACTER_AUTHOR = new Author("‘Processed�‘Cooked�");
    private static final Author SECOND_SPECIAL_CHARACTER_AUTHOR = new Author("(codeeong)");
    private static final Author THIRD_SPECIAL_CHARACTER_AUTHOR = new Author("^:jordancjq;$");
    private static final List<Author> AUTHOR_CONFIG_SPECIAL_CHARACTER_AUTHORS = Arrays.asList(
            FIRST_SPECIAL_CHARACTER_AUTHOR, SECOND_SPECIAL_CHARACTER_AUTHOR, THIRD_SPECIAL_CHARACTER_AUTHOR);

    private static StandaloneConfig validStandaloneConfig;

    @BeforeClass
    public static void setUp() throws Exception {
        validStandaloneConfig = new StandaloneConfigJsonParser().parse(VALID_CONFIG);
    }

    @Test
    public void standaloneConfig_validJson_success() throws Exception {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(VALID_CONFIG);
        config.update(standaloneConfig);
    }

    @Test
    public void standaloneConfig_specialCharacterAuthor_success() throws Exception {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(SPECIAL_CHARACTER_AUTHOR_CONFIG);
        config.update(standaloneConfig);

        Assert.assertEquals(AUTHOR_CONFIG_SPECIAL_CHARACTER_AUTHORS, config.getAuthorList());
    }

    @Test
    public void standaloneConfig_trailingCommasInList_success() throws Exception {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(AUTHORS_TRAILING_COMMAS_CONFIG);
        config.update(standaloneConfig);

        Assert.assertEquals(validStandaloneConfig, standaloneConfig);
    }

    @Test(expected = JsonSyntaxException.class)
    public void standaloneConfig_trailingCommasInMaps_throwsJsonSyntaxException() throws Exception {
        new StandaloneConfigJsonParser().parse(LITHIUMLKID_TRAILING_COMMAS_CONFIG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void standaloneConfig_invalidIgnoreGlob_throwIllegalArgumentException() throws Exception {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(INVALID_IGNOREGLOB_CONFIG);
        config.update(standaloneConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void standaloneConfig_invalidFormats_throwIllegalArgumentException() throws Exception {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(INVALID_FORMATS_CONFIG);
        config.update(standaloneConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void standaloneConfig_invalidIgnoreCommit_throwIllegalArgumentException() throws Exception {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(INVALID_IGNORECOMMIT_CONFIG);
        config.update(standaloneConfig);
    }
}
