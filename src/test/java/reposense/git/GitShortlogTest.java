package reposense.git;

import static reposense.git.GitShortlog.getAuthors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import reposense.model.Author;
import reposense.template.GitTestTemplate;
import reposense.util.TestUtil;

public class GitShortlogTest extends GitTestTemplate {

    @Test
    public void getShortlogSummary_noDateRange_success()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = GitShortlog.class.getDeclaredMethod("getShortlogSummary", String.class, Date.class, Date.class);
        m.setAccessible(true);
        String result = (String) m.invoke(null, config.getRepoRoot(), null, null);

        Assert.assertTrue(result.contains(EUGENE_AUTHOR_NAME));
        Assert.assertTrue(result.contains(FAKE_AUTHOR_NAME));
        Assert.assertTrue(result.contains(MAIN_AUTHOR_NAME));
    }

    @Test
    public void getShortlogSummary_dateRange_success()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Date sinceDate = TestUtil.getDate(2018, Calendar.MAY, 5);
        Date untilDate = TestUtil.getDate(2018, Calendar.MAY, 10);

        Method m = GitShortlog.class.getDeclaredMethod("getShortlogSummary", String.class, Date.class, Date.class);
        m.setAccessible(true);
        String result = (String) m.invoke(null, config.getRepoRoot(), sinceDate, untilDate);

        Assert.assertTrue(result.contains(EUGENE_AUTHOR_NAME));
    }

    @Test
    public void getShortlogSummary_dateOutOfRange_emptyResult()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Date sinceDate = TestUtil.getDate(2018, Calendar.JUNE, 1);
        Date untilDate = TestUtil.getDate(2018, Calendar.JUNE, 10);

        Method m = GitShortlog.class.getDeclaredMethod("getShortlogSummary", String.class, Date.class, Date.class);
        m.setAccessible(true);
        String result = (String) m.invoke(null, config.getRepoRoot(), sinceDate, untilDate);

        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void getAuthors_validRepoNoDateRange_success() {
        List<Author> expectedAuthorList = new ArrayList<>();
        expectedAuthorList.add(new Author("Eugene Peh"));
        expectedAuthorList.add(new Author("eugenepeh"));
        expectedAuthorList.add(new Author("fakeAuthor"));
        expectedAuthorList.add(new Author("harryggg"));

        List<Author> actualAuthorList = getAuthors(config);

        Assert.assertEquals(expectedAuthorList.size(), actualAuthorList.size());
        Assert.assertEquals(expectedAuthorList, actualAuthorList);
    }

    @Test
    public void getAuthors_validRepoDateRange_success() {
        List<Author> expectedAuthorList = new ArrayList<>();

        expectedAuthorList.add(new Author("eugenepeh"));
        config.setSinceDate(TestUtil.getDate(2018, Calendar.MAY, 5));
        config.setUntilDate(TestUtil.getDate(2018, Calendar.MAY, 10));

        List<Author> actualAuthorList = getAuthors(config);

        Assert.assertEquals(expectedAuthorList.size(), actualAuthorList.size());
        Assert.assertEquals(expectedAuthorList, actualAuthorList);
    }

    @Test
    public void getAuthors_validRepoDateOutOfRange_success() {
        config.setSinceDate(TestUtil.getDate(2018, Calendar.JUNE, 1));
        config.setUntilDate(TestUtil.getDate(2018, Calendar.JUNE, 20));

        List<Author> actualAuthorList = getAuthors(config);

        Assert.assertTrue(actualAuthorList.isEmpty());
    }
}
