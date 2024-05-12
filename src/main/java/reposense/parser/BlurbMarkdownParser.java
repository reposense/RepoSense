package reposense.parser;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import reposense.model.BlurbMap;
import reposense.parser.exceptions.InvalidMarkdownException;

/**
 * Parses the Markdown file and retrieves the mappings from URLs to blurbs from the blurbs
 * configuration file.
 */
public class BlurbMarkdownParser extends MarkdownParser<BlurbMap> {
    public static final String DEFAULT_BLURB_FILENAME = "blurbs.md";

    private static final class UrlRecord {
        private final String url;
        private final int nextPosition;

        public UrlRecord(String url, int nextPosition) {
            this.url = url;
            this.nextPosition = nextPosition;
        }

        public String getUrl() {
            return url;
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

    public BlurbMarkdownParser(Path markdownPath) throws FileNotFoundException {
        super(markdownPath);
    }

    /**
     * Parses the markdown file containing the url to blurb mapping and returns a
     * {@code BlurbMap} containing the mappings between the url and blurbs.
     *
     * @return {@code BlurbMap} object.
     * @throws IOException if there are any issues opening or parsing the {@code blurbs.md} file.
     */
    @Override
    public BlurbMap parse() throws IOException, InvalidMarkdownException {
        logger.log(Level.INFO, "Parsing Blurbs...");
        // read all the lines first
        List<String> mdLines = Files.readAllLines(this.markdownPath);

        // if the file is empty, then we throw the exception and let the adder handle
        if (mdLines.isEmpty()) {
            throw new InvalidMarkdownException("Empty blurbs.md file");
        }

        // prepare the blurb builder
        BlurbMap.Builder builder = new BlurbMap.Builder();

        // define temporary local variables to track blurbs
        String url = "";
        StringBuilder blurb = new StringBuilder();
        int counter = 0;

        while (counter < mdLines.size()) {
            // extract the url record first
            // this is guaranteed to be in the first line or else we fail
            UrlRecord urlRecord = this.getUrlRecord(mdLines, counter);
            url = urlRecord.getUrl();
            counter = urlRecord.getNextPosition();

            // then extract the blurb record next
            // we extract until the delimiter is found and then we will stop
            BlurbRecord blurbRecord = this.getBlurbRecord(mdLines, counter);
            List<String> blurbExtracted = blurbRecord.getBlurb();
            for (String string : blurbExtracted) {
                blurb.append(string);
            }
            counter = blurbRecord.getNextPosition();

            // add the recorded entry into the BlurbMap
            // strip the trailing /n
            builder = builder.withRecord(url, blurb.toString().stripTrailing());
            blurb.setLength(0);
        }

        // return the built BlurbMap instance
        logger.log(Level.INFO, "Blurbs parsed successfully!");
        return builder.build();
    }

    private UrlRecord getUrlRecord(List<String> lines, int position) throws InvalidMarkdownException {
        // checks if url is valid
        // adapted from https://www.baeldung.com/java-validate-url
        try {
            String url = lines.get(position).strip();
            new URL(url).toURI();
            return new UrlRecord(lines.get(position), position + 1);
        } catch (MalformedURLException | URISyntaxException ex) {
            throw new InvalidMarkdownException("URL provided is not valid!");
        }
    }

    private BlurbRecord getBlurbRecord(List<String> lines, int position) {
        int lineSize = lines.size();
        int posCounter = position;
        List<String> blurbs = new ArrayList<>();

        while (posCounter < lineSize) {
            String currLine = lines.get(posCounter);

            if (MarkdownParser.DELIMITER.matcher(currLine).matches()) {
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
