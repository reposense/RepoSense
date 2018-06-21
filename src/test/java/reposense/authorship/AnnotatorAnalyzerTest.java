package reposense.authorship;

import org.junit.Test;

import reposense.authorship.model.FileResult;
import reposense.template.GitTestTemplate;


public class AnnotatorAnalyzerTest extends GitTestTemplate {
    @Test
    public void noAnnotationTest() {
        config.setAnnotationOverwrite(false);
        FileResult fileResult = getFileResult("blameTest.java");
        assertFileAnalysisCorrectness(fileResult);
    }

    @Test
    public void annotationTest() {
        config.setAnnotationOverwrite(true);
        FileResult fileResult = getFileResult("blameTest.java");
        assertFileAnalysisCorrectness(fileResult);
    }
}
