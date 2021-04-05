package reposense.parser;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

/**
 * Verifies and parses a string-formatted integer to an {@code Integer} object.
 */
public class CloningThreadsArgumentType implements ArgumentType<Integer> {
    public Integer convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        return Integer.parseInt(value);
    }
}
