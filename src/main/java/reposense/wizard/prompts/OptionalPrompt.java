package reposense.wizard.prompts;

import reposense.wizard.InputBuilder;

/**
 * Represents an optional prompt that only calls the provided prompt if requested by the user.
 */
public abstract class OptionalPrompt extends Prompt {
    public static final String YES_FLAG = "Y";
    public static final String NO_FLAG = "N";
    private static final String FORMAT = String.format("%s/%s", YES_FLAG, NO_FLAG);

    public OptionalPrompt(String description) {
        super(description, FORMAT);
    }

    // optionally run array of Prompts if YES_FLAG is provided as input
    public abstract Prompt[] optionallyRun();

    @Override
    public InputBuilder addToInput(InputBuilder inputBuilder) {
        return inputBuilder;
    }

    @Override
    public Prompt[] run() {
        if (getResponse().compareToIgnoreCase(YES_FLAG) == 0) {
            return optionallyRun();
        }

        if (getResponse().compareToIgnoreCase(NO_FLAG) == 0) {
            return new Prompt[] {};
        }

        // Repeat OptionalPrompt until a valid input is provided
        return new Prompt[] {this};
    }
}
