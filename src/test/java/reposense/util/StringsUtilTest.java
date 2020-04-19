package reposense.util;

import java.io.File;
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
    public void filterText_standardInput_success() throws Exception {
        Path gitBlameStandardOutputFile =
                Paths.get(STRINGS_UTIL_TEST_DIRECTORY.toString(), "standardBlameOutput.txt");
        List<String> lines = Files.readAllLines(gitBlameStandardOutputFile);
        String text = String.join("\n", lines);

        String expected = "a79711ed676fd3a27af2f466be6d5b48177580e0 4 4\n"
                + "author example\n";

        String filteredText = StringsUtil.filterText(text, "(^author .*)|(^[0-9a-f]{40} .*)");

        Assert.assertEquals(expected, filteredText);
    }

    @Test
    public void replaceSpecialSymbols_noSpecialSymbols_noChange() {
        String noSpecialSymbolString = "Just A Normal String";
        String convertedString = StringsUtil.replaceSpecialSymbols(noSpecialSymbolString, ".");

        Assert.assertEquals(noSpecialSymbolString, convertedString);
    }

    @Test
    public void replaceSpecialSymbols_stringWithSpecialSymbolsToPeriod_success() {
        String noSpecialSymbolString = "($t^!ng W!th $pec!@l Symbols)";
        String convertedString = StringsUtil.replaceSpecialSymbols(noSpecialSymbolString, ".");
        String expectedConvertedString = "..t..ng W.th .pec..l Symbols.";

        Assert.assertEquals(expectedConvertedString, convertedString);
    }
}
