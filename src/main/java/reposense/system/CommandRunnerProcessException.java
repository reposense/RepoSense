package reposense.system;


/**
 * Signals that an error has occurred in {@link CommandRunnerProcess}.
 */
public class CommandRunnerProcessException extends Exception {
    public CommandRunnerProcessException(String message) {
        super(message);
    }
}
