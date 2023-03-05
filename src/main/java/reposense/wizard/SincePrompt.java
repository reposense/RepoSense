package reposense.wizard;

public class SincePrompt extends Prompt {
    public SincePrompt() {
        super("Enter the start date for the period to be analyzed", "DD/MM/YYYY");
    }

    @Override
    public InputBuilder addToInput(InputBuilder inputBuilder) {
        return inputBuilder.addSinceDate(getResponse());
    }

    @Override
    public Prompt[] run() {
        return new Prompt[] {};
    }
}
