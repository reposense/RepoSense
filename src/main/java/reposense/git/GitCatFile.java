package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;

/**
 * Contains git cat file related functionalities.
 * Git cat file provides content or type and size information for repository objects.
 */
public class GitCatFile {
    /**
     * Returns parent commits for the commit associated with the input commit hash.
     */
    public static List<String> getParentCommits(String root, String commitHash) {
        Path rootPath = Paths.get(root);
        String catFileCommand = "git cat-file -p " + commitHash;
        String output = runCommand(rootPath, catFileCommand);
        List<String> parents = new ArrayList();
        for (String line : output.split("\n")) {
            if (line.startsWith("parent")) {
                parents.add(line.substring(7).trim());
            }
        }
        return parents;
    }

    /**
     * Given the input list of commit hashes, return a list of commit hashes for the parent
     * commits of all the commits.
     */
    public static List<String> getParentsOfCommits(String root, List<String> commitHashes) {
        List<String> parents = new ArrayList();
        for (String commitHash : commitHashes) {
            parents.addAll(getParentCommits(root, commitHash));
        }
        return parents;
    }
}
