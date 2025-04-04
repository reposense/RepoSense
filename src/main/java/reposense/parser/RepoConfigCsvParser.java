package reposense.parser;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import reposense.model.CliArguments;
import reposense.model.CommitHash;
import reposense.model.FileType;
import reposense.model.RepoConfiguration;
import reposense.model.RepoLocation;
import reposense.parser.exceptions.InvalidLocationException;
import reposense.parser.exceptions.ParseException;
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

    private static final String LOCAL_DATETIME_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private static final String DEFAULT_START_TIME = " 00:00:00";
    private static final String DEFAULT_END_TIME = " 23:59:59";
    private static final String MESSAGE_SINCE_DATE_LATER_THAN_TODAY_DATE =
            "\"Since Date\" should not be later than \"Until Date\"";
    private static final String MESSAGE_PARSING_INVALID_FORMAT =
            "The format for since date and until date should be dd/MM/yyyy";

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
    private static final String[] SINCE_HEADER = {"Since Date"};
    private static final String[] UNTIL_HEADER = {"Until Date"};
    private boolean isCliSinceProvided = false;
    private boolean isCliUntilProvided = false;
    private LocalDateTime cliSinceDate;
    private LocalDateTime cliUntilDate;


    public RepoConfigCsvParser(Path csvFilePath) throws FileNotFoundException {
        super(csvFilePath);
    }

    public RepoConfigCsvParser(Path csvFilePath, CliArguments cliArguments) throws FileNotFoundException {
        super(csvFilePath);
        this.isCliSinceProvided = cliArguments.isSinceDateProvided();
        this.isCliUntilProvided = cliArguments.isUntilDateProvided();
        this.cliSinceDate = cliArguments.getSinceDate();
        this.cliUntilDate = cliArguments.getUntilDate();
    }

    /**
     * Gets the list of headers that are mandatory for verification.
     */
    @Override
    protected String[][] mandatoryHeaders() {
        return new String[][]{
                LOCATION_HEADER,
        };
    }

    /**
     * Gets the list of optional headers that can be parsed.
     */
    @Override
    protected String[][] optionalHeaders() {
        return new String[][]{
                BRANCH_HEADER, FILE_FORMATS_HEADER, IGNORE_GLOB_LIST_HEADER, IGNORE_STANDALONE_CONFIG_HEADER,
                IGNORE_FILESIZE_LIMIT_HEADER, IGNORE_COMMIT_LIST_CONFIG_HEADER, IGNORE_AUTHOR_LIST_CONFIG_HEADER,
                SHALLOW_CLONING_CONFIG_HEADER, FIND_PREVIOUS_AUTHORS_CONFIG_HEADER, FILESIZE_LIMIT_HEADER,
                SKIP_IGNORED_FILE_ANALYSIS_HEADER, SINCE_HEADER, UNTIL_HEADER
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
    protected void processLine(List<RepoConfiguration> results, CSVRecord record)
            throws ParseException {
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

        boolean isFileSizeLimitIgnored = matchValueAndKeyword(record, IGNORE_FILESIZE_LIMIT_HEADER,
                IGNORE_FILESIZE_LIMIT_KEYWORD);

        boolean isIgnoredFileAnalysisSkipped = matchValueAndKeyword(record, SKIP_IGNORED_FILE_ANALYSIS_HEADER,
                SKIP_IGNORED_FILE_ANALYSIS_KEYWORD);

        if (isFileSizeLimitIgnored && isIgnoredFileAnalysisSkipped) {
            logger.warning("Ignoring skip ignored file analysis column since file size limit is ignored");
            isIgnoredFileAnalysisSkipped = false;
        }

        boolean isFileSizeLimitOverriding = isElementOverridingStandaloneConfig(record, FILESIZE_LIMIT_HEADER);
        List<String> fileSizeLimitStringList = getAsListWithoutOverridePrefix(record, FILESIZE_LIMIT_HEADER);
        long fileSizeLimit = RepoConfiguration.DEFAULT_FILE_SIZE_LIMIT;

        // Retrieve and update date
        LocalDateTime sinceDate = null;
        LocalDateTime untilDate = null;
        boolean hasUpdatedSinceDateTime = false;
        boolean hasUpdatedUntilDateTime = false;

        sinceDate = this.extractCsvSinceDate(record);
        untilDate = this.extractCsvUntilDate(record);

        hasUpdatedSinceDateTime = (sinceDate != null);
        hasUpdatedUntilDateTime = (untilDate != null);

        if (isCliSinceProvided && isCliUntilProvided) {
            if (hasUpdatedUntilDateTime) {
                this.checkValidDatesWithCli(sinceDate);
            } else {
                sinceDate = cliSinceDate;
            }

            if (hasUpdatedUntilDateTime) {
                this.checkValidDatesWithCli(untilDate);
            } else {
                untilDate = cliUntilDate;
            }
        } else {
            if (!hasUpdatedSinceDateTime) {
                sinceDate = cliSinceDate;
            }

            if (!hasUpdatedUntilDateTime) {
                untilDate = cliUntilDate;
            }
        }

        hasUpdatedSinceDateTime = true;
        hasUpdatedUntilDateTime = true;

        if (sinceDate != null && untilDate != null && sinceDate.isAfter(untilDate)) {
            throw new ParseException(MESSAGE_SINCE_DATE_LATER_THAN_TODAY_DATE);
        }

        // If file diff limit is specified
        if (fileSizeLimitStringList.size() > 0) {
            String fileSizeLimitString = fileSizeLimitStringList.get(0).trim();
            int parseValue;

            if (isFileSizeLimitIgnored) {
                logger.warning("Ignoring file size limit column since file size limit is ignored");
                isFileSizeLimitOverriding = false;
            } else if (!StringsUtil.isNumeric(fileSizeLimitString)
                    || (parseValue = Integer.parseInt(fileSizeLimitString)) <= 0) {
                logger.warning(String.format("Values in \"%s\" column should be positive integers.",
                        FILESIZE_LIMIT_HEADER[0]));
                isFileSizeLimitOverriding = false;
            } else {
                fileSizeLimit = parseValue;
            }
        }

        boolean isStandaloneConfigIgnored = matchValueAndKeyword(record, IGNORE_STANDALONE_CONFIG_HEADER,
                IGNORE_STANDALONE_CONFIG_KEYWORD);

        boolean isShallowCloningPerformed = matchValueAndKeyword(record, SHALLOW_CLONING_CONFIG_HEADER,
                SHALLOW_CLONING_CONFIG_KEYWORD);

        boolean isFindingPreviousAuthorsPerformed = matchValueAndKeyword(record, FIND_PREVIOUS_AUTHORS_CONFIG_HEADER,
                FIND_PREVIOUS_AUTHORS_KEYWORD);

        addConfig(results, location, branch, isFormatsOverriding, formats, isIgnoreGlobListOverriding, ignoreGlobList,
                isIgnoreCommitListOverriding, ignoreCommitList, isIgnoredAuthorsListOverriding, ignoredAuthorsList,
                isFileSizeLimitIgnored, isIgnoredFileAnalysisSkipped, isFileSizeLimitOverriding, fileSizeLimit,
                isStandaloneConfigIgnored, isShallowCloningPerformed, isFindingPreviousAuthorsPerformed,
                hasUpdatedSinceDateTime, hasUpdatedUntilDateTime, sinceDate, untilDate);
    }

    /**
     * Checks dates is within since date and until date provided by CLI flags.
     *
     * @throws ParseException if it is not within the range.
     */
    private void checkValidDatesWithCli(LocalDateTime date) throws ParseException {
        if (date == null || date.isAfter(cliUntilDate) || cliSinceDate.isAfter(date)) {
            throw new ParseException("");
        }
    }

    /**
     * Extracts since date from csv file.
     *
     * @throws ParseException if the format of since date is not recognizable.
     */
    private LocalDateTime extractCsvSinceDate(CSVRecord record) throws ParseException {
        String sinceDateStr = get(record, SINCE_HEADER);
        boolean hasUpdatedSinceDateTime = !sinceDateStr.isEmpty();
        try {
            if (hasUpdatedSinceDateTime) {
                return LocalDateTime.parse(sinceDateStr + DEFAULT_START_TIME,
                        DateTimeFormatter.ofPattern(LOCAL_DATETIME_FORMAT));
            } else {
                return null;
            }
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_PARSING_INVALID_FORMAT);
        }
    }

    /**
     * Extracts end date from csv file.
     *
     * @throws ParseException if the format of until date is not recognizable.
     */
    private LocalDateTime extractCsvUntilDate(CSVRecord record) throws ParseException {
        String untilDateStr = get(record, UNTIL_HEADER);

        try {
            if (!untilDateStr.isEmpty()) {
                return LocalDateTime.parse(untilDateStr + DEFAULT_END_TIME,
                        DateTimeFormatter.ofPattern(LOCAL_DATETIME_FORMAT));
            } else {
                return null;
            }
        } catch (DateTimeParseException e) {
            throw new ParseException(MESSAGE_PARSING_INVALID_FORMAT);
        }
    }

    /**
     * Returns true if value from {@code record}, that matches any of the equivalent headers in
     * {@code equivalentHeaders}, is the same as the given {@code keyword}, else false.
     */
    private boolean matchValueAndKeyword(CSVRecord record, String[] equivalentHeaders, String keyword) {
        String value = get(record, equivalentHeaders);
        boolean isIgnored = value.equalsIgnoreCase(keyword);

        if (!isIgnored && !value.isEmpty()) {
            logger.warning(String.format("Ignoring unknown value %s in %s column.", value, keyword.toLowerCase()));
        }

        return isIgnored;
    }

    /**
     * Creates a new {@link RepoConfiguration} with the supplied inputs and attempts to add it to {@code results}.
     * Does nothing if the repo already exists in {@code results}.
     */
    private void addConfig(List<RepoConfiguration> results, RepoLocation location, String branch,
                           boolean isFormatsOverriding, List<FileType> formats,
                           boolean isIgnoreGlobListOverriding, List<String> ignoreGlobList,
                           boolean isIgnoreCommitListOverriding, List<CommitHash> ignoreCommitList,
                           boolean isIgnoredAuthorsListOverriding, List<String> ignoredAuthorsList,
                           boolean isFileSizeLimitIgnored, boolean isIgnoredFileAnalysisSkipped,
                           boolean isFileSizeLimitOverriding, long fileSizeLimit,
                           boolean isStandaloneConfigIgnored, boolean isShallowCloningPerformed,
                           boolean isFindingPreviousAuthorsPerformed, boolean hasUpdatedSinceDateTime,
                           boolean hasUpdatedUntilDateTime, LocalDateTime since, LocalDateTime until) {
        RepoConfiguration config = new RepoConfiguration.Builder()
                .location(location)
                .branch(branch)
                .fileTypeManager(formats)
                .ignoreGlobList(ignoreGlobList)
                .fileSizeLimit(fileSizeLimit)
                .isStandaloneConfigIgnored(isStandaloneConfigIgnored)
                .isFileSizeLimitIgnored(isFileSizeLimitIgnored)
                .ignoreCommitList(ignoreCommitList)
                .isFormatsOverriding(isFormatsOverriding)
                .isIgnoreGlobListOverriding(isIgnoreGlobListOverriding)
                .isIgnoreCommitListOverriding(isIgnoreCommitListOverriding)
                .isFileSizeLimitOverriding(isFileSizeLimitOverriding)
                .isShallowCloningPerformed(isShallowCloningPerformed)
                .isFindingPreviousAuthorsPerformed(isFindingPreviousAuthorsPerformed)
                .isIgnoredFileAnalysisSkipped(isIgnoredFileAnalysisSkipped)
                .ignoredAuthorsList(ignoredAuthorsList)
                .isIgnoredAuthorsListOverriding(isIgnoredAuthorsListOverriding)
                .setSinceDateBasedOnConfig(hasUpdatedSinceDateTime, since)
                .setUntilDateBasedOnConfig(hasUpdatedUntilDateTime, until)
                .build();

        if (results.contains(config)) {
            logger.warning("Ignoring duplicated repository " + location + " " + branch);
            return;
        }

        results.add(config);
    }
}
