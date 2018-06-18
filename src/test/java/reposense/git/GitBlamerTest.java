package reposense.git;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import reposense.dataobject.FileInfo;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;


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

    @Test
    public void blameTestDateRange() {
        Date sinceDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 6);
        Date untilDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 8);

        GitChecker.checkoutToDate(config.getRepoRoot(), config.getBranch(), untilDate);
        config.setSinceDate(sinceDate);
        config.setUntilDate(untilDate);
        FileInfo fileInfo = getBlamedFileInfo("blameTest.java");
        checkBlameInfoCorrectness(fileInfo);
    }

    @Test
    public void movedFileBlameTestDateRange() {
        Date sinceDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 7);
        Date untilDate = TestUtil.getDate(2018, Calendar.FEBRUARY, 9);

        GitChecker.checkoutToDate(config.getRepoRoot(), config.getBranch(), untilDate);
        config.setSinceDate(sinceDate);
        config.setUntilDate(untilDate);
        FileInfo fileInfo = getBlamedFileInfo("newPos/movedFile.java");
        checkBlameInfoCorrectness(fileInfo);
    }
}
