package reposense.commits;

import static reposense.util.StringsUtil.removeQuote;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import reposense.commits.model.CommitInfo;
import reposense.commits.model.CommitResult;
import reposense.commits.model.ContributionPair;
import reposense.commits.model.FileChangeStats;
import reposense.model.Author;
import reposense.model.CommitHash;
import reposense.model.FileType;
import reposense.model.RepoConfiguration;
import reposense.system.LogsManager;

/**
 * Analyzes commit information found in the git log.
 */
public class CommitInfoAnalyzer {
    public static final DateTimeFormatter GIT_STRICT_ISO_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssz");

    private static final String TAB_SPLITTER = "\t";
    private static final String MOVED_FILE_INDICATION = "=> ";
    private static final String BINARY_FILE_CONTRIBUTION = "-";
    private static final int STAT_ADDITION_INDEX = 0;
    private static final int STAT_DELETION_INDEX = 1;
    private static final int STAT_FILE_PATH_INDEX = 2;

    private static final Logger logger = LogsManager.getLogger(CommitInfoAnalyzer.class);
    private static final String MESSAGE_START_ANALYZING_COMMIT_INFO = "Analyzing commits info for %s (%s)...";

    private static final String HASH_SPLITTER = "\\s";
    private static final String LOG_SPLITTER = "\\|\\n\\|";
    private static final String REF_SPLITTER = ",\\s";
    private static final String NEW_LINE_SPLITTER = "\\n";
    private static final String TAG_PREFIX = "tag:";

    private static final int COMMIT_HASH_INDEX = 0;
    private static final int PARENT_HASHES_INDEX = 1;
    private static final int AUTHOR_INDEX = 2;
    private static final int EMAIL_INDEX = 3;
    private static final int DATE_INDEX = 4;
    private static final int MESSAGE_TITLE_INDEX = 5;
    private static final int MESSAGE_BODY_INDEX = 6;
    private static final int REF_NAME_INDEX = 7;

    private static final String INTEGER_REGEX = "-?\\d+";
    private static final int NUM_FILES_CHANGED_INDEX = 0;
    private static final int TOTAL_NUM_INSERTION_INDEX = 1;
    private static final int TOTAL_NUM_DELETION_INDEX = 2;
    private static final int FILE_STATS_COUNT = 3;

    private static final Pattern MESSAGEBODY_LEADING_PATTERN = Pattern.compile("^ {4}", Pattern.MULTILINE);

    /**
     * Analyzes each {@link CommitInfo} in {@code commitInfos} and returns a list of {@link CommitResult} that is not
     * specified to be ignored or the author is inside {@code config}.
     */
    public List<CommitResult> analyzeCommits(List<CommitInfo> commitInfos, RepoConfiguration config) {
        logger.info(String.format(MESSAGE_START_ANALYZING_COMMIT_INFO, config.getLocation(), config.getBranch()));

        return commitInfos.stream()
                .map(commitInfo -> analyzeCommit(commitInfo, config))
                .filter(commitResult -> !commitResult.getAuthor().equals(Author.UNKNOWN_AUTHOR)
                        && !CommitHash.isInsideCommitList(commitResult.getHash(), config.getIgnoreCommitList()))
                .distinct()
                .sorted(Comparator.comparing(CommitResult::getTime))
                .collect(Collectors.toList());
    }

    /**
     * Extracts the relevant data from {@code commitInfo} into a {@link CommitResult}. Retrieves the author of the
     * commit from {@code config}.
     */
    public CommitResult analyzeCommit(CommitInfo commitInfo, RepoConfiguration config) {
        String infoLine = commitInfo.getInfoLine();
        String statLine = commitInfo.getStatLine();

        String[] elements = infoLine.split(LOG_SPLITTER, 8);
        String hash = elements[COMMIT_HASH_INDEX];
        Boolean isMergeCommit = elements[PARENT_HASHES_INDEX].split(HASH_SPLITTER).length > 1;
        Author author = config.getAuthor(elements[AUTHOR_INDEX], elements[EMAIL_INDEX]);

        ZonedDateTime date = null;
        try {
            date = ZonedDateTime.parse(elements[DATE_INDEX], GIT_STRICT_ISO_DATE_FORMAT);
        } catch (DateTimeParseException pe) {
            logger.log(Level.WARNING, "Unable to parse the date from git log result for commit.", pe);
        }

        // Commit date may be in a timezone different from the one given in the config.
        LocalDateTime adjustedDate = date.withZoneSameInstant(config.getZoneId()).toLocalDateTime();

        String messageTitle = (elements.length > MESSAGE_TITLE_INDEX) ? elements[MESSAGE_TITLE_INDEX] : "";
        String messageBody = (elements.length > MESSAGE_BODY_INDEX)
                ? getCommitMessageBody(elements[MESSAGE_BODY_INDEX]) : "";

        String[] refs = (elements.length > REF_NAME_INDEX)
                ? elements[REF_NAME_INDEX].split(REF_SPLITTER)
                : new String[]{""};
        String[] tags = Arrays.stream(refs).filter(ref -> ref.contains(TAG_PREFIX)).toArray(String[]::new);
        if (tags.length == 0) {
            tags = null; // set to null so it won't be converted to json
        } else {
            extractTagNames(tags);
        }

        if (statLine.isEmpty()) { // empty commit, no files changed
            return new CommitResult(author, hash, isMergeCommit, adjustedDate, messageTitle, messageBody, tags);
        }

        String[] statInfos = statLine.split(NEW_LINE_SPLITTER);

        // Only copy from 0 to the second last element of statInfos (last line of statLine).
        // Don't include the last element of statInfos in fileTypeContributions as it stores
        // the total number of files changed, insertions and deletions.
        String[] fileTypeContributions = Arrays.copyOfRange(statInfos, 0, statInfos.length - 1);
        Map<FileType, ContributionPair> fileTypeAndContributionMap =
                getFileTypesAndContribution(fileTypeContributions, config);

        // Extract number of files changed, total insertions and deletions
        String fileChangeSummaryString = statInfos[statInfos.length - 1];
        FileChangeStats fileChangeStats = getFileChangeStats(fileChangeSummaryString);

        return new CommitResult(author, hash, isMergeCommit, adjustedDate, messageTitle, messageBody, tags,
                fileTypeAndContributionMap, fileChangeStats);
    }

