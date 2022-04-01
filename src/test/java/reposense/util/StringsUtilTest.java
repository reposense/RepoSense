package reposense.util;

import static reposense.util.TestUtil.loadResource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import reposense.system.CommandRunner;

public class StringsUtilTest {

    private static final Path STRINGS_UTIL_TEST_DIRECTORY = loadResource(FileUtilTest.class, "StringsUtilTest");

    @Test
    public void filterText_standardInput_success() throws Exception {
        Path gitBlameStandardOutputFile =
                Paths.get(STRINGS_UTIL_TEST_DIRECTORY.toString(), "standardBlameOutput.txt");
        List<String> lines = Files.readAllLines(gitBlameStandardOutputFile);
        String text = String.join("\n", lines);

        String expected = "a79711ed676fd3a27af2f466be6d5b48177580e0 4 4\n"
                + "author example\n";

        String filteredText = StringsUtil.filterText(text, "(^author .*)|(^[0-9a-f]{40} .*)");

        Assertions.assertEquals(expected, filteredText);
    }

    @Test
    public void replaceSpecialSymbols_noSpecialSymbols_noChange() {
        String noSpecialSymbolString = "Just A Normal String";
        String convertedString = StringsUtil.replaceSpecialSymbols(noSpecialSymbolString, ".");

        Assertions.assertEquals(noSpecialSymbolString, convertedString);
    }

    @Test
    public void replaceSpecialSymbols_stringWithSpecialSymbolsToPeriod_success() {
        String noSpecialSymbolString = "($t^!ng W!th $pec!@l Symbols)";
        String convertedString = StringsUtil.replaceSpecialSymbols(noSpecialSymbolString, ".");
        String expectedConvertedString = "..t..ng W.th .pec..l Symbols.";

        Assertions.assertEquals(expectedConvertedString, convertedString);
    }

    @Test
    public void removeTrailingBackslash_stringWithMultipleTrailingBackslash_success() {
        String multipleTrailingBackslashString = "abc\\cde\\\\\\";
        String expectedString = "abc\\cde";
        String actualString = StringsUtil.removeTrailingBackslash(multipleTrailingBackslashString);

        Assertions.assertEquals(expectedString, actualString);
    }

    @Test
    public void removeTrailingBackslash_stringWithNoTrailingBackslash_noChange() {
        String multipleTrailingBackslashString = "abc\\cde";
        String actualString = StringsUtil.removeTrailingBackslash(multipleTrailingBackslashString);

        Assertions.assertEquals(multipleTrailingBackslashString, actualString);
    }

    @Test
    public void removeTrailingBackslash_emptyString_noChange() {
        String emptyString = "";
        String actualString = StringsUtil.removeTrailingBackslash(emptyString);

        Assertions.assertEquals(emptyString, actualString);
    }

    @Test
    public void addQuotationMarksForPath_specialBashCharacters_success() {
        Assumptions.assumeTrue(!SystemUtil.isWindows());

        // The characters being tested against are taken from this post: https://unix.stackexchange.com/a/357932
        String specialBashSymbols = "!\"#$&'()*,;<=>?\\[]^`{| }";
        String result = CommandRunner.runCommand(Paths.get("./"),
                "echo " + StringsUtil.addQuotesForFilePath(specialBashSymbols));

        // CommandRunner's output ends with an LF character which should be removed.
        result = result.substring(0, result.length() - 1);

        Assertions.assertEquals(specialBashSymbols, result);
    }
}
