package reposense.model;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CommitHashTest {
    @Test
    public void validateCommits_validHash_success() {
        CommitHash.validateCommits(Arrays.asList("8d0ac2ee20f04dce8df0591caed460bffacb65a4",
                "136c6713fc00cfe79a1598e8ce83c6ef3b878660"));
    }

    @Test
    public void validateCommits_invalidAlphabet_throwIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> CommitHash.validateCommits(
                Arrays.asList("8d0ac2ee20f04dce8df0591caed460gffacb65a4")));
    }

    @Test
    public void validateCommits_nonAlphanumeric_throwIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> CommitHash.validateCommits(
                Arrays.asList("!d0ac2ee20f04dce8df0591caed460gffacb65a4")));
    }
}
