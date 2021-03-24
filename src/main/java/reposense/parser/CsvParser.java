package reposense.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import reposense.system.LogsManager;

/**
 * Contains CSV parsing related functionalities.
 */
public abstract class CsvParser<T> {
    protected static final String COLUMN_VALUES_SEPARATOR = ";";
    protected static final Logger logger = LogsManager.getLogger(CsvParser.class);

    private static final String EMPTY_STRING = "";
    private static final String OVERRIDE_KEYWORD = "override:";
    private static final String MESSAGE_EMPTY_LINE = "[EMPTY LINE]";
    private static final String MESSAGE_UNABLE_TO_READ_CSV_FILE = "Unable to read the supplied CSV file.";
    private static final String MESSAGE_MALFORMED_LINE_FORMAT = "Line %d in CSV file, %s, is malformed.\n"
            + "Content: %s";
    private static final String MESSAGE_LINE_PARSE_EXCEPTION_FORMAT = "Error parsing line %d in CSV file, %s.\n"
            + "Content: %s\n"
            + "Error: %s";
    private static final String MESSAGE_EMPTY_CSV_FORMAT = "The CSV file, %s, is empty.";
    private static final String MESSAGE_MANDATORY_HEADER_MISSING = "Required column header, %s, not found in "
            + "CSV file, %s";
    private static final String MESSAGE_DUPLICATE_COLUMN_HEADER = "Duplicate columns are present in CSV file, %s.";
    private static final String MESSAGE_COLUMNS_RECOGNIZED = "Parsed header of CSV file, %s, and recognized columns: "
            + "%s";
    private static final String MESSAGE_ZERO_VALID_CONFIGS = "No valid configurations in the %s.";

    private Path csvFilePath;
    private Map<String, Integer> headerMap = new HashMap();
    private int numOfLinesBeforeFirstRecord = 0;

    /**
     * @throws IOException if {@code csvFilePath} is an invalid path.
     */
    public CsvParser(Path csvFilePath) throws IOException {
        if (csvFilePath == null || !Files.exists(csvFilePath)) {
            throw new FileNotFoundException("Csv file does not exist at the given path.\n"
                    + "Use '-help' to list all the available subcommands and some concept guides.");
        }

        this.csvFilePath = csvFilePath;
    }

    /**
     * Parses the csv file associated with this instance of the {@code CsvParser} and returns a {@code List}
     * containing the records in this file.
     *
     * @throws IOException if there are errors accessing the given csv file.
     * @throws InvalidCsvException if the csv is malformed.
     */
    public List<T> parse() throws IOException, InvalidCsvException {
        List<T> results = new ArrayList<>();
        Iterable<CSVRecord> records;

        try (BufferedReader csvReader = new BufferedReader(new FileReader(csvFilePath.toFile()))) {
            String[] header = getHeader(csvReader);
            try {
                records = CSVFormat.DEFAULT.withIgnoreEmptyLines(false).withHeader(header).withTrim()
                        .withIgnoreHeaderCase().parse(csvReader);
            } catch (IllegalArgumentException iae) {
                throw new InvalidCsvException(
                        String.format(MESSAGE_DUPLICATE_COLUMN_HEADER, csvFilePath.getFileName()));
            }

            for (CSVRecord record : records) {
                if (isLineMalformed(record)) {
                    continue;
                }
                try {
                    processLine(results, record);
                } catch (ParseException pe) {
                    logger.warning(String.format(MESSAGE_LINE_PARSE_EXCEPTION_FORMAT, getLineNumber(record),
                            csvFilePath.getFileName(), getRowContentAsRawString(record), pe.getMessage()));
                } catch (IllegalArgumentException iae) {
                    logger.log(Level.WARNING, iae.getMessage(), iae);
                }
            }
        } catch (IOException ioe) {
            throw new IOException(MESSAGE_UNABLE_TO_READ_CSV_FILE, ioe);
        }

        if (results.isEmpty()) {
            throw new InvalidCsvException(String.format(MESSAGE_ZERO_VALID_CONFIGS, csvFilePath.getFileName()));
        }
        return results;
    }

    /**
     * Returns the header of a CSV file, which is assumed to be the first non-empty / non-whitespace line in the file.
     * The line is split into an array of Strings, using the comma symbol as delimiter.
     *
     * @throws IOException if there is an error accessing the file.
     * @throws InvalidCsvException if the file has only empty or blank lines.
     */
    private String[] getHeader(BufferedReader reader) throws IOException, InvalidCsvException {
        String currentLine = "";

        // read from file until we encounter a line that is neither blank nor empty
        while (currentLine.isEmpty()) {
            currentLine = Optional.ofNullable(reader.readLine())
                    .map(String::trim)
                    .orElseThrow(() -> new InvalidCsvException(String.format(
                            MESSAGE_EMPTY_CSV_FORMAT, csvFilePath.getFileName())));

            numOfLinesBeforeFirstRecord++;
        }
        String[] header = currentLine.split(",");
        validateHeader(header);
        return header;
    }

