package reposense.model;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CliArgumentsTest {
    private static final CliArguments TEST_CLI_ARGUMENTS = new CliArguments.Builder()
            .formats(Arrays.asList(new FileType("docx", Arrays.asList("path"))))
            .locations(Arrays.asList("location", "location2"))
            .reportConfiguration(new ReportConfiguration())
            .build();
    @Test
    public void clone_cloneCliArguments_success() throws Exception {
        Assertions.assertNotSame(CliArgumentsTest.TEST_CLI_ARGUMENTS, CliArgumentsTest.TEST_CLI_ARGUMENTS.clone());
    }

    @Test
    public void clone_cloneCliArgumentsDeeply_success() throws Exception {
        CliArguments clonedCliArguments = CliArgumentsTest.TEST_CLI_ARGUMENTS.clone();
        List<FileType> formatsListOriginal = CliArgumentsTest.TEST_CLI_ARGUMENTS.getFormats();
        List<FileType> formatsListClone = clonedCliArguments.getFormats();

        for (int i = 0; i < formatsListOriginal.size(); i++) {
            Assertions.assertNotSame(formatsListOriginal.get(i), formatsListClone.get(i));
        }

        Assertions.assertNotSame(formatsListOriginal, formatsListClone);
        Assertions.assertNotSame(CliArgumentsTest.TEST_CLI_ARGUMENTS.getLocations(), clonedCliArguments.getLocations());
        Assertions.assertNotSame(CliArgumentsTest.TEST_CLI_ARGUMENTS.getReportConfiguration(),
                clonedCliArguments.getReportConfiguration());
    }
}
