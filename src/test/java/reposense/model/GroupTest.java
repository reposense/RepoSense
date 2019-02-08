package reposense.model;

import org.junit.Test;

public class GroupTest {
    @Test
    public void validateGroups_alphaNumeric_success() {
        Group.validateGroup("test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void validateGroups_nonAlphaNumeric_throwIllegalArgumentException() {
        Group.validateGroup("#test");
    }
}
