package reposense.wizard;

/**
 * Represents a Prompt to get the view flag.
 */
import java.nio.file.Paths;

public class ViewPrompt extends Prompt {
    public static final String OPTIONAL_PROMPT = "Do you want to start a server to display the report?";

    public ViewPrompt() {
        super("Enter directory of report to display in server. If not specified, default directory will be used.");
    }

    @Override
    public InputBuilder addToInput(InputBuilder inputBuilder) {
        if (getResponse().isEmpty()) {
            return inputBuilder.addView();
        }
        return inputBuilder.addView(Paths.get(getResponse()));
    }

    @Override
    public Prompt[] run() {
        return new Prompt[] {};
    }
}
