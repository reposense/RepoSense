package reposense.git;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reposense.template.GitTestTemplate;

public class GitConfigTest extends GitTestTemplate {
    private List<String[]> previousSetting;

    @BeforeEach
    public void before() throws Exception {
        previousSetting = GitConfig.getGlobalGitLfsConfig();
    }

    @AfterEach
    public void after() {
        if (previousSetting.size() == 0) {
            GitConfig.deleteGlobalGitLfsConfig();
        } else {
            GitConfig.setGlobalGitLfsConfig(previousSetting);
        }
    }

    @Test
    public void setGlobalLfsConfig_skipSmudge_success() {
        GitConfig.setGlobalGitLfsConfig(GitConfig.SKIP_SMUDGE_CONFIG_SETTINGS);
        List<String[]> gitConfigSetting = GitConfig.getGlobalGitLfsConfig();

        Assertions.assertEquals(2, gitConfigSetting.size());
        for (String[] setting : gitConfigSetting) {
            if (setting[0].equals(GitConfig.FILTER_LFS_PROCESS_KEY)) {
                Assertions.assertEquals(GitConfig.FILTER_LFS_PROCESS_VALUE, setting[1]);
            } else if (setting[0].equals(GitConfig.FILTER_LFS_SMUDGE_KEY)) {
                Assertions.assertEquals(GitConfig.FILTER_LFS_SMUDGE_VALUE, setting[1]);
            } else {
                Assertions.fail();
            }
        }
    }
}
