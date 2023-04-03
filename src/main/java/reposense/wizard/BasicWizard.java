package reposense.wizard;

import reposense.wizard.prompt.OptionalSincePrompt;
import reposense.wizard.prompt.OptionalUntilPrompt;
import reposense.wizard.prompt.Prompt;
import reposense.wizard.prompt.RepoPrompt;
import reposense.wizard.prompt.ViewPrompt;

/**
 * Represents a basic wizard run.
 */
public class BasicWizard extends Wizard {
    private static final Prompt[] INITIAL_PROMPTS = new Prompt[] {
            new RepoPrompt(),
            new OptionalSincePrompt(),
            new OptionalUntilPrompt(),
            new ViewPrompt()
    };
    public BasicWizard() {
        super(INITIAL_PROMPTS);
    }
}
