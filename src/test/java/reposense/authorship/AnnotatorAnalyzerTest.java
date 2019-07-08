package reposense.authorship;

import org.junit.Test;

import reposense.authorship.model.FileResult;
import reposense.template.GitTestTemplate;


public class AnnotatorAnalyzerTest extends GitTestTemplate {
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
