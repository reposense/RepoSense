package reposense.wizard;

import java.util.Scanner;

/**
 * Represents an abstract prompt.
 */
public abstract class Prompt {
    private final String question;
    private final String description;
    private String response;

    public Prompt(String question, String description) {
        this.question = question;
        this.description = description;
    }

    public void promptAndGetInput(Scanner sc) {
        System.out.println(this);
        response = getInput(sc);
    }

    private String getInput(Scanner sc) {
        return sc.nextLine();
    }

    public String getResponse() {
        return response;
    }

    public abstract InputBuilder addToInput(InputBuilder inputBuilder);

    // each prompt may have additional effects depending on the prompt.
    // when we run a prompt, the prompt will ask for input.
    // for example, repos prompt will launch a vim window.
    // a prompt may also create other prompts () (for example a open vim prompt)
    public abstract Prompt[] run();

    @Override
    public String toString() {
        return String.format("%s (%s): ", question, description);
    }
}
