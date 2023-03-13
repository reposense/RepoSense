package reposense.parser;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import reposense.model.FileType;
import reposense.model.GroupConfiguration;
import reposense.model.RepoLocation;
import reposense.report.ErrorSummary;

/**
 * Container for the values parsed from {@code group-config.csv} file.
 */
public class GroupConfigCsvParser extends CsvParser<GroupConfiguration> {
    public static final String GROUP_CONFIG_FILENAME = "group-config.csv";

    /**
     * Positions of the elements of a line in group-config.csv config file
     */
    private static final String[] LOCATION_HEADER = {"Repository's Location"};
    private static final String[] GROUP_NAME_HEADER = {"Group Name"};
    private static final String[] FILES_GLOB_HEADER = {"Globs"};

    public GroupConfigCsvParser(Path csvFilePath, ErrorSummary errorSummary) throws FileNotFoundException {
        super(csvFilePath, errorSummary);
    }

    /**
     * Gets the list of headers that are mandatory for verification.
     */
    @Override
    protected String[][] mandatoryHeaders() {
        return new String[][] {
                GROUP_NAME_HEADER, FILES_GLOB_HEADER,
        };
    }

    /**
     * Gets the list of optional headers that can be parsed.
     */
    @Override
    protected String[][] optionalHeaders() {
        return new String[][] {
                LOCATION_HEADER,
        };
    }

    /**
     * Processes the csv {@code record} line by line and adds created {@link GroupConfiguration} into {@code results}.
     */
    @Override
    protected void processLine(List<GroupConfiguration> results, CSVRecord record) throws InvalidLocationException {
        String location = get(record, LOCATION_HEADER);
        String groupName = get(record, GROUP_NAME_HEADER);
        List<String> globList = getAsList(record, FILES_GLOB_HEADER);

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
     * Gets an existing {@link GroupConfiguration} from {@code results} if {@code location} matches.
     * Otherwise, adds a newly created {@link GroupConfiguration} into {@code results} and returns it.
     *
     * @throws InvalidLocationException if {@code location} is invalid.
     */
    private GroupConfiguration findMatchingGroupConfiguration(List<GroupConfiguration> results,
            String location) throws InvalidLocationException {
        try {
            GroupConfiguration config = new GroupConfiguration(new RepoLocation(location));

            for (GroupConfiguration groupConfig : results) {
                if (groupConfig.getLocation().equals(config.getLocation())) {
                    return groupConfig;
                }
            }

            results.add(config);
            return config;
        } catch (InvalidLocationException ile) {
            errorSummary.addErrorMessage(location, ile.getMessage());
            throw ile;
        }
    }
}
