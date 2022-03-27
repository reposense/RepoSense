package reposense.git;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import reposense.system.CommandRunner;

/**
 * Contains git remote related functionality.
 * Git remote is responsible for getting remote repository information.
 */
public class GitRemote {

    /**
     * Extracts remote repository information. Returns a map
     * with keys of the form REMOTE_NAME(fetch) or REMOTE_NAME(push).
     */
    public static Map<String, String> getRemotes(String repoRoot) {
        String result =  CommandRunner.runCommand(Paths.get(repoRoot), "git remote -v");
        Map<String, String> remotes = new HashMap<>();

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
