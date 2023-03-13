package reposense.wizard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import reposense.util.WizardInputBuilder;

public class WizardRunnerTest {
    private static final String SINCE_DATE = "30/09/2022";
    private static final String REPORT_PATH = "C:\\User\\RepoSense";
    private static final String REPO_LINK = "https://github.com/reposense/RepoSense.git";

    @Test
    public void buildInput_repoOnly_success() {
        WizardRunner wizardRunner = new WizardRunner(new BasicWizard());
        String input = new WizardInputBuilder()
                .addNoFlag()
                .addNoFlag()
                .add(REPO_LINK)
                .build();
        InputStream targetStream = new ByteArrayInputStream(input.getBytes());
        Scanner sc = new Scanner(targetStream);
        wizardRunner.buildInput(sc);

        String expectedInput = new InputBuilder().addRepos(REPO_LINK).build();
        assertEquals(expectedInput, wizardRunner.getBuiltInput());
    }

    @Test
    public void buildInput_sinceDateAndRepo_success() {
        WizardRunner wizardRunner = new WizardRunner(new BasicWizard());
        // Yes only for sinceDate flag
        String input = new WizardInputBuilder()
                .addYesFlag()
                .add(SINCE_DATE)
                .addNoFlag()
                .add(REPO_LINK)
                .build();
        InputStream targetStream = new ByteArrayInputStream(input.getBytes());
        Scanner sc = new Scanner(targetStream);
        wizardRunner.buildInput(sc);

        String expectedInput = new InputBuilder()
                .addSinceDate(SINCE_DATE)
                .addRepos(REPO_LINK)
                .build();
        assertEquals(expectedInput, wizardRunner.getBuiltInput());
    }

    @Test
    public void buildInput_viewAndRepo_success() {
        WizardRunner wizardRunner = new WizardRunner(new BasicWizard());
        // Yes only for view flag
        String input = new WizardInputBuilder()
                .addNoFlag()
                .addYesFlag()
                .add(REPORT_PATH)
                .add(REPO_LINK)
                .build();
        InputStream targetStream = new ByteArrayInputStream(input.getBytes());
        Scanner sc = new Scanner(targetStream);
        wizardRunner.buildInput(sc);

        String expectedInput = new InputBuilder()
                .addView(Paths.get(REPORT_PATH))
                .addRepos(REPO_LINK)
                .build();
        assertEquals(expectedInput, wizardRunner.getBuiltInput());
    }

    @Test
    public void buildInput_allFlags_success() {
        WizardRunner wizardRunner = new WizardRunner(new BasicWizard());
        // Yes for all flags
        String input = new WizardInputBuilder()
                .addYesFlag()
                .add(SINCE_DATE)
                .addYesFlag()
                .add(REPORT_PATH)
                .add(REPO_LINK)
                .build();
        InputStream targetStream = new ByteArrayInputStream(input.getBytes());
        Scanner sc = new Scanner(targetStream);
        wizardRunner.buildInput(sc);

        String expectedInput = new InputBuilder()
                .addSinceDate(SINCE_DATE)
                .addView(Paths.get(REPORT_PATH))
                .addRepos(REPO_LINK)
                .build();
        assertEquals(expectedInput, wizardRunner.getBuiltInput());
    }
}
