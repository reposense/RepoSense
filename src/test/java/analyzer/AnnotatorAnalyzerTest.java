package analyzer;

import dataObject.FileInfo;
import dataObject.LineInfo;
import org.junit.Test;
import template.GitTestTemplate;

/**
 * Created by matanghao1 on 9/2/18.
 */
public class AnnotatorAnalyzerTest extends GitTestTemplate{
    @Test
    public void noAnnotationTest(){
        FileInfo fileInfo = getBlamedFileInfo("blameTest.java");
        AnnotatorAnalyzer.aggregateAnnotationAuthorInfo(fileInfo,config);
        checkBlameInfoCorrectness(fileInfo);
    }

    @Test
    public void AnnotationTest(){
        FileInfo fileInfo = getBlamedFileInfo("annotationTest.java");
        AnnotatorAnalyzer.aggregateAnnotationAuthorInfo(fileInfo,config);
        checkBlameInfoCorrectness(fileInfo);
    }
}
