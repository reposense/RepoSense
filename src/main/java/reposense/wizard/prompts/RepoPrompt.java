package reposense.wizard.prompts;

import reposense.wizard.InputBuilder;

/**
 * Represents a Prompt to get the repo flag.
 */
public class RepoPrompt extends Prompt {
    private static final String DESCRIPTION = "Enter a list of URLs or the disk location of the git repositories "
            + "to analyze, separated by spaces";

    public RepoPrompt() {
        super(DESCRIPTION);
    }

    @Override
    public InputBuilder addToInput(InputBuilder inputBuilder) {
        return inputBuilder.addRepos(getResponse());
    }

    @Override
    public Prompt[] run() {
        return new Prompt[] {};
    }
}
