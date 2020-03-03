package reposense.parser;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import reposense.model.CommitHash;
import reposense.model.FileType;
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
    private static final int IGNORE_AUTHOR_LIST_CONFIG_POSITION = 6;
    private static final int HEADER_SIZE = IGNORE_AUTHOR_LIST_CONFIG_POSITION + 1; // last position + 1

    public RepoConfigCsvParser(Path csvFilePath) throws IOException {
        super(csvFilePath, HEADER_SIZE);
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
    protected void processLine(List<RepoConfiguration> results, CSVRecord record) throws InvalidLocationException {
        RepoLocation location = new RepoLocation(get(record, LOCATION_POSITION));
        String branch = getOrDefault(record, BRANCH_POSITION, RepoConfiguration.DEFAULT_BRANCH);

        boolean isFormatsOverriding = isElementOverridingStandaloneConfig(record, FILE_FORMATS_POSITION);
        List<FileType> formats = FileType.convertFormatStringsToFileTypes(
                getAsListWithoutOverridePrefix(record, FILE_FORMATS_POSITION));

        boolean isIgnoreGlobListOverriding = isElementOverridingStandaloneConfig(record, IGNORE_GLOB_LIST_POSITION);
        List<String> ignoreGlobList = getAsListWithoutOverridePrefix(record, IGNORE_GLOB_LIST_POSITION);

        boolean isIgnoreCommitListOverriding =
                isElementOverridingStandaloneConfig(record, IGNORE_COMMIT_LIST_CONFIG_POSITION);
        List<CommitHash> ignoreCommitList = CommitHash.convertStringsToCommits(
                getAsListWithoutOverridePrefix(record, IGNORE_COMMIT_LIST_CONFIG_POSITION));

        boolean isIgnoredAuthorsListOverriding =
                isElementOverridingStandaloneConfig(record, IGNORE_AUTHOR_LIST_CONFIG_POSITION);
        List<String> ignoredAuthorsList = getAsListWithoutOverridePrefix(record, IGNORE_AUTHOR_LIST_CONFIG_POSITION);

        String ignoreStandaloneConfig = get(record, IGNORE_STANDALONE_CONFIG_POSITION);
        boolean isStandaloneConfigIgnored = ignoreStandaloneConfig.equalsIgnoreCase(IGNORE_STANDALONE_CONFIG_KEYWORD);

        if (!isStandaloneConfigIgnored && !ignoreStandaloneConfig.isEmpty()) {
            logger.warning(
                    "Ignoring unknown value " + ignoreStandaloneConfig + " in ignore standalone config column.");
        }

        RepoConfiguration config = new RepoConfiguration(
                location, branch, formats, ignoreGlobList, isStandaloneConfigIgnored, ignoreCommitList,
                isFormatsOverriding, isIgnoreGlobListOverriding, isIgnoreCommitListOverriding);
        config.setIgnoredAuthorsList(ignoredAuthorsList);
        config.setIsIgnoredAuthorsListOverriding(isIgnoredAuthorsListOverriding);

        if (results.contains(config)) {
            logger.warning("Ignoring duplicated repository " + location + " " + branch);
            return;
        }

        results.add(config);
    }
}
