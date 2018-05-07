package git;

import org.junit.Test;

import dataObject.FileInfo;
import template.GitTestTemplate;


public class GitBlamerTest extends GitTestTemplate {

    @Test
    public void blameTest() {
        FileInfo fileInfo = getBlamedFileInfo("blameTest.java");
        checkBlameInfoCorrectness(fileInfo);
    }

    @Test
    public void movedFileBlameTest() {
        FileInfo fileInfo = getBlamedFileInfo("newPos/movedFile.java");
        checkBlameInfoCorrectness(fileInfo);

    }
}
