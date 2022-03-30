package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;

import reposense.git.exception.GitBranchException;
import reposense.util.StringsUtil;

/**
 * Contains git branch related functionalities.
 * Git branch is responsible for list, create, or delete branches.
 */
public class GitBranch {

    /**
     * Returns the current working branch of the repository at {@code root}.
     *
     * @throws GitBranchException if command execution is unsuccessful.
     */
    public static String getCurrentBranch(String root) throws GitBranchException {
        Path rootPath = Paths.get(root);
        String gitBranchCommand = "git branch";

        try {
            return StringsUtil.filterText(runCommand(rootPath, gitBranchCommand), "\\* (.*)").split("\\*")[1].trim();
        } catch (RuntimeException rte) {
            throw new GitBranchException(rte);
        }
    }
}
