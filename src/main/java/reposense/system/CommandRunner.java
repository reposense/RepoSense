package reposense.system;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommandRunner {

    private static boolean isWindows = isWindows();

    public static String checkStyleRaw(String absoluteDirectory) {
        Path rootPath = Paths.get(absoluteDirectory);
        return runCommand(
                rootPath,
                "java -jar checkstyle-7.7-all.jar -c /google_checks.xml -f xml " + absoluteDirectory
        );
    }

    public static String runCommand(Path path, String command) {
        CommandRunnerProcess crp = spawnCommandProcess(path, command);
        return waitForCommandProcess(path, command, crp);
    }

    /**
     * Spawns a {@code CommandRunnerProcess} to execute {@code command}. Does not wait for process to finish executing.
     */
    public static CommandRunnerProcess spawnCommandProcess(Path path, String command) {
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
        CommandRunnerProcess crp = new CommandRunnerProcess(pb);

        try {
            crp.spawnProcess();
        } catch (IOException e) {
            throw new RuntimeException("Error Creating Thread:" + e.getMessage());
        }
        return crp;
    }

    /**
     * Waits for {@code crp} to finish executing and returns the output from the execution.
     */
    public static String waitForCommandProcess(Path path, String command, CommandRunnerProcess crp) {
        int exit = 0;
        try {
            exit = crp.waitForProcess();
        } catch (InterruptedException e) {
            throw new RuntimeException("Error Handling Thread.");
        }

        if (exit == 0) {
            return crp.getOutputGobbler().getValue();
        } else {
            String errorMessage = "Error returned from command ";
            errorMessage += command + "on path ";
            errorMessage += path.toString() + " :\n" + crp.getErrorGobbler().getValue();
            throw new RuntimeException(errorMessage);
        }
    }

    private static boolean isWindows() {
        return (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0);
    }
}
