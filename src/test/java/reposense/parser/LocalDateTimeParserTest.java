package reposense.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reposense.parser.exceptions.InvalidDatesException;

import java.time.LocalDateTime;

public class LocalDateTimeParserTest {
    @Test
    public void singleDigitDayAndMonth_validFormat_success() {
        String input = "1/5/2024";
        LocalDateTime expected = LocalDateTime.of(2024, 5, 1, 0, 0, 0);
        try {
            LocalDateTime output = LocalDateTimeParser.parse(input, true);
            Assertions.assertEquals(output, expected);
        } catch (InvalidDatesException e) {
            Assertions.fail("Unexpected InvalidDatesException thrown: " + e.getMessage());
        }
    }

    @Test
    public void dateOnlyIsEndOfDay_validFormat_success() {
        String input = "1/5/2024";
        LocalDateTime expected = LocalDateTime.of(2024, 5, 1, 23, 59, 59);
        try {
            LocalDateTime output = LocalDateTimeParser.parse(input, false);
            Assertions.assertEquals(output, expected);
        } catch (InvalidDatesException e) {
            Assertions.fail("Unexpected InvalidDatesException thrown: " + e.getMessage());
        }
    }

    @Test
    public void dateAndTimeMinutes_validFormat_success() {
        String input = "10/10/2024 12:30";
        LocalDateTime expected = LocalDateTime.of(2024, 10, 10, 12, 30, 0);
        try {
            LocalDateTime output = LocalDateTimeParser.parse(input, true);
            Assertions.assertEquals(output, expected);
        } catch (InvalidDatesException e) {
            Assertions.fail("Unexpected InvalidDatesException thrown: " + e.getMessage());
        }
    }

    @Test
    public void dateAndTimeSeconds_validFormat_success() {
        String input = "10/10/2024 12:30:19";
        LocalDateTime expected = LocalDateTime.of(2024, 10, 10, 12, 30, 19);
        try {
            LocalDateTime output = LocalDateTimeParser.parse(input, true);
            Assertions.assertEquals(output, expected);
        } catch (InvalidDatesException e) {
            Assertions.fail("Unexpected InvalidDatesException thrown: " + e.getMessage());
        }
    }

    @Test
    public void usingDash_invalidDateFormat_failure() {
        String input = "10-10-2024 12:30:19"; // using - instead of /
        Assertions.assertThrows(InvalidDatesException.class, () -> LocalDateTimeParser.parse(input, true));
    }

    @Test
    public void wrongDateOrdering_invalidFormat_failure() {
        String input = "2024/10/10 12:30:19"; // yyyy/MM/dd instead of dd/MM/yyyy
        Assertions.assertThrows(InvalidDatesException.class, () -> LocalDateTimeParser.parse(input, true));
    }
}
