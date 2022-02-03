package reposense.util;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.Month;

import org.junit.Assert;
import org.junit.Test;


public class TimeUtilTest {
    @Test
    public void extractDate_validDate_success() {
        String expectedDate = "20/05/2019";
        String actualDate = TimeUtil.extractDate(expectedDate);
        Assert.assertEquals(expectedDate, actualDate);
    }

    @Test
    public void extractDate_validDateAndTime_success() {
        String originalDateAndTime = "20/05/2020 12:34:56";
        String expectedDate = "20/05/2020";
        String actualDate = TimeUtil.extractDate(originalDateAndTime);
        Assert.assertEquals(expectedDate, actualDate);
    }

    @Test
    public void parseDate_validDateAndTime_success() throws Exception {
        String originalDateAndTime = "20/05/2020 00:00:00";
        LocalDateTime expectedDate = TestUtil.getSinceDate(2020, Month.MAY.getValue(), 20);
        LocalDateTime actualDate = TimeUtil.parseDate(originalDateAndTime);
        Assert.assertEquals(expectedDate, actualDate);
    }

    @Test (expected = ParseException.class)
    public void parseDate_invalidDate_throwsParseException() throws Exception {
        TimeUtil.parseDate("31/02/2020 00:00:00");
    }

    @Test (expected = ParseException.class)
    public void parseDate_invalidTime_throwsParseException() throws Exception {
        TimeUtil.parseDate("20/05/2020 23:69:70");
    }
}
