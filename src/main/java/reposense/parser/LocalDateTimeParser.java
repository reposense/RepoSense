package reposense.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import reposense.parser.exceptions.InvalidDatesException;

public class LocalDateTimeParser {
    private static final DateTimeFormatter DATE_TIME_SECONDS = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_MINUTES = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String DEFAULT_START_TIME = " 00:00:00";
    private static final String DEFAULT_END_TIME = " 23:59:59";
    private static final String MESSAGE_PARSING_INVALID_FORMAT
            = "The format for since date and until date should be dd/MM/yyyy";

    public LocalDateTime parseDateOnly(String input, boolean isStartOfDay) {
        if (isStartOfDay) {
            LocalDateTime time = LocalDateTime.parse(input + DEFAULT_START_TIME, DATE_TIME_SECONDS);
            return time;
        } else {
            LocalDateTime time = LocalDateTime.parse(input + DEFAULT_END_TIME, DATE_TIME_SECONDS);
            return time;
        }
    }
    public LocalDateTime parseDateAndTime(String input) throws InvalidDatesException {
        DateTimeFormatter[] formatters = { DATE_TIME_SECONDS, DATE_TIME_MINUTES };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(input, formatter);
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }

        throw new InvalidDatesException(MESSAGE_PARSING_INVALID_FORMAT);
    }
}