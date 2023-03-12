package reposense.wizard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import reposense.util.WizardInputBuilder;

public class WizardRunnerTest {
    private static final String YES_FLAG = "Y";
    private static final String NO_FLAG = "N";
    private static final String SINCE_DATE = "30/09/2022";
    private static final String REPORT_PATH = "C:\\User\\RepoSense";
    private static final String REPO_LINK = "https://github.com/reposense/RepoSense.git";

    @Test
    public void buildInput_repoOnly_success() {
        WizardRunner wizardRunner = new WizardRunner(new BasicWizard());
        String input = new WizardInputBuilder()
                .append(NO_FLAG)
                .append(NO_FLAG)
                .append(REPO_LINK)
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
                .append(YES_FLAG)
                .append(SINCE_DATE)
                .append(NO_FLAG)
                .append(REPO_LINK)
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
                .append(NO_FLAG)
                .append(YES_FLAG)
                .append(REPORT_PATH)
                .append(REPO_LINK)
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
                .append(YES_FLAG)
                .append(SINCE_DATE)
                .append(YES_FLAG)
                .append(REPORT_PATH)
                .append(REPO_LINK)
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
