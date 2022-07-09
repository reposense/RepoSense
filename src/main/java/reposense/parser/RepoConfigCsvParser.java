package reposense.parser;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import reposense.model.CommitHash;
import reposense.model.FileType;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.util.FileUtil;
import reposense.util.StringsUtil;

/**
 * Container for the values parsed from {@code repo-config.csv} file.
 */
public class RepoConfigCsvParser extends CsvParser<RepoConfiguration> {
    public static final String REPO_CONFIG_FILENAME = "repo-config.csv";
    private static final String IGNORE_STANDALONE_CONFIG_KEYWORD = "yes";
    private static final String IGNORE_FILESIZE_LIMIT_KEYWORD = "yes";
    private static final String SKIP_IGNORED_FILE_ANALYSIS_KEYWORD = "yes";
    private static final String SHALLOW_CLONING_CONFIG_KEYWORD = "yes";
    private static final String FIND_PREVIOUS_AUTHORS_KEYWORD = "yes";

    /**
     * Positions of the elements of a line in repo-config.csv config file
     */
    private static final String[] LOCATION_HEADER = {"Repository's Location"};
    private static final String[] BRANCH_HEADER = {"Branch"};
    private static final String[] FILE_FORMATS_HEADER = {"File formats"};
    private static final String[] IGNORE_GLOB_LIST_HEADER = {"Ignore Glob List"};
    private static final String[] IGNORE_STANDALONE_CONFIG_HEADER = {"Ignore Standalone Config"};
    private static final String[] IGNORE_FILESIZE_LIMIT_HEADER = {"Ignore File Size Limit"};
    private static final String[] IGNORE_COMMIT_LIST_CONFIG_HEADER = {"Ignore Commits List"};
    private static final String[] IGNORE_AUTHOR_LIST_CONFIG_HEADER = {"Ignore Authors List"};
    private static final String[] SKIP_IGNORED_FILE_ANALYSIS_HEADER = {"Skip Ignored File Analysis"};
    private static final String[] SHALLOW_CLONING_CONFIG_HEADER = {"Shallow Cloning"};
    private static final String[] FIND_PREVIOUS_AUTHORS_CONFIG_HEADER = {"Find Previous Authors"};
    private static final String[] FILESIZE_LIMIT_HEADER = {"File Size Limit"};

    public RepoConfigCsvParser(Path csvFilePath) throws FileNotFoundException {
        super(csvFilePath);
    }

    /**
     * Gets the list of headers that are mandatory for verification.
     */
    @Override
    protected String[][] mandatoryHeaders() {
        return new String[][] {
                LOCATION_HEADER,
        };
    }

    /**
     * Gets the list of optional headers that can be parsed.
     */
    @Override
    protected String[][] optionalHeaders() {
        return new String[][] {
                BRANCH_HEADER, FILE_FORMATS_HEADER, IGNORE_GLOB_LIST_HEADER, IGNORE_STANDALONE_CONFIG_HEADER,
                IGNORE_FILESIZE_LIMIT_HEADER, IGNORE_COMMIT_LIST_CONFIG_HEADER, IGNORE_AUTHOR_LIST_CONFIG_HEADER,
                SHALLOW_CLONING_CONFIG_HEADER, FIND_PREVIOUS_AUTHORS_CONFIG_HEADER, FILESIZE_LIMIT_HEADER,
                SKIP_IGNORED_FILE_ANALYSIS_HEADER
        };
    }

