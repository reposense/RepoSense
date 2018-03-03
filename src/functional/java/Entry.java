/**
 * Created by matanghao1 on 3/3/18.
 */

import dataObject.RepoConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.json.*;
import report.RepoInfoFileGenerator;
import system.CSVConfigurationBuilder;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Entry {
    static final String FT_TEMP_DIR = "ft_temp";
    @Test
    public void test(){
        generateReport();
        String actualRelativeDir = getRelativeDir();
        File actualFiles = new File(getClass().getClassLoader().getResource("expected").getFile());
        verifyAllJSON(actualFiles,actualRelativeDir);
        FileUtil.deleteDirectory(FT_TEMP_DIR);
    }

    private void generateReport(){
        File configFile = new File(getClass().getClassLoader().getResource("sample_full.csv").getFile());
        Calendar c = Calendar.getInstance();
        c.set(2017,6,1);
        Date fromDate = c.getTime();
        c.set(2017,10,30);
        Date toDate = c.getTime();
        List<RepoConfiguration> configs = CSVConfigurationBuilder.buildConfigs(configFile,fromDate, toDate);
        RepoInfoFileGenerator.generateReposReport(configs, FT_TEMP_DIR);
    }

    private void verifyAllJSON(File expectedDirectory,String actualRelative){
        for (File file : expectedDirectory.listFiles()) {
            if (file.isDirectory()) {
                verifyAllJSON(file,actualRelative);
            } else {
                if (!file.getName().endsWith(".json")) continue;
                String relativeDirectory = file.getAbsolutePath().split("expected/")[1];
                assertJSON(file,relativeDirectory,actualRelative);
            }
        }
    }

    private void assertJSON(File expectedJSON,String expectedPosition,String actualRelative){
        File actual = new File(actualRelative+expectedPosition);
        Assert.assertTrue(actual.exists());
        verifyContent(expectedJSON,actual);
    }

    private void verifyContent(File expected, File actual){
        String expectedContent="";
        String actualContent="";
        try {
            expectedContent = new String(Files.readAllBytes(expected.toPath()));
            actualContent = new String(Files.readAllBytes(actual.toPath()));
        } catch (IOException e) {
            Assert.fail();
        }
        Assert.assertEquals(expectedContent,actualContent);

    }

    private String getRelativeDir(){
        for (File file:(new File("ft_temp")).listFiles()){
            if (file.getName().contains("DS_Store")) continue;
            return file.getName();
        }
        Assert.fail();
        return "";

    }


}
