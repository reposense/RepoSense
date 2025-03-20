package reposense.model.reportconfig;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.model.FileType;

public class ReportGroupNameAndGlobsTest {
    private static final ReportGroupNameAndGlobs reportGroupNameAndGlobs1 = new ReportGroupNameAndGlobs("My Group",
            List.of("code"));

    @Test
    public void constructor_withValidInputs_success() {
        Assertions.assertNotNull(reportGroupNameAndGlobs1);
        Assertions.assertEquals("My Group", reportGroupNameAndGlobs1.getGroupName());
        Assertions.assertEquals(List.of("code"), reportGroupNameAndGlobs1.getGlobs());
    }

    @Test
    public void constructor_nullGroupName_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ReportGroupNameAndGlobs(null,
                List.of("code")));
    }

    @Test
    public void constructor_nullGlobs_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ReportGroupNameAndGlobs("My Group", null));
    }

    @Test
    public void constructor_nullGroupNameAndNullGlobs_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ReportGroupNameAndGlobs(null, null));
    }

    @Test
    public void toFileType_success() {
        Assertions.assertEquals(new FileType("My Group", List.of("code")), reportGroupNameAndGlobs1.toFileType());
    }

    @Test
    public void equals_sameObject_success() {
        Assertions.assertEquals(reportGroupNameAndGlobs1, reportGroupNameAndGlobs1);
    }

    @Test
    public void equals_differentObject_failure() {
        ReportGroupNameAndGlobs reportGroupNameAndGlobs2 = new ReportGroupNameAndGlobs("My Group", List.of("test"));
        Assertions.assertNotEquals(reportGroupNameAndGlobs1, reportGroupNameAndGlobs2);
    }

    @Test
    public void equals_differentClass_failure() {
        Assertions.assertNotEquals(new Object(), reportGroupNameAndGlobs1);
    }
}
