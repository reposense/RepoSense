package reposense.util;

import static reposense.util.StringsUtil.decodeString;
import static reposense.util.TestUtil.loadResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class StringsUtilTest {

    private static final Path STRINGS_UTIL_TEST_DIRECTORY = loadResource(FileUtilTest.class, "StringsUtilTest");

    @Test
    public void filterText_standardInput_success() throws Exception {
        Path gitBlameStandardOutputFile =
                Paths.get(decodeString(STRINGS_UTIL_TEST_DIRECTORY), "standardBlameOutput.txt");
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

    @Test
    public void decodeString_pathWithWhiteSpace_success() {
        String pathString = "src\\a b\\c d\\e";
        String convertedString = StringsUtil.decodeString(Paths.get(pathString));

        Assert.assertEquals(pathString, convertedString);
    }

    @Test
    public void decodeString_pathWithInvalidEncoding_success() {
        StringsUtil.setEncoding("invalid encoding");
        String pathString = "src\\a b\\c d\\e";
        String convertedString = StringsUtil.decodeString(Paths.get(pathString));

        Assert.assertNotEquals(pathString, convertedString);
        StringsUtil.setEncoding("UTF-8");
    }
}
