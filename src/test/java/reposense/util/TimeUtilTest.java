package reposense.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class TimeUtilTest {
    @Test
    public void extractDate_validDate_success() throws Exception {
        String expectedDate = "20/05/2019";
        String actualDate = TimeUtil.extractDate(expectedDate);
        Assert.assertEquals(expectedDate, actualDate);
    }

    @Test
    public void extractDate_validDateAndTime_success() throws Exception {
        String originalDateAndTime = "20/05/2020 12:34:56";
        String expectedDate = "20/05/2020";
        String actualDate = TimeUtil.extractDate(originalDateAndTime);
        Assert.assertEquals(expectedDate, actualDate);
    }

    @Test
    public void parseDate_validDateAndTime_success() throws Exception {
        String originalDateAndTime = "20/05/2020 00:00:00";
        Date expectedDate = TestUtil.getLocalSinceDate(2020, Calendar.MAY, 20);
        Date actualDate = TimeUtil.parseDate(originalDateAndTime);
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
