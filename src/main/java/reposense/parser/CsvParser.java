package reposense.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import reposense.system.LogsManager;

/**
 * Contains CSV parsing related functionalities.
 */
public abstract class CsvParser<T> {
    protected static final String COLUMN_VALUES_SEPARATOR = ";";
    protected static final Logger logger = LogsManager.getLogger(CsvParser.class);

    private static final String OVERRIDE_KEYWORD = "override:";
    private static final String MESSAGE_UNABLE_TO_READ_CSV_FILE = "Unable to read the supplied CSV file.";
    private static final String MESSAGE_MALFORMED_LINE_FORMAT = "Line %d in CSV file, %s, is malformed.\n"
            + "Content: %s";
    private static final String MESSAGE_LINE_PARSE_EXCEPTION_FORMAT = "Error parsing line %d in CSV file, %s.\n"
            + "Content: %s\n"
            + "Error: %s";
    private static final String MESSAGE_EMPTY_CSV_FORMAT = "The CSV file, %s, is empty.";

    private Path csvFilePath;
    private int numOfLinesBeforeFirstRecord = 0;

    /**
     * @throws IOException if {@code csvFilePath} is an invalid path.
     */
    public CsvParser(Path csvFilePath) throws IOException {
        if (csvFilePath == null || !Files.exists(csvFilePath)) {
            throw new IOException("Csv file does not exists in given path.\n"
                    + "Use '-help' to list all the available subcommands and some concept guides.");
        }

        this.csvFilePath = csvFilePath;
    }

    /**
     * @throws IOException if there are error accessing the given csv file.
     */
    public List<T> parse() throws IOException {
        List<T> results = new ArrayList<>();
        Iterable<CSVRecord> records;

        try (BufferedReader csvReader = new BufferedReader(new FileReader(csvFilePath.toFile()))) {
            String[] header = getHeader(csvReader);
            records = CSVFormat.DEFAULT.withIgnoreEmptyLines(false).withHeader(header).parse(csvReader);

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
        } catch (InvalidCsvException ice) {
            throw new IOException(ice.getMessage(), ice);
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
        return currentLine.split(",");
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
        for (int position : mandatoryPositions()) {
            if (record.get(position).isEmpty()) {
                logger.warning(String.format(MESSAGE_MALFORMED_LINE_FORMAT, getLineNumber(record),
                        csvFilePath.getFileName(), getRowContentAsRawString(record)));
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the value of {@code record} at {@code colNum}.
     */
    protected String get(final CSVRecord record, int colNum) {
        return record.get(colNum).trim();
    }

    /**
     * Returns the value of {@code record} at {@code colNum} if present, or
     * returns {@code defaultValue} otherwise.
     */
    protected String getOrDefault(final CSVRecord record, int colNum, String defaultValue) {
        return get(record, colNum).isEmpty() ? defaultValue : get(record, colNum);
    }

    /**
     * Returns the value of {@code record} at {@code colNum} as a {@code List},
     * delimited by {@code COLUMN_VALUES_SEPARATOR} if it is in {@code record} and not empty, or
     * returns an empty {@code List} otherwise.
     */
    protected List<String> getAsList(final CSVRecord record, int colNum) {
        if (get(record, colNum).isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.stream(get(record, colNum).split(COLUMN_VALUES_SEPARATOR))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    /**
     * Returns the values in {@code record} as a list with the {@link CsvParser#OVERRIDE_KEYWORD} prefix removed.
     * Returns an empty list if {@code record} at {@code colNum} is empty.
     */
    protected List<String> getAsListWithoutOverridePrefix(final CSVRecord record, int colNum) {
        List<String> data = getAsList(record, colNum);
        if (isElementOverridingStandaloneConfig(record, colNum)) {
            data.set(0, data.get(0).replaceFirst(OVERRIDE_KEYWORD, ""));
            data.removeIf(String::isEmpty);
        }
        return data;
    }


    private long getLineNumber(final CSVRecord record) {
        return  record.getRecordNumber() + numOfLinesBeforeFirstRecord;
    }

    /**
     * Returns true if the {@code record} at {@code colNum} is prefixed with the override keyword.
     */
    protected boolean isElementOverridingStandaloneConfig(final CSVRecord record, int colNum) {
        return get(record, colNum).startsWith(OVERRIDE_KEYWORD);
    }

    /**
     * Returns the contents of {@code record} as a raw string.
     */
    private String getRowContentAsRawString(final CSVRecord record) {
        StringBuilder inputRowString = new StringBuilder();
        for (int colNum = 0; colNum < record.size(); colNum++) {
            inputRowString.append(get(record, colNum)).append(",");
        }
        return inputRowString.toString();
    }

    /**
     * Gets the list of positions that are mandatory for verification.
     */
    protected abstract int[] mandatoryPositions();

    /**
     * Processes the csv file line by line.
     * All CsvParsers must use {@link CsvParser#get}, {@link CsvParser#getOrDefault},
     * {@link CsvParser#getAsList} or {@link CsvParser#getAsListWithoutOverridePrefix} to read contents in
     * {@code record} and add created objects into {@code results}.
     */
    protected abstract void processLine(List<T> results, final CSVRecord record) throws ParseException;
}
