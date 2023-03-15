package reposense.wizard;

/**
 * Represents an Optional Prompt to get the untilDate flag.
 */
public class OptionalUntilPrompt extends OptionalPrompt {
    private static final String DESCRIPTION = "Do you want to specify the end date for the period to be analyzed? "
            + "The default is today";

    public OptionalUntilPrompt() {
        super(DESCRIPTION);
    }

    @Override
    public Prompt[] optionallyRun() {
        return new Prompt[]{new UntilPrompt()};
    }
}
