package git;

import analyzer.FileInfoGenerator;
import dataObject.Author;
import dataObject.FileInfo;
import dataObject.LineInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import template.GitTestTemplate;
import util.TestConstants;


/**
 * Created by matanghao1 on 8/2/18.
 */
public class GitBlamerTest extends GitTestTemplate {

    @Test
    public void blameTest(){
        FileInfo fileInfo = getBlamedFileInfo("blameTest.java");
        checkBlameInfoCorrectness(fileInfo);
    }

    @Test
    public void movedFileBlameTest(){
        FileInfo fileInfo = getBlamedFileInfo("newPos/movedFile.java");
        checkBlameInfoCorrectness(fileInfo);

    }

}
