package reposense.git;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import reposense.system.CommandRunner;
import reposense.system.LogsManager;

/**
 * Contains git remote related functionality.
 * Git remote is responsible for getting remote repository information.
 */
public class GitRemote {

    public static final String DEFAULT_FETCH_REMOTE = "origin(fetch)";
    public static final String DEFAULT_PUSH_REMOTE = "origin(push)";

    private static final Logger logger = LogsManager.getLogger(GitRemote.class);

    /**
     * Extracts remote repository information at {@code repoRoot}.
     *
     * @return Map of keys of the form REMOTE_NAME(fetch) or REMOTE_NAME(push) to their corresponding remote URLs.
     */
    public static Map<String, String> getRemotes(String repoRoot) {
        Map<String, String> remotes = new HashMap<>();
        String result;
        try {
            result = CommandRunner.runCommand(Paths.get(repoRoot), "git remote -v");
        } catch (RuntimeException re) {
            logger.warning(String.format("Unable to run git remote command in directory: %s", repoRoot));
            return remotes;
        }

        Arrays.stream(result.split("\n"))
                .map(s -> s.split("[ \\t]+"))
                .forEach(l -> {
                    if (l.length == 3) {
                        // l[0]: remote name
                        // l[1]: remote URL
                        // l[2]: '(fetch)' | '(push)'
                        remotes.put(l[0] + l[2], l[1]);
                    }
                });

        return remotes;
    }

    /**
     * Finds an available fetch remote location in a given {@code remoteMap}, checking first for the default fetch
     * remote (origin).
     *
     * @return an {@code Optional} of an available remote location.
     */
    public static Optional<String> getAvailableRemoteLocation(Map<String, String> remoteMap) {
        String remoteLocation = remoteMap.size() == 0
                ? null
                : remoteMap.containsKey(DEFAULT_FETCH_REMOTE)
                // Get default fetch remote if possible
                ? remoteMap.get(DEFAULT_FETCH_REMOTE)
                // Get any remote otherwise
                : remoteMap.values().iterator().next();
        return Optional.ofNullable(remoteLocation);
    }

}
