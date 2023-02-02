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
    private static final LocalDateTime UNTIL_DATE = TestUtil.getUntilDate(2023, Month.JANUARY.getValue(), 31);
    private static final Author[] EXPECTED_LINE_AUTHORS_OVERRIDE_AUTHORSHIP_TEST = {
            FAKE_AUTHOR, FAKE_AUTHOR, FAKE_AUTHOR, FAKE_AUTHOR,
            MAIN_AUTHOR, MAIN_AUTHOR, MAIN_AUTHOR, MAIN_AUTHOR, MAIN_AUTHOR,
            FAKE_AUTHOR, FAKE_AUTHOR,
            UNCONVENTIONAL_AUTHOR, UNCONVENTIONAL_AUTHOR, UNCONVENTIONAL_AUTHOR, UNCONVENTIONAL_AUTHOR,
            UNCONVENTIONAL_AUTHOR,
            FAKE_AUTHOR,
            WHITESPACE_AUTHOR, WHITESPACE_AUTHOR, WHITESPACE_AUTHOR,
            FAKE_AUTHOR, FAKE_AUTHOR
    };
    private static final Author[] EXPECTED_LINE_AUTHORS_DISOWN_CODE_TEST = {
            FAKE_AUTHOR, FAKE_AUTHOR, FAKE_AUTHOR, FAKE_AUTHOR,
            UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR,
            FAKE_AUTHOR, FAKE_AUTHOR,
            UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR,
            FAKE_AUTHOR,
            UNKNOWN_AUTHOR, UNKNOWN_AUTHOR, UNKNOWN_AUTHOR,
            FAKE_AUTHOR, FAKE_AUTHOR
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
    public void extractAuthorName_matchCommentPattern0_returnAuthorName() {
        int index = 0;
        String line;
        line = "//@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "// @@author fakeauthor  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "  //  @@author   fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "// @@author   fake 4u-th0r";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake 4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "// @@author   --fake4u-th0r--";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("--fake4u-th0r--", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "// @@author   --fake--  __4u-th0r--   ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("--fake--  __4u-th0r--", AnnotatorAnalyzer.extractAuthorName(line).get());
    }

    @Test
    public void extractAuthorName_matchCommentPattern1_returnAuthorName() {
        int index = 1;
        String line;
        line = "/*@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "/* @@author fakeauthor  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "  /*  @@author   fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "/*@@author fakeAuthor*/";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "/* @@author fakeauthor */";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "  /*  @@author   fake-4u-th0r  */  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "/*  @@author   fake  4u-th0r  */";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake  4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "/*  @@author   --_fake4u-th0r  */";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("--_fake4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());
    }

    @Test
    public void extractAuthorName_matchCommentPattern2_returnAuthorName() {
        int index = 2;
        String line;
        line = "#@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "# @@author fakeauthor  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "  #  @@author   fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "#  @@author   fake  4u-th0r";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake  4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "#  @@author   fake -4u-th0r     ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake -4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());
    }

    @Test
    public void extractAuthorName_matchCommentPattern3_returnAuthorName() {
        int index = 3;
        String line;
        line = "<!--@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "<!-- @@author fakeauthor  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "<!-- @@author fake  author  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake  author", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "  <!--  @@author   fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "<!--@@author fakeAuthor-->";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "<!-- @@author fakeauthor -->";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "  <!--  @@author   fake-4u-th0r  -->  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "  <!--  @@author   fake-4u-th0r-->  -->  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "  <!--  @@author   fake-4u-th0r--->  -->  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r-", AnnotatorAnalyzer.extractAuthorName(line).get());
    }

    @Test
    public void extractAuthorName_matchCommentPattern4_returnAuthorName() {
        int index = 4;
        String line;
        line = "%@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "% @@author fakeauthor  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = " % @@author fake-4u-th0r";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "  %  @@author   fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "  %  @@author   *()fake-4u-th0r-- %%  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("*()fake-4u-th0r-- %%", AnnotatorAnalyzer.extractAuthorName(line).get());
    }

    @Test
    public void extractAuthorName_matchMarkdownCommentPattern5_returnAuthorName() {
        int index = 5;
        String line;

        line = "[//]:#(@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "[//]: # (@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "[//]: # (@@author fakeauthor   ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "  [//]:   #   (@@author    fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "[//]: # (@@author fakeAuthor)";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "[//]: # (@@author fakeauthor   )";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "   [//]:   #   (@@author fake-4u-th0r   )  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "   [fake inner text 123+%^&!@#$(`]:   #   (@@author fake-4u-th0r   )  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "   [    space   separated   inner   text   ]:   #   (  @@author fake-4u-th0r   )  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());
    }


    @Test
    public void extractAuthorName_matchCommentPattern6_returnAuthorName() {
        int index = 6;
        String line;
        line = "<!---@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "<!--- @@author fakeauthor  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "  <!---  @@author   fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "<!---@@author fakeAuthor--->";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "<!--- @@author fakeauthor --->";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line).get());

        line = "  <!---  @@author   fake-4u-th0r  --->  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line).get());
    }

    @Test
    public void extractAuthorName_noAuthorName_returnNull() {
        String line;

        line = "//@@author";
        Assertions.assertEquals(0, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line).isPresent());

        line = "/*@@author ";
        Assertions.assertEquals(1, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line).isPresent());

        line = "/* @@author*/ ";
        Assertions.assertEquals(1, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line).isPresent());

        line = "#@@author ";
        Assertions.assertEquals(2, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line).isPresent());

        line = "  <!--@@author --> ";
        Assertions.assertEquals(3, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line).isPresent());

        line = "% @@author ";
        Assertions.assertEquals(4, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line).isPresent());

        line = "[//]: # (@@author)";
        Assertions.assertEquals(5, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line).isPresent());

        line = "  <!---@@author ---> ";
        Assertions.assertEquals(6, AnnotatorAnalyzer.getCommentTypeIndex(line));
        Assertions.assertFalse(AnnotatorAnalyzer.extractAuthorName(line).isPresent());
    }

    @Test
    public void getCommentType_matchCommentPattern0_success() {
        Assertions.assertEquals(0, AnnotatorAnalyzer.getCommentTypeIndex("//@@author fakeAuthor"));
        Assertions.assertEquals(0, AnnotatorAnalyzer.getCommentTypeIndex("// @@author fakeAuthor"));
        Assertions.assertEquals(0, AnnotatorAnalyzer.getCommentTypeIndex("   // @@author fakeAuthor"));
        Assertions.assertEquals(0, AnnotatorAnalyzer.getCommentTypeIndex("   //    @@author  fakeAuthor     "));
    }

    @Test
    public void getCommentType_matchCommentPattern1_success() {
        Assertions.assertEquals(1, AnnotatorAnalyzer.getCommentTypeIndex("/*@@author fakeAuthor"));
        Assertions.assertEquals(1, AnnotatorAnalyzer.getCommentTypeIndex("/* @@author fakeAuthor"));
        Assertions.assertEquals(1, AnnotatorAnalyzer.getCommentTypeIndex("   /* @@author fakeAuthor*/"));
        Assertions.assertEquals(1, AnnotatorAnalyzer.getCommentTypeIndex("   /*  @@author  fakeAuthor  */  "));
    }

    @Test
    public void getCommentType_matchCommentPattern2_success() {
        Assertions.assertEquals(2, AnnotatorAnalyzer.getCommentTypeIndex("#@@author fakeAuthor"));
        Assertions.assertEquals(2, AnnotatorAnalyzer.getCommentTypeIndex("# @@author fakeAuthor"));
        Assertions.assertEquals(2, AnnotatorAnalyzer.getCommentTypeIndex("   # @@author fakeAuthor"));
        Assertions.assertEquals(2, AnnotatorAnalyzer.getCommentTypeIndex("   #    @@author  fakeAuthor     "));
    }

    @Test
    public void getCommentType_matchCommentPattern3_success() {
        Assertions.assertEquals(3, AnnotatorAnalyzer.getCommentTypeIndex("<!--@@author fakeAuthor"));
        Assertions.assertEquals(3, AnnotatorAnalyzer.getCommentTypeIndex("<!-- @@author fakeAuthor"));
        Assertions.assertEquals(3, AnnotatorAnalyzer.getCommentTypeIndex("   <!-- @@author fakeAuthor-->"));
        Assertions.assertEquals(3, AnnotatorAnalyzer.getCommentTypeIndex("   <!--  @@author  fakeAuthor  -->  "));
    }

    @Test
    public void getCommentType_matchCommentPattern4_success() {
        Assertions.assertEquals(4, AnnotatorAnalyzer.getCommentTypeIndex("%@@author fakeAuthor"));
        Assertions.assertEquals(4, AnnotatorAnalyzer.getCommentTypeIndex("% @@author fakeAuthor"));
        Assertions.assertEquals(4, AnnotatorAnalyzer.getCommentTypeIndex("   % @@author fakeAuthor"));
        Assertions.assertEquals(4, AnnotatorAnalyzer.getCommentTypeIndex("   %    @@author  fakeAuthor     "));
    }

    @Test
    public void getCommentType_matchCommentPattern5_success() {
        Assertions.assertEquals(5, AnnotatorAnalyzer.getCommentTypeIndex("[//]:#(@@author fakeAuthor)"));
        Assertions.assertEquals(5, AnnotatorAnalyzer.getCommentTypeIndex("[//]: # (@@author fakeAuthor)"));
        Assertions.assertEquals(5, AnnotatorAnalyzer.getCommentTypeIndex("   [//]:  #  (@@author fakeAuthor)  "));
        Assertions.assertEquals(5, AnnotatorAnalyzer.getCommentTypeIndex("   [//]:  #  (  @@author   fakeAuthor  )  "));
    }

    @Test
    public void getCommentType_matchCommentPattern6_success() {
        Assertions.assertEquals(6, AnnotatorAnalyzer.getCommentTypeIndex("<!---@@author fakeAuthor"));
        Assertions.assertEquals(6, AnnotatorAnalyzer.getCommentTypeIndex("<!--- @@author fakeAuthor"));
        Assertions.assertEquals(6, AnnotatorAnalyzer.getCommentTypeIndex("   <!--- @@author fakeAuthor--->"));
        Assertions.assertEquals(6, AnnotatorAnalyzer.getCommentTypeIndex("   <!---  @@author  fakeAuthor  --->  "));
    }

    @Test
    public void getCommentType_invalidCommentPattern_returnMinus1() {
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("@@author fakeAuthor"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("/@@author fakeAuthor"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("@@author fakeAuthor */"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("# something @@author fakeAuthor"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("something % @@author fakeAuthor"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("<!--@@authorfakeAuthor-->"));
    }

    @Test
    public void getCommentType_invalidMarkdownCommentPattern_returnMinus1() {
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("@@author fakeAuthor"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("/@@author fakeAuthor"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("@@author fakeAuthor */"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("# something @@author fakeAuthor"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("something % @@author fakeAuthor"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("<!--@@authorfakeAuthor-->"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.getCommentTypeIndex("[//]: # (@@authorfakeAuthor)"));
    }
}
