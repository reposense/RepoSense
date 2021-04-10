package reposense.parser;

import java.util.Optional;
import java.util.regex.Pattern;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

/**
 * Verifies and parses a string-formatted period to an integer.
 */
public class PeriodArgumentType implements ArgumentType<Optional<Integer>> {
    private static final String PARSE_EXCEPTION_MESSAGE_NOT_IN_NUMERIC =
            "Invalid format. Period must be in the format of nd (n days) or nw (n weeks), "
                    + "where n is a number greater than 0.";
    private static final String PARSE_EXCEPTION_MESSAGE_SMALLER_THAN_ZERO =
            "Invalid format. Period must be greater than 0.";
    private static final String PARSE_EXCEPTION_MESSAGE_NUMBER_TOO_LARGE =
            "Invalid format. Input number may be too large.";
    private static final Pattern PERIOD_PATTERN = Pattern.compile("[0-9]+[dw]");

    @Override
    public Optional<Integer> convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        try {
            return parse(value);
        } catch (ParseException pe) {
            throw new ArgumentParserException(pe.getMessage(), parser);
        }
    }

    /**
     * This function parses a {@code period} String and returns an Integer representing the number of days.
     */
    public static Optional<Integer> parse(String period) throws ParseException {
        if (!PERIOD_PATTERN.matcher(period).matches()) {
            throw new ParseException(String.format(PARSE_EXCEPTION_MESSAGE_NOT_IN_NUMERIC, period));
        }

        int multiplier = period.substring(period.length() - 1).equals("d") ? 1 : 7;

        try {
            int convertedValue = Integer.parseInt(period.substring(0, period.length() - 1)) * multiplier;
            if (convertedValue <= 0) {
                throw new ParseException(String.format(PARSE_EXCEPTION_MESSAGE_SMALLER_THAN_ZERO, period));
            }

            return Optional.of(convertedValue);
        } catch (NumberFormatException e) {
            throw new ParseException(String.format(PARSE_EXCEPTION_MESSAGE_NUMBER_TOO_LARGE, period));
        }
    }
}
