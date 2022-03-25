package reposense.system;

import java.io.IOException;
import java.nio.file.Path;

import reposense.util.SystemUtil;

/**
 * Contains command running related functionalities.
 */
public class CommandRunner {

    /**
     * Spawns a backend terminal process, with working directory at {@code path}, to execute the {@code command}.
     *
     * @throws RuntimeException if an exception happens while executing the {@code command}.
     */
    public static String runCommand(Path path, String command) throws RuntimeException {
        CommandRunnerProcess crp = spawnCommandProcess(path, command);
        try {
            return crp.waitForProcess();
        } catch (CommandRunnerProcessException cre) {
            throw new RuntimeException(cre);
        }
    }

    /**
     * Spawns a backend terminal process, with working directory at {@code path}, to execute the {@code command}.
     * Does not wait for process to finish executing.
     */
    public static CommandRunnerProcess runCommandAsync(Path path, String command) {
        return spawnCommandProcess(path, command);
    }

    /**
     * Spawns a {@link CommandRunnerProcess} to execute {@code command}, with working directory at {@code path}.
     * Does not wait for process to finish executing.
     *
     * @throws RuntimeException if an error happens while attempting to spawn the process.
     */
    private static CommandRunnerProcess spawnCommandProcess(Path path, String command) throws RuntimeException {
        ProcessBuilder pb = null;
        if (SystemUtil.isWindows()) {
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
}
