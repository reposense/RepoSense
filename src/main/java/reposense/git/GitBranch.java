package reposense.git;

import static reposense.system.CommandRunner.runCommand;

import java.nio.file.Path;
import java.nio.file.Paths;

import reposense.util.StringsUtil;

public class GitBranch {

    /**
     * Returns the current working branch.
     */
    public static String getCurrentBranch(String root) {
        Path rootPath = Paths.get(root);
        String gitBranchCommand = "git branch";

        return StringsUtil.filterText(runCommand(rootPath, gitBranchCommand), "\\* (.*)").split("\\*")[1].trim();
    }
}
