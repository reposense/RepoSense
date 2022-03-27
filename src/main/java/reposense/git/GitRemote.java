package reposense.git;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import reposense.system.CommandRunner;
import reposense.system.LogsManager;

/**
 * Contains git remote related functionality.
 * Git remote is responsible for getting remote repository information.
 */
public class GitRemote {

    private static final Logger logger = LogsManager.getLogger(GitRemote.class);

    /**
     * Extracts remote repository information. Returns a map
     * with keys of the form REMOTE_NAME(fetch) or REMOTE_NAME(push).
     */
    public static Map<String, String> getRemotes(String repoRoot) {
        Map<String, String> remotes = new HashMap<>();
        String result;
        try {
            result = CommandRunner.runCommand(Paths.get(repoRoot), "git remote -v");
        } catch (RuntimeException re) {
            logger.warning("Unable to find remotes in given directory");
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

}
