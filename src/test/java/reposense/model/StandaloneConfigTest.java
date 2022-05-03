package reposense.model;

import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
    private static final Path FILE_SIZE_LIMIT_CONFIG = loadResource(StandaloneConfigTest.class,
            "StandaloneConfigTest/fileSizeLimit_config.json");

    private static final Author FIRST_SPECIAL_CHARACTER_AUTHOR = new Author("‘Processed�‘Cooked�");
    private static final Author SECOND_SPECIAL_CHARACTER_AUTHOR = new Author("(codeeong)");
    private static final Author THIRD_SPECIAL_CHARACTER_AUTHOR = new Author("^:jordancjq;$");
    private static final List<Author> AUTHOR_CONFIG_SPECIAL_CHARACTER_AUTHORS = Arrays.asList(
            FIRST_SPECIAL_CHARACTER_AUTHOR, SECOND_SPECIAL_CHARACTER_AUTHOR, THIRD_SPECIAL_CHARACTER_AUTHOR);
    private static final long FILE_SIZE_LIMIT = 100000;

    private static StandaloneConfig validStandaloneConfig;

    @BeforeAll
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

        Assertions.assertEquals(AUTHOR_CONFIG_SPECIAL_CHARACTER_AUTHORS, config.getAuthorList());
    }

    @Test
    public void standaloneConfig_trailingCommasInList_success() throws Exception {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(AUTHORS_TRAILING_COMMAS_CONFIG);
        config.update(standaloneConfig);

        Assertions.assertEquals(validStandaloneConfig, standaloneConfig);
    }

    @Test
    public void standaloneConfig_fileSizeLimit_success() throws Exception {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(FILE_SIZE_LIMIT_CONFIG);
        config.update(standaloneConfig);

        Assertions.assertEquals(FILE_SIZE_LIMIT, config.getFileSizeLimit());
    }

    @Test
    public void standaloneConfig_trailingCommasInMaps_throwsJsonSyntaxException() {
        Assertions.assertThrows(JsonSyntaxException.class, () -> new StandaloneConfigJsonParser()
                .parse(LITHIUMLKID_TRAILING_COMMAS_CONFIG));
    }

    @Test
    public void standaloneConfig_invalidIgnoreGlob_throwIllegalArgumentException() throws Exception {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(INVALID_IGNOREGLOB_CONFIG);
        Assertions.assertThrows(IllegalArgumentException.class, () -> config.update(standaloneConfig));
    }

    @Test
    public void standaloneConfig_invalidFormats_throwIllegalArgumentException() throws Exception {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(INVALID_FORMATS_CONFIG);
        Assertions.assertThrows(IllegalArgumentException.class, () -> config.update(standaloneConfig));
    }

    @Test
    public void standaloneConfig_invalidIgnoreCommit_throwIllegalArgumentException() throws Exception {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(INVALID_IGNORECOMMIT_CONFIG);
        Assertions.assertThrows(IllegalArgumentException.class, () -> config.update(standaloneConfig));
    }
}
