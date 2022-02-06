package reposense.git;

import reposense.system.CommandRunner;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GitRemote {

    public static Map<String, String> getRemotes(String repoRoot) {
        String result =  CommandRunner.runCommand(Paths.get(repoRoot), "git remote -v");
        Map<String, String> remotes = new HashMap<>();

        Arrays.stream(result.split("\n"))
                .map(s -> s.split("[ \\t]+"))
                .forEach(l -> remotes.put(l[0], l[1]));

        return remotes;
    }

}
