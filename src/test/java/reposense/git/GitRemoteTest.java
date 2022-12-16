package reposense.git;

import static reposense.git.GitRemote.DEFAULT_FETCH_REMOTE;
import static reposense.git.GitRemote.DEFAULT_PUSH_REMOTE;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reposense.model.RepoConfiguration;
import reposense.template.GitTestTemplate;

class GitRemoteTest extends GitTestTemplate {

    private static final String REMOTE_URL_ORIGIN = "https://github.com/reposense/RepoSense.git";
    private static final String REMOTE_URL_NON_ORIGIN = "https://github.com/TestSenseRepo/RepoSense.git";
    private static final String NON_ORIGIN_FETCH_REMOTE_NAME_1 = "testrepo(fetch)";
    private static final String NON_ORIGIN_PUSH_REMOTE_NAME_1 = "testrepo(push)";
    private static final String NON_ORIGIN_FETCH_REMOTE_NAME_2 = "testrepo2(fetch)";
    private static final String NON_ORIGIN_PUSH_REMOTE_NAME_2 = "testrepo2(push)";

    private RepoConfiguration config;

    @BeforeEach
    public void before() throws Exception {
        super.before();
        config = configs.get();
    }

    @Test
    void getRemotes_singleOriginRemote_success() {
        Map<String, String> remotes = GitRemote.getRemotes(config.getRepoRoot());
        Assertions.assertEquals(remotes.entrySet().size(), 2);
        Assertions.assertTrue(remotes.containsKey(DEFAULT_FETCH_REMOTE));
        Assertions.assertTrue(remotes.containsKey(DEFAULT_PUSH_REMOTE));
        Assertions.assertTrue(remotes.values().stream().allMatch(s ->
                s.equals(TEST_REPO_GIT_LOCATION)));
    }

    @Test
    void getAvailableRemoteLocation_emptyRemotes_returnsEmpty() {
        Map<String, String> remotes = new HashMap<>();

        Assertions.assertEquals(Optional.empty(),
                GitRemote.getAvailableRemoteLocation(remotes));
    }

    @Test
    void getAvailableRemoteLocation_remotesContainingOrigin_returnsOrigin() {
        Map<String, String> remotes = new HashMap<>();
        remotes.put(NON_ORIGIN_FETCH_REMOTE_NAME_1, REMOTE_URL_NON_ORIGIN);
        remotes.put(NON_ORIGIN_PUSH_REMOTE_NAME_1, REMOTE_URL_NON_ORIGIN);
        remotes.put(DEFAULT_FETCH_REMOTE, REMOTE_URL_ORIGIN);
        remotes.put(DEFAULT_PUSH_REMOTE, REMOTE_URL_ORIGIN);

        Assertions.assertEquals(Optional.of(REMOTE_URL_ORIGIN),
                GitRemote.getAvailableRemoteLocation(remotes));
    }

    @Test
    void getAvailableRemoteLocation_remotesNotContainingOrigin_returnsAny() {
        Map<String, String> remotes = new HashMap<>();
        remotes.put(NON_ORIGIN_FETCH_REMOTE_NAME_1, REMOTE_URL_NON_ORIGIN);
        remotes.put(NON_ORIGIN_PUSH_REMOTE_NAME_1, REMOTE_URL_NON_ORIGIN);
        remotes.put(NON_ORIGIN_FETCH_REMOTE_NAME_2, REMOTE_URL_ORIGIN);
        remotes.put(NON_ORIGIN_PUSH_REMOTE_NAME_2, REMOTE_URL_ORIGIN);

        String remoteName = GitRemote.getAvailableRemoteLocation(remotes).orElse("");
        Assertions.assertTrue(remotes.containsValue(remoteName));
    }
}
