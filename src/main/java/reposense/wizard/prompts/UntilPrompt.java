package reposense.wizard.prompts;

import reposense.wizard.InputBuilder;

/**
 * Represents a Prompt to get the untilDate flag.
 */
public class UntilPrompt extends Prompt {
    private static final String DESCRIPTION = "Enter the end date for the period to be analyzed";
    private static final String FORMAT = "DD/MM/YYYY";

    public UntilPrompt() {
        super(DESCRIPTION, FORMAT);
    }

    @Override
    public InputBuilder addToInput(InputBuilder inputBuilder) {
        return inputBuilder.addUntilDate(getResponse());
    }

    @Override
    public Prompt[] run() {
        return new Prompt[] {};
    }
}