    /**
     * Returns true if {@code record} does not contain the same number of columns as the header or contains missing
     * values at the mandatory columns in CSV format.
     */
    private boolean isLineMalformed(CSVRecord record) {
        if (!record.isConsistent()) {
            logger.warning(String.format(MESSAGE_MALFORMED_LINE_FORMAT, getLineNumber(record),
                    csvFilePath.getFileName(), getRowContentAsRawString(record)));
            return true;
        }
        for (String header : mandatoryHeaders()) {
            if (get(record, header).isEmpty()) {
                logger.warning(String.format(MESSAGE_MALFORMED_LINE_FORMAT, getLineNumber(record),
                        csvFilePath.getFileName(), getRowContentAsRawString(record)));
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the value of {@code record} at the column with the header {@code header}.
     */
    protected String get(final CSVRecord record, String header) {
        if (headerMap.containsKey(header)) {
            return record.get(headerMap.get(header)).trim();
        } else {
            return EMPTY_STRING;
        }
    }

    /**
     * Returns the value of {@code record} at the column with the header {@code header} if present, or
     * returns {@code defaultValue} otherwise.
     */
    protected String getOrDefault(final CSVRecord record, String header, String defaultValue) {
        return get(record, header).isEmpty() ? defaultValue : get(record, header);
    }

    /**
     * Returns the value of {@code record} at the column with the header {@code header} as a {@code List},
     * delimited by {@code COLUMN_VALUES_SEPARATOR} if it is in {@code record} and not empty, or
     * returns an empty {@code List} otherwise.
     */
    protected List<String> getAsList(final CSVRecord record, String header) {
        if (get(record, header).isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(get(record, header).split(COLUMN_VALUES_SEPARATOR))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /**
     * Returns the values in {@code record} as a list with the {@link CsvParser#OVERRIDE_KEYWORD} prefix removed.
     * Returns an empty list if {@code record} at the column with the header {@code header} is empty.
     */
    protected List<String> getAsListWithoutOverridePrefix(final CSVRecord record, String header) {
        List<String> data = getAsList(record, header);
        if (isElementOverridingStandaloneConfig(record, header)) {
            data.set(0, data.get(0).replaceFirst(OVERRIDE_KEYWORD, ""));
            data.removeIf(String::isEmpty);
        }
        return data;
    }


    private long getLineNumber(final CSVRecord record) {
        return  record.getRecordNumber() + numOfLinesBeforeFirstRecord;
    }

    /**
     * Returns true if the {@code record} at the column with the header {@code header} is prefixed with
     * the override keyword.
     */
    protected boolean isElementOverridingStandaloneConfig(final CSVRecord record, String header) {
        return get(record, header).startsWith(OVERRIDE_KEYWORD);
    }

    /**
     * Returns the contents of {@code record} as a raw string.
     */
    private String getRowContentAsRawString(final CSVRecord record) {
        StringJoiner inputRowString = new StringJoiner(",");
        for (String value : record) {
            inputRowString.add(value);
        }
        String contentAsString = inputRowString.toString();
        if (contentAsString.trim().isEmpty()) {
            contentAsString = MESSAGE_EMPTY_LINE;
        }
        return contentAsString;
    }

    /**
     * Generates map of column header to position number for input {@code possibleHeader}.
     * @throws InvalidCsvException if {@code possibleHeader} does not contain all the mandatory headers.
     */
    private void validateHeader(String[] possibleHeader) throws InvalidCsvException {
        int headerSize = possibleHeader.length;
        for (int i = 0; i < headerSize; i++) {
            String possible = possibleHeader[i].trim();
            for (String parsedHeader : mandatoryAndOptionalHeaders()) {
                if (possible.equalsIgnoreCase(parsedHeader)) {
                    headerMap.put(parsedHeader, i);
                    break;
                }
            }
        }
        for (String mandatory : mandatoryHeaders()) {
            if (!headerMap.containsKey(mandatory)) {
                throw new InvalidCsvException(String.format(
                        MESSAGE_MANDATORY_HEADER_MISSING, mandatory, csvFilePath.getFileName()));
            }
        }
        logger.info(String.format(MESSAGE_COLUMNS_RECOGNIZED, csvFilePath.getFileName(),
                String.join(",  ", headerMap.keySet())));
    }

    /**
     * Gets the list of headers that are mandatory for verification.
     */
    protected abstract String[] mandatoryHeaders();

    /**
     * Gets the list of optional headers that can be parsed.
     */
    protected abstract String[] optionalHeaders();

    /**
     * Gets the list of all mandatory and optional headers that can be parsed.
     */
    protected List<String> mandatoryAndOptionalHeaders() {
        return Stream.concat(Arrays.stream(mandatoryHeaders()), Arrays.stream(optionalHeaders()))
                .collect(Collectors.toList());
    }

    /**
     * Processes the csv file line by line.
     * All CsvParsers must use {@link CsvParser#get}, {@link CsvParser#getOrDefault},
     * {@link CsvParser#getAsList} or {@link CsvParser#getAsListWithoutOverridePrefix} to read contents in
     * {@code record} and add created objects into {@code results}.
     */
    protected abstract void processLine(List<T> results, final CSVRecord record) throws ParseException;
}
