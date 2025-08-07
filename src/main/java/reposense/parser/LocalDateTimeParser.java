package reposense.parser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import reposense.parser.exceptions.InvalidDatesException;

/**
 * Parses date strings into {@link LocalDateTime} objects.
 *
 * <p>Supported formats:
 * <ul>
 *   <li>dd/MM/yyyy</li>
 *   <li>dd/MM/yyyy HH:mm</li>
 *   <li>dd/MM/yyyy HH:mm:ss</li>
 * </ul>
 * If time is missing, it defaults to either 00:00:00 or 23:59:59.
 */
public class LocalDateTimeParser {
    private static final DateTimeFormatter DATE_TIME_SECONDS =
            DateTimeFormatter.ofPattern("[d][dd]/[M][MM]/yyyy HH:mm:ss");
    private static final DateTimeFormatter DATE_TIME_MINUTES =
            DateTimeFormatter.ofPattern("[d][dd]/[M][MM]/yyyy HH:mm");
    private static final String DEFAULT_START_TIME = " 00:00:00";
    private static final String DEFAULT_END_TIME = " 23:59:59";
    private static final String HAS_TIME_COMPONENT_REGEX = ".*\\d{2}:\\d{2}.*"; // Checks for at least HH:mm
    private static final String MESSAGE_PARSING_INVALID_FORMAT =
            "Invalid date: '%s'. The format should be dd/MM/yyyy, dd/MM/yyyy HH:mm, dd/MM/yyyy HH:mm:ss.";

    /**
     * Parses a date string into a {@link LocalDateTime}.
     *
     * <p>If time is missing, 00:00:00 or 23:59:59 is added based on {@code isStartOfDay}.
     *
     * @param input The date string.
     *
     * @param isStartOfDay Whether to use 00:00:00 (true) or 23:59:59 (false) if time is missing.
     * @return The parsed {@link LocalDateTime}.
     * @throws InvalidDatesException If the format is invalid.
     */
    public static LocalDateTime parse(String input, boolean isStartOfDay) throws InvalidDatesException {
        boolean hasTimeComponent = input.matches(HAS_TIME_COMPONENT_REGEX);
        if (hasTimeComponent) {
            return parseDateAndTime(input);
        } else {
            return parseDateOnly(input, isStartOfDay);
        }
    }
    /**
     * Parses a date without a time component and appends a default time.
     *
     * @param date The date string.
     * @param isStartOfDay Whether to append start or end of day time.
     * @return Parsed {@link LocalDateTime}.
     * @throws InvalidDatesException If parsing fails.
     */
    private static LocalDateTime parseDateOnly(String date, boolean isStartOfDay) throws InvalidDatesException {
        String dateTime;
        if (isStartOfDay) {
            dateTime = date + DEFAULT_START_TIME;
        } else {
            dateTime = date + DEFAULT_END_TIME;
        }

        try {
            return LocalDateTime.parse(dateTime, DATE_TIME_SECONDS);
        } catch (DateTimeParseException e) {
            throw new InvalidDatesException(String.format(MESSAGE_PARSING_INVALID_FORMAT, dateTime));
        }
    }
    /**
     * Parses a date with a time component using known formatters.
     *
     * @param dateTime The full date-time string.
     * @return Parsed {@link LocalDateTime}.
     * @throws InvalidDatesException If parsing fails for all formats.
     */
    private static LocalDateTime parseDateAndTime(String dateTime) throws InvalidDatesException {
        DateTimeFormatter[] formatters = { DATE_TIME_SECONDS, DATE_TIME_MINUTES };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDateTime.parse(dateTime, formatter);
            } catch (DateTimeParseException ignored) {
                // Try next formatter
            }
        }

        throw new InvalidDatesException(String.format(MESSAGE_PARSING_INVALID_FORMAT, dateTime));
    }
}
