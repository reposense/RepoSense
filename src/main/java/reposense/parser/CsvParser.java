package reposense.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import reposense.system.LogsManager;

public abstract class CsvParser<T> {
    protected static final String AUTHOR_ALIAS_AND_GLOB_SEPARATOR = ";";
    protected static final Logger logger = LogsManager.getLogger(CsvParser.class);

    private static final String ELEMENT_SEPARATOR = ",";
    private static final String MESSAGE_UNABLE_TO_READ_CSV_FILE = "Unable to read the supplied CSV file.";
    private static final String MESSAGE_MALFORMED_LINE_FORMAT = "Warning! line %d in configuration file is malformed.\n"
            + "Contents: %s";

    private Path csvFilePath;

    /**
     * @throws IOException if {@code csvFilePath} is an invalid path.
     */
    public CsvParser(Path csvFilePath) throws IOException {
        if (csvFilePath == null || !Files.exists(csvFilePath)) {
            throw new IOException("Csv file does not exists in given path");
        }

        this.csvFilePath = csvFilePath;
    }

    /**
     * @throws IOException if there are error accessing the given csv file.
     */
    public List<T> parse() throws IOException {
        List<T> results = new ArrayList<>();
        int lineNumber = 1;

        try {
            // Skip first line, which is the header row
            final Collection<String> lines = Files.lines(csvFilePath).skip(1).collect(Collectors.toList());

            for (final String line : lines) {
                String[] elements = line.split(ELEMENT_SEPARATOR);

                if (line.isEmpty() || isLineMalformed(elements, lineNumber, line)) {
                    continue;
                }

                processLine(results, elements);
                lineNumber++;
            }
        } catch (IOException ioe) {
            throw new IOException(MESSAGE_UNABLE_TO_READ_CSV_FILE, ioe);
        } catch (ParseException pe) {
            logger.log(Level.WARNING, pe.getMessage(), pe);
        }

        return results;
    }

    private boolean isLineMalformed(final String[] elements, int lineNumber, String line) {
        for (int position : mandatoryPositions()) {
            if (!containsValueAtPosition(elements, position)) {
                logger.warning(String.format(MESSAGE_MALFORMED_LINE_FORMAT, lineNumber, line));
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
     * Gets the value of {@code position} in {@code elements}.
     * Returns the value of {@code position} if it is in {@code element} and not empty.
     * Otherwise returns an empty string.
     */
    protected String getValueInElement(final String[] elements, int position) {
        return (containsValueAtPosition(elements, position)) ? elements[position] : "";
    }

    /**
     * Gets the value of {@code position} in {@code elements}.
     * Returns the value of {@code position} in an array, delimited by {@code AUTHOR_ALIAS_AND_GLOB_SEPARATOR}
     * if it is in {@code element} and not empty.
     * Otherwise returns an empty array.
     */
    protected List<String> getManyValueInElement(final String[] elements, int position) {
        if (!containsValueAtPosition(elements, position)) {
            return Collections.emptyList();
        }

        String manyValue = getValueInElement(elements, position);
        // Wrap with new ArrayList<> to make the created List is mutable.
        return new ArrayList<>(Arrays.asList(manyValue.split(AUTHOR_ALIAS_AND_GLOB_SEPARATOR)));
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
