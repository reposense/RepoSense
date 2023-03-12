package reposense.wizard;

/**
 * Represents a basic wizard run.
 */
public class BasicWizard extends Wizard {
    private static final Prompt[] INITIAL_PROMPTS = new Prompt[] {
            new OptionalSincePrompt(),
            new OptionalViewPrompt(),
            new RepoPrompt()
    };
    public BasicWizard() {
        super(INITIAL_PROMPTS);
    }
}
