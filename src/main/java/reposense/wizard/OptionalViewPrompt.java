package reposense.wizard;

/**
 * Represents an Optional Prompt to get the view flag.
 */
public class OptionalViewPrompt extends OptionalPrompt {
    private static final String DESCRIPTION = "Do you want to start a server to display the report?";

    public OptionalViewPrompt() {
        super(DESCRIPTION);
    }

    @Override
    public Prompt[] optionallyRun() {
        return new Prompt[]{new ViewPrompt()};
    }
}
