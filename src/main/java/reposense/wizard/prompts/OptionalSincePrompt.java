package reposense.wizard.prompts;

/**
 * Represents an Optional Prompt to get the sinceDate flag.
 */
public class OptionalSincePrompt extends OptionalPrompt {
    private static final String DESCRIPTION = "Do you want to specify the start date for the period to be analyzed? "
            + "The default is one month before the current date";

    public OptionalSincePrompt() {
        super(DESCRIPTION);
    }

    @Override
    public Prompt[] optionallyRun() {
        return new Prompt[]{new SincePrompt()};
    }
}
