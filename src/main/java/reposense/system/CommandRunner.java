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
        try {
            return crp.waitForProcess();
        } catch (CommandRunnerException cre) {
            throw new RuntimeException(cre);
        }
    }

    public static CommandRunnerProcess runCommandAsync(Path path, String command) {
        return spawnCommandProcess(path, command);
    }

    /**
     * Spawns a {@code CommandRunnerProcess} to execute {@code command}. Does not wait for process to finish executing.
     */
    private static CommandRunnerProcess spawnCommandProcess(Path path, String command) {
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
        return new CommandRunnerProcess(path, command, p, outputGobbler, errorGobbler);
    }

    private static boolean isWindows() {
        return (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0);
    }
}
