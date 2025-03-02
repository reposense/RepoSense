package reposense.parser;

import java.io.IOException;
import reposense.model.BlurbMap;
import reposense.parser.exceptions.InvalidMarkdownException;

/**
 * Interface for parsing blurbs from Markdown configuration files.
 */
public interface BlurbMarkdownParser {
    BlurbMap parse() throws IOException, InvalidMarkdownException;
}