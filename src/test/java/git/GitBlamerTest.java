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
        testAuthorCorrectness("blameTest.java");
    }

    @Test
    public void movedFileBlameTest(){
        testAuthorCorrectness("newPos/movedFile.java");
    }

    private void testAuthorCorrectness(String relativePath){
        FileInfo fileinfo = FileInfoGenerator.generateFileInfo(TestConstants.LOCAL_TEST_REPO_ADDRESS, relativePath);

        config.getAuthorAliasMap().put(TestConstants.MAIN_AUTHOR_NAME,new Author(TestConstants.MAIN_AUTHOR_NAME));
        config.getAuthorAliasMap().put(TestConstants.FAKE_AUTHOR_NAME,new Author(TestConstants.FAKE_AUTHOR_NAME));
        GitBlamer.aggregateBlameInfo(fileinfo,config);
        checkBlameInfoCorrectness(fileinfo);
    }

    private boolean checkBlameInfoCorrectness(FileInfo fileinfo){
        for (LineInfo line:fileinfo.getLines()){
            if (line.getContent().startsWith("fake")){
                Assert.assertEquals(line.getAuthor(),new Author(TestConstants.FAKE_AUTHOR_NAME));
            } else {
                Assert.assertNotEquals(line.getAuthor(),new Author(TestConstants.FAKE_AUTHOR_NAME));
            }
        }
        return true;
    }
}
