package reposense.wizard;

public class BasicWizard extends Wizard {
    private static final Prompt[] INITIAL_PROMPTS = new Prompt[] {new SincePrompt()};
    public BasicWizard() {
        super(INITIAL_PROMPTS);
    }
}
