package reposense.parser;

import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.parser.exceptions.InvalidMarkdownException;

public class ChartBlurbMarkdownParserTest {
    private static final Path EMPTY_BLURB_TESTER = loadResource(ChartBlurbMarkdownParserTest.class,
            "BlurbMarkdownParserTest/empty_blurbs.md");
    private static final Path MALFORMED_DELIMITER_TESTER = loadResource(ChartBlurbMarkdownParserTest.class,
            "BlurbMarkdownParserTest/ChartBlurbMarkdownParserTest/malformed_delimiter_blurb.md");
    private static final Path MULTILINE_BLURB_TESTER = loadResource(ChartBlurbMarkdownParserTest.class,
            "BlurbMarkdownParserTest/ChartBlurbMarkdownParserTest/multiline_blurb.md");
    private static final Path MULTIPLE_BLURB_TESTER = loadResource(ChartBlurbMarkdownParserTest.class,
            "BlurbMarkdownParserTest/ChartBlurbMarkdownParserTest/multiple_blurbs.md");
    private static final Path INCOMPLETE_KEY_BLURB_URL_TESTER = loadResource(ChartBlurbMarkdownParserTest.class,
            "BlurbMarkdownParserTest/ChartBlurbMarkdownParserTest/incomplete_key_blurb_URL.md");
    private static final Path INCOMPLETE_KEY_BLURB_AUTHOR_TESTER = loadResource(ChartBlurbMarkdownParserTest.class,
            "BlurbMarkdownParserTest/ChartBlurbMarkdownParserTest/incomplete_key_blurb_author.md");

    @Test
    public void parse_emptyBlurbTest_throwsException() {
        Assertions.assertThrows(
                InvalidMarkdownException.class, () -> new ChartBlurbMarkdownParser(EMPTY_BLURB_TESTER).parse()
        );
    }

    @Test
    public void parse_incompleteKeyBlurbUrl_throwsException() {
        Assertions.assertThrows(
                InvalidMarkdownException.class, () -> new RepoBlurbMarkdownParser(INCOMPLETE_KEY_BLURB_URL_TESTER)
                        .parse()
        );
    }

    @Test
    public void parse_incompleteKeyBlurbAuthor_throwsException() {
        Assertions.assertThrows(
                InvalidMarkdownException.class, () -> new RepoBlurbMarkdownParser(INCOMPLETE_KEY_BLURB_AUTHOR_TESTER)
                        .parse()
        );
    }

    @Test
    public void parse_malformedDelimiterBlurbTest_success() throws Exception {
        ChartBlurbMarkdownParser bmp = new ChartBlurbMarkdownParser(MALFORMED_DELIMITER_TESTER);
        Map<String, String> bm = bmp.parse().getAllMappings();
        Assertions.assertTrue(bm.containsKey("https://github.com/reposense/testrepo-Alpha/tree/master|author1"));
        Assertions.assertEquals(
                bm.get("https://github.com/reposense/testrepo-Alpha/tree/master|author1"),
                "This is a test to check for malformed delimiters in the blurb.\n"
                        + "<!--chart--bunchofcharacters that don't count>\n"
                        + "https://github.com/reposense/testrepo-Alpha/tree/master|author2\n"
                        + "Another test to check\n"
                        + "<!--chart - this is not legal and will be captured as part of the text -->"
        );
    }

    @Test
    public void parse_multilineBlurbTest_success() throws Exception {
        ChartBlurbMarkdownParser bmp = new ChartBlurbMarkdownParser(MULTILINE_BLURB_TESTER);
        Map<String, String> bm = bmp.parse().getAllMappings();
        Assertions.assertTrue(bm.containsKey("https://github.com/reposense/testrepo-Alpha/tree/master|author1"));
        Assertions.assertEquals(
                bm.get("https://github.com/reposense/testrepo-Alpha/tree/master|author1"),
                "Master branch of testrepo-Alpha\n"
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
        ChartBlurbMarkdownParser bmp = new ChartBlurbMarkdownParser(MULTIPLE_BLURB_TESTER);
        Map<String, String> bm = bmp.parse().getAllMappings();
        Assertions.assertTrue(bm.keySet().containsAll(
                List.of("https://github.com/reposense/testrepo-Alpha/tree/master|author1",
                        "https://github.com/reposense/testrepo-Beta/tree/master|author2",
                        "https://github.com/reposense/testrepo-Gamma/tree/master|author3",
                        "https://github.com/reposense/testrepo-Sigma/tree/master|author4")
        ));
        Assertions.assertEquals(
                bm.get("https://github.com/reposense/testrepo-Alpha/tree/master|author1"),
                "Master branch of testrepo-Alpha"
        );
        Assertions.assertEquals(
                bm.get("https://github.com/reposense/testrepo-Beta/tree/master|author2"),
                "Master branch of testrepo-Beta"
        );
        Assertions.assertEquals(
                bm.get("https://github.com/reposense/testrepo-Gamma/tree/master|author3"),
                "Master branch of testrepo-Gamma"
        );
        Assertions.assertEquals(
                bm.get("https://github.com/reposense/testrepo-Sigma/tree/master|author4"),
                "Master branch of testrepo-Sigma"
        );
    }
}
