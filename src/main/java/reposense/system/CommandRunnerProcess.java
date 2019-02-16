package reposense.system;

import java.io.IOException;

/**
 * Represents a process created by {@code CommandRunner}.
 */
public class CommandRunnerProcess {

    private ProcessBuilder processBuilder;
    private Process process;
    private StreamGobbler outputGobbler;
    private StreamGobbler errorGobbler;

    public CommandRunnerProcess(ProcessBuilder processBuilder) {
        this.processBuilder = processBuilder;
    }

    /**
     * Starts executing the process and the output and error gobblers. Does not wait for process to finish executing.
     */
    public void spawnProcess() throws IOException {
        if (process != null) {
            return;
        }
        process = processBuilder.start();
        outputGobbler = new StreamGobbler(process.getInputStream());
        errorGobbler = new StreamGobbler(process.getErrorStream());
        outputGobbler.start();
        errorGobbler.start();
    }

    /**
     * Waits for the previously spawned process to finish executing before returning the exit value.
     * Should only be called after {@code spawnProcess} has been called.
     */
    public int waitForProcess() throws InterruptedException {
        int exit = process.waitFor();
        outputGobbler.join();
        errorGobbler.join();
        return exit;
    }

    public Process getProcess() {
        return process;
    }

    public StreamGobbler getOutputGobbler() {
        return outputGobbler;
    }

    public StreamGobbler getErrorGobbler() {
        return errorGobbler;
    }
}
