package reposense.authorship;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import reposense.authorship.model.FileResult;
import reposense.git.GitChecker;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;


public class FileAnalyzerTest extends GitTestTemplate {

    @Test
    public void blameTest() {
        FileResult fileResult = getFileResult("blameTest.java");
        assertFileAnalysisCorrectness(fileResult);
    }

    @Test
    public void movedFileBlameTest() {
        FileResult fileResult = getFileResult("newPos/movedFile.java");
        assertFileAnalysisCorrectness(fileResult);

    }

    @Test
    public void blameTestDateRange() {
        Date sinceDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 6);
        Date untilDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 8);

        GitChecker.checkoutToDate(config.getRepoRoot(), config.getBranch(), untilDate);
        config.setSinceDate(sinceDate);
        config.setUntilDate(untilDate);

        FileResult fileResult = getFileResult("blameTest.java");
        assertFileAnalysisCorrectness(fileResult);
    }

    @Test
    public void movedFileBlameTestDateRange() {
        Date sinceDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 7);
        Date untilDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 9);

        GitChecker.checkoutToDate(config.getRepoRoot(), config.getBranch(), untilDate);
        config.setSinceDate(sinceDate);
        config.setUntilDate(untilDate);

        FileResult fileResult = getFileResult("newPos/movedFile.java");
        assertFileAnalysisCorrectness(fileResult);
    }
}
