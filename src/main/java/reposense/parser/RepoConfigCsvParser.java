package reposense.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import reposense.model.RepoConfiguration;

public class RepoConfigCsvParser extends CsvParser<RepoConfiguration> {
    public static final String REPO_CONFIG_FILENAME = "repo-config.csv";
    private static final String IGNORE_STANDALONE_CONFIG_JSON_KEYWORD = "overrule";

    /**
     * Positions of the elements of a line in repo-config.csv config file
     */
    private static final int LOCATION_POSITION = 0;
    private static final int BRANCH_POSITION = 1;
    private static final int IGNORE_GLOB_LIST_POSITION = 2;
    private static final int IGNORE_STANDALONE_CONFIG_JSON_POSITION = 3;

    public RepoConfigCsvParser(Path csvFilePath) throws IOException {
        super(csvFilePath);
    }

    /**
     * Gets the list of positions that are mandatory for verification.
     */
    @Override
    protected int[] mandatoryPositions() {
        return new int[] {
            LOCATION_POSITION,
            BRANCH_POSITION
        };
    }

    /**
     * Processes the csv file line by line and add created {@code RepoConfiguration} into {@code results} but
     * ignores duplicated {@code RepoConfiguration} if there exists one that has same {@code location} and
     * {@code branch}.
     */
    @Override
    protected void processLine(List<RepoConfiguration> results, String[] elements) throws InvalidLocationException {
        String location = getValueInElement(elements, LOCATION_POSITION);
        String branch = getValueInElement(elements, BRANCH_POSITION);
        List<String> ignoreGlobList = getManyValueInElement(elements, IGNORE_GLOB_LIST_POSITION);
        String ignoreStandaloneConfigJson = getValueInElement(elements, IGNORE_STANDALONE_CONFIG_JSON_POSITION);

        if (!ignoreStandaloneConfigJson.equalsIgnoreCase(IGNORE_STANDALONE_CONFIG_JSON_KEYWORD)
                && !ignoreStandaloneConfigJson.isEmpty()) {
            logger.warning("Unknown value " + ignoreStandaloneConfigJson + " used for ignore config.json");
            ignoreStandaloneConfigJson = "";
        }

        boolean isIgnoreStandaloneConfigJson = !ignoreStandaloneConfigJson.isEmpty();

        RepoConfiguration config =
                new RepoConfiguration(location, branch, ignoreGlobList, isIgnoreStandaloneConfigJson);

        if (results.contains(config)) {
            logger.warning("Ignoring duplicated repository " + location + " " + branch);
            return;
        }

        results.add(config);
    }
}
