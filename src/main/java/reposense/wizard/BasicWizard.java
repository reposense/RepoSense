package reposense.wizard;

import reposense.wizard.prompts.OptionalSincePrompt;
import reposense.wizard.prompts.OptionalUntilPrompt;
import reposense.wizard.prompts.OptionalViewPrompt;
import reposense.wizard.prompts.Prompt;
import reposense.wizard.prompts.RepoPrompt;

/**
 * Represents a basic wizard run.
 */
public class BasicWizard extends Wizard {
    private static final Prompt[] INITIAL_PROMPTS = new Prompt[] {
            new OptionalSincePrompt(),
            new OptionalUntilPrompt(),
            new OptionalViewPrompt(),
            new RepoPrompt()
    };
    public BasicWizard() {
        super(INITIAL_PROMPTS);
    }
}
