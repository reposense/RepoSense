package system;

import org.junit.*;
import template.GitTestTemplate;
import util.TestConstants;

import java.io.File;
import java.util.Calendar;

/**
 * Created by matanghao1 on 5/2/18.
 */
public class CommandRunnerTest extends GitTestTemplate {

    @Test
    public void cloneTest(){
        File dir = new File(TestConstants.LOCAL_TEST_REPO_ADDRESS);
        Assert.assertTrue(dir.exists());
    }

    @Test
    public void checkoutTest(){
        CommandRunner.checkOut(TestConstants.LOCAL_TEST_REPO_ADDRESS,"test");
        File branchFile = new File(TestConstants.LOCAL_TEST_REPO_ADDRESS+File.separator+"inTestBranch.java");
        Assert.assertTrue(branchFile.exists());
    }

    @Test
    public void logWithContentTest(){
        String content = CommandRunner.gitLog(TestConstants.LOCAL_TEST_REPO_ADDRESS,null,null);
        Assert.assertNotEquals(content.length(),0);
    }

    @Test
    public void logWithoutContentTest(){
        Calendar c = Calendar.getInstance();
        c.set(2050, 1, 1, 0, 0);
        String content ="";
        try {
            content = CommandRunner.gitLog(TestConstants.LOCAL_TEST_REPO_ADDRESS, c.getTime(), null);
            Assert.assertEquals(content.length(),0);
        } catch (Exception e){
            Assert.assertTrue(false);
        }
        c.set(1950, 1, 1, 0, 0);

        try {
            content = CommandRunner.gitLog(TestConstants.LOCAL_TEST_REPO_ADDRESS, null, c.getTime());
            Assert.assertEquals(content.length(),0);
        } catch (Exception e){
            Assert.assertTrue(false);
        }

    }

    @Test
    public void blameRawTest(){
        try {
            String content = CommandRunner.blameRaw(TestConstants.LOCAL_TEST_REPO_ADDRESS,"blameTest.java");
            Assert.assertNotEquals(content.length(),0);
        } catch (Exception e){
            Assert.assertTrue(false);
        }
    }


}
