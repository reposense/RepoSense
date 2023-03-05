package reposense.wizard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

public class WizardRunnerTest {
    @Test
    public void buildInput_sinceOnly_success() {
        WizardRunner wizardRunner = new WizardRunner(new BasicWizard());
        String initialString = "30/09/2022\n";
        InputStream targetStream = new ByteArrayInputStream(initialString.getBytes());
        Scanner sc = new Scanner(targetStream);
        wizardRunner.buildInput(sc);

        String expectedInput = new InputBuilder().addSinceDate("30/09/2022").build();
        assertEquals(expectedInput, wizardRunner.getBuiltInput());
    }
}
