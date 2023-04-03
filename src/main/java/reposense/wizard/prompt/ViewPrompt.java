package reposense.wizard.prompt;

import reposense.wizard.InputBuilder;

/**
 * Represents a Prompt to get the view flag.
 */
public class ViewPrompt extends Prompt {
    private static final String DESCRIPTION = "Do you want to start a server to display the report?";
    private static final String YES_FLAG = "Y";
    private static final String NO_FLAG = "N";
    private static final String FORMAT = String.format("%s/%s", YES_FLAG, NO_FLAG);

    public ViewPrompt() {
        super(DESCRIPTION, FORMAT);
    }

    @Override
    public InputBuilder addToInput(InputBuilder inputBuilder) {
        if (getResponse().compareToIgnoreCase(YES_FLAG) == 0) {
            return inputBuilder.addView();
        }

        return inputBuilder;
    }

    @Override
    public Prompt[] run() {
        if (getResponse().compareToIgnoreCase(YES_FLAG) == 0
                || getResponse().compareToIgnoreCase(NO_FLAG) == 0) {
            return new Prompt[] {};
        }

        // Repeat ViewPrompt until a valid input is provided
        return new Prompt[] {this};
    }
}