    /**
     * Processes the csv {@code record} line by line and add created {@link RepoConfiguration} into {@code results} but
     * ignores duplicated {@link RepoConfiguration} if there exists one that has same {@code location} and
     * {@code branch}.
     *
     * @throws InvalidLocationException if the location represented in {@code record} is invalid.
     */
    @Override
    protected void processLine(List<RepoConfiguration> results, CSVRecord record) throws InvalidLocationException {
        // The variable expansion is performed to simulate running the same location from command line.
        // This helps to support things like tilde expansion and other Bash/CMD features.
        RepoLocation location = new RepoLocation(FileUtil.getVariableExpandedFilePath(get(record, LOCATION_HEADER)));
        String branch = getOrDefault(record, BRANCH_HEADER, RepoConfiguration.DEFAULT_BRANCH);

        boolean isFormatsOverriding = isElementOverridingStandaloneConfig(record, FILE_FORMATS_HEADER);
        List<FileType> formats = FileType.convertFormatStringsToFileTypes(
                getAsListWithoutOverridePrefix(record, FILE_FORMATS_HEADER));

        boolean isIgnoreGlobListOverriding = isElementOverridingStandaloneConfig(record, IGNORE_GLOB_LIST_HEADER);
        List<String> ignoreGlobList = getAsListWithoutOverridePrefix(record, IGNORE_GLOB_LIST_HEADER);

        boolean isIgnoreCommitListOverriding =
                isElementOverridingStandaloneConfig(record, IGNORE_COMMIT_LIST_CONFIG_HEADER);
        List<CommitHash> ignoreCommitList = CommitHash.convertStringsToCommits(
                getAsListWithoutOverridePrefix(record, IGNORE_COMMIT_LIST_CONFIG_HEADER));

        boolean isIgnoredAuthorsListOverriding =
                isElementOverridingStandaloneConfig(record, IGNORE_AUTHOR_LIST_CONFIG_HEADER);
        List<String> ignoredAuthorsList = getAsListWithoutOverridePrefix(record, IGNORE_AUTHOR_LIST_CONFIG_HEADER);

        String ignoreFileSizeLimit = get(record, IGNORE_FILESIZE_LIMIT_HEADER);
        boolean isFileSizeLimitIgnored = ignoreFileSizeLimit.equalsIgnoreCase(IGNORE_FILESIZE_LIMIT_KEYWORD);

        if (!isFileSizeLimitIgnored && !ignoreFileSizeLimit.isEmpty()) {
            logger.warning(
                    "Ignoring unknown value " + ignoreFileSizeLimit + " in ignore filesize limit column.");
        }

        String skipIgnoredFileAnalysis = get(record, SKIP_IGNORED_FILE_ANALYSIS_HEADER);
        boolean isIgnoredFileAnalysisSkipped = skipIgnoredFileAnalysis.equalsIgnoreCase(
                SKIP_IGNORED_FILE_ANALYSIS_KEYWORD);

        if (!isIgnoredFileAnalysisSkipped && !skipIgnoredFileAnalysis.isEmpty()) {
            logger.warning(
                    "Ignoring unknown value " + skipIgnoredFileAnalysis + " in skip ignored file analysis column.");
        }

        if (isFileSizeLimitIgnored && isIgnoredFileAnalysisSkipped) {
            logger.warning("Ignoring skip ignored file analysis column since file size limit is ignored");
            isIgnoredFileAnalysisSkipped = false;
        }

        boolean isFileSizeLimitOverriding = isElementOverridingStandaloneConfig(record, FILESIZE_LIMIT_HEADER);
        List<String> fileSizeLimitStringList = getAsListWithoutOverridePrefix(record, FILESIZE_LIMIT_HEADER);
        long fileSizeLimit = RepoConfiguration.DEFAULT_FILE_SIZE_LIMIT;

        // If file diff limit is specified
        if (isFileSizeLimitIgnored && fileSizeLimitStringList.size() > 0) {
            logger.warning("Ignoring file size limit column since file size limit is ignored");
            isFileSizeLimitOverriding = false;
        } else if (fileSizeLimitStringList.size() > 0) {
            String fileSizeLimitString = fileSizeLimitStringList.get(0).trim();
            int parseValue;
            if (!StringsUtil.isNumeric(fileSizeLimitString)
                    || (parseValue = Integer.parseInt(fileSizeLimitString)) <= 0) {
                logger.warning(String.format("Values in \"%s\" column should be positive integers.",
                        FILESIZE_LIMIT_HEADER));
                isFileSizeLimitOverriding = false;
            } else {
                fileSizeLimit = parseValue;
            }
        }

        String ignoreStandaloneConfig = get(record, IGNORE_STANDALONE_CONFIG_HEADER);
        boolean isStandaloneConfigIgnored = ignoreStandaloneConfig.equalsIgnoreCase(IGNORE_STANDALONE_CONFIG_KEYWORD);

        if (!isStandaloneConfigIgnored && !ignoreStandaloneConfig.isEmpty()) {
            logger.warning(
                    "Ignoring unknown value " + ignoreStandaloneConfig + " in ignore standalone config column.");
        }

        String shallowCloningConfig = get(record, SHALLOW_CLONING_CONFIG_HEADER);
        boolean isShallowCloningPerformed = shallowCloningConfig.equalsIgnoreCase(SHALLOW_CLONING_CONFIG_KEYWORD);

        if (!isShallowCloningPerformed && !shallowCloningConfig.isEmpty()) {
            logger.warning(
                    "Ignoring unknown value " + shallowCloningConfig + " in shallow cloning column.");
        }

        String findPreviousAuthorsConfig = get(record, FIND_PREVIOUS_AUTHORS_CONFIG_HEADER);
        boolean isFindingPreviousAuthorsPerformed = findPreviousAuthorsConfig
                                                        .equalsIgnoreCase(FIND_PREVIOUS_AUTHORS_KEYWORD);

        if (!isFindingPreviousAuthorsPerformed && !findPreviousAuthorsConfig.isEmpty()) {
            logger.warning(
                    "Ignoring unknown value " + findPreviousAuthorsConfig + " in find previous authors column.");
        }

        RepoConfiguration config = new RepoConfiguration(
                location, branch, formats, ignoreGlobList, fileSizeLimit, isStandaloneConfigIgnored,
                isFileSizeLimitIgnored, ignoreCommitList, isFormatsOverriding, isIgnoreGlobListOverriding,
                isIgnoreCommitListOverriding, isFileSizeLimitOverriding, isShallowCloningPerformed,
                isFindingPreviousAuthorsPerformed, isIgnoredFileAnalysisSkipped);
        config.setIgnoredAuthorsList(ignoredAuthorsList);
        config.setIsIgnoredAuthorsListOverriding(isIgnoredAuthorsListOverriding);

        if (results.contains(config)) {
            logger.warning("Ignoring duplicated repository " + location + " " + branch);
            return;
        }

        results.add(config);
    }
}
