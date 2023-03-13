package reposense.parser;

import static reposense.util.TestUtil.loadResource;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import reposense.model.FileType;
import reposense.model.GroupConfiguration;
import reposense.report.ErrorSummary;

public class GroupConfigParserTest {
    private static final Path GROUP_CONFIG_MULTI_LOCATION_FILE = loadResource(GroupConfigParserTest.class,
            "GroupConfigParserTest/groupconfig_multipleLocation_test.csv");
    private static final Path GROUP_CONFIG_EMPTY_LOCATION_FILE = loadResource(GroupConfigParserTest.class,
            "GroupConfigParserTest/groupconfig_emptyLocation_test.csv");
    private static final Path GROUP_CONFIG_DIFFERENT_COLUMN_ORDER_FILE = loadResource(GroupConfigParserTest.class,
            "GroupConfigParserTest/groupconfig_differentColumnOrder_test.csv");
    private static final Path GROUP_CONFIG_MISSING_OPTIONAL_HEADER_FILE = loadResource(GroupConfigParserTest.class,
            "GroupConfigParserTest/groupconfig_missingOptionalHeader_test.csv");
    private static final Path GROUP_CONFIG_MISSING_MANDATORY_HEADER_FILE = loadResource(GroupConfigParserTest.class,
            "GroupConfigParserTest/groupconfig_missingMandatoryHeader_test.csv");
    private static final Path GROUP_CONFIG_UNKNOWN_HEADER_FILE = loadResource(GroupConfigParserTest.class,
            "GroupConfigParserTest/groupconfig_unknownHeader_test.csv");

    private static final String TEST_REPO_BETA_LOCATION = "https://github.com/reposense/testrepo-Beta.git";
    private static final List<FileType> TEST_REPO_BETA_GROUPS = Arrays.asList(
            new FileType("Code", Arrays.asList("**/*.java", "**/*.py")),
            new FileType("Docs", Collections.singletonList("docs/**")));

    private static final String TEST_REPO_DELTA_LOCATION = "https://github.com/reposense/testrepo-Delta.git";
    private static final List<FileType> TEST_REPO_DELTA_GROUPS = Arrays.asList(
            new FileType("Main", Collections.singletonList("src/main/**")),
            new FileType("Test", Arrays.asList("src/test/**", "src/systest/**")));
    private ErrorSummary errorSummary = new ErrorSummary();

    @Test
    public void groupConfig_emptyLocation_success() throws Exception {
        GroupConfigCsvParser groupConfigCsvParser = new GroupConfigCsvParser(GROUP_CONFIG_EMPTY_LOCATION_FILE,
                errorSummary);
        List<GroupConfiguration> groupConfigs = groupConfigCsvParser.parse();

        Assertions.assertEquals(2, groupConfigs.size());

        GroupConfiguration actualReposenseConfig = groupConfigs.get(0);
        Assertions.assertEquals(2, actualReposenseConfig.getGroupsList().size());

        GroupConfiguration actualEmptyLocationConfig = groupConfigs.get(1);
        Assertions.assertEquals(1, actualEmptyLocationConfig.getGroupsList().size());
    }

    @Test
    public void groupConfig_multipleLocations_success() throws Exception {
        GroupConfigCsvParser groupConfigCsvParser = new GroupConfigCsvParser(GROUP_CONFIG_MULTI_LOCATION_FILE,
                errorSummary);
        List<GroupConfiguration> groupConfigs = groupConfigCsvParser.parse();

        Assertions.assertEquals(2, groupConfigs.size());

        GroupConfiguration actualBetaConfig = groupConfigs.get(0);
        Assertions.assertEquals(TEST_REPO_BETA_LOCATION, actualBetaConfig.getLocation().toString());
        Assertions.assertEquals(TEST_REPO_BETA_GROUPS, actualBetaConfig.getGroupsList());

        GroupConfiguration actualDeltaConfig = groupConfigs.get(1);
        Assertions.assertEquals(TEST_REPO_DELTA_LOCATION, actualDeltaConfig.getLocation().toString());
        Assertions.assertEquals(TEST_REPO_DELTA_GROUPS, actualDeltaConfig.getGroupsList());
    }

    @Test
    public void groupConfig_differentColumnOrder_success() throws Exception {
        GroupConfigCsvParser groupConfigCsvParser = new GroupConfigCsvParser(GROUP_CONFIG_DIFFERENT_COLUMN_ORDER_FILE,
                errorSummary);
        List<GroupConfiguration> groupConfigs = groupConfigCsvParser.parse();

        Assertions.assertEquals(2, groupConfigs.size());

        GroupConfiguration actualBetaConfig = groupConfigs.get(0);
        Assertions.assertEquals(TEST_REPO_BETA_LOCATION, actualBetaConfig.getLocation().toString());
        Assertions.assertEquals(TEST_REPO_BETA_GROUPS, actualBetaConfig.getGroupsList());

        GroupConfiguration actualDeltaConfig = groupConfigs.get(1);
        Assertions.assertEquals(TEST_REPO_DELTA_LOCATION, actualDeltaConfig.getLocation().toString());
        Assertions.assertEquals(TEST_REPO_DELTA_GROUPS, actualDeltaConfig.getGroupsList());
    }

    @Test
    public void groupConfig_missingOptionalHeader_success() throws Exception {
        GroupConfigCsvParser groupConfigCsvParser = new GroupConfigCsvParser(GROUP_CONFIG_MISSING_OPTIONAL_HEADER_FILE,
                errorSummary);
        List<GroupConfiguration> groupConfigs = groupConfigCsvParser.parse();

        Assertions.assertEquals(1, groupConfigs.size());

        Assertions.assertEquals(3, groupConfigs.get(0).getGroupsList().size());
    }

    @Test
    public void groupConfig_missingMandatoryHeader_throwsInvalidCsvException() throws Exception {
        GroupConfigCsvParser groupConfigCsvParser = new GroupConfigCsvParser(
                GROUP_CONFIG_MISSING_MANDATORY_HEADER_FILE, errorSummary);
        Assertions.assertThrows(InvalidCsvException.class, () -> groupConfigCsvParser.parse());
    }

    @Test
    public void groupConfig_unknownHeader_throwsInvalidHeaderException() throws Exception {
        GroupConfigCsvParser groupConfigCsvParser = new GroupConfigCsvParser(
                GROUP_CONFIG_UNKNOWN_HEADER_FILE, errorSummary);
        Assertions.assertThrows(InvalidHeaderException.class, () -> groupConfigCsvParser.parse());
    }
}
