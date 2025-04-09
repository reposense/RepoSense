package reposense.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import reposense.model.BlurbMap;
import reposense.parser.exceptions.InvalidMarkdownException;

/**
 * Abstract class for parsing Markdown files for blurbs.
 *
 * @param <T> The type of blurb map (repos or authors).
 * @param <K> The type of blurb map key (URL for repo, gitId for author).
 */
public abstract class BlurbMarkdownParser<T extends BlurbMap, K> extends MarkdownParser<BlurbMap> {
    protected Pattern delimiter;
    protected String blurbFileName;

    public BlurbMarkdownParser(Path markdownPath, Pattern delimiter,
                               String blurbFileName) throws FileNotFoundException {
        super(markdownPath);
        this.delimiter = delimiter;
        this.blurbFileName = blurbFileName;
    }

    /**
     * Static class for the blurb key record.
     *
     * @param <K> The type of key (URL for repo, String(gitId) for author).
     */
    protected static class KeyRecord<K> {
        private final K key;

        /**
         * The next position to read from the markdown file.
         */
        private final int nextPosition;

        public KeyRecord(K key, int nextPosition) {
            this.key = key;
            this.nextPosition = nextPosition;
        }

        public K getKey() {
            return key;
        }


        public int getNextPosition() {
            return nextPosition;
        }
    }

    /**
     * Static class for the blurb record.
     */
    private static final class BlurbsRecord {
        private final List<String> blurb;
        private final int nextPosition;

        public BlurbsRecord(List<String> blurb, int nextPosition) {
            this.blurb = blurb;
            this.nextPosition = nextPosition;
        }

        public List<String> getBlurb() {
            return blurb;
        }

        public int getNextPosition() {
            return nextPosition;
        }
    }

    /**
     * Parses the markdown file containing key-to-blurb mappings and returns a
     * BlurbMap containing these mappings.
     *
     * @return A BlurbMap object.
     * @throws IOException if there are issues opening or parsing the markdown file.
     * @throws InvalidMarkdownException if the markdown file is not in the correct format.
     */
    @Override
    public T parse() throws IOException, InvalidMarkdownException {
        logger.log(Level.INFO, "Parsing blurbs...");
        // Read all the lines first
        List<String> mdLines = Files.readAllLines(this.markdownPath);

        // If the file is empty, throw exception
        if (mdLines.isEmpty()) {
            throw new InvalidMarkdownException("Empty blurb markdown file");
        }

        // Prepare the blurb map
        T blurbMap = createBlurbMap();

        StringBuilder blurb = new StringBuilder();
        int counter = 0;

        while (counter < mdLines.size()) {
            // Extract the key first (author git id or repository URL)
            KeyRecord<K> keyRecord = extractKey(mdLines, counter);

            // If no more keys found, we're done
            if (keyRecord == null) {
                break;
            }

            K key = keyRecord.getKey();
            counter = keyRecord.getNextPosition();

            // Extract the blurb content
            BlurbsRecord blurbRecord = getBlurbRecord(mdLines, counter);
            List<String> blurbExtracted = blurbRecord.getBlurb();
            for (String line : blurbExtracted) {
                blurb.append(line);
            }

            counter = blurbRecord.getNextPosition();

            // Add the recorded entry into the blurb map
            addRecord(blurbMap, key, blurb.toString().stripTrailing());
            blurb.setLength(0);
        }

        logger.log(Level.INFO, "Blurbs parsed successfully!");
        return blurbMap;
    }

    /**
     * Creates a new instance of the blurb map.
     *
     * @return A new instance of the blurb map.
     */
    protected abstract T createBlurbMap();

    /**
     * Extract the blurb record key from the markdown file.
     *
     * @param mdLines The list of lines in the markdown file.
     * @param position The current position in the markdown file.
     *
     * @return The {@code KeyRecord} containing the key and the next position to read from the markdown file.
     *
     * @throws InvalidMarkdownException if the key is not valid.
     */
    protected abstract KeyRecord<K> extractKey(List<String> mdLines, int position) throws InvalidMarkdownException;

    /**
     * Adds a record to the blurb map.
     *
     * @param blurbMap The blurb map to add the record to.
     * @param key The key for the blurb record.
     * @param blurb The blurb content.
     */
    protected abstract void addRecord(T blurbMap, K key, String blurb);

    /**
     * Extract the blurb content from the markdown file.
     *
     * @param lines The list of lines in the markdown file.
     * @param position The current position in the markdown file.
     *
     * @return The {@code BlurbRecord} containing the blurb content and the next position to read from the
     *         markdown file.
     */
    protected BlurbsRecord getBlurbRecord(List<String> lines, int position) {
        // Extract the blurb content
        int lineSize = lines.size();
        int posCounter = position;
        List<String> blurb = new ArrayList<>();

        while (posCounter < lineSize) {
            String currLine = lines.get(posCounter);

            if (delimiter.matcher(currLine).matches()) {
                break;
            }

            currLine += "\n";
            blurb.add(currLine);

            posCounter++;
        }

        return new BlurbsRecord(blurb, posCounter);
    }
}
