package reposense.parser;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;

import reposense.model.AuthorBlurbMap;
import reposense.parser.exceptions.InvalidMarkdownException;

/**
 * Parses the Markdown file and retrieve mappings from author to blurbs from the blurbs.
 */
public class AuthorBlurbMarkdownParser extends BlurbMarkdownParser<AuthorBlurbMap, String> {
    public static final Pattern DELIMITER = Pattern.compile("<!--author-->(.*)");
    public static final String DEFAULT_BLURB_FILENAME = "author-blurbs.md";

    public AuthorBlurbMarkdownParser(Path markdownPath) throws FileNotFoundException {
        super(markdownPath, DELIMITER, DEFAULT_BLURB_FILENAME);
    }

    @Override
    protected AuthorBlurbMap createBlurbMap() {
        return new AuthorBlurbMap();
    }

    @Override
    protected KeyRecord<String> extractKey(List<String> lines, int position) throws InvalidMarkdownException {
        // Extract the author git id
        String authorGitId = "";

        while (position < lines.size()) {
            String line = lines.get(position++).strip();

            // Skip delimiter lines
            if (DELIMITER.matcher(line).matches()) {
                continue;
            }

            // Found a non-empty author ID
            if (!line.isEmpty()) {
                authorGitId = line;
                break;
            }
        }

        return new KeyRecord<>(authorGitId, position);
    }

    @Override
    protected void addRecord(AuthorBlurbMap blurbMap, String key, String blurb) {
        blurbMap.withRecord(key, blurb);
    }

}
