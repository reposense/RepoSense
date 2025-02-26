package reposense.model.reportconfig;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.model.FileType;
import reposense.model.GroupConfiguration;
import reposense.model.RepoLocation;
import reposense.parser.exceptions.InvalidLocationException;

public class ReportRepoConfigurationTest {
    private static final List<ReportGroupNameAndGlobs> groups = List.of(
            new ReportGroupNameAndGlobs("group1", List.of("glob1", "glob2")),
            new ReportGroupNameAndGlobs("group2", List.of("glob3", "glob4"))
    );

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
                null,
                null
        );

        Assertions.assertThrows(IllegalArgumentException.class, () -> config.getFullyQualifiedRepoNamesWithBlurbs());
    }

    @Test
    void getGroupConfiguration_validGroups_returnsCorrectMapping() throws InvalidLocationException {
        ReportRepoConfiguration config = new ReportRepoConfiguration(
                "https://github.com/test/repo.git", groups, null);

        RepoLocation repoLocation = new RepoLocation("https://github.com/test/repo.git");
        GroupConfiguration expectedGroupConfiguration = new GroupConfiguration(repoLocation);
        expectedGroupConfiguration.addGroup(new FileType("group1", List.of("glob1", "glob2")));
        expectedGroupConfiguration.addGroup(new FileType("group2", List.of("glob3", "glob4")));

        Assertions.assertEquals(expectedGroupConfiguration,
                config.getGroupConfiguration(new RepoLocation("https://github.com/test/repo.git")));
    }

    @Test
    void equals_sameObject_success() {
        ReportRepoConfiguration config = new ReportRepoConfiguration(
                "https://github.com/test/repo.git", groups, null);

        Assertions.assertEquals(config, config);
    }

    @Test
    void equals_differentObject_failure() {
        ReportRepoConfiguration config1 = new ReportRepoConfiguration(
                "https://github.com/dev/repo.git", groups, null);
        ReportRepoConfiguration config2 = new ReportRepoConfiguration(
                "https://github.com/test/repo.git", groups, null);

        Assertions.assertFalse(config1.equals(config2));
    }
}
