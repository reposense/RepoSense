package reposense.model.reportconfig;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportGroupNameAndGlobsTest {
    @Test
    public void constructor_withValidInputs_success() {
        ReportGroupNameAndGlobs reportGroupNameAndGlobs = new ReportGroupNameAndGlobs("My Group", List.of("code"));
        Assertions.assertNotNull(reportGroupNameAndGlobs);
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
}
