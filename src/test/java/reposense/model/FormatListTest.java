package reposense.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FormatListTest {
    @Test
    public void validateFormats_alphaNumeric_success()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method m = FormatList.class.getDeclaredMethod("validateFormats", List.class);
        m.setAccessible(true);
        m.invoke(null, Arrays.asList("java", "7z"));
    }
    @Test(expected = IllegalArgumentException.class)
    public void validateFormats_nonAlphaNumeric_throwIllegalArgumentException() throws Throwable {
        try {
            Method m = FormatList.class.getDeclaredMethod("validateFormats", List.class);
            m.setAccessible(true);
            m.invoke(null, Arrays.asList(".java"));
        } catch (InvocationTargetException ite) {
            throw ite.getCause();
        }
    }
}
