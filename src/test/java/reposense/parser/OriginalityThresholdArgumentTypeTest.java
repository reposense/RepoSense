package reposense.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.sourceforge.argparse4j.inf.ArgumentParserException;

public class OriginalityThresholdArgumentTypeTest {

    @Test
    public void originalityThresholdArgumentType_convertValidInput_success() throws ArgumentParserException {
        Assertions.assertEquals(0, convert("0"));
        Assertions.assertEquals(0.0001, convert("0.0001"));
        Assertions.assertEquals(0.5, convert("0.5"));
        Assertions.assertEquals(0.9999, convert("0.9999"));
        Assertions.assertEquals(1, convert("1"));
    }

    @Test
    public void originalityThresholdArgumentType_convertOutOfBoundInput_throwException() {
        Assertions.assertThrowsExactly(ArgumentParserException.class, () -> convert("-10.123"));
        Assertions.assertThrowsExactly(ArgumentParserException.class, () -> convert("-0.0001"));
        Assertions.assertThrowsExactly(ArgumentParserException.class, () -> convert("1.0001"));
        Assertions.assertThrowsExactly(ArgumentParserException.class, () -> convert("10.123"));
    }

    private double convert(String input) throws ArgumentParserException {
        OriginalityThresholdArgumentType converter = new OriginalityThresholdArgumentType();
        return converter.convert(null, null, input);
    }
}
