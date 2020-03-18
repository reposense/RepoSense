package reposense.authorship;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import reposense.authorship.model.FileInfo;
import reposense.authorship.model.FileResult;
import reposense.authorship.model.LineInfo;
import reposense.model.Author;
import reposense.parser.InvalidLocationException;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class AnnotatorAnalyzerTest extends GitTestTemplate {

    private static final Author MAIN_AUTHOR = new Author(MAIN_AUTHOR_NAME);
    private static final Author FAKE_AUTHOR = new Author(FAKE_AUTHOR_NAME);
    private static final Date SINCE_DATE = TestUtil.getSinceDate(2018, Calendar.FEBRUARY, 8);
    private static final Date UNTIL_DATE = TestUtil.getUntilDate(2018, Calendar.FEBRUARY, 10);

    @Before
    public void before() throws InvalidLocationException {
        super.before();
        config.setSinceDate(SINCE_DATE);
        config.setUntilDate(UNTIL_DATE);
    }

    @Test
    public void analyzeAnnotation_authorNamePresentInConfig_overrideAuthorship() {
        config.setAuthorList(Arrays.asList(FAKE_AUTHOR, MAIN_AUTHOR));
        FileResult fileResult = getFileResult("annotationTest.java");
        assertAnnotationAnalysisCorrectness(fileResult, FAKE_AUTHOR, MAIN_AUTHOR);
    }

    @Test
    public void analyzeAnnotation_authorNameNotInConfig_disownCode() {
        config.setAuthorList(Collections.singletonList(FAKE_AUTHOR));
        FileResult fileResult = getFileResult("annotationTest.java");
        assertAnnotationAnalysisCorrectness(fileResult, FAKE_AUTHOR, Author.UNKNOWN_AUTHOR);
    }

    public FileResult getFileResult(String relativePath) {
        FileInfo fileInfo = FileInfoExtractor.generateFileInfo(config.getRepoRoot(), relativePath);
        return FileInfoAnalyzer.analyzeFile(config, fileInfo);
    }

    /**
     * Asserts the correctness of authorship overriding with regards to @@author tags given
     * in the test file.
     */
    private void assertAnnotationAnalysisCorrectness(FileResult fileResult, Author originalAuthor,
            Author overridingAuthor) {
        for (LineInfo line : fileResult.getLines()) {
            Author lineAuthor = line.getAuthor();
            if (line.getContent().startsWith("fake")) {
                assertEquals(originalAuthor, lineAuthor);
            } else {
                assertEquals(overridingAuthor, lineAuthor);
            }
        }
    }

}
