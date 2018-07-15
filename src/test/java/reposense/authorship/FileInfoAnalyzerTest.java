package reposense.authorship;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import reposense.authorship.model.FileResult;
import reposense.git.GitChecker;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;


public class FileInfoAnalyzerTest extends GitTestTemplate {
    private static final String BLAME_TEST_FILE_CREATED_COMMIT_HASH = "8d0ac2e";
    private static final String LATEST_BLAME_TEST_FILE_COMMIT_HASH = "7680153";

    @Test
    public void analyzeFile_blameTestFileNoDateRange_success() {
        FileResult fileResult = FileInfoAnalyzer.analyzeFile(config, generateTestFileInfo("blameTest.java"));
        assertFileAnalysisCorrectness(fileResult);
    }

    @Test
    public void analyzeFile_movedFileNoDateRange_success() {
        FileResult fileResult = FileInfoAnalyzer.analyzeFile(config, generateTestFileInfo("newPos/movedFile.java"));
        assertFileAnalysisCorrectness(fileResult);
    }

    @Test
    public void analyzeFile_blameTestFileDateRange_success() {
        Date sinceDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 6);
        Date untilDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 8);

        GitChecker.checkoutToDate(config.getRepoRoot(), config.getBranch(), untilDate);
        config.setSinceDate(sinceDate);
        config.setUntilDate(untilDate);

        FileResult fileResult = FileInfoAnalyzer.analyzeFile(config, generateTestFileInfo("blameTest.java"));
        assertFileAnalysisCorrectness(fileResult);
    }

    @Test
    public void analyzeFile_movedFileDateRange_success() {
        Date sinceDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 7);
        Date untilDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 9);

        GitChecker.checkoutToDate(config.getRepoRoot(), config.getBranch(), untilDate);
        config.setSinceDate(sinceDate);
        config.setUntilDate(untilDate);

        FileResult fileResult = FileInfoAnalyzer.analyzeFile(config, generateTestFileInfo("newPos/movedFile.java"));
        assertFileAnalysisCorrectness(fileResult);
    }
}
