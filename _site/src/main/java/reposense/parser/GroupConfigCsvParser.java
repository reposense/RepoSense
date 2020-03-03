package reposense.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import reposense.model.FileType;
import reposense.model.GroupConfiguration;
import reposense.model.RepoLocation;

/**
 * Container for the values parsed from {@code group-config.csv} file.
 */
public class GroupConfigCsvParser extends CsvParser<GroupConfiguration> {
    public static final String GROUP_CONFIG_FILENAME = "group-config.csv";

    /**
     * Positions of the elements of a line in group-config.csv config file
     */
    private static final int LOCATION_POSITION = 0;
    private static final int GROUP_NAME_POSITION = 1;
    private static final int FILES_GLOB_POSITION = 2;
    private static final int HEADER_SIZE = FILES_GLOB_POSITION + 1; // last position + 1

    public GroupConfigCsvParser(Path csvFilePath) throws IOException {
        super(csvFilePath, HEADER_SIZE);
    }

    /**
     * Gets the list of positions that are mandatory for verification.
     */
    @Override
    protected int[] mandatoryPositions() {
        return new int[] {
            GROUP_NAME_POSITION, FILES_GLOB_POSITION,
        };
    }

    /**
     * Processes the csv file line by line and adds created {@code Group} into {@code results}.
     */
    @Override
    protected void processLine(List<GroupConfiguration> results, CSVRecord record) throws InvalidLocationException {
        String location = get(record, LOCATION_POSITION);
        String groupName = get(record, GROUP_NAME_POSITION);
        List<String> globList = getAsList(record, FILES_GLOB_POSITION);

        GroupConfiguration groupConfig = null;
        groupConfig = findMatchingGroupConfiguration(results, location);

        FileType group = new FileType(groupName, globList);
        if (groupConfig.containsGroup(group)) {
            logger.warning(String.format(
                    "Skipping group as %s has already been specified for the repository %s",
                    group.toString(), groupConfig.getLocation()));
            return;
        }

        groupConfig.addGroup(group);
    }

    /**
     * Gets an existing {@code GroupConfiguration} from {@code results} if {@code location} matches.
     * Otherwise adds a newly created {@code GroupConfiguration} into {@code results} and returns it.
     *
     * @throws InvalidLocationException if {@code location} is invalid.
     */
    private static GroupConfiguration findMatchingGroupConfiguration(
            List<GroupConfiguration> results, String location) throws InvalidLocationException {
        GroupConfiguration config = new GroupConfiguration(new RepoLocation(location));

        for (GroupConfiguration groupConfig : results) {
            if (groupConfig.getLocation().equals(config.getLocation())) {
                return groupConfig;
            }
        }

        results.add(config);
        return config;
    }
}
