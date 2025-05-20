package reposense.parser;

import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.parser.exceptions.InvalidMarkdownException;

public class AuthorBlurbMarkdownParserTest {
    private static final Path EMPTY_BLURB_TESTER = loadResource(AuthorBlurbMarkdownParserTest.class,
            "BlurbMarkdownParserTest/empty_blurbs.md");
    private static final Path MALFORMED_DELIMITER_TESTER = loadResource(AuthorBlurbMarkdownParserTest.class,
            "BlurbMarkdownParserTest/AuthorBlurbMarkdownParserTest/malformed_delimiter_blurb.md");
    private static final Path MULTILINE_BLURB_TESTER = loadResource(AuthorBlurbMarkdownParser.class,
            "BlurbMarkdownParserTest/AuthorBlurbMarkdownParserTest/multiline_blurb.md");
    private static final Path MULTIPLE_BLURB_TESTER = loadResource(AuthorBlurbMarkdownParser.class,
            "BlurbMarkdownParserTest/AuthorBlurbMarkdownParserTest/multiple_blurbs.md");

    @Test
    public void parse_emptyBlurbTest_throwsException() {
        Assertions.assertThrows(
                InvalidMarkdownException.class, () -> new AuthorBlurbMarkdownParser(EMPTY_BLURB_TESTER).parse()
        );
    }

    @Test
    public void parse_malformedDelimiterBlurbTest_success() throws Exception {
        AuthorBlurbMarkdownParser bmp = new AuthorBlurbMarkdownParser(MALFORMED_DELIMITER_TESTER);
        Map<String, String> bm = bmp.parse().getAllMappings();
        Assertions.assertTrue(bm.containsKey("testAuthor1"));
        Assertions.assertEquals(
                bm.get("testAuthor1"),
                "This is a test to check for malformed deliminaters in the blurb.\n"
                        + "<!--author--bunchofchar that don't count>\n"
                        + "testAuthor2\n"
                        + "Another test to check\n"
                        + "<!--author - this is not legal and will be captured as part of the text -->"
        );
    }

    @Test
    public void parse_multilineBlurbTest_success() throws Exception {
        AuthorBlurbMarkdownParser bmp = new AuthorBlurbMarkdownParser(MULTILINE_BLURB_TESTER);
        Map<String, String> bm = bmp.parse().getAllMappings();
        Assertions.assertTrue(bm.containsKey("testAuthor1"));
        Assertions.assertEquals(
                bm.get("testAuthor1"),
                "dummy test for multiline\n"
                        + "A\n"
                        + "long\n"
                        + "line\n"
                        + "of\n"
                        + "description\n"
                        + "of\n"
                        + "testrepo\n"
                        + "Alpha"
        );
    }

    @Test
    public void parse_multipleBlurbTest_success() throws Exception {
        AuthorBlurbMarkdownParser bmp = new AuthorBlurbMarkdownParser(MULTIPLE_BLURB_TESTER);
        Map<String, String> bm = bmp.parse().getAllMappings();
        Assertions.assertTrue(bm.keySet().containsAll(
                List.of("testAuthor1", "testAuthor2", "testAuthor3")));
        Assertions.assertEquals(
                bm.get("testAuthor1"),
                "dummy text for the first blurb"
        );
        Assertions.assertEquals(
                bm.get("testAuthor2"),
                "dummy text for the second blurb"
        );
        Assertions.assertEquals(
                bm.get("testAuthor3"),
                "dummy text for the third blurb"
        );
    }

}
