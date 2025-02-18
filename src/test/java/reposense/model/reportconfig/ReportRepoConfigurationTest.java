package reposense.model.reportconfig;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReportRepoConfigurationTest {
    @Test
    void constructor_nullRepo_throwsIllegalArgumentException() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                new ReportRepoConfiguration(null, null, null));
    }

    @Test
    void constructor_nullGroupsAndBranches_createsEmptyLists() {
        ReportRepoConfiguration config = new ReportRepoConfiguration("https://github.com/test/repo.git", null, null);

        Assertions.assertNotNull(config.getGroupDetails());
        Assertions.assertTrue(config.getGroupDetails().isEmpty());
        Assertions.assertNotNull(config.getBranches());
        Assertions.assertTrue(config.getBranches().isEmpty());
    }

    @Test
    void getFullyQualifiedRepoNamesWithBlurbs_validUrl_returnsCorrectMapping() {
        List<ReportBranchData> branches = List.of(
                new ReportBranchData("main", "Main branch", null, null, null, null),
                new ReportBranchData("dev", "Development branch", null, null, null, null)
        );
        ReportRepoConfiguration config = new ReportRepoConfiguration(
                "https://github.com/test/repo.git",
                new ArrayList<>(),
                branches
        );

        List<ReportRepoConfiguration.MapEntry> result = config.getFullyQualifiedRepoNamesWithBlurbs();

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals("https://github.com/test/repo/tree/main", result.get(0).getKey());
        Assertions.assertEquals("Main branch", result.get(0).getValue());
        Assertions.assertEquals("https://github.com/test/repo/tree/dev", result.get(1).getKey());
        Assertions.assertEquals("Development branch", result.get(1).getValue());
    }

    @Test
    void getFullyQualifiedRepoNamesWithBlurbs_invalidUrl_throwsIllegalArgumentException() {
        ReportRepoConfiguration config = new ReportRepoConfiguration(
                "https://github.com/test/repo",  // Missing .git
                new ArrayList<>(),
                new ArrayList<>()
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> config.getFullyQualifiedRepoNamesWithBlurbs());
    }
}
