package reposense.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import reposense.parser.exceptions.InvalidMarkdownException;
import reposense.system.LogsManager;

/**
 * Parses Markdown file according to the "<!--repo-->" tag.
 *
 * @param <T> Generic Type {@code T}.
 */
public abstract class MarkdownParser<T> {
    protected static final Pattern DELIMITER = Pattern.compile("<!--repo-->(.*)");
    protected static final Logger logger = LogsManager.getLogger(MarkdownParser.class);

    protected Path markdownPath;

    public MarkdownParser(Path markdownPath) throws FileNotFoundException {
        if (markdownPath == null || !Files.exists(markdownPath)) {
            throw new FileNotFoundException("Markdown file does not exist at the given path.\n"
                    + "Use '-help' to list all the available subcommands and some concept guides.");
        }

        this.markdownPath = markdownPath;
    }

    public abstract T parse() throws IOException, InvalidMarkdownException;
}
