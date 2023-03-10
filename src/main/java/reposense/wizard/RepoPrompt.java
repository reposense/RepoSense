package reposense.wizard;
/**
 * Represents a Prompt to get the repo flag.
 */
public class RepoPrompt extends Prompt {
    public RepoPrompt() {
        super("Enter a list of URLs or the disk location of the git repositories to analyze, separated by spaces");
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
