package reposense.parser;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import reposense.model.ChartBlurbMap;
import reposense.parser.exceptions.InvalidMarkdownException;

/**
 * Parses the Markdown file and retrieves the mappings from (repoURL|authorGitId) to blurbs from the blurbs
 * configuration file.
 */
public class ChartBlurbMarkdownParser extends BlurbMarkdownParser<ChartBlurbMap, String> {
    public static final Pattern DELIMITER = Pattern.compile("<!--chart-->(.*)");
    public static final String DEFAULT_BLURB_FILENAME = "chart-blurbs.md";

    public ChartBlurbMarkdownParser(Path markdownPath) throws FileNotFoundException {
        super(markdownPath, DELIMITER, DEFAULT_BLURB_FILENAME);
    }

    @Override
    protected ChartBlurbMap createBlurbMap() {
        return new ChartBlurbMap();
    }

    @Override
    protected KeyRecord<String> extractKey(List<String> lines, int position) throws InvalidMarkdownException {
        // Extract the key (repoURL|authorGitId)
        String key = "";

        while (key.length() == 0) {

            if (position >= lines.size()) {
                return null;
            }

            // Skip delimiter lines
            if (DELIMITER.matcher(lines.get(position)).matches()) {
                position++;
                continue;
            }

            key = lines.get(position++).strip();
        }

        // Validate the key
        if (!isValidKey(key)) {
            throw new InvalidMarkdownException("Invalid key format: " + key);
        }

        return new KeyRecord<>(key, position);
    }

    /**
     * Validates the key (repoURL|authorGitId) format.
     * The repoURL should be a valid URL and the authorGitId should be present.
     *
     * @param key The key to validate.
     * @throws InvalidMarkdownException if the key format is invalid.
     * @return true if the key is valid, false otherwise.
     */
    private boolean isValidKey(String key) throws InvalidMarkdownException {
        String[] parts = key.split("\\|");

        // Check if the key has exactly 2 parts (repoURL and authorGitId)
        if (parts.length != 2) {
            return false;
        }

        String repoURL = parts[0].strip();
        String authorGitId = parts[1].strip();

        // Check if the repoURL is a valid URL
        if (repoURL.isEmpty() || authorGitId.isEmpty()) {
            return false;
        }

        // Validate the repoURL format
        // Adapted from https://www.baeldung.com/java-validate-url
        try {
            new URL(repoURL).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
    }

    @Override
    protected void addRecord(ChartBlurbMap blurbMap, String key, String blurb) {
        blurbMap.withRecord(key, blurb);
    }
}
