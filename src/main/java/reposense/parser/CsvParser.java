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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import reposense.system.LogsManager;

/**
 * Contains CSV parsing related functionalities.
 */
public abstract class CsvParser<T> {
    protected static final String COLUMN_VALUES_SEPARATOR = ";";
    protected static final Logger logger = LogsManager.getLogger(CsvParser.class);

    private static final String ELEMENT_SEPARATOR = ",";
    private static final String OVERRIDE_KEYWORD = "override:";
    private static final String MESSAGE_UNABLE_TO_READ_CSV_FILE = "Unable to read the supplied CSV file.";
    private static final String MESSAGE_MALFORMED_LINE_FORMAT = "Warning! line %d in CSV file, %s, is malformed.\n"
            + "Content: %s";
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

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath.toFile()))) {
            // Skip first line, which is the header row
            br.readLine();
            String line;

            for (int lineNumber = 2; (line = br.readLine()) != null; lineNumber++) {
                String[] elements = line.split(ELEMENT_SEPARATOR);

                if (line.isEmpty() || isLineMalformed(elements, lineNumber, line)) {
                    continue;
                }

                try {
                    processLine(results, elements);
                } catch (ParseException pe) {
                    logger.warning(String.format(MESSAGE_LINE_PARSE_EXCEPTION_FORMAT,
                            lineNumber, csvFilePath.getFileName(), line, pe.getMessage()));
                }
            }
        } catch (IOException ioe) {
            throw new IOException(MESSAGE_UNABLE_TO_READ_CSV_FILE, ioe);
        } catch (IllegalArgumentException iae) {
            logger.log(Level.WARNING, iae.getMessage(), iae);
        }

        return results;
    }

    /**
     * Checks if the {@code line} contains values at the mandatory positions in CSV format.
     */
    private boolean isLineMalformed(final String[] elements, int lineNumber, String line) {
        for (int position : mandatoryPositions()) {
            if (!containsValueAtPosition(elements, position)) {
                logger.warning(String.format(MESSAGE_MALFORMED_LINE_FORMAT,
                        lineNumber, csvFilePath.getFileName(), line));
                return true;
            }
        }

        return false;
    }

    /**
     * Checks that {@code position} in within the range of {@code element} array and
     * value in {@code position} is not empty.
     */
    private boolean containsValueAtPosition(final String[] elements, int position) {
        return elements.length > position && !elements[position].isEmpty();
    }

    /**
     * Removes the override keyword for {@code position} in {@code elements}.
     */
    protected void removeOverrideKeywordFromElement(final String[] elements, int position) {
        if (isElementOverridingStandaloneConfig(elements, position)) {
            elements[position] = elements[position].replaceFirst(OVERRIDE_KEYWORD, "");
        }
    }

    /**
     * Gets the value of {@code position} in {@code elements}.
     * Returns the value of {@code position} if it is in {@code element} and not empty.
     * Otherwise returns an empty string.
     */
    protected String getValueInElement(final String[] elements, int position) {
        return (containsValueAtPosition(elements, position)) ? elements[position] : "";
    }

    /**
     * Gets the value of {@code position} in {@code elements}.
     * Returns the value of {@code position} if it is in {@code element} and not empty.
     * Otherwise returns the {@code defaultValue}.
     */
    protected String getValueInElement(final String[] elements, int position, String defaultValue) {
        return (containsValueAtPosition(elements, position)) ? elements[position] : defaultValue;
    }

    /**
     * Gets the value of {@code position} in {@code elements}.
     * Returns the value of {@code element} at {@code position} as a {@code List},
     * delimited by {@code COLUMN_VALUES_SEPARATOR} if it is in {@code element} and not empty.
     * Otherwise returns an empty {@code List}.
     */
    protected List<String> getManyValueInElement(final String[] elements, int position) {
        if (!containsValueAtPosition(elements, position)) {
            return Collections.emptyList();
        }

        String manyValue = getValueInElement(elements, position);
        return Arrays.stream(manyValue.split(COLUMN_VALUES_SEPARATOR)).map(String::trim).collect(Collectors.toList());
    }

    /**
     * Checks if {@code position} in {@code element} is prefixed with the override keyword.
     */
    protected boolean isElementOverridingStandaloneConfig(final String[] elements, int position) {
        return (containsValueAtPosition(elements, position)) && elements[position].startsWith(OVERRIDE_KEYWORD);
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
    protected abstract void processLine(List<T> results, final String[] elements) throws ParseException;
}
