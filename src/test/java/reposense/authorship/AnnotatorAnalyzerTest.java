package reposense.authorship;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import reposense.authorship.model.FileResult;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;


public class AnnotatorAnalyzerTest extends GitTestTemplate {
    private static final Date BLAME_TEST_SINCE_DATE = TestUtil.getDate(2018, Calendar.FEBRUARY, 6);
    private static final Date BLAME_TEST_UNTIL_DATE = TestUtil.getDate(2018, Calendar.FEBRUARY, 8);

    @Test
    public void noAnnotationTest() {
        config.setAnnotationOverwrite(false);
        config.setSinceDate(BLAME_TEST_SINCE_DATE);
        config.setUntilDate(BLAME_TEST_UNTIL_DATE);
        FileResult fileResult = getFileResult("blameTest.java");
        assertFileAnalysisCorrectness(fileResult);
    }

    @Test
    public void annotationTest() {
        config.setAnnotationOverwrite(true);
        config.setSinceDate(BLAME_TEST_SINCE_DATE);
        config.setUntilDate(BLAME_TEST_UNTIL_DATE);
        FileResult fileResult = getFileResult("blameTest.java");
        assertFileAnalysisCorrectness(fileResult);
    }
}
