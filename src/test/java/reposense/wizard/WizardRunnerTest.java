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
    private static final String UNTIL_DATE = "15/03/2023";
    private static final String REPORT_PATH = "C:\\User\\RepoSense";
    private static final String REPO_LINK = "https://github.com/reposense/RepoSense.git";

    @Test
    public void buildInput_repoOnly_success() {
        WizardRunner wizardRunner = new WizardRunner(new BasicWizard());
        String input = new WizardInputBuilder()
                .add(REPO_LINK)
                .addNoFlag()
                .addNoFlag()
                .addNoFlag()
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
                .add(REPO_LINK)
                .addYesFlag()
                .add(SINCE_DATE)
                .addNoFlag()
                .addNoFlag()
                .build();
        InputStream targetStream = new ByteArrayInputStream(input.getBytes());
        Scanner sc = new Scanner(targetStream);
        wizardRunner.buildInput(sc);

        String expectedInput = new InputBuilder()
                .addRepos(REPO_LINK)
                .addSinceDate(SINCE_DATE)
                .build();
        assertEquals(expectedInput, wizardRunner.getBuiltInput());
    }

    @Test
    public void buildInput_untilDateAndRepo_success() {
        WizardRunner wizardRunner = new WizardRunner(new BasicWizard());
        String input = new WizardInputBuilder()
                .add(REPO_LINK)
                .addNoFlag()
                .addYesFlag()
                .add(UNTIL_DATE)
                .addNoFlag()
                .build();
        InputStream targetStream = new ByteArrayInputStream(input.getBytes());
        Scanner sc = new Scanner(targetStream);
        wizardRunner.buildInput(sc);

        String expectedInput = new InputBuilder()
                .addRepos(REPO_LINK)
                .addUntilDate(UNTIL_DATE)
                .build();
        assertEquals(expectedInput, wizardRunner.getBuiltInput());
    }

    @Test
    public void buildInput_sinceDateAndUntilDateAndRepo_success() {
        WizardRunner wizardRunner = new WizardRunner(new BasicWizard());
        String input = new WizardInputBuilder()
                .add(REPO_LINK)
                .addYesFlag()
                .add(SINCE_DATE)
                .addYesFlag()
                .add(UNTIL_DATE)
                .addNoFlag()
                .build();
        InputStream targetStream = new ByteArrayInputStream(input.getBytes());
        Scanner sc = new Scanner(targetStream);
        wizardRunner.buildInput(sc);

        String expectedInput = new InputBuilder()
                .addRepos(REPO_LINK)
                .addSinceDate(SINCE_DATE)
                .addUntilDate(UNTIL_DATE)
                .build();
        assertEquals(expectedInput, wizardRunner.getBuiltInput());
    }

    @Test
    public void buildInput_viewAndRepo_success() {
        WizardRunner wizardRunner = new WizardRunner(new BasicWizard());
        // Yes only for view flag
        String input = new WizardInputBuilder()
                .add(REPO_LINK)
                .addNoFlag()
                .addNoFlag()
                .addYesFlag()
                .add(REPORT_PATH)
                .build();
        InputStream targetStream = new ByteArrayInputStream(input.getBytes());
        Scanner sc = new Scanner(targetStream);
        wizardRunner.buildInput(sc);

        String expectedInput = new InputBuilder()
                .addRepos(REPO_LINK)
                .addView(Paths.get(REPORT_PATH))
                .build();
        assertEquals(expectedInput, wizardRunner.getBuiltInput());
    }

    @Test
    public void buildInput_allFlags_success() {
        WizardRunner wizardRunner = new WizardRunner(new BasicWizard());
        // Yes for all flags
        String input = new WizardInputBuilder()
                .add(REPO_LINK)
                .addYesFlag()
                .add(SINCE_DATE)
                .addYesFlag()
                .add(UNTIL_DATE)
                .addYesFlag()
                .add(REPORT_PATH)
                .build();
        InputStream targetStream = new ByteArrayInputStream(input.getBytes());
        Scanner sc = new Scanner(targetStream);
        wizardRunner.buildInput(sc);

        String expectedInput = new InputBuilder()
                .addRepos(REPO_LINK)
                .addSinceDate(SINCE_DATE)
                .addUntilDate(UNTIL_DATE)
                .addView(Paths.get(REPORT_PATH))
                .build();
        assertEquals(expectedInput, wizardRunner.getBuiltInput());
    }
}
