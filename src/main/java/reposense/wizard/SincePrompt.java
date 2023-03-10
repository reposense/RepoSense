package reposense.wizard;

/**
 * Represents a Prompt to get the sinceDate flag.
 */
public class SincePrompt extends Prompt {
    public static final String OPTIONAL_PROMPT = "Do you want to specify the start date for the period to be" +
            " analyzed? The default is one month before the current date";

    public SincePrompt() {
        super("Enter the start date for the period to be analyzed", "DD/MM/YYYY");
    }

    @Override
    public InputBuilder addToInput(InputBuilder inputBuilder) {
        return inputBuilder.addSinceDate(getResponse());
    }

    @Override
    public Prompt[] run() {
        return new Prompt[] {};
    }
}
