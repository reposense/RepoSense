package reposense.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import reposense.model.Group;

public class GroupConfigCsvParser extends CsvParser<Group> {

    /**
     * Positions of the elements of a line in groups-config.csv config file
     */
    private static final int GROUP_NAME_POSITION = 0;
    private static final int FILES_GLOB_POSITION = 1;

    public GroupConfigCsvParser(Path csvFilePath) throws IOException {
        super(csvFilePath);
    }

    /**
     * Gets the list of positions that are mandatory for verification.
     */
    @Override
    protected int[] mandatoryPositions() {
        return new int[] {
            GROUP_NAME_POSITION,
            FILES_GLOB_POSITION,
        };
    }

    /**
     * Processes the csv file line by line and add created {@code Group} into {@code results}
     */
    @Override
    protected void processLine(List<Group> results, String[] elements) {
        Group group = new Group(getValueInElement(elements, GROUP_NAME_POSITION),
            getManyValueInElement(elements, FILES_GLOB_POSITION));
        results.add(group);
    }
}
