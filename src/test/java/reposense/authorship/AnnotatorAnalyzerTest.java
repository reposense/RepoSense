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
import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.model.Author;
import reposense.model.AuthorConfiguration;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class AnnotatorAnalyzerTest extends GitTestTemplate {
    private static final LocalDateTime SINCE_DATE = TestUtil.getSinceDate(2018, Month.FEBRUARY.getValue(), 8);
    private static final LocalDateTime UNTIL_DATE = TestUtil.getUntilDate(2021, Month.AUGUST.getValue(), 3);
    private static final String TIME_ZONE_ID_STRING = "Asia/Singapore";
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


    @BeforeEach
    public void before() throws Exception {
        super.before();
        config.setSinceDate(SINCE_DATE);
        config.setUntilDate(UNTIL_DATE);
        config.setZoneId(TIME_ZONE_ID_STRING);
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

    public FileResult getFileResult(String relativePath) {
        FileInfo fileInfo = FileInfoExtractor.generateFileInfo(config.getRepoRoot(), relativePath);
        return FileInfoAnalyzer.analyzeTextFile(config, fileInfo);
    }

    @Test
    public void extractAuthorName_matchCommentPattern0_returnAuthorName() {
        int index = 0;
        String line;
        line = "//@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line, index));

        line = "// @@author fakeauthor  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line, index));

        line = "  //  @@author   fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, index));
    }

    @Test
    public void extractAuthorName_matchCommentPattern1_returnAuthorName() {
        int index = 1;
        String line;
        line = "/*@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line, index));

        line = "/* @@author fakeauthor  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line, index));

        line = "  /*  @@author   fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, index));

        line = "/*@@author fakeAuthor*/";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line, index));

        line = "/* @@author fakeauthor */";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line, index));

        line = "  /*  @@author   fake-4u-th0r  */  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, index));
    }

    @Test
    public void extractAuthorName_matchCommentPattern2_returnAuthorName() {
        int index = 2;
        String line;
        line = "#@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line, index));

        line = "# @@author fakeauthor  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line, index));

        line = "  #  @@author   fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, index));
    }

    @Test
    public void extractAuthorName_matchCommentPattern3_returnAuthorName() {
        int index = 3;
        String line;
        line = "<!--@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line, index));

        line = "<!-- @@author fakeauthor  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line, index));

        line = "  <!--  @@author   fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, index));

        line = "<!--@@author fakeAuthor-->";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line, index));

        line = "<!-- @@author fakeauthor -->";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line, index));

        line = "  <!--  @@author   fake-4u-th0r  -->  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, index));
    }

    @Test
    public void extractAuthorName_matchCommentPattern4_returnAuthorName() {
        int index = 4;
        String line;
        line = "%@@author fakeAuthor";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fakeAuthor", AnnotatorAnalyzer.extractAuthorName(line, index));

        line = "% @@author fakeauthor  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fakeauthor", AnnotatorAnalyzer.extractAuthorName(line, index));

        line = "  %  @@author   fake-4u-th0r  ";
        Assertions.assertEquals(index, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertEquals("fake-4u-th0r", AnnotatorAnalyzer.extractAuthorName(line, index));
    }

    @Test
    public void extractAuthorName_noAuthorName_returnNull() {
        String line;

        line = "//@@author";
        Assertions.assertEquals(0, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertNull(AnnotatorAnalyzer.extractAuthorName(line, 0));

        line = "/*@@author ";
        Assertions.assertEquals(1, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertNull(AnnotatorAnalyzer.extractAuthorName(line, 1));

        line = "/* @@author*/ ";
        Assertions.assertEquals(1, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertNull(AnnotatorAnalyzer.extractAuthorName(line, 1));

        line = "#@@author ";
        Assertions.assertEquals(2, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertNull(AnnotatorAnalyzer.extractAuthorName(line, 2));

        line = "  <!--@@author --> ";
        Assertions.assertEquals(3, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertNull(AnnotatorAnalyzer.extractAuthorName(line, 3));

        line = "% @@author ";
        Assertions.assertEquals(4, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertNull(AnnotatorAnalyzer.extractAuthorName(line, 4));
    }

    @Test
    public void extractAuthorName_invalidAuthorName_returnNull() {
        String line;

        line = "% @@author thisAuthorNameHasMoreThanThirtyNineLetters";
        Assertions.assertEquals(4, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertNull(AnnotatorAnalyzer.extractAuthorName(line, 4));

        line = "# @@author -invalidUsernameFormat";
        Assertions.assertEquals(2, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertNull(AnnotatorAnalyzer.extractAuthorName(line, 2));

        line = "/*@@author fakeAuthor-->";
        Assertions.assertEquals(1, AnnotatorAnalyzer.checkValidCommentLine(line));
        Assertions.assertNull(AnnotatorAnalyzer.extractAuthorName(line, 1));
    }

    @Test
    public void checkValidCommentLine_matchCommentPattern0_success() {
        Assertions.assertEquals(0, AnnotatorAnalyzer.checkValidCommentLine("//@@author fakeAuthor"));
        Assertions.assertEquals(0, AnnotatorAnalyzer.checkValidCommentLine("// @@author fakeAuthor"));
        Assertions.assertEquals(0, AnnotatorAnalyzer.checkValidCommentLine("   // @@author fakeAuthor"));
        Assertions.assertEquals(0, AnnotatorAnalyzer.checkValidCommentLine("   //    @@author  fakeAuthor     "));
    }

    @Test
    public void checkValidCommentLine_matchCommentPattern1_success() {
        Assertions.assertEquals(1, AnnotatorAnalyzer.checkValidCommentLine("/*@@author fakeAuthor"));
        Assertions.assertEquals(1, AnnotatorAnalyzer.checkValidCommentLine("/* @@author fakeAuthor"));
        Assertions.assertEquals(1, AnnotatorAnalyzer.checkValidCommentLine("   /* @@author fakeAuthor*/"));
        Assertions.assertEquals(1, AnnotatorAnalyzer.checkValidCommentLine("   /*  @@author  fakeAuthor  */  "));
    }

    @Test
    public void checkValidCommentLine_matchCommentPattern2_success() {
        Assertions.assertEquals(2, AnnotatorAnalyzer.checkValidCommentLine("#@@author fakeAuthor"));
        Assertions.assertEquals(2, AnnotatorAnalyzer.checkValidCommentLine("# @@author fakeAuthor"));
        Assertions.assertEquals(2, AnnotatorAnalyzer.checkValidCommentLine("   # @@author fakeAuthor"));
        Assertions.assertEquals(2, AnnotatorAnalyzer.checkValidCommentLine("   #    @@author  fakeAuthor     "));
    }

    @Test
    public void checkValidCommentLine_matchCommentPattern3_success() {
        Assertions.assertEquals(3, AnnotatorAnalyzer.checkValidCommentLine("<!--@@author fakeAuthor"));
        Assertions.assertEquals(3, AnnotatorAnalyzer.checkValidCommentLine("<!-- @@author fakeAuthor"));
        Assertions.assertEquals(3, AnnotatorAnalyzer.checkValidCommentLine("   <!-- @@author fakeAuthor-->"));
        Assertions.assertEquals(3, AnnotatorAnalyzer.checkValidCommentLine("   <!--  @@author  fakeAuthor  -->  "));
    }

    @Test
    public void checkValidCommentLine_matchCommentPattern4_success() {
        Assertions.assertEquals(4, AnnotatorAnalyzer.checkValidCommentLine("%@@author fakeAuthor"));
        Assertions.assertEquals(4, AnnotatorAnalyzer.checkValidCommentLine("% @@author fakeAuthor"));
        Assertions.assertEquals(4, AnnotatorAnalyzer.checkValidCommentLine("   % @@author fakeAuthor"));
        Assertions.assertEquals(4, AnnotatorAnalyzer.checkValidCommentLine("   %    @@author  fakeAuthor     "));
    }

    @Test
    public void checkValidCommentLine_invalidCommentPattern_returnMinus1() {
        Assertions.assertEquals(-1, AnnotatorAnalyzer.checkValidCommentLine("// @@author fakeAuthor //"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.checkValidCommentLine("@@author fakeAuthor"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.checkValidCommentLine("/@@author fakeAuthor"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.checkValidCommentLine("@@author fakeAuthor */"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.checkValidCommentLine("# something @@author fakeAuthor"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.checkValidCommentLine("something % @@author fakeAuthor"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.checkValidCommentLine("# @@author fakeAuthor something"));
        Assertions.assertEquals(-1, AnnotatorAnalyzer.checkValidCommentLine("<!--@@authorfakeAuthor-->"));
    }
}
