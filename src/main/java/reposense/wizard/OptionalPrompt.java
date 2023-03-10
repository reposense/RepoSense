package reposense.wizard;

/**
 * Represents an optional prompt that only calls the provided prompt if requested by the user.
 */
public class OptionalPrompt extends Prompt {
    private final Prompt prompt;
    private final static String YES_FLAG = "Y";
    private final static String NO_FLAG = "N";
    private final static String DESCRIPTION = String.format("%s/%s", YES_FLAG, NO_FLAG);

    public OptionalPrompt(String question, Prompt prompt) {
        super(question, DESCRIPTION);
        this.prompt = prompt;
    }

    @Override
    public InputBuilder addToInput(InputBuilder inputBuilder) {
        return inputBuilder;
    }

    @Override
    public Prompt[] run() {
        if (getResponse().compareToIgnoreCase(YES_FLAG) == 0) {
            return new Prompt[] {this.prompt};
        }

        if (getResponse().compareToIgnoreCase(NO_FLAG) == 0) {
            return new Prompt[] {};
        }

        // Repeat OptionalPrompt until a valid flag is provided
        return new Prompt[] {new OptionalPrompt(this.getQuestion(), this.prompt)};
    }
}
