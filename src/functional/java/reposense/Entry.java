package reposense;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.dataobject.RepoConfiguration;
import reposense.report.RepoInfoFileGenerator;
import reposense.system.CsvConfigurationBuilder;
import reposense.util.FileUtil;


public class Entry {
    static final String FT_TEMP_DIR = "ft_temp";

    @Test
    public void test() {
        generateReport();
        String actualRelativeDir = getRelativeDir();
        File actualFiles = new File(getClass().getClassLoader().getResource("expected").getFile());
        verifyAllJson(actualFiles, actualRelativeDir);
        FileUtil.deleteDirectory(FT_TEMP_DIR);
    }

    private void generateReport() {
        File configFile = new File(getClass().getClassLoader().getResource("sample_full.csv").getFile());
        Calendar c = Calendar.getInstance();
        c.set(2017, 6, 1);
        Date fromDate = c.getTime();
        c.set(2017, 10, 30);
        Date toDate = c.getTime();
        List<RepoConfiguration> configs = CsvConfigurationBuilder.buildConfigs(configFile, fromDate, toDate);
        RepoInfoFileGenerator.generateReposReport(configs, FT_TEMP_DIR);
    }

    private void verifyAllJson(File expectedDirectory, String actualRelative) {
        for (File file : expectedDirectory.listFiles()) {
            if (file.isDirectory()) {
                verifyAllJson(file, actualRelative);
            } else {
                if (!file.getName().endsWith(".js")) {
                    continue;
                }
                String relativeDirectory = file.getAbsolutePath().split("expected/")[1];
                assertJson(file, relativeDirectory, actualRelative);
            }
        }
    }

    private void assertJson(File expectedJson, String expectedPosition, String actualRelative) {
        File actual = new File(actualRelative + "/" + expectedPosition);
        Assert.assertTrue(actual.exists());
        verifyContent(expectedJson, actual);
    }

    private void verifyContent(File expected, File actual) {
        System.out.println("checking "+actual+" with "+expected);

        String expectedContent = "";
        String actualContent = "";
        try {
            expectedContent = new String(Files.readAllBytes(expected.toPath()));
            actualContent = new String(Files.readAllBytes(actual.toPath()));
        } catch (IOException e) {
            Assert.fail();
        }
        Assert.assertEquals(expectedContent, actualContent);
    }

    private String getRelativeDir() {
        for (File file : (new File(FT_TEMP_DIR)).listFiles()) {
            if (file.getName().contains("DS_Store")) {
                continue;
            }
            return FT_TEMP_DIR + "/" + file.getName();
        }
        Assert.fail();
        return "";

    }


}
