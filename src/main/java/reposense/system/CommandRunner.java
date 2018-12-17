package reposense.system;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import reposense.git.Util;
import reposense.util.StringsUtil;

public class CommandRunner {

    private static boolean isWindows = isWindows();

    public static String checkStyleRaw(String absoluteDirectory) {
        Path rootPath = Paths.get(absoluteDirectory);
        return runCommand(
                rootPath,
                "java -jar checkstyle-7.7-all.jar -c /google_checks.xml -f xml " + absoluteDirectory
        );
    }

    /**
     * Returns the git diff result of the current commit compared to {@code lastCommitHash}, without any context.
     */
    public static String diffCommit(String root, String lastCommitHash) {
        Path rootPath = Paths.get(root);
        return runCommand(rootPath, "git diff -U0 " + lastCommitHash);
    }

    /**
     * Returns the latest commit hash before {@code date}.
     * Returns an empty {@code String} if {@code date} is null, or there is no such commit.
     */
    public static String getCommitHashBeforeDate(String root, String branchName, Date date) {
        if (date == null) {
            return "";
        }

        Path rootPath = Paths.get(root);
        String revListCommand = "git rev-list -1 --before="
                + Util.GIT_LOG_SINCE_DATE_FORMAT.format(date) + " " + branchName;
        return runCommand(rootPath, revListCommand);
    }

    /**
     * Returns the current working branch.
     */
    public static String getCurrentBranch(String root) {
        Path rootPath = Paths.get(root);
        String gitBranchCommand = "git branch";

        return StringsUtil.filterText(runCommand(rootPath, gitBranchCommand), "\\* (.*)").split("\\*")[1].trim();
    }

    public static String runCommand(Path path, String command) {
        ProcessBuilder pb = null;
        if (isWindows) {
            pb = new ProcessBuilder()
                    .command(new String[]{"CMD", "/c", command})
                    .directory(path.toFile());
        } else {
            pb = new ProcessBuilder()
                    .command(new String[]{"bash", "-c", command})
                    .directory(path.toFile());
        }
        Process p = null;
        try {
            p = pb.start();
        } catch (IOException e) {
            throw new RuntimeException("Error Creating Thread:" + e.getMessage());
        }
        StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream());
        StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream());
        outputGobbler.start();
        errorGobbler.start();
        int exit = 0;
        try {
            exit = p.waitFor();
            outputGobbler.join();
            errorGobbler.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Error Handling Thread.");
        }

        if (exit == 0) {
            return outputGobbler.getValue();
        } else {
            String errorMessage = "Error returned from command ";
            errorMessage += command + "on path ";
            errorMessage += path.toString() + " :\n" + errorGobbler.getValue();
            throw new RuntimeException(errorMessage);
        }
    }

    private static boolean isWindows() {
        return (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0);
    }
}
