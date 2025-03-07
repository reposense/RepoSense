package reposense.parser;

import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.parser.exceptions.InvalidMarkdownException;

public class BlurbMarkdownParserTest {
    private static final Path EMPTY_BLURB_TESTER = loadResource(BlurbMarkdownParserTest.class,
            "BlurbMarkdownParserTest/empty_blurbs.md");
    private static final Path MALFORMED_URL_TESTER = loadResource(BlurbMarkdownParserTest.class,
            "BlurbMarkdownParserTest/malformed_url_blurb.md");
    private static final Path MALFORMED_DELIMITER_TESTER = loadResource(BlurbMarkdownParserTest.class,
            "BlurbMarkdownParserTest/malformed_delimiter_blurb.md");
    private static final Path MULTILINE_BLURB_TESTER = loadResource(BlurbMarkdownParserTest.class,
            "BlurbMarkdownParserTest/multiline_blurb.md");
    private static final Path MULTIPLE_BLURB_TESTER = loadResource(RepoBlurbMarkdownParser.class,
            "BlurbMarkdownParserTest/multiple_blurbs.md");

    @Test
    public void parse_emptyBlurbTest_throwsException() {
        Assertions.assertThrows(
                InvalidMarkdownException.class, () -> new RepoBlurbMarkdownParser(EMPTY_BLURB_TESTER).parse()
        );
    }

    @Test
    public void parse_malformedUrlBlurbTest_throwsException() {
        Assertions.assertThrows(
                InvalidMarkdownException.class, () -> new RepoBlurbMarkdownParser(MALFORMED_URL_TESTER).parse()
        );
    }

    @Test
    public void parse_malformedDelimiterBlurbTest_success() throws Exception {
        RepoBlurbMarkdownParser bmp = new RepoBlurbMarkdownParser(MALFORMED_DELIMITER_TESTER);
        Map<String, String> bm = bmp.parse().getAllMappings();
        Assertions.assertTrue(bm.containsKey("https://github.com/reposense/testrepo-Alpha/tree/master"));
        Assertions.assertEquals(
                bm.get("https://github.com/reposense/testrepo-Alpha/tree/master"),
                "Master branch of testrepo-Alpha\n"
                        + "<!--repo--bunchofcharacters that don't count>\n"
                        + "https://github.com/reposense/testrepo-Alpha/tree/master\n"
                        + "Master branch of testrepo-Alpha\n"
                        + "<!--repo - this is not legal and will be captured as part of the text -->"
        );
    }

    @Test
    public void parse_multilineBlurbTest_success() throws Exception {
        RepoBlurbMarkdownParser bmp = new RepoBlurbMarkdownParser(MULTILINE_BLURB_TESTER);
        Map<String, String> bm = bmp.parse().getAllMappings();
        Assertions.assertTrue(bm.containsKey("https://github.com/reposense/testrepo-Alpha/tree/master"));
        Assertions.assertEquals(
                bm.get("https://github.com/reposense/testrepo-Alpha/tree/master"),
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
        RepoBlurbMarkdownParser bmp = new RepoBlurbMarkdownParser(MULTIPLE_BLURB_TESTER);
        Map<String, String> bm = bmp.parse().getAllMappings();
        Assertions.assertTrue(bm.keySet().containsAll(
                List.of("https://github.com/reposense/testrepo-Alpha/tree/master",
                        "https://github.com/reposense/testrepo-Beta/tree/master",
                        "https://github.com/reposense/testrepo-Gamma/tree/master",
                        "https://github.com/reposense/testrepo-Sigma/tree/master")
        ));
        Assertions.assertEquals(
                bm.get("https://github.com/reposense/testrepo-Alpha/tree/master"),
                "Master branch of testrepo-Alpha"
        );
        Assertions.assertEquals(
                bm.get("https://github.com/reposense/testrepo-Beta/tree/master"),
                "Master branch of testrepo-Beta"
        );
        Assertions.assertEquals(
                bm.get("https://github.com/reposense/testrepo-Gamma/tree/master"),
                "Master branch of testrepo-Gamma"
        );
        Assertions.assertEquals(
                bm.get("https://github.com/reposense/testrepo-Sigma/tree/master"),
                "Master branch of testrepo-Sigma"
        );
    }
}
