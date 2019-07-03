package reposense.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import reposense.model.Group;
import reposense.model.GroupConfiguration;
import reposense.model.RepoLocation;

/**
 * Container for the values parsed from {@code groups-config.csv} file.
 */
public class GroupConfigCsvParser extends CsvParser<GroupConfiguration> {
    public static final String GROUP_CONFIG_FILENAME = "groups-config.csv";

    /**
     * Positions of the elements of a line in groups-config.csv config file
     */
    private static final int LOCATION_POSITION = 0;
    private static final int GROUP_NAME_POSITION = 1;
    private static final int FILES_GLOB_POSITION = 2;

    public GroupConfigCsvParser(Path csvFilePath) throws IOException {
        super(csvFilePath);
    }

    /**
     * Gets the list of positions that are mandatory for verification.
     */
    @Override
    protected int[] mandatoryPositions() {
        return new int[] {
            LOCATION_POSITION, GROUP_NAME_POSITION, FILES_GLOB_POSITION,
        };
    }

    /**
     * Processes the csv file line by line and adds created {@code Group} into {@code results}.
     */
    @Override
    protected void processLine(List<GroupConfiguration> results, String[] elements) {
        String location = getValueInElement(elements, LOCATION_POSITION);
        String groupName = getValueInElement(elements, GROUP_NAME_POSITION);
        List<String> globList = getManyValueInElement(elements, FILES_GLOB_POSITION);

        GroupConfiguration config = null;
        try {
            config = findMatchingGroupConfiguration(results, location);
        } catch (InvalidLocationException e) {
            e.printStackTrace();
        }

        Group group = new Group(groupName, globList);
        if (config.containsGroup(group)) {
            logger.warning(String.format(
                    "Skipping group as %s has already been specified for the repository %s",
                    group.toString(), config.getLocation()));
            return;
        }

        config.addGroup(group);
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

        for (GroupConfiguration groupConfig: results) {
            if (groupConfig.getLocation().equals(config.getLocation())) {
                return groupConfig;
            }
        }

        results.add(config);
        return config;
    }
}
