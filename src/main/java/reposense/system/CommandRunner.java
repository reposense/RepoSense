package reposense.system;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import reposense.util.Constants;

public class CommandRunner {

    private static final DateFormat GIT_LOG_SINCE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00+08:00");
    private static final DateFormat GIT_LOG_UNTIL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'23:59:59+08:00");

    private static boolean isWindows = isWindows();

    public static String gitLog(String root, Date sinceDate, Date untilDate) {
        Path rootPath = Paths.get(root);

        String command = "git log --no-merges ";
        command += getGitDateRangeArgs(sinceDate, untilDate);
        command += " --pretty=format:\"%h|%aN|%ad|%s\" --date=iso --shortstat -- \"*.java\" -- \"*.adoc\"";

        return runCommand(rootPath, command);
    }

    public static void checkout(String root, String hash) {
        Path rootPath = Paths.get(root);
        runCommand(rootPath, "git checkout " + hash);
    }

    /**
     * Checks out to the latest commit before {@code untilDate} in {@code branchName} branch
     * if {@code untilDate} is not null.
     */
    public static void checkoutToDate(String root, String branchName, Date untilDate) {
        if (untilDate == null) {
            return;
        }

        Path rootPath = Paths.get(root);
        String checkoutCommand;
        String substituteCommand = "git rev-list -1 --before="
                + GIT_LOG_UNTIL_DATE_FORMAT.format(untilDate) + " " + branchName;

        if (isWindows) {
            checkoutCommand = "for /f %g in ('" + substituteCommand + "') do git checkout %g";
        } else {
            checkoutCommand = "git checkout `" + substituteCommand + "`";
        }

        runCommand(rootPath, checkoutCommand);
    }

    public static String blameRaw(String root, String fileDirectory, Date sinceDate, Date untilDate) {
        Path rootPath = Paths.get(root);

        String blameCommand = "git blame -w -C -C -M --line-porcelain";
        blameCommand += getGitDateRangeArgs(sinceDate, untilDate);
        blameCommand += " " + addQuote(fileDirectory);
        blameCommand += getAuthorFilterCommand();

        return runCommand(rootPath, blameCommand);
    }

    public static String checkStyleRaw(String absoluteDirectory) {
        Path rootPath = Paths.get(absoluteDirectory);
        return runCommand(
                rootPath,
                "java -jar checkstyle-7.7-all.jar -c /google_checks.xml -f xml " + absoluteDirectory
        );
    }

    public static String cloneRepo(String org, String repoName) throws IOException {
        Path rootPath = Paths.get(Constants.REPOS_ADDRESS, org);
        Files.createDirectories(rootPath);
        return runCommand(rootPath, "git clone " + Constants.GITHUB_URL_ROOT + org + "/" + repoName);
    }

    private static String runCommand(Path path, String command) {
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

    private static String addQuote(String original) {
        return "\"" + original + "\"";
    }

    private static boolean isWindows() {
        return (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0);
    }

    /**
     * Returns the {@code String} command which filters the git blame output to produce only the necessary author
     * name for each line.
     */
    private static String getAuthorFilterCommand() {
        return isWindows ? "| findstr /B /C:" + addQuote("author ") : "| grep " + addQuote("^author .*");
    }

    /**
     * Returns the {@code String} command to specify the date range of commits to analyze for `git` commands.
     */
    private static String getGitDateRangeArgs(Date sinceDate, Date untilDate) {
        String gitDateRangeArgs = "";

        if (sinceDate != null) {
            gitDateRangeArgs += " --since=" + addQuote(GIT_LOG_SINCE_DATE_FORMAT.format(sinceDate));
        }
        if (untilDate != null) {
            gitDateRangeArgs += " --until=" + addQuote(GIT_LOG_UNTIL_DATE_FORMAT.format(untilDate));
        }

        return gitDateRangeArgs;
    }
}