    private FileChangeStats getFileChangeStats(String fileChangeSummaryString) {
        String[] patterns = {
                "(\\d+)\\s+files? changed",         // "X file(s) changed"
                "(\\d+)\\s+insertions?\\(\\+\\)",    // "Y insertions(+)"
                "(\\d+)\\s+deletions?\\(-\\)"        // "Z deletions(-)"
        };
        int[] stats = new int[patterns.length]; // [filesChanged, insertions, deletions]

        for (int i = 0; i < patterns.length; i++) {
            Matcher matcher = Pattern.compile(patterns[i]).matcher(fileChangeSummaryString);
            // Get the integer value from the first parenthesized group in each pattern
            // If pattern is found, parse the captured number; otherwise keep 0
            stats[i] = matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
        }
        return new FileChangeStats(stats[NUM_FILES_CHANGED_INDEX], stats[TOTAL_NUM_INSERTION_INDEX],
                stats[TOTAL_NUM_DELETION_INDEX]);
    }

    /**
     * Returns the number of lines added and deleted in {@code filePathContributions} for the specified file types
     * in {@code config}.
     */
    private Map<FileType, ContributionPair> getFileTypesAndContribution(String[] filePathContributions,
            RepoConfiguration config) {
        Map<FileType, ContributionPair> fileTypesAndContributionMap = new HashMap<>();
        for (String filePathContribution : filePathContributions) {
            String[] infos = filePathContribution.split(TAB_SPLITTER);

            if (isBinaryContribution(infos[STAT_ADDITION_INDEX], infos[STAT_DELETION_INDEX])) {
                continue; // skip binary file contributions
            }

            int addition = Integer.parseInt(infos[STAT_ADDITION_INDEX]);
            int deletion = Integer.parseInt(infos[STAT_DELETION_INDEX]);
            String filePath = extractFilePath(infos[STAT_FILE_PATH_INDEX]);
            FileType fileType = config.getFileType(filePath);

            if (!fileTypesAndContributionMap.containsKey(fileType)) {
                fileTypesAndContributionMap.put(fileType, new ContributionPair());
            }

            ContributionPair contributionPair = fileTypesAndContributionMap.get(fileType);
            contributionPair.addInsertions(addition);
            contributionPair.addDeletions(deletion);
        }
        return fileTypesAndContributionMap;
    }

    /**
     * Extracts the correct file path from the unprocessed git log {@code filePath}.
     */
    private String extractFilePath(String filePath) {
        String filteredFilePath = filePath;
        if (filteredFilePath.contains(MOVED_FILE_INDICATION)) {
            // moved file has the format: fileA => newPosition/fileA
            filteredFilePath = filteredFilePath.substring(filePath.indexOf(MOVED_FILE_INDICATION)
                    + MOVED_FILE_INDICATION.length());
            // Removes the trailing '}' character from the file name, as renamed file names have ending '}' character.
            filteredFilePath = filteredFilePath.replaceAll("}$", "");
        }

        // Removes the trailing double quotes from the file name, as filenames that have special characters
        // will be escaped and surrounded by double quotes automatically. e.g. READ\ME.md -> "READ\\ME.md"
        filteredFilePath = removeQuote(filteredFilePath);
        return filteredFilePath;
    }

    /**
     * Detects binary file contribution based on the git log {@code addition} and {@code deletion}.
     */
    private boolean isBinaryContribution(String addition, String deletion) {
        // git log returns "-" for binary file additions and deletions
        return addition.equals(BINARY_FILE_CONTRIBUTION) && deletion.equals(BINARY_FILE_CONTRIBUTION);
    }

    /**
     * Extracts the tag names in {@code tags}.
     */
    private void extractTagNames(String[] tags) {
        for (int i = 0; i < tags.length; i++) {
            tags[i] = tags[i].substring(tags[i].lastIndexOf(TAG_PREFIX) + TAG_PREFIX.length()).trim();
        }
    }

    private String getCommitMessageBody(String raw) {
        Matcher matcher = MESSAGEBODY_LEADING_PATTERN.matcher(raw);
        return matcher.replaceAll("");
    }
}
