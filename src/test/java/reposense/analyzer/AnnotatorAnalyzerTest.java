package reposense.analyzer;

import org.junit.Test;

import reposense.dataobject.FileResult;
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
