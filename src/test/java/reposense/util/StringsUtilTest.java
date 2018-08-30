package reposense.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class StringsUtilTest {
    private static final Path STRINGS_UTIL_TEST_DIRECTORY = new File(FileUtilTest.class.getClassLoader()
            .getResource("StringsUtilTest").getFile()).toPath().toAbsolutePath();

    @Test
    public void filterText_standardInput_success() throws IOException {
        Path gitBlameStandardInputFile =
                Paths.get(STRINGS_UTIL_TEST_DIRECTORY.toString(), "standardBlameOutput.txt");
        List<String> lines = Files.readAllLines(gitBlameStandardInputFile);
        String text = String.join("\n", lines);

        String expected = "d8a264aa4bb443a4526d8ae6ed11667e1b86c8ff 1 1 3\n"
                + "author eugenepeh\n";

        String filteredText = StringsUtil.filterText(text, "(^author .*)|(^[0-9a-f]{40} .*)");

        Assert.assertEquals(expected, filteredText);
    }
}
