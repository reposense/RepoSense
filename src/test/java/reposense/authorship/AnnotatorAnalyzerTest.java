package reposense.authorship;

import static reposense.model.Author.UNKNOWN_AUTHOR;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import reposense.authorship.analyzer.AnnotatorAnalyzer;
import reposense.authorship.model.FileResult;
import reposense.model.Author;
import reposense.model.AuthorConfiguration;
import reposense.model.RepoConfiguration;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class AnnotatorAnalyzerTest extends GitTestTemplate {
    private static final LocalDateTime SINCE_DATE = TestUtil.getSinceDate(2018, Month.FEBRUARY.getValue(), 8);
    private static final LocalDateTime UNTIL_DATE = TestUtil.getUntilDate(2021, Month.AUGUST.getValue(), 3);
    private static final Author[] EXPECTED_LINE_AUTHORS_OVERRIDE_AUTHORSHIP_TEST = {
            FAKE_AUTHOR, FAKE_AUTHOR, FAKE_AUTHOR, FAKE_AUTHOR,
            MAIN_AUTHOR, MAIN_AUTHOR, MAIN_AUTHOR, MAIN_AUTHOR, MAIN_AUTHOR,
            FAKE_AUTHOR, FAKE_AUTHOR,
            UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR,
            FAKE_AUTHOR, FAKE_AUTHOR, FAKE_AUTHOR,
            UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR
    };
    private static final Author[] EXPECTED_LINE_AUTHORS_DISOWN_CODE_TEST = {
            FAKE_AUTHOR, FAKE_AUTHOR, FAKE_AUTHOR, FAKE_AUTHOR,
            UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR,
            FAKE_AUTHOR, FAKE_AUTHOR,
            UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR,
            FAKE_AUTHOR, FAKE_AUTHOR, FAKE_AUTHOR,
            UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR
    };

    private RepoConfiguration config;

    @BeforeEach
    public void before() throws Exception {
        super.before();

        config = configs.get();
        config.setSinceDate(SINCE_DATE);
        config.setUntilDate(UNTIL_DATE);
        config.setZoneId(TIME_ZONE_ID);

        AuthorConfiguration.setHasAuthorConfigFile(AuthorConfiguration.DEFAULT_HAS_AUTHOR_CONFIG_FILE);
    }

    @AfterEach
    public void after() {
        super.after();
        AuthorConfiguration.setHasAuthorConfigFile(AuthorConfiguration.DEFAULT_HAS_AUTHOR_CONFIG_FILE);
    }

    @Test
    public void analyzeAnnotation_authorNamePresentInConfig_overrideAuthorship() {
        config.setAuthorList(new ArrayList<>(Arrays.asList(FAKE_AUTHOR)));
        FileResult fileResult = getFileResult("annotationTest.java");
        assertFileAnalysisCorrectness(fileResult, Arrays.asList(EXPECTED_LINE_AUTHORS_OVERRIDE_AUTHORSHIP_TEST));
    }

    @Test
    public void analyzeAnnotation_authorNameNotInConfigAndNoAuthorConfigFile_acceptTaggedAuthor() {
        config.setAuthorList(new ArrayList<>(Arrays.asList(FAKE_AUTHOR)));
        FileResult fileResult = getFileResult("annotationTest.java");
        assertFileAnalysisCorrectness(fileResult, Arrays.asList(EXPECTED_LINE_AUTHORS_OVERRIDE_AUTHORSHIP_TEST));
    }

    @Test
    public void analyzeAnnotation_authorNameNotInConfigAndHaveAuthorConfigFile_disownCode() {
        config.setAuthorList(new ArrayList<>(Arrays.asList(FAKE_AUTHOR)));
        AuthorConfiguration.setHasAuthorConfigFile(true);
        FileResult fileResult = getFileResult("annotationTest.java");
        assertFileAnalysisCorrectness(fileResult, Arrays.asList(EXPECTED_LINE_AUTHORS_DISOWN_CODE_TEST));
    }

    @Test
    public void extractAuthorName_matchGenericCommentPattern0_returnAuthorName() {
        int index = 0;
        String line;
        line = "//@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line, false).get());

        line = "// @@author fakeauthor  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line, false).get());

        line = "  //  @@author   fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, false).get());
    }

    @Test
    public void extractAuthorName_matchGenericCommentPattern1_returnAuthorName() {
        int index = 1;
        String line;
        line = "/*@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line, false).get());

        line = "/* @@author fakeauthor  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line, false).get());

        line = "  /*  @@author   fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, false).get());

        line = "/*@@author fakeAuthor*/";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line, false).get());

        line = "/* @@author fakeauthor */";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line, false).get());

        line = "  /*  @@author   fake-4u-th0r  */  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, false).get());
    }

    @Test
    public void extractAuthorName_matchGenericCommentPattern2_returnAuthorName() {
        int index = 2;
        String line;
        line = "#@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line, false).get());

        line = "# @@author fakeauthor  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line, false).get());

        line = "  #  @@author   fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, false).get());
    }

    @Test
    public void extractAuthorName_matchGenericCommentPattern3_returnAuthorName() {
        int index = 3;
        String line;
        line = "<!--@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line, false).get());

        line = "<!-- @@author fakeauthor  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line, false).get());

        line = "  <!--  @@author   fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, false).get());

        line = "<!--@@author fakeAuthor-->";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line, false).get());

        line = "<!-- @@author fakeauthor -->";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line, false).get());

        line = "  <!--  @@author   fake-4u-th0r  -->  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, false).get());
    }

    @Test
    public void extractAuthorName_matchGenericCommentPattern4_returnAuthorName() {
        int index = 4;
        String line;
        line = "%@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line, false).get());

        line = "% @@author fakeauthor  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line, false).get());

        line = "  %  @@author   fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, false).get());
    }

    @Test
    public void extractAuthorName_matchMarkdownCommentPattern0_returnAuthorName() {
        int index = 0;
        String line;

        line = "[//]:#(@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line,
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line, true).get());

        line = "[//]: # (@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line,
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line, true).get());

        line = "[//]: # (@@author fakeauthor   ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line,
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line, true).get());

        line = "  [//]:   #   (@@author    fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line,
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, true).get());

        line = "[//]: # (@@author fakeAuthor)";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line,
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line, true).get());

        line = "[//]: # (@@author fakeauthor   )";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line,
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line, true).get());

        line = "   [//]:   #   (@@author fake-4u-th0r   )  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line,
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, true).get());

        line = "   [fake inner text 123+%^&!@#$(`]:   #   (@@author fake-4u-th0r   )  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line,
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, true).get());

        line = "   [    space   separated   inner   text   ]:   #   (  @@author fake-4u-th0r   )  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line,
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, true).get());
    }

    @Test
    public void extractAuthorName_noAuthorNameGeneric_returnNull() {
        String line;

        line = "//@@author";
        Assertions.assertEquals(0, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line, false).isPresent());

        line = "/*@@author ";
        Assertions.assertEquals(1, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line, false).isPresent());

        line = "/* @@author*/ ";
        Assertions.assertEquals(1, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line, false).isPresent());

        line = "#@@author ";
        Assertions.assertEquals(2, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line, false).isPresent());

        line = "  <!--@@author --> ";
        Assertions.assertEquals(3, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line, false).isPresent());

        line = "% @@author ";
        Assertions.assertEquals(4, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line, false).isPresent());
    }

    @Test
    public void extractAuthorName_noAuthorNameMarkdown_returnNull() {
        String line;

        line = "[//]: # (@@author)";
        Assertions.assertEquals(0, AnnotatorAnalyzer.getCommentTypeIndex(line,
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line, true).isPresent());

        line = "[//]: # (@@author     )  ";
        Assertions.assertEquals(0, AnnotatorAnalyzer.getCommentTypeIndex(line,
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line, true).isPresent());
    }

    @Test
    public void extractAuthorName_invalidAuthorNameGeneric_returnNull() {
        String line;

        line = "% @@author thisAuthorNameHasMoreThanThirtyNineLetters";
        Assertions.assertEquals(4, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line, false).isPresent());

        line = "# @@author -invalidUsernameFormat";
        Assertions.assertEquals(2, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line, false).isPresent());

        line = "/*@@author fakeAuthor-->";
        Assertions.assertEquals(1, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line, false).isPresent());
    }

    @Test
    public void getCommentType_matchGenericCommentPattern0_success() {
        Assertions.assertEquals(0, AnnotatorAnalyzer.getCommentTypeIndex("//@@author fakeAuthor"));
        Assertions.assertEquals(0, AnnotatorAnalyzer.getCommentTypeIndex("// @@author fakeAuthor"));
        Assertions.assertEquals(0, AnnotatorAnalyzer.getCommentTypeIndex("   // @@author fakeAuthor"));
        Assertions.assertEquals(0, AnnotatorAnalyzer.getCommentTypeIndex("   //    @@author  fakeAuthor     "));
    }

    @Test
    public void getCommentType_matchGenericCommentPattern1_success() {
        Assertions.assertEquals(1, AnnotatorAnalyzer.getCommentTypeIndex("/*@@author fakeAuthor"));
        Assertions.assertEquals(1, AnnotatorAnalyzer.getCommentTypeIndex("/* @@author fakeAuthor"));
        Assertions.assertEquals(1, AnnotatorAnalyzer.getCommentTypeIndex("   /* @@author fakeAuthor*/"));
        Assertions.assertEquals(1, AnnotatorAnalyzer.getCommentTypeIndex("   /*  @@author  fakeAuthor  */  "));
    }

    @Test
    public void getCommentType_matchGenericCommentPattern2_success() {
        Assertions.assertEquals(2, AnnotatorAnalyzer.getCommentTypeIndex("#@@author fakeAuthor"));
        Assertions.assertEquals(2, AnnotatorAnalyzer.getCommentTypeIndex("# @@author fakeAuthor"));
        Assertions.assertEquals(2, AnnotatorAnalyzer.getCommentTypeIndex("   # @@author fakeAuthor"));
        Assertions.assertEquals(2, AnnotatorAnalyzer.getCommentTypeIndex("   #    @@author  fakeAuthor     "));
    }

    @Test
    public void getCommentType_matchGenericCommentPattern3_success() {
        Assertions.assertEquals(3, AnnotatorAnalyzer.getCommentTypeIndex("<!--@@author fakeAuthor"));
        Assertions.assertEquals(3, AnnotatorAnalyzer.getCommentTypeIndex("<!-- @@author fakeAuthor"));
        Assertions.assertEquals(3, AnnotatorAnalyzer.getCommentTypeIndex("   <!-- @@author fakeAuthor-->"));
        Assertions.assertEquals(3, AnnotatorAnalyzer.getCommentTypeIndex("   <!--  @@author  fakeAuthor  -->  "));
    }

    @Test
    public void getCommentType_matchGenericCommentPattern4_success() {
        Assertions.assertEquals(4, AnnotatorAnalyzer.getCommentTypeIndex("%@@author fakeAuthor"));
        Assertions.assertEquals(4, AnnotatorAnalyzer.getCommentTypeIndex("% @@author fakeAuthor"));
        Assertions.assertEquals(4, AnnotatorAnalyzer.getCommentTypeIndex("   % @@author fakeAuthor"));
        Assertions.assertEquals(4, AnnotatorAnalyzer.getCommentTypeIndex("   %    @@author  fakeAuthor     "));
    }

    @Test
    public void getCommentType_invalidGenericCommentPattern_returnMinus1() {
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("// @@author fakeAuthor //"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("@@author fakeAuthor"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("/@@author fakeAuthor"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("@@author fakeAuthor */"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("# something @@author fakeAuthor"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("something % @@author fakeAuthor"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("# @@author fakeAuthor something"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("<!--@@authorfakeAuthor-->"));
    }

    @Test
    public void getCommentType_matchMarkdownCommentPattern0_success() {
        Assertions.assertEquals(0, AnnotatorAnalyzer.getCommentTypeIndex("[//]:#(@@author fakeAuthor)",
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals(0, AnnotatorAnalyzer.getCommentTypeIndex("[//]: # (@@author fakeAuthor)",
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals(0, AnnotatorAnalyzer.getCommentTypeIndex("   [//]:  #  (@@author fakeAuthor)  ",
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals(0, AnnotatorAnalyzer.getCommentTypeIndex("   [//]:  #  (  @@author   fakeAuthor  )  ",
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
    }

    @Test
    public void getCommentType_invalidMarkdownCommentPattern_returnMinus1() {
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("// @@author fakeAuthor //",
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("@@author fakeAuthor",
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("/@@author fakeAuthor",
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("@@author fakeAuthor */",
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("# something @@author fakeAuthor",
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("something % @@author fakeAuthor",
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("# @@author fakeAuthor something",
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("<!--@@authorfakeAuthor-->",
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("[//]: # (@@authorfakeAuthor)",
                AnnotatorAnalyzer.COMMENT_PATTERNS_MARKDOWN));
    }
}
