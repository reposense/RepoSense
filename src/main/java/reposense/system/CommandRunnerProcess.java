package reposense.system;


public class CommandRunnerProcess {

    private Process process;
    private StreamGobbler outputGobbler;
    private StreamGobbler errorGobbler;

    public CommandRunnerProcess(Process process, StreamGobbler outputGobbler, StreamGobbler errorGobbler) {
        this.process = process;
        this.outputGobbler = outputGobbler;
        this.errorGobbler = errorGobbler;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public StreamGobbler getOutputGobbler() {
        return outputGobbler;
    }

    public void setOutputGobbler(StreamGobbler outputGobbler) {
        this.outputGobbler = outputGobbler;
    }

    public StreamGobbler getErrorGobbler() {
        return errorGobbler;
    }

    public void setErrorGobbler(StreamGobbler errorGobbler) {
        this.errorGobbler = errorGobbler;
    }
}
