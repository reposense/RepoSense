package reposense.wizard;

import reposense.wizard.prompt.Prompt;

/**
 * This class implements an abstract wizard.
 * A concrete implementation of a wizard contains
 * prompts specific to that wizard.
 */
public abstract class Wizard {
    // contains the basic skeleton of the wizard.

    private final Prompt[] initialPrompts;

    public Wizard(Prompt[] initialPrompts) {
        this.initialPrompts = initialPrompts;
    }

    public Prompt[] getInitialPrompts() {
        return initialPrompts;
    };
}
