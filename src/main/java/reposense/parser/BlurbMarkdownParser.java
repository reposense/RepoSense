package reposense.parser;

import java.io.IOException;

import reposense.model.BlurbMap;
import reposense.parser.exceptions.InvalidMarkdownException;

/**
 * Interface for parsing blurbs from Markdown configuration files.
 */
public interface BlurbMarkdownParser {
    /**
     * Parses the markdown file containing the key to blurb mapping and returns a
     * {@code BlurbMap} containing the mappings between the key and blurbs.
     * Key can be repository URLs or author git IDs.
     *
     * @return {@code BlurbMap} object.
     * @throws IOException if there are any issues opening or parsing the {@code blurbs.md} file.
     * @throws InvalidMarkdownException if the markdown file is not in the correct format.
     */
    BlurbMap parse() throws IOException, InvalidMarkdownException;
}
