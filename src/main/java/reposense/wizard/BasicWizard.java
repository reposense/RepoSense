package reposense.wizard;

/**
 * Represents a basic wizard run.
 */
public class BasicWizard extends Wizard {
    private static final Prompt[] INITIAL_PROMPTS = new Prompt[] {
            new OptionalPrompt(SincePrompt.OPTIONAL_PROMPT, new SincePrompt()),
            new OptionalPrompt(ViewPrompt.OPTIONAL_PROMPT, new ViewPrompt()),
            new RepoPrompt()
    };
    public BasicWizard() {
        super(INITIAL_PROMPTS);
    }
}
