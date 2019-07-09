package reposense.parser;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

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
    private static final String MESSAGE_MALFORMED_LINE_FORMAT = "Warning! line %d in CSV file, %s, is malformed.";
    private static final String MESSAGE_LINE_PARSE_EXCEPTION_FORMAT =
            "Warning! Error parsing line %d in CSV file, %s.\n"
            + "Content: %s\n"
            + "Error: %s";

    private Path csvFilePath;

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
        Reader csvReader;
        Iterable<CSVRecord> records;

        try {
            csvReader = new FileReader(this.csvFilePath.toFile());
            records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvReader);
        } catch (IOException ioe) {
            throw new IOException(MESSAGE_UNABLE_TO_READ_CSV_FILE, ioe);
        }

        for (CSVRecord record : records) {
            if (isLineMalformed(record)) {
                continue;
            }

            try {
                processLine(results, record);
            } catch (ParseException pe) {
                logger.warning(String.format(MESSAGE_LINE_PARSE_EXCEPTION_FORMAT,
                        getLineNumber(record), csvFilePath.getFileName(), record.toString(), pe.getMessage()));
            }
        }

        return results;
    }

    /**
     * Returns true if {@code elements} at row {@code lineNumber} does not contain values at the mandatory positions
     * in CSV format.
     */
    private boolean isLineMalformed(CSVRecord record) {
        for (int position : mandatoryPositions()) {
            if (record.get(position).isEmpty()) {
                logger.warning(String.format(MESSAGE_MALFORMED_LINE_FORMAT, getLineNumber(record),
                        csvFilePath.getFileName()));
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the value of {@code record} at column number {@code position} if present, or
     * returns {@code defaultValue} otherwise.
     */
    protected String getOrDefault(final CSVRecord record, int position, String defaultValue) {
        return (record.get(position).isEmpty()) ? defaultValue : record.get(position);
    }

    /**
     * Returns the value of {@code record} at {@code position} as a {@code List},
     * delimited by {@code COLUMN_VALUES_SEPARATOR} if it is in {@code element} and not empty, or
     * returns an empty {@code List} otherwise.
     */
    protected List<String> getAsList(final CSVRecord record, int position) {
        return (position >= record.size() || record.get(position).isEmpty())
                ? Collections.emptyList()
                : Arrays.asList(record.get(position).split(COLUMN_VALUES_SEPARATOR));
    }

    /**
     * Returns the values in {@code record} as a list with the {@link CsvParser#OVERRIDE_KEYWORD} prefix removed.
     * Returns an empty list if {@code record} at column number {@code position} is empty.
     */
    protected List<String> getAsListWithoutOverridePrefix(final CSVRecord record, int position) {
        if (position >= record.size() || record.get(position).isEmpty()) {
            return Collections.emptyList();
        }

        String rawValue = (isElementOverridingStandaloneConfig(record, position))
                ? record.get(position).replaceFirst(OVERRIDE_KEYWORD, "")
                : record.get(position);
        return Arrays.asList(rawValue.split(COLUMN_VALUES_SEPARATOR));
    }

    private long getLineNumber(final CSVRecord record) {
        return  record.getRecordNumber() + 1;
    }

    /**
     * Checks if {@code position} in {@code element} is prefixed with the override keyword.
     */
    protected boolean isElementOverridingStandaloneConfig(final CSVRecord record, int position) {
        return position < record.size() && record.get(position).startsWith(OVERRIDE_KEYWORD);
    }

    /**
     * Gets the list of positions that are mandatory for verification.
     */
    protected abstract int[] mandatoryPositions();

    /**
     * Processes the csv file line by line.
     * All CsvParsers should use {@code getValueInElement} or {@code getManyValueInElement} to read contents in
     * {@code elements} and add created objects into {@code results}.
     */
    protected abstract void processLine(List<T> results, final CSVRecord record) throws ParseException;
}
