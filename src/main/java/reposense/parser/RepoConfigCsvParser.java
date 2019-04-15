package reposense.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import reposense.model.CommitHash;
import reposense.model.Format;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;

/**
 * Container for the values parsed from {@code repo-config.csv} file.
 */
public class RepoConfigCsvParser extends CsvParser<RepoConfiguration> {
    public static final String REPO_CONFIG_FILENAME = "repo-config.csv";
    private static final String IGNORE_STANDALONE_CONFIG_KEYWORD = "yes";

    /**
     * Positions of the elements of a line in repo-config.csv config file
     */
    private static final int LOCATION_POSITION = 0;
    private static final int BRANCH_POSITION = 1;
    private static final int FILE_FORMATS_POSITION = 2;
    private static final int IGNORE_GLOB_LIST_POSITION = 3;
    private static final int IGNORE_STANDALONE_CONFIG_POSITION = 4;
    private static final int IGNORE_COMMIT_LIST_CONFIG_POSITION = 5;

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
        };
    }

    /**
     * Processes the csv file line by line and add created {@code RepoConfiguration} into {@code results} but
     * ignores duplicated {@code RepoConfiguration} if there exists one that has same {@code location} and
     * {@code branch}.
     */
    @Override
    protected void processLine(List<RepoConfiguration> results, String[] elements) throws InvalidLocationException {
        RepoLocation location = new RepoLocation(getValueInElement(elements, LOCATION_POSITION));
        String branch = getValueInElement(elements, BRANCH_POSITION, RepoConfiguration.DEFAULT_BRANCH);

        boolean isFormatsOverriding = isElementOverridingStandaloneConfig(elements, FILE_FORMATS_POSITION);
        if (isFormatsOverriding) {
            removeOverrideKeywordFromElement(elements, FILE_FORMATS_POSITION);
        }
        List<Format> formats = Format.convertStringsToFormats(getManyValueInElement(elements, FILE_FORMATS_POSITION));

        boolean isIgnoreGlobListOverriding = isElementOverridingStandaloneConfig(elements, IGNORE_GLOB_LIST_POSITION);
        if (isIgnoreGlobListOverriding) {
            removeOverrideKeywordFromElement(elements, IGNORE_GLOB_LIST_POSITION);
        }
        List<String> ignoreGlobList = getManyValueInElement(elements, IGNORE_GLOB_LIST_POSITION);

        boolean isIgnoreCommitListOverriding =
                isElementOverridingStandaloneConfig(elements, IGNORE_COMMIT_LIST_CONFIG_POSITION);
        if (isIgnoreCommitListOverriding) {
            removeOverrideKeywordFromElement(elements, IGNORE_COMMIT_LIST_CONFIG_POSITION);
        }
        List<CommitHash> ignoreCommitList = CommitHash.convertStringsToCommits(
                getManyValueInElement(elements, IGNORE_COMMIT_LIST_CONFIG_POSITION));

        String ignoreStandaloneConfig = getValueInElement(elements, IGNORE_STANDALONE_CONFIG_POSITION);
        boolean isStandaloneConfigIgnored = ignoreStandaloneConfig.equalsIgnoreCase(IGNORE_STANDALONE_CONFIG_KEYWORD);

        if (!isStandaloneConfigIgnored && !ignoreStandaloneConfig.isEmpty()) {
            logger.warning(
                    "Ignoring unknown value " + ignoreStandaloneConfig + " in ignore standalone config column.");
        }

        RepoConfiguration config = new RepoConfiguration(
                location, branch, formats, ignoreGlobList, isStandaloneConfigIgnored, ignoreCommitList,
                isFormatsOverriding, isIgnoreGlobListOverriding, isIgnoreCommitListOverriding);

        if (results.contains(config)) {
            logger.warning("Ignoring duplicated repository " + location + " " + branch);
            return;
        }

        results.add(config);
    }
}
