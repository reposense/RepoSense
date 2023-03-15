package reposense.wizard.prompts;

import reposense.wizard.InputBuilder;

/**
 * Represents a Prompt to get the sinceDate flag.
 */
public class SincePrompt extends Prompt {
    private static final String DESCRIPTION = "Enter the start date for the period to be analyzed";
    private static final String FORMAT = "DD/MM/YYYY";

    public SincePrompt() {
        super(DESCRIPTION, FORMAT);
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
