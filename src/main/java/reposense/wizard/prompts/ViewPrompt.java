package reposense.wizard.prompts;

import java.nio.file.Paths;

import reposense.wizard.InputBuilder;

/**
 * Represents a Prompt to get the view flag.
 */
public class ViewPrompt extends Prompt {
    public static final String DESCRIPTION = "Enter directory of report to display in server. "
            + "If not specified, default directory will be used";

    public ViewPrompt() {
        super(DESCRIPTION);
    }

    @Override
    public InputBuilder addToInput(InputBuilder inputBuilder) {
        if (getResponse().isEmpty()) {
            return inputBuilder.addView();
        }
        return inputBuilder.addView(Paths.get(getResponse()));
    }

    @Override
    public Prompt[] run() {
        return new Prompt[] {};
    }
}
