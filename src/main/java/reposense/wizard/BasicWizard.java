package reposense.wizard;

/**
 * Represents a basic wizard run.
 */
public class BasicWizard extends Wizard {
    private static final Prompt[] INITIAL_PROMPTS = new Prompt[] {
            new SincePrompt(),
            new OptionalPrompt(ViewPrompt.OPTIONAL_PROMPT, new ViewPrompt())
    };
    public BasicWizard() {
        super(INITIAL_PROMPTS);
    }
}
