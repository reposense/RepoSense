package reposense.parser;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import reposense.model.RepoBlurbMap;
import reposense.parser.exceptions.InvalidMarkdownException;

/**
 * Parses the Markdown file and retrieves the mappings from URLs to blurbs from the blurbs
 * configuration file.
 */
public class RepoBlurbMarkdownParser extends BlurbMarkdownParser<RepoBlurbMap, String> {
    public static final Pattern DELIMITER = Pattern.compile("<!--repo-->(.*)");
    public static final String DEFAULT_BLURB_FILENAME = "repo-blurbs.md";

    public RepoBlurbMarkdownParser(Path markdownPath) throws FileNotFoundException {
        super(markdownPath, DELIMITER, DEFAULT_BLURB_FILENAME);
    }

    @Override
    protected RepoBlurbMap createBlurbMap() {
        return new RepoBlurbMap();
    }

    @Override
    protected KeyRecord<String> extractKey(List<String> lines, int position) throws InvalidMarkdownException {
        // checks if url is valid
        // adapted from https://www.baeldung.com/java-validate-url
        try {
            String url = "";
            // skips blank lines
            while (url.length() == 0) {
                // checks if delimiter is the last non-blank line
                if (position >= lines.size()) {
                    return null;
                }

                if (DELIMITER.matcher(lines.get(position)).matches()) {
                    position++;
                    continue;
                }

                url = lines.get(position++).strip();
            }
            new URL(url).toURI();
            return new KeyRecord<>(url, position);
        } catch (MalformedURLException | URISyntaxException ex) {
            throw new InvalidMarkdownException("URL provided is not valid!");
        }
    }

    @Override
    protected void addRecord(RepoBlurbMap blurbMap, String key, String blurb) {
        blurbMap.withRecord(key, blurb);
    }
}
