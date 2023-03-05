package reposense.wizard;

import static reposense.wizard.InputBuilder.translateCommandline;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Scanner;

import reposense.RepoSense;

/**
 * This class implements a wizard runner in the form
 * of a discrete event simulator.
 * It maintains a queue of prompts and run through
 * the prompts until the queue is empty.
 */
public class WizardRunner {
    private final Queue<Prompt> prompts;
    private InputBuilder inputBuilder;

    public WizardRunner(Wizard wizard) {
        prompts = new ArrayDeque<>();
        for (Prompt p : wizard.getInitialPrompts()) {
            prompts.add(p);
        }
        inputBuilder = new InputBuilder();
    }

    /**
     * Run the wizard until no more prompts is in the queue.
     * If the wizard returns one or more prompts, add them
     * to the queue, and repeat.
     */
    public void buildInput(Scanner sc) {
        Prompt prompt = prompts.poll();
        while (prompt != null) {
            prompt.promptAndGetInput(sc);
            inputBuilder = prompt.addToInput(inputBuilder);
            Prompt[] newPrompts = prompt.run();
            for (Prompt p : newPrompts) {
                prompts.add(p);
            }
            prompt = prompts.poll();
        }
    }

    public String getBuiltInput() {
        return inputBuilder.build();
    }

    public void run() {
        RepoSense.main(translateCommandline(getBuiltInput()));
    }
}
