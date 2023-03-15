package reposense.wizard.prompt;

import java.util.Scanner;

import reposense.wizard.InputBuilder;

/**
 * Represents an abstract prompt.
 */
public abstract class Prompt {
    private final String description;
    private final String format;
    private String response;

    public Prompt(String description, String format) {
        this.description = description;
        this.format = format;
    }

    public Prompt(String description) {
        this.description = description;
        this.format = "";
    }

    /**
     * Prompts the user for an input and stores it in response.
     *
     * @param sc Scanner that takes in the input
     */
    public void promptAndGetInput(Scanner sc) {
        System.out.println(this);
        response = getInput(sc);
    }

    private String getInput(Scanner sc) {
        return sc.nextLine();
    }

    public String getResponse() {
        return response.trim();
    }

    public abstract InputBuilder addToInput(InputBuilder inputBuilder);

    // each prompt may have additional effects depending on the prompt.
    // when we run a prompt, the prompt will ask for input.
    // for example, repos prompt will launch a vim window.
    // a prompt may also create other prompts () (for example a open vim prompt)
    public abstract Prompt[] run();

    @Override
    public String toString() {
        if (format.isEmpty()) {
            return String.format("%s: ", description);
        }
        return String.format("%s (%s): ", description, format);
    }
}
