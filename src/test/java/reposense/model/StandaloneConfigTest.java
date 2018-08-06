package reposense.model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.Test;

import reposense.parser.StandaloneConfigJsonParser;
import reposense.template.GitTestTemplate;

public class StandaloneConfigTest extends GitTestTemplate {

    private static final Path VALID_CONFIG = new File(StandaloneConfigTest.class.getClassLoader()
            .getResource("StandaloneConfigTest/valid_config.json").getFile()).toPath();
    private static final Path INVALID_GITID_CONFIG = new File(StandaloneConfigTest.class.getClassLoader()
            .getResource("StandaloneConfigTest/lithiumlkid_invalidGitId_config.json").getFile()).toPath();
    private static final Path INVALID_DISPLAYNAME_CONFIG = new File(StandaloneConfigTest.class.getClassLoader()
            .getResource("StandaloneConfigTest/jordancjq_invalidDisplayName_config.json").getFile()).toPath();
    private static final Path INVALID_ALIASES_CONFIG = new File(StandaloneConfigTest.class.getClassLoader()
            .getResource("StandaloneConfigTest/codeeong_invalidAuthorAliases_config.json").getFile()).toPath();
    private static final Path INVALID_IGNOREGLOB_CONFIG = new File(StandaloneConfigTest.class.getClassLoader()
            .getResource("StandaloneConfigTest/lithiumlkid_invalidIgnoreGlob_config.json").getFile()).toPath();

    @Test
    public void standaloneConfig_validJson_success() throws IOException {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(VALID_CONFIG);
        config.update(standaloneConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void standaloneConfig_invalidGitIdJson_throwIllegalArgumentException() throws IOException {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(INVALID_GITID_CONFIG);
        config.update(standaloneConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void standaloneConfig_invalidDisplayNameJson_throwIllegalArgumentException() throws IOException {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(INVALID_DISPLAYNAME_CONFIG);
        config.update(standaloneConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void standaloneConfig_invalidAuthorAliases_throwIllegalArgumentException() throws IOException {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(INVALID_ALIASES_CONFIG);
        config.update(standaloneConfig);
    }

    @Test(expected = IllegalArgumentException.class)
    public void standaloneConfig_invalidIgnoreGlob_throwIllegalArgumentException() throws IOException {
        StandaloneConfig standaloneConfig = new StandaloneConfigJsonParser().parse(INVALID_IGNOREGLOB_CONFIG);
        config.update(standaloneConfig);
    }
}
