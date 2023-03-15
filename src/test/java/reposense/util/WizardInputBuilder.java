package reposense.util;

import reposense.wizard.prompts.OptionalPrompt;

/**
 * A utility class to help with building command line input for Wizard CLI.
 * Example usage: <br>
 *     {@code String input = new WizardInputBuilder().append("22/02/2022").build();}
 */
public class WizardInputBuilder {
    private static final String NEW_LINE = "\n";
    private StringBuilder input;

    public WizardInputBuilder() {
        this.input = new StringBuilder();
    }

    /**
     * Adds command entered by the user to the input.
     *
     * @param command The command by the user
     */
    public WizardInputBuilder add(String command) {
        input.append(command).append(NEW_LINE);
        return this;
    }

    /**
     * Adds Yes Flag entered by the user to the input.
     */
    public WizardInputBuilder addYesFlag() {
        input.append(OptionalPrompt.YES_FLAG).append(NEW_LINE);
        return this;
    }

    /**
     * Adds No Flag entered by the user to the input.
     */
    public WizardInputBuilder addNoFlag() {
        input.append(OptionalPrompt.NO_FLAG).append(NEW_LINE);
        return this;
    }

    /**
     * Returns the {@code input} generated from this {@link WizardInputBuilder}.
     */
    public String build() {
        return input.toString();
    }

}
