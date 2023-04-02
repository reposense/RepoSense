package reposense.wizard;

import reposense.wizard.prompt.OptionalSincePrompt;
import reposense.wizard.prompt.OptionalUntilPrompt;
import reposense.wizard.prompt.OptionalViewPrompt;
import reposense.wizard.prompt.Prompt;
import reposense.wizard.prompt.RepoPrompt;

/**
 * Represents a basic wizard run.
 */
public class BasicWizard extends Wizard {
    private static final Prompt[] INITIAL_PROMPTS = new Prompt[] {
            new RepoPrompt(),
            new OptionalSincePrompt(),
            new OptionalUntilPrompt(),
            new OptionalViewPrompt()
    };
    public BasicWizard() {
        super(INITIAL_PROMPTS);
    }
}
