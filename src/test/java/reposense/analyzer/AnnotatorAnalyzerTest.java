package reposense.analyzer;

import org.junit.Test;

import reposense.dataObject.FileInfo;
import reposense.template.GitTestTemplate;


public class AnnotatorAnalyzerTest extends GitTestTemplate {
    @Test
    public void noAnnotationTest() {
        FileInfo fileInfo = getBlamedFileInfo("blameTest.java");
        AnnotatorAnalyzer.aggregateAnnotationAuthorInfo(fileInfo, config);
        checkBlameInfoCorrectness(fileInfo);
    }

    @Test
    public void annotationTest() {
        FileInfo fileInfo = getBlamedFileInfo("annotationTest.java");
        AnnotatorAnalyzer.aggregateAnnotationAuthorInfo(fileInfo, config);
        checkBlameInfoCorrectness(fileInfo);
    }
}
