package reposense.parser;

import java.util.regex.Pattern;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

/**
 * Represents an alphanumeric type String argument.
 */
public class AlphanumericArgumentType implements ArgumentType<String> {
    private static final String PARSE_EXCEPTION_MESSAGE_NOT_IN_ALPLANUMERIC =
            "Invalid format. It must be in alphanumeric.";
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("[A-Za-z0-9]+");

    @Override
    public String convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        if (!ALPHANUMERIC_PATTERN.matcher(value).matches()) {
            throw new ArgumentParserException(
                    String.format(PARSE_EXCEPTION_MESSAGE_NOT_IN_ALPLANUMERIC, value), parser);
        }

        return value;
    }
}
