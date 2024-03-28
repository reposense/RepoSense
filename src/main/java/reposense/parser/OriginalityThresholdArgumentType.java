package reposense.parser;

import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.ArgumentType;

/**
 * Verifies and parses a string-formatted double, between 0.0 and 1.0, to an {@link Double} object.
 */
public class OriginalityThresholdArgumentType implements ArgumentType<Double> {
    private static final String PARSE_EXCEPTION_MESSAGE_THRESHOLD_OUT_OF_BOUND =
            "Invalid threshold. It must be a number between 0.0 and 1.0.";

    @Override
    public Double convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
        double threshold = Double.parseDouble(value);

        if (Double.compare(threshold, 0.0) < 0 || Double.compare(threshold, 1.0) > 0) {
            throw new ArgumentParserException(PARSE_EXCEPTION_MESSAGE_THRESHOLD_OUT_OF_BOUND, parser);
        }

        return threshold;
    }
}
