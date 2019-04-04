package reposense.model;

import java.io.File;
import java.io.IOException;
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

    private static final Path VALID_CONFIG = new File(StandaloneConfigTest.class.getClassLoader()
            .getResource("StandaloneConfigTest/valid_config.json").getFile()).toPath();
    private static final Path INVALID_IGNOREGLOB_CONFIG = new File(StandaloneConfigTest.class.getClassLoader()
            .getResource("StandaloneConfigTest/lithiumlkid_invalidIgnoreGlob_config.json").getFile()).toPath();
    private static final Path INVALID_FORMATS_CONFIG = new File(StandaloneConfigTest.class.getClassLoader()
            .getResource("StandaloneConfigTest/invalidFormats_config.json").getFile()).toPath();
    private static final Path INVALID_IGNORECOMMIT_CONFIG = new File(StandaloneConfigTest.class.getClassLoader()
            .getResource("StandaloneConfigTest/invalidIgnoreCommit_config.json").getFile()).toPath();
    private static final Path SPECIAL_CHARACTER_AUTHOR_CONFIG = new File(StandaloneConfigTest.class.getClassLoader()
            .getResource("StandaloneConfigTest/specialCharacterAuthor_config.json").getFile()).toPath();
    private static final Path AUTHORS_TRAILING_COMMAS_CONFIG = new File(StandaloneConfigTest.class.getClassLoader()
            .getResource("StandaloneConfigTest/authors_trailingCommas_config.json").getFile()).toPath();
    private static final Path LITHIUMLKID_TRAILING_COMMAS_CONFIG = new File(StandaloneConfigTest.class.getClassLoader()
            .getResource("StandaloneConfigTest/lithiumlkid_trailingCommas_config.json").getFile()).toPath();

    private static final Author FIRST_SPECIAL_CHARACTER_AUTHOR = new Author("‘Processed�‘Cooked�");
    private static final Author SECOND_SPECIAL_CHARACTER_AUTHOR = new Author("(codeeong)");
    private static final Author THIRD_SPECIAL_CHARACTER_AUTHOR = new Author("^:jordancjq;$");
    private static final List<Author> AUTHOR_CONFIG_SPECIAL_CHARACTER_AUTHORS = Arrays.asList(
            FIRST_SPECIAL_CHARACTER_AUTHOR, SECOND_SPECIAL_CHARACTER_AUTHOR, THIRD_SPECIAL_CHARACTER_AUTHOR);

    private static StandaloneConfig validStandaloneConfig;

    @BeforeClass
    public static void setUp() throws IOException {
        validStandaloneConfig = new StandaloneConfigJsonParser().parse(VALID_CONFIG);
    }

    @Test
    public void standaloneConfig_validJson_success() throws IOException {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(VALID_CONFIG);
        config.update(standaloneConfig);
    }

    @Test
    public void standaloneConfig_specialCharacterAuthor_success() throws IOException {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(SPECIAL_CHARACTER_AUTHOR_CONFIG);
        config.update(standaloneConfig);

        Assert.assertEquals(AUTHOR_CONFIG_SPECIAL_CHARACTER_AUTHORS, config.getAuthorList());
    }

    @Test
    public void standaloneConfig_trailingCommasInList_success() throws IOException {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(AUTHORS_TRAILING_COMMAS_CONFIG);
        config.update(standaloneConfig);

        Assert.assertEquals(validStandaloneConfig, standaloneConfig);
    }

    @Test(expected = JsonSyntaxException.class)
    public void standaloneConfig_trailingCommasInMaps_throwsJsonSyntaxException() throws IOException {
        new StandaloneConfigJsonParser().parse(LITHIUMLKID_TRAILING_COMMAS_CONFIG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void standaloneConfig_invalidIgnoreGlob_throwIllegalArgumentException() throws IOException {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(INVALID_IGNOREGLOB_CONFIG);
        config.update(standaloneConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void standaloneConfig_invalidFormats_throwIllegalArgumentException() throws IOException {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(INVALID_FORMATS_CONFIG);
        config.update(standaloneConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void standaloneConfig_invalidIgnoreCommit_throwIllegalArgumentException() throws IOException {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(INVALID_IGNORECOMMIT_CONFIG);
        config.update(standaloneConfig);
    }
}
