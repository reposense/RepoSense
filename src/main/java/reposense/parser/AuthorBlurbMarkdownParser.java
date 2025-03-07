package reposense.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Pattern;

import reposense.model.AuthorBlurbMap;
import reposense.parser.exceptions.InvalidMarkdownException;

/**
 * Parses the Markdown file and retrieves the mappings from authors to their blurbs
 * from the author blurbs configuration file.
 */
public class AuthorBlurbMarkdownParser extends MarkdownParser<AuthorBlurbMap> implements BlurbMarkdownParser {
    public static final Pattern DELIMITER = Pattern.compile("<!--author-->(.*)");
    public static final String DEFAULT_BLURB_FILENAME = "author-blurbs.md";

    private static final class AuthorRecord {
        private final String author;
        private final int nextPosition;

        public AuthorRecord(String author, int nextPosition) {
            this.author = author;
            this.nextPosition = nextPosition;
        }

        public String getAuthor() {
            return author;
        }

        public int getNextPosition() {
            return nextPosition;
        }
    }

    private static final class BlurbRecord {
        private final List<String> blurb;
        private final int nextPosition;

        public BlurbRecord(List<String> blurb, int nextPosition) {
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

    public AuthorBlurbMarkdownParser(Path markdownPath) throws FileNotFoundException {
        super(markdownPath);
    }

    /**
     * Parses the markdown file containing the author to blurb mapping and returns an
     * {@code AuthorBlurbMap} containing the mappings.
     *
     * @return {@code AuthorBlurbMap} object.
     * @throws IOException if there are any issues opening or parsing the {@code author-blurbs.md} file.
     */
    @Override
    public AuthorBlurbMap parse() throws IOException, InvalidMarkdownException {
        logger.log(Level.INFO, "Parsing Author Blurbs...");
        List<String> mdLines = Files.readAllLines(this.markdownPath);

        if (mdLines.isEmpty()) {
            throw new InvalidMarkdownException("Empty author-blurbs.md file");
        }

        AuthorBlurbMap authorBlurbMap = new AuthorBlurbMap();

        String author = "";
        StringBuilder blurb = new StringBuilder();
        int counter = 0;

        while (counter < mdLines.size()) {
            // Extract author name
            AuthorRecord authorRecord = this.getAuthorRecord(mdLines, counter);
            if (authorRecord == null) {
                break;
            }
            author = authorRecord.getAuthor();
            counter = authorRecord.getNextPosition();

            // Extract blurb
            BlurbRecord blurbRecord = this.getBlurbRecord(mdLines, counter);
            List<String> blurbExtracted = blurbRecord.getBlurb();
            for (String string : blurbExtracted) {
                blurb.append(string);
            }
            counter = blurbRecord.getNextPosition();

            // Add to AuthorBlurbMap
            authorBlurbMap.withRecord(author, blurb.toString().stripTrailing());
            blurb.setLength(0);
        }

        logger.log(Level.INFO, "Author Blurbs parsed successfully!");
        return authorBlurbMap;
    }

    private AuthorRecord getAuthorRecord(List<String> lines, int position) throws InvalidMarkdownException {
        String author = "";
        while (author.length() == 0) {
            if (position >= lines.size()) {
                return null;
            }
            author = lines.get(position++).strip();
        }

        return new AuthorRecord(author, position);
    }

    private BlurbRecord getBlurbRecord(List<String> lines, int position) {
        int lineSize = lines.size();
        int posCounter = position;
        List<String> blurbs = new ArrayList<>();

        while (posCounter < lineSize) {
            String currLine = lines.get(posCounter);

            if (AuthorBlurbMarkdownParser.DELIMITER.matcher(currLine).matches()) {
                break;
            } else {
                currLine += "\n";
                blurbs.add(currLine);
            }

            posCounter++;
        }

        return new BlurbRecord(blurbs, posCounter + 1);
    }
}
