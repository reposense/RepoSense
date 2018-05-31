package reposense.system;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import reposense.util.Constants;


public class CommandRunner {

    private static boolean isWindows = isWindows();

    public static String gitLog(String root, Date fromDate, Date toDate) {
        Path rootPath = Paths.get(root);
        String command = "git log --no-merges ";
        if (fromDate != null) {
            command += " --since=\"" + Constants.GIT_LOG_DATE_FORMAT.format(fromDate) + "\" ";
        }
        if (toDate != null) {
            command += " --until=\"" + Constants.GIT_LOG_DATE_FORMAT.format(toDate) + "\" ";
        }
        command += " --pretty=format:\"%h|%aN|%ad|%s\" --date=iso --shortstat -- \"*.java\" -- \"*.adoc\"";
        return runCommand(rootPath, command);
    }

    public static void checkOut(String root, String hash) {
        Path rootPath = Paths.get(root);
        runCommand(rootPath, "git checkout " + hash);
    }

    public static String blameRaw(String root, String fileDirectory) {
        Path rootPath = Paths.get(root);
        return runCommand(rootPath, "git blame " + addQuote(fileDirectory) + " -w -C -C -M --line-porcelain");
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


}
